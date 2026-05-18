package com.example.ass2.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    // ✅ 插入
    @Insert
    suspend fun insertMeal(meal: Meal)

    @Query("SELECT * FROM meals WHERE userEmail = :userEmail ORDER BY id DESC")
    fun getMealsByUserEmail(userEmail: String): Flow<List<Meal>>

    // ✅ 删除（可选）
    @Delete
    suspend fun deleteMeal(meal: Meal)
}