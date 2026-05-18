package com.example.ass2.data.local

data class Target(
    val id: Int,
    val title: String,
    val points: Int,
    val icon: String,
    val subtitle: String,
    var completed: Boolean = false
)