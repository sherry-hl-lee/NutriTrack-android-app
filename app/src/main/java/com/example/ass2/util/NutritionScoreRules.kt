package com.example.ass2.util

import com.example.ass2.data.local.Meal
import kotlin.math.roundToInt

object NutritionScoreRules {

    data class Result(
        val baseScore: Int,
        val calorieScore: Int,
        val proteinScore: Int
    )

    private const val IDEAL_RATIO_MIN = 0.85f
    private const val IDEAL_RATIO_MAX = 1.10f
    private const val IDEAL_BAND_SCORE = 95
    private const val CALORIE_WEIGHT = 0.55f
    private const val PROTEIN_WEIGHT = 0.45f

    fun compute(
        meals: List<Meal>,
        targetCalories: Int,
        targetProteinGrams: Int
    ): Result {
        if (meals.isEmpty()) {
            return Result(baseScore = 0, calorieScore = 0, proteinScore = 0)
        }

        val consumedCalories = meals.sumOf { it.calories }
        if (consumedCalories <= 0) {
            return Result(baseScore = 0, calorieScore = 0, proteinScore = 0)
        }

        val consumedProtein = NutritionTargets.estimateProteinFromCalories(consumedCalories)

        val calorieRatio = ratio(consumedCalories, targetCalories)
        val proteinRatio = ratio(consumedProtein, targetProteinGrams)

        val calorieScore = scoreFromRatio(calorieRatio)
        val proteinScore = scoreFromRatio(proteinRatio)

        val baseScore = (calorieScore * CALORIE_WEIGHT + proteinScore * PROTEIN_WEIGHT)
            .roundToInt()
            .coerceIn(0, 100)

        return Result(
            baseScore = baseScore,
            calorieScore = calorieScore,
            proteinScore = proteinScore
        )
    }

    private fun ratio(consumed: Int, target: Int): Float =
        if (target <= 0) 1f else consumed / target.toFloat()

    /**
     * Maps how close intake is to target (1.0 = on target).
     * Ideal band: 85%–110% of target → 95 points.
     */
    private fun scoreFromRatio(ratio: Float): Int = when {
        ratio in IDEAL_RATIO_MIN..IDEAL_RATIO_MAX -> IDEAL_BAND_SCORE
        ratio < IDEAL_RATIO_MIN -> (ratio / IDEAL_RATIO_MIN * 90f).roundToInt().coerceIn(0, 90)
        else -> (IDEAL_BAND_SCORE - ((ratio - IDEAL_RATIO_MAX) * 120f)).roundToInt().coerceIn(0, 90)
    }
}
