package com.example.ass2.util

import java.util.Calendar

object ReminderTimeUtils {

    fun triggerTimeMillis(hour: Int, minute: Int, now: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    fun millisUntilReminder(hour: Int, minute: Int, now: Long = System.currentTimeMillis()): Long {
        return (triggerTimeMillis(hour, minute, now) - now).coerceAtLeast(0L)
    }

    fun formatCountdown(hour: Int, minute: Int, now: Long = System.currentTimeMillis()): String {
        val diffMillis = millisUntilReminder(hour, minute, now)
        val totalSeconds = diffMillis / 1_000
        val hours = totalSeconds / 3_600
        val minutes = (totalSeconds % 3_600) / 60
        val seconds = totalSeconds % 60
        return "${hours}h ${minutes}m ${seconds}s"
    }

    fun formatTime12Hour(hour24: Int, minute: Int): String {
        val hour12 = if (hour24 % 12 == 0) 12 else hour24 % 12
        val amPm = if (hour24 < 12) "AM" else "PM"
        return String.format("%02d:%02d %s", hour12, minute, amPm)
    }
}
