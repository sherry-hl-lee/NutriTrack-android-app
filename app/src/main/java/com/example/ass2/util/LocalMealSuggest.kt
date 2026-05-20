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
        lines += "Quick summary: you've logged about $consumed kcal out of $targetCalories kcal today."
        lines += "Remaining calorie budget: ~$remaining kcal."
        lines += ""
        lines += "Top 3 actionable tips:"
        lines += "• Prioritize protein in your next meal."
        lines += "• Keep one lighter option if dinner is expected to be heavier."
        lines += "• Prefer water/unsweetened drinks with high-calorie meals."
        lines += ""
        lines += "Meal ideas (Name | kcal | protein(g) | why it fits):"
        if (remaining < 150) {
            lines += "• Greek yogurt + berries | ~150 | ~12 | light and protein-supportive when budget is low"
        } else {
            val fits = PresetFoodCatalog.foods
                .filter { it.calories in 1..remaining }
                .shuffled()
                .take(4)
            if (fits.isEmpty()) {
                lines += "• Vegetable soup | ~120 | ~4 | low-calorie option when close to goal"
            } else {
                fits.take(3).forEach { food ->
                    lines += "• ${food.name} | ~${food.calories} | ~0 | fits remaining budget (${food.mealType})"
                }
            }
        }
        lines += ""
        lines += "Tip: add GEMINI_API_KEY in local.properties for personalized AI suggestions."
        return lines.joinToString("\n")
    }
}