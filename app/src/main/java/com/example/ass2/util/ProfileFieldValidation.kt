package com.example.ass2.util

object ProfileFieldLimits {
    const val MIN_WEIGHT = 0f
    const val MAX_WEIGHT = 500f
    const val MIN_HEIGHT = 0f
    const val MAX_HEIGHT = 400f
    const val MIN_AGE = 0
    const val MAX_AGE = 200
}

fun validateWeight(input: String, required: Boolean = false): String? {
    if (input.isBlank()) return if (required) "Weight is required" else null
    val value = input.toFloatOrNull()
        ?: return "Enter a valid weight"
    if (value < ProfileFieldLimits.MIN_WEIGHT || value > ProfileFieldLimits.MAX_WEIGHT) {
        return "Weight must be between 0 and 500 kg"
    }
    return null
}

fun validateHeight(input: String, required: Boolean = false): String? {
    if (input.isBlank()) return if (required) "Height is required" else null
    val value = input.toFloatOrNull()
        ?: return "Enter a valid height"
    if (value < ProfileFieldLimits.MIN_HEIGHT || value > ProfileFieldLimits.MAX_HEIGHT) {
        return "Height must be between 0 and 400 cm"
    }
    return null
}

fun validateAge(input: String, required: Boolean = false): String? {
    if (input.isBlank()) return if (required) "Age is required" else null
    val value = input.toIntOrNull()
        ?: return "Enter a valid age"
    if (value < ProfileFieldLimits.MIN_AGE || value > ProfileFieldLimits.MAX_AGE) {
        return "Age must be between 0 and 200"
    }
    return null
}

fun parseValidatedWeight(input: String): Float? =
    validateWeight(input, required = true)?.let { null } ?: input.toFloatOrNull()

fun parseValidatedHeight(input: String): Float? =
    validateHeight(input, required = true)?.let { null } ?: input.toFloatOrNull()

fun parseValidatedAge(input: String): Int? =
    validateAge(input, required = true)?.let { null } ?: input.toIntOrNull()
