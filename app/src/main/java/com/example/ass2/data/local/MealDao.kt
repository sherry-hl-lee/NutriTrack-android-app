package com.example.ass2.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    // Insert meal
    @Insert
    suspend fun insertMeal(meal: Meal)

    @Query("SELECT * FROM meals WHERE userEmail = :userEmail ORDER BY id DESC")
    fun getMealsByUserEmail(userEmail: String): Flow<List<Meal>>

    // Delete meal
    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Update
    suspend fun updateMeal(meal: Meal)

    @Query("SELECT * FROM meals WHERE id = :id LIMIT 1")
    suspend fun getMealById(id: Int): Meal?

}