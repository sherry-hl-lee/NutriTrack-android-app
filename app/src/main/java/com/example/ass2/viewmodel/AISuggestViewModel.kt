package com.example.ass2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.Meal
import com.example.ass2.data.local.User
import com.example.ass2.data.remote.GeminiMealSuggestClient
import com.example.ass2.util.DateUtils
import com.example.ass2.util.LocalMealSuggestFallback
import com.example.ass2.util.NutritionScoreRules
import com.example.ass2.util.NutritionTargets
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

    /** Email of the user whose in-flight or displayed suggestion belongs to this session. */
    private var activeUserEmail: String? = null

    fun requestSuggestion(
        user: User?,
        meals: List<Meal>,
        targetCalories: Int
    ) {
        val u = user
        if (u == null || u.weight <= 0f || u.height <= 0f || u.age <= 0) {
            activeUserEmail = null
            _uiState.value = AiSuggestUiState.Error(
                "Complete your profile (weight, height, age) for better suggestions."
            )
            return
        }

        val requestEmail = u.email
        activeUserEmail = requestEmail

        viewModelScope.launch {
            _uiState.value = AiSuggestUiState.Loading
            val todayMeals = meals.filter { DateUtils.isToday(it.date) }
            val targetProtein = NutritionTargets.recommendedProteinGrams(u.weight)
            val scoreResult = NutritionScoreRules.compute(
                meals = todayMeals,
                targetCalories = targetCalories,
                targetProteinGrams = targetProtein
            )
            val scoreHeader = buildScoreHeader(
                scoreResult = scoreResult,
                targetCalories = targetCalories,
                targetProtein = targetProtein,
                todayMeals = todayMeals
            )
            val prompt = buildPrompt(u, targetCalories, meals)

            if (geminiApiKey.isBlank()) {
                val offline = LocalMealSuggestFallback.buildSuggestion(u, targetCalories, meals)
                publishSuccess(
                    requestEmail,
                    scoreHeader + "\n\n" + offline,
                    MealSuggestSource.Offline
                )
                return@launch
            }

            val client = GeminiMealSuggestClient(geminiApiKey)
            val aiResult = client.generateMealSuggestions(prompt)
            if (aiResult.isSuccess) {
                publishSuccess(
                    requestEmail,
                    scoreHeader + "\n\n" + aiResult.getOrThrow(),
                    MealSuggestSource.Gemini
                )
            } else {
                val reason = aiResult.exceptionOrNull()?.message
                    ?.takeIf { it.isNotBlank() }
                    ?: "Unknown error"
                val note = "(Could not reach Gemini: $reason — showing offline ideas instead.)\n\n"
                val offline = LocalMealSuggestFallback.buildSuggestion(u, targetCalories, meals)
                publishSuccess(
                    requestEmail,
                    scoreHeader + "\n\n" + note + offline,
                    MealSuggestSource.Offline
                )
            }
        }
    }

    fun reset() {
        activeUserEmail = null
        _uiState.value = AiSuggestUiState.Idle
    }

    private fun publishSuccess(
        requestEmail: String,
        text: String,
        source: MealSuggestSource
    ) {
        if (activeUserEmail != requestEmail) return
        _uiState.value = AiSuggestUiState.Success(text, source)
    }

    private fun buildPrompt(user: User, targetCalories: Int, meals: List<Meal>): String {
        val todayMeals = meals.filter { DateUtils.isToday(it.date) }
        val consumed = todayMeals.sumOf { it.calories }
        val remaining = (targetCalories - consumed).coerceAtLeast(0)
        val targetProtein = NutritionTargets.recommendedProteinGrams(user.weight)
        val consumedProtein = NutritionTargets.estimateProteinFromCalories(consumed)
        val mealLines = todayMeals.takeIf { it.isNotEmpty() }?.joinToString("\n") { m ->
            "- ${m.mealType}: ${m.name} (${m.calories} kcal)"
        } ?: "- (no meals logged yet today)"

        return """
            You are a concise nutrition coach for a calorie-tracking app. Reply in plain text (no markdown symbols like **).
            User profile: ${user.weight} kg, ${user.height} cm, age ${user.age}, gender ${user.gender}.
            Daily calorie target: $targetCalories kcal.
            Daily protein target: $targetProtein g.
            Today's meals:
            $mealLines
            Approximate calories consumed today: $consumed kcal.
            Approximate protein consumed today: $consumedProtein g.
            Approximate remaining budget for the rest of the day: $remaining kcal.

            Output with this exact structure:
            1) Quick summary (max 1 sentence).
            2) Top 3 actionable tips as bullets.
            3) Meal ideas section with 3 items, each item on one line:
               - Name | kcal | protein(g) | why it fits today
            Keep total under 170 words. No medical claims or diagnoses.
        """.trimIndent()
    }

    private fun buildScoreHeader(
        scoreResult: NutritionScoreRules.Result,
        targetCalories: Int,
        targetProtein: Int,
        todayMeals: List<Meal>
    ): String {
        val consumedCalories = todayMeals.sumOf { it.calories }
        val consumedProtein = NutritionTargets.estimateProteinFromCalories(consumedCalories)
        val remainingCalories = (targetCalories - consumedCalories).coerceAtLeast(0)
        val remainingProtein = (targetProtein - consumedProtein).coerceAtLeast(0)
        val score = scoreResult.baseScore
        val grade = when {
            score >= 85 -> "A"
            score >= 70 -> "B"
            score >= 55 -> "C"
            else -> "D"
        }
        return """
            Today Score: $score/100 (Grade $grade)
            Calories: $consumedCalories / $targetCalories kcal (remaining $remainingCalories)
            Protein: $consumedProtein / $targetProtein g (remaining $remainingProtein)
        """.trimIndent()
    }
}