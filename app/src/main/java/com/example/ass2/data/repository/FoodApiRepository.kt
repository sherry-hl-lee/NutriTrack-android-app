package com.example.ass2.data.repository

import com.example.ass2.BuildConfig
import com.example.ass2.data.remote.RetrofitClient
import com.example.ass2.data.remote.UsdaFood
import com.example.ass2.viewmodel.Food

class FoodApiRepository {

    private val api = RetrofitClient.usdaApi

    suspend fun searchFoods(query: String): Result<List<Food>> {
        if (BuildConfig.USDA_API_KEY.isBlank()) {
            return Result.failure(
                IllegalStateException("USDA API key missing. Add USDA_API_KEY to local.properties")
            )
        }
        if (query.isBlank()) {
            return Result.success(emptyList())
        }

        return try {
            val response = api.searchFoods(
                apiKey = BuildConfig.USDA_API_KEY,
                query = query.trim()
            )
            val foods = response.foods
                ?.mapNotNull { it.toFood() }
                ?.distinctBy { it.name.lowercase() }
                ?: emptyList()
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun UsdaFood.toFood(): Food? {
        val name = description?.trim().orEmpty()
        if (name.isEmpty()) return null
        val calories = extractCalories() ?: return null
        return Food(name = name, calories = calories)
    }

    private fun UsdaFood.extractCalories(): Int? {
        val nutrients = foodNutrients ?: return null
        val energy = nutrients.firstOrNull { nutrient ->
            val n = nutrient.nutrientName?.lowercase().orEmpty()
            n.contains("energy") || n == "calories"
        } ?: return null

        val value = energy.value ?: return null
        val unit = energy.unitName?.uppercase().orEmpty()
        return when {
            unit == "KCAL" -> value.toInt()
            unit == "KJ" -> (value / 4.184).toInt()
            else -> value.toInt()
        }.takeIf { it > 0 }
    }
}
