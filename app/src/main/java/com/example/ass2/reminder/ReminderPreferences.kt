package com.example.ass2.reminder

import android.content.Context

/** Lightweight prefs for in-app dialog after a notification (per user). */
class ReminderPreferences(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    data class PendingAlert(val title: String, val body: String)

    fun setPendingAlert(userEmail: String, title: String, body: String) {
        prefs.edit()
            .putString(keyPendingUser(), userEmail)
            .putString(keyTitle(userEmail), title)
            .putString(keyBody(userEmail), body)
            .apply()
    }

    fun hasPendingMealAlert(userEmail: String?): Boolean {
        if (userEmail.isNullOrBlank()) return false
        return prefs.getString(keyPendingUser(), null) == userEmail &&
            prefs.getString(keyTitle(userEmail), null) != null
    }

    fun peekPendingAlert(userEmail: String?): PendingAlert? {
        if (userEmail.isNullOrBlank()) return null
        if (prefs.getString(keyPendingUser(), null) != userEmail) return null
        val title = prefs.getString(keyTitle(userEmail), null) ?: return null
        val body = prefs.getString(keyBody(userEmail), null) ?: return null
        return PendingAlert(title, body)
    }

    fun consumePendingMealAlert(userEmail: String?): PendingAlert? {
        val alert = peekPendingAlert(userEmail) ?: return null
        clearPendingAlert(userEmail)
        return alert
    }

    fun clearPendingAlert(userEmail: String?) {
        if (userEmail.isNullOrBlank()) return
        prefs.edit()
            .remove(keyTitle(userEmail))
            .remove(keyBody(userEmail))
            .apply()
        if (prefs.getString(keyPendingUser(), null) == userEmail) {
            prefs.edit().remove(keyPendingUser()).apply()
        }
    }

    private fun keyPendingUser() = "pending_user"
    private fun keyTitle(email: String) = "pending_title_$email"
    private fun keyBody(email: String) = "pending_body_$email"

    companion object {
        private const val PREFS_NAME = "nutritrack_reminder"
    }
}
