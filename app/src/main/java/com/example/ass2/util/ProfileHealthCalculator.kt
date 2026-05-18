package com.example.ass2.util

data class ProfileHealthMetrics(
    val bmi: Float,
    val weightStatus: String,
    val recommendedDailyCalories: Int
)

object ProfileHealthCalculator {

    fun calculate(
        weightKg: Float,
        heightCm: Float,
        age: Int,
        gender: String
    ): ProfileHealthMetrics? {
        if (weightKg <= 0f || heightCm <= 0f || age <= 0) return null

        val heightM = heightCm / 100f
        val bmi = weightKg / (heightM * heightM)

        return ProfileHealthMetrics(
            bmi = bmi,
            weightStatus = weightStatusFromBmi(bmi),
            recommendedDailyCalories = recommendedDailyCalories(weightKg, heightCm, age, gender)
        )
    }

    /** WHO BMI categories */
    private fun weightStatusFromBmi(bmi: Float): String = when {
        bmi < 18.5f -> "Underweight"
        bmi < 25f -> "Normal weight"
        bmi < 30f -> "Overweight"
        else -> "Obese"
    }

    /** Mifflin–St Jeor BMR × moderate activity (1.55) */
    private fun recommendedDailyCalories(
        weightKg: Float,
        heightCm: Float,
        age: Int,
        gender: String
    ): Int {
        val bmr = when (gender.trim().lowercase()) {
            "female" -> 10 * weightKg + 6.25 * heightCm - 5 * age - 161
            "male" -> 10 * weightKg + 6.25 * heightCm - 5 * age + 5
            else -> 10 * weightKg + 6.25 * heightCm - 5 * age - 78
        }
        return (bmr * 1.55).toInt().coerceAtLeast(1200)
    }
}
