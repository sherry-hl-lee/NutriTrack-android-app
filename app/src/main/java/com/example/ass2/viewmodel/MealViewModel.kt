package com.example.ass2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.repository.MealRepository
import kotlinx.coroutines.launch
import com.example.ass2.data.local.Meal


class MealViewModel(private val repository: MealRepository) : ViewModel() {
    val meals = repository.getAllMeals()
    fun addMeal(
        name: String,
        calories: Int,
        mealType: String,
        date: Long
    ) {
        viewModelScope.launch {
            repository.insertMeal(
                Meal(
                    name = name,
                    calories = calories,
                    mealType = mealType,
                    date = date
                )
            )
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }

}