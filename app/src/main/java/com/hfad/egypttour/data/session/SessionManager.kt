package com.hfad.egypttour.data.session

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages user session persistence using SharedPreferences
 * Injected via Hilt as a singleton
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // SharedPreferences instance for storing session data
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "user_session"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
    }

    /**
     * Save login session (called when "Remember me" is checked)
     */
    fun saveLoginSession() {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    /**
     * Clear login session (called on logout)
     */
    fun clearLoginSession() {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }

    /**
     * Check if user session exists
     */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // ==================== USER PROFILE CACHING ====================

    /**
     * Save user profile data to cache
     * Called after successful login or signup
     */
    fun saveUserProfile(username: String, email: String) {
        sharedPreferences.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    /**
     * Get cached username
     */
    fun getCachedUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    /**
     * Get cached email
     */
    fun getCachedEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    /**
     * Clear all user data (called on logout)
     */
    fun clearAllUserData() {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USERNAME)
            .remove(KEY_EMAIL)
            .apply()
    }
}
