package com.example.ass2.data.local

import java.sql.Time

data class Reminder (
    val id: Int,
    val time: Time,
    val description: String,
)