package com.example.ass2.data.remote

import com.google.gson.annotations.SerializedName

data class UsdaSearchResponse(
    @SerializedName("foods") val foods: List<UsdaFood>? = null
)

data class UsdaFood(
    @SerializedName("description") val description: String? = null,
    @SerializedName("foodNutrients") val foodNutrients: List<UsdaNutrient>? = null
)

data class UsdaNutrient(
    @SerializedName("nutrientName") val nutrientName: String? = null,
    @SerializedName("unitName") val unitName: String? = null,
    @SerializedName("value") val value: Double? = null
)
