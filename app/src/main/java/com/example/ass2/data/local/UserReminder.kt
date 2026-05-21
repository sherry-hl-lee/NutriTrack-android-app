package com.example.ass2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_reminders")
data class UserReminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    /** water | breakfast | lunch | dinner */
    val type: String,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean = true
)
