package com.BigBon.bugs.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "bugs_game_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val KEY_CURRENT_USERNAME = "current_username"
    }

    fun setCurrentUser(userId: Long, username: String) {
        prefs.edit().apply {
            putLong(KEY_CURRENT_USER_ID, userId)
            putString(KEY_CURRENT_USERNAME, username)
            apply()
        }
    }

    fun getCurrentUserId(): Long {
        return prefs.getLong(KEY_CURRENT_USER_ID, -1L)
    }

    fun getCurrentUsername(): String? {
        return prefs.getString(KEY_CURRENT_USERNAME, null)
    }

    fun clearCurrentUser() {
        prefs.edit().apply {
            remove(KEY_CURRENT_USER_ID)
            remove(KEY_CURRENT_USERNAME)
            apply()
        }
    }

    fun hasCurrentUser(): Boolean {
        return getCurrentUserId() != -1L
    }
}
