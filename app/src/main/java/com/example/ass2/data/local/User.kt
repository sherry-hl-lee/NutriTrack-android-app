package com.example.ass2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String,
    val weight: Float = 0f,
    val height: Float = 0f,
    val age: Int = 0,
    val gender: String = ""
)