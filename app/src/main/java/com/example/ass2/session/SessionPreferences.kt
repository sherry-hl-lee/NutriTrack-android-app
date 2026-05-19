package com.example.ass2.session

import android.content.Context

class SessionPreferences(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserSession(email: String) {
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putBoolean(KEY_IS_GUEST, false)
            .apply()
    }

    fun saveGuestSession() {
        prefs.edit()
            .putString(KEY_EMAIL, null)
            .putBoolean(KEY_IS_GUEST, true)
            .apply()
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_EMAIL)
            .remove(KEY_IS_GUEST)
            .apply()
    }

    fun getSavedEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun isGuestSession(): Boolean = prefs.getBoolean(KEY_IS_GUEST, false)

    fun hasSession(): Boolean =
        isGuestSession() || !getSavedEmail().isNullOrBlank()

    companion object {
        private const val PREFS_NAME = "nutritrack_session"
        private const val KEY_EMAIL = "logged_in_email"
        private const val KEY_IS_GUEST = "is_guest"
    }
}
