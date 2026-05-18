package com.example.ass2.data.local

import java.sql.Date

data class Target(
    val id: Int,
    val calories: Int,
    val date: Date,
    val points: Int,
    var completed: Boolean = false
)