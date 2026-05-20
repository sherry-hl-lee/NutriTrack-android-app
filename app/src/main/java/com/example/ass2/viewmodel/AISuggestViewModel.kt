package com.example.ass2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Meal
import com.example.ass2.data.local.User
import com.example.ass2.data.remote.GeminiMealSuggestClient
import com.example.ass2.util.DateUtils
import com.example.ass2.util.LocalMealSuggestFallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class MealSuggestSource {
    Gemini,
    Offline
}

sealed interface AiSuggestUiState {
    data object Idle : AiSuggestUiState
    data object Loading : AiSuggestUiState
    data class Success(val text: String, val source: MealSuggestSource) : AiSuggestUiState
    data class Error(val message: String) : AiSuggestUiState
}

class AiSuggestViewModel(
    private val geminiApiKey: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<AiSuggestUiState>(AiSuggestUiState.Idle)
    val uiState: StateFlow<AiSuggestUiState> = _uiState.asStateFlow()

    fun requestSuggestion(
        user: User?,
        meals: List<Meal>,
        targetCalories: Int
    ) {
        val u = user
        if (u == null || u.weight <= 0f || u.height <= 0f || u.age <= 0) {
            _uiState.value = AiSuggestUiState.Error(
                "Complete your profile (weight, height, age) for better suggestions."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = AiSuggestUiState.Loading
            val prompt = buildPrompt(u, targetCalories, meals)

            if (geminiApiKey.isBlank()) {
                val offline = LocalMealSuggestFallback.buildSuggestion(u, targetCalories, meals)
                _uiState.value = AiSuggestUiState.Success(offline, MealSuggestSource.Offline)
                return@launch
            }

            val client = GeminiMealSuggestClient(geminiApiKey)
            val aiResult = client.generateMealSuggestions(prompt)
            if (aiResult.isSuccess) {
                _uiState.value = AiSuggestUiState.Success(
                    aiResult.getOrThrow(),
                    MealSuggestSource.Gemini
                )
            } else {
                val reason = aiResult.exceptionOrNull()?.message
                    ?.takeIf { it.isNotBlank() }
                    ?: "Unknown error"
                val note = "(Could not reach Gemini: $reason — showing offline ideas instead.)\n\n"
                val offline = LocalMealSuggestFallback.buildSuggestion(u, targetCalories, meals)
                _uiState.value = AiSuggestUiState.Success(
                    note + offline,
                    MealSuggestSource.Offline
                )
            }
        }
    }

    fun reset() {
        _uiState.value = AiSuggestUiState.Idle
    }

    private fun buildPrompt(user: User, targetCalories: Int, meals: List<Meal>): String {
        val todayMeals = meals.filter { DateUtils.isToday(it.date) }
        val consumed = todayMeals.sumOf { it.calories }
        val remaining = (targetCalories - consumed).coerceAtLeast(0)
        val mealLines = todayMeals.takeIf { it.isNotEmpty() }?.joinToString("\n") { m ->
            "- ${m.mealType}: ${m.name} (${m.calories} kcal)"
        } ?: "- (no meals logged yet today)"

        return """
            You are a concise nutrition coach for a calorie-tracking app.
            User profile: ${user.weight} kg, ${user.height} cm, age ${user.age}, gender ${user.gender}.
            Daily calorie target: $targetCalories kcal.
            Today's meals:
            $mealLines
            Approximate calories consumed today: $consumed kcal.
            Approximate remaining budget for the rest of the day: $remaining kcal.

            Give 3 to 5 practical meal or snack ideas (food names + rough kcal each) that fit the remaining budget.
            Use short bullet points. No medical claims or diagnoses. Keep the answer under 180 words.
        """.trimIndent()
    }
}