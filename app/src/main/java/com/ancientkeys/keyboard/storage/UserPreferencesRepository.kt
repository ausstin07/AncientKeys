package com.ancientkeys.keyboard.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user preferences, recent script selection, favorites, and keyboard layout configuration.
 *
 * Privacy Guarantees:
 * - Stores ONLY fixed script IDs (e.g., "egyptian") and official Unicode code point strings (e.g., "U+1313F").
 * - NEVER stores, records, or caches user-entered text, typing history, or keystrokes.
 * - Operates entirely locally on-device without network access.
 */
class UserPreferencesRepository(private val context: Context? = null) {

    private val prefs: SharedPreferences? by lazy {
        context?.getSharedPreferences("ancientkeys_user_prefs", Context.MODE_PRIVATE)
    }

    // In-memory fallback for testing or non-context execution
    private val memoryRecents = mutableListOf<String>()
    private val memoryFavorites = mutableSetOf<String>()
    private var memoryKeyboardHeightDp = DEFAULT_KEYBOARD_HEIGHT_DP
    private var memoryHapticsEnabled = true
    private var memoryThemePreference = THEME_SYSTEM
    private var memoryLastKeyboardMode = MODE_HISTORICAL
    private var memoryScholarMode = false
    private var memoryOnboardingCompleted = false

    companion object {
        const val DEFAULT_KEYBOARD_HEIGHT_DP = 290
        const val MIN_KEYBOARD_HEIGHT_DP = 240
        const val MAX_KEYBOARD_HEIGHT_DP = 360

        const val THEME_SYSTEM = "system"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"

        const val MODE_HISTORICAL = "historical"
        const val MODE_ENGLISH = "english"
        const val MODE_ROMANIZED = "romanized"
        const val MODE_EASY_PHONETIC = "easy_phonetic"

        private const val KEY_RECENTS = "recent_scripts_csv"
        private const val KEY_FAVORITES = "favorite_characters_set"
        private const val KEY_KEYBOARD_HEIGHT = "keyboard_height_dp"
        private const val KEY_HAPTICS = "keyboard_haptics_enabled"
        private const val KEY_THEME = "keyboard_theme_preference"
        private const val KEY_LAST_MODE = "keyboard_last_mode"
        private const val KEY_SCHOLAR_MODE = "scholar_mode_enabled"
        private const val KEY_ONBOARDING_DONE = "onboarding_completed"

        @Volatile
        private var instance: UserPreferencesRepository? = null

        fun getInstance(context: Context?): UserPreferencesRepository {
            return instance ?: synchronized(this) {
                instance ?: UserPreferencesRepository(context?.applicationContext ?: context).also { instance = it }
            }
        }
    }

    // --- Recent Scripts ---

    fun getRecentScriptIds(): List<String> {
        val stored = prefs?.getString(KEY_RECENTS, null)
        return if (stored != null) {
            stored.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            memoryRecents.toList()
        }
    }

    fun recordScriptAccess(scriptId: String) {
        if (scriptId.isBlank()) return
        val current = getRecentScriptIds().toMutableList()
        current.remove(scriptId)
        current.add(0, scriptId) // Add to top
        val trimmed = current.take(6)

        prefs?.edit()?.putString(KEY_RECENTS, trimmed.joinToString(","))?.apply()
        memoryRecents.clear()
        memoryRecents.addAll(trimmed)
    }

    // --- Favorite Characters ---

    private fun makeFavoriteKey(scriptId: String, codePoint: String): String {
        return "${scriptId.lowercase().trim()}:${codePoint.uppercase().trim()}"
    }

    fun isFavorite(scriptId: String, codePoint: String): Boolean {
        val key = makeFavoriteKey(scriptId, codePoint)
        val favs = prefs?.getStringSet(KEY_FAVORITES, null) ?: memoryFavorites
        return favs.contains(key)
    }

    fun toggleFavorite(scriptId: String, codePoint: String): Boolean {
        val key = makeFavoriteKey(scriptId, codePoint)
        val currentFavs = (prefs?.getStringSet(KEY_FAVORITES, null)?.toMutableSet() ?: memoryFavorites.toMutableSet())
        val newState = if (currentFavs.contains(key)) {
            currentFavs.remove(key)
            false
        } else {
            currentFavs.add(key)
            true
        }

        prefs?.edit()?.putStringSet(KEY_FAVORITES, currentFavs)?.apply()
        memoryFavorites.clear()
        memoryFavorites.addAll(currentFavs)
        return newState
    }

    fun getFavoriteKeysForScript(scriptId: String): Set<String> {
        val prefix = "${scriptId.lowercase().trim()}:"
        val favs = prefs?.getStringSet(KEY_FAVORITES, null) ?: memoryFavorites
        return favs.filter { it.startsWith(prefix) }.map { it.removePrefix(prefix) }.toSet()
    }

    // --- Keyboard Height Setting ---

    fun getKeyboardHeightDp(): Int {
        val stored = prefs?.getInt(KEY_KEYBOARD_HEIGHT, memoryKeyboardHeightDp) ?: memoryKeyboardHeightDp
        return stored.coerceIn(MIN_KEYBOARD_HEIGHT_DP, MAX_KEYBOARD_HEIGHT_DP)
    }

    fun setKeyboardHeightDp(heightDp: Int) {
        val safeHeight = heightDp.coerceIn(MIN_KEYBOARD_HEIGHT_DP, MAX_KEYBOARD_HEIGHT_DP)
        prefs?.edit()?.putInt(KEY_KEYBOARD_HEIGHT, safeHeight)?.apply()
        memoryKeyboardHeightDp = safeHeight
    }

    // --- Haptic Feedback Setting ---

    fun isHapticsEnabled(): Boolean {
        return prefs?.getBoolean(KEY_HAPTICS, memoryHapticsEnabled) ?: memoryHapticsEnabled
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs?.edit()?.putBoolean(KEY_HAPTICS, enabled)?.apply()
        memoryHapticsEnabled = enabled
    }

    // --- Theme Preference ---

    fun getThemePreference(): String {
        return prefs?.getString(KEY_THEME, memoryThemePreference) ?: memoryThemePreference
    }

    fun setThemePreference(theme: String) {
        val safeTheme = when (theme.lowercase()) {
            THEME_LIGHT -> THEME_LIGHT
            THEME_DARK -> THEME_DARK
            else -> THEME_SYSTEM
        }
        prefs?.edit()?.putString(KEY_THEME, safeTheme)?.apply()
        memoryThemePreference = safeTheme
    }

    // --- Last Keyboard Mode ---

    fun getLastKeyboardMode(): String {
        return prefs?.getString(KEY_LAST_MODE, memoryLastKeyboardMode) ?: memoryLastKeyboardMode
    }

    fun setLastKeyboardMode(mode: String) {
        prefs?.edit()?.putString(KEY_LAST_MODE, mode)?.apply()
        memoryLastKeyboardMode = mode
    }

    // --- Scholar Mode Setting ---

    fun isScholarMode(): Boolean {
        return prefs?.getBoolean(KEY_SCHOLAR_MODE, memoryScholarMode) ?: memoryScholarMode
    }

    fun setScholarMode(enabled: Boolean) {
        prefs?.edit()?.putBoolean(KEY_SCHOLAR_MODE, enabled)?.apply()
        memoryScholarMode = enabled
    }

    // --- Onboarding Completion Setting ---

    fun isOnboardingCompleted(): Boolean {
        return prefs?.getBoolean(KEY_ONBOARDING_DONE, memoryOnboardingCompleted) ?: memoryOnboardingCompleted
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs?.edit()?.putBoolean(KEY_ONBOARDING_DONE, completed)?.apply()
        memoryOnboardingCompleted = completed
    }
}
