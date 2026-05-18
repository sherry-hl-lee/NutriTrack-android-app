package com.example.ass2.reminder

import android.content.Context

class ReminderPreferences(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val isActive: Boolean
        get() = prefs.getBoolean(KEY_ACTIVE, false)

    val hour: Int
        get() = prefs.getInt(KEY_HOUR, 0)

    val minute: Int
        get() = prefs.getInt(KEY_MINUTE, 0)

    fun saveReminder(hour: Int, minute: Int) {
        prefs.edit()
            .putBoolean(KEY_ACTIVE, true)
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()
    }

    fun clearReminder() {
        prefs.edit()
            .remove(KEY_ACTIVE)
            .remove(KEY_HOUR)
            .remove(KEY_MINUTE)
            .remove(KEY_PENDING_ALERT)
            .apply()
    }

    fun setPendingMealAlert(pending: Boolean) {
        prefs.edit().putBoolean(KEY_PENDING_ALERT, pending).apply()
    }

    fun consumePendingMealAlert(): Boolean {
        val pending = prefs.getBoolean(KEY_PENDING_ALERT, false)
        if (pending) {
            prefs.edit().remove(KEY_PENDING_ALERT).apply()
        }
        return pending
    }

    companion object {
        private const val PREFS_NAME = "nutritrack_reminder"
        private const val KEY_ACTIVE = "active"
        private const val KEY_HOUR = "hour"
        private const val KEY_MINUTE = "minute"
        private const val KEY_PENDING_ALERT = "pending_meal_alert"
    }
}
