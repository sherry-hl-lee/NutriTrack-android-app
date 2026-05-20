package com.example.ass2.util

import com.example.ass2.data.PresetFoodCatalog
import com.example.ass2.data.local.Meal
import com.example.ass2.data.local.User

/** Simple offline meal ideas when no API key is set or the network call fails. */
object LocalMealSuggestFallback {

    fun buildSuggestion(user: User, targetCalories: Int, meals: List<Meal>): String {
        val todayMeals = meals.filter { DateUtils.isToday(it.date) }
        val consumed = todayMeals.sumOf { it.calories }
        val remaining = (targetCalories - consumed).coerceAtLeast(0)

        val lines = mutableListOf<String>()
        lines += "• Today you logged about $consumed kcal (goal $targetCalories kcal)."
        lines += "• Roughly $remaining kcal left for the rest of the day."
        lines += ""
        lines += "Ideas that usually fit a balanced day:"
        if (remaining < 150) {
            lines += "• Greek yogurt, a piece of fruit, or vegetable soup — keep it light."
        } else {
            val fits = PresetFoodCatalog.foods
                .filter { it.calories in 1..remaining }
                .shuffled()
                .take(4)
            if (fits.isEmpty()) {
                lines += "• You are already at or above goal — consider water, tea, or extra vegetables."
            } else {
                fits.forEach { food ->
                    lines += "• ${food.name} (~${food.calories} kcal, ${food.mealType})"
                }
            }
        }
        lines += ""
        lines += "Tip: add GEMINI_API_KEY in local.properties for personalized AI suggestions."
        return lines.joinToString("\n")
    }
}