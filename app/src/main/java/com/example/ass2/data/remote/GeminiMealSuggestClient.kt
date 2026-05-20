package com.example.ass2.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Calls Google Gemini generateContent over HTTPS.
 * Tries newer model names first, then falls back if the API returns 404.
 */
class GeminiMealSuggestClient(
    private val apiKey: String
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    suspend fun generateMealSuggestions(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalStateException("Missing API key"))
        }
        var lastError: String? = null
        for (model in MODEL_CANDIDATES) {
            val result = callModel(model, prompt)
            if (result.isSuccess) return@withContext result
            lastError = result.exceptionOrNull()?.message
            if (lastError?.contains("HTTP 404", ignoreCase = true) != true) {
                return@withContext result
            }
        }
        Result.failure(Exception(lastError ?: "Gemini request failed"))
    }

    private fun callModel(model: String, prompt: String): Result<String> {
        return try {
            val bodyJson = JSONObject().apply {
                put(
                    "contents",
                    JSONArray().put(
                        JSONObject().put(
                            "parts",
                            JSONArray().put(JSONObject().put("text", prompt))
                        )
                    )
                )
            }
            val url =
                "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(bodyJson.toString().toRequestBody(jsonMedia))
                .build()

            client.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val msg = parseApiError(raw) ?: "HTTP ${response.code}"
                    return Result.failure(Exception(msg))
                }
                val text = extractAnswerText(raw)
                    ?: return Result.failure(Exception("Empty model response"))
                Result.success(text)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractAnswerText(responseJson: String): String? {
        val root = JSONObject(responseJson)
        val candidates = root.optJSONArray("candidates") ?: return null
        if (candidates.length() == 0) return null
        val content = candidates.getJSONObject(0).optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        if (parts.length() == 0) return null
        return parts.getJSONObject(0).optString("text").takeIf { it.isNotBlank() }
    }

    private fun parseApiError(raw: String): String? {
        return try {
            val root = JSONObject(raw)
            root.optJSONObject("error")?.optString("message")
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private val MODEL_CANDIDATES = listOf(
            "gemini-2.5-flash",
            "gemini-3.5-flash"
        )
    }
}