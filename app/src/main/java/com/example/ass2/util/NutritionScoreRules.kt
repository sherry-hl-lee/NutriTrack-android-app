package com.example.ass2.util

import com.example.ass2.data.local.Meal

object NutritionScoreRules {
    data class Result(val baseScore: Int)

    fun compute(
        dayStartMillis: Long,
        meals: List<Meal>,
        targetCalories: Int,
        targetProteinGrams: Int
    ): Result {
        if (meals.isEmpty()) {
            return Result(baseScore = 0)
        }
        val consumed = meals.sumOf { it.calories }
        if (consumed <= 0) {
            return Result(baseScore = 0)
        }
        val ratio = if (targetCalories <= 0) 1f else consumed / targetCalories.toFloat()
        val calorieScore = when {
            ratio in 0.85f..1.10f -> 95
            ratio < 0.85f -> (ratio / 0.85f * 90f).toInt().coerceIn(30, 90)
            else -> (95 - ((ratio - 1.10f) * 120f)).toInt().coerceIn(20, 90)
        }
        return Result(baseScore = calorieScore)
    }
}
