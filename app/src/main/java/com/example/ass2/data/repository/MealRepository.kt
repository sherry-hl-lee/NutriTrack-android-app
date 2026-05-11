package com.example.ass2.data.repository

import com.example.ass2.data.local.Meal
import com.example.ass2.data.local.MealDao
import kotlinx.coroutines.flow.Flow

class MealRepository(private val dao: MealDao) {

    fun getAllMeals(): Flow<List<Meal>> = dao.getAllMeals()

    suspend fun insertMeal(meal: Meal) {
        dao.insertMeal(meal)
    }

    suspend fun deleteMeal(meal: Meal) {
        dao.deleteMeal(meal)
    }
}