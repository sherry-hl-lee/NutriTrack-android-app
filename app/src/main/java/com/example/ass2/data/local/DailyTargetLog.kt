package com.example.ass2.data.local

import androidx.room.Entity

@Entity(
    tableName = "daily_target_log",
    primaryKeys = ["userEmail", "dayStart"]
)
data class DailyTargetLog(
    val userEmail: String,
    val dayStart: Long,
    val earnedPoints: Int,
    val totalPoints: Int,
    val completedTaskIds: String,
    val isGoalAchieved: Boolean
)
