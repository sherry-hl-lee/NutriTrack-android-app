package com.example.ass2.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Food(val name: String, val calories: Int)

class FoodViewModel : ViewModel() {
    private val _foods = MutableStateFlow<List<Food>>(emptyList())
    val foods: StateFlow<List<Food>> = _foods

    fun search(query: String) {
        _foods.value = listOf() // A4再接API
    }
}
