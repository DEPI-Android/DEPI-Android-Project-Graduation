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
        private const val KEY_FAVORITES = "cached_favorites"
        private const val KEY_SAVES = "cached_saves"
        private const val KEY_DARK_MODE = "dark_mode"
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
            .remove(KEY_FAVORITES)
            .remove(KEY_SAVES)
            .apply()
    }

    // ==================== FAVORITES/SAVES CACHING ====================

    /**
     * Cache favorites list
     */
    fun cacheFavorites(ids: List<Int>) {
        sharedPreferences.edit()
            .putString(KEY_FAVORITES, ids.joinToString(","))
            .apply()
    }

    /**
     * Get cached favorites
     */
    fun getCachedFavorites(): List<Int> {
        val str = sharedPreferences.getString(KEY_FAVORITES, "") ?: ""
        return if (str.isEmpty()) emptyList()
        else str.split(",").mapNotNull { it.toIntOrNull() }
    }

    /**
     * Add single favorite to cache
     */
    fun addFavoriteToCache(id: Int) {
        val current = getCachedFavorites().toMutableList()
        if (!current.contains(id)) current.add(id)
        cacheFavorites(current)
    }

    /**
     * Remove single favorite from cache
     */
    fun removeFavoriteFromCache(id: Int) {
        cacheFavorites(getCachedFavorites().filter { it != id })
    }

    /**
     * Cache saves list
     */
    fun cacheSaves(ids: List<Int>) {
        sharedPreferences.edit()
            .putString(KEY_SAVES, ids.joinToString(","))
            .apply()
    }

    /**
     * Get cached saves
     */
    fun getCachedSaves(): List<Int> {
        val str = sharedPreferences.getString(KEY_SAVES, "") ?: ""
        return if (str.isEmpty()) emptyList()
        else str.split(",").mapNotNull { it.toIntOrNull() }
    }

    /**
     * Add single save to cache
     */
    fun addSaveToCache(id: Int) {
        val current = getCachedSaves().toMutableList()
        if (!current.contains(id)) current.add(id)
        cacheSaves(current)
    }

    /**
     * Remove single save from cache
     */
    fun removeSaveFromCache(id: Int) {
        cacheSaves(getCachedSaves().filter { it != id })
    }

    // ==================== DARK MODE ====================

    /**
     * Set dark mode preference
     */
    fun setDarkMode(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
    }

    /**
     * Get dark mode preference
     */
    fun isDarkMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }
}
