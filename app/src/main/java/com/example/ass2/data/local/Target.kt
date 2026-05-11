package com.example.ass2.data.local

data class Target(
    val id: Int,
    val title: String,
    val points: Int,
    var completed: Boolean = false
)