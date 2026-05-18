package com.example.ass2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val name: String,
    val calories: Int,
    val mealType: String,
    val date: Long
)
