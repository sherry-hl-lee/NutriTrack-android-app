package com.example.ass2.util

object NutritionTargets {
    fun recommendedProteinGrams(weightKg: Float): Int =
        if (weightKg <= 0f) 75 else (weightKg * 1.1f).toInt().coerceIn(55, 180)

    // Temporary estimate until protein grams are stored per meal.
    fun estimateProteinFromCalories(calories: Int): Int =
        (calories * 0.03f).toInt().coerceAtLeast(0)
}
