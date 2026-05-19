package com.example.ass2.data.repository

import com.example.ass2.data.local.Meal
import com.example.ass2.data.local.MealDao
import kotlinx.coroutines.flow.Flow

class MealRepository(private val dao: MealDao) {

    fun getMealsByUserEmail(userEmail: String): Flow<List<Meal>> =
        dao.getMealsByUserEmail(userEmail)

    suspend fun insertMeal(meal: Meal) {
        dao.insertMeal(meal)
    }

    suspend fun deleteMeal(meal: Meal) {
        dao.deleteMeal(meal)
    }

    suspend fun updateMeal(meal: Meal){
        dao.updateMeal(meal)
    }

    suspend fun getMealById(id: Int): Meal? {
        return dao.getMealById(id)
    }
}