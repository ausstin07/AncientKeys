package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Dedicated unit tests for Phase E: Theme & Settings UI/UX Redesign.
 *
 * Verifies:
 * - Theme selection persistence across Sage, Light, Dark, Sepia, and System
 * - Dynamic theme change listeners and lifecycle unregistration
 * - Key size preference persistence (small, medium, large, fallback)
 * - Show key labels (transliterations) preference toggling
 * - Default script configuration across all historical scripts + English
 * - Confirm script switch preference
 * - Keyboard height clamping and presets (250, 290, 330)
 * - Haptic feedback toggle
 */
class ThemeSettingsTest {

    private lateinit var repo: UserPreferencesRepository

    @Before
    fun setUp() {
        repo = UserPreferencesRepository(null)
    }

    @Test
    fun testThemePreferencePersistenceAndDefaults() {
        // Default theme
        assertEquals(UserPreferencesRepository.DEFAULT_THEME, repo.getThemePreference())

        // Minimal Sage (Screen 4 Default Highlight)
        repo.setThemePreference(UserPreferencesRepository.THEME_SAGE)
        assertEquals(UserPreferencesRepository.THEME_SAGE, repo.getThemePreference())

        // Light
        repo.setThemePreference(UserPreferencesRepository.THEME_LIGHT)
        assertEquals(UserPreferencesRepository.THEME_LIGHT, repo.getThemePreference())

        // Dark
        repo.setThemePreference(UserPreferencesRepository.THEME_DARK)
        assertEquals(UserPreferencesRepository.THEME_DARK, repo.getThemePreference())

        // Scholar / Sepia
        repo.setThemePreference(UserPreferencesRepository.THEME_SEPIA)
        assertEquals(UserPreferencesRepository.THEME_SEPIA, repo.getThemePreference())

        // System
        repo.setThemePreference(UserPreferencesRepository.THEME_SYSTEM)
        assertEquals(UserPreferencesRepository.THEME_SYSTEM, repo.getThemePreference())

        // Unknown theme falls back safely to default
        repo.setThemePreference("unknown_custom_theme")
        assertEquals(UserPreferencesRepository.DEFAULT_THEME, repo.getThemePreference())
    }

    @Test
    fun testThemeChangeListenerNotification() {
        var notifiedTheme = ""
        var callCount = 0

        val unregister = repo.registerThemeChangeListener { theme ->
            notifiedTheme = theme
            callCount++
        }

        repo.setThemePreference(UserPreferencesRepository.THEME_SAGE)
        assertEquals(UserPreferencesRepository.THEME_SAGE, notifiedTheme)
        assertEquals(1, callCount)

        repo.setThemePreference(UserPreferencesRepository.THEME_DARK)
        assertEquals(UserPreferencesRepository.THEME_DARK, notifiedTheme)
        assertEquals(2, callCount)

        // After unregistration, listener should not be notified
        unregister()
        repo.setThemePreference(UserPreferencesRepository.THEME_LIGHT)
        assertEquals(UserPreferencesRepository.THEME_DARK, notifiedTheme)
        assertEquals(2, callCount)
    }

    @Test
    fun testKeySizePreference() {
        // Default should be medium
        assertEquals(UserPreferencesRepository.DEFAULT_KEY_SIZE, repo.getKeySizePreference())
        assertEquals(UserPreferencesRepository.KEY_SIZE_MEDIUM, repo.getKeySizePreference())

        // Small
        repo.setKeySizePreference(UserPreferencesRepository.KEY_SIZE_SMALL)
        assertEquals(UserPreferencesRepository.KEY_SIZE_SMALL, repo.getKeySizePreference())

        // Large
        repo.setKeySizePreference(UserPreferencesRepository.KEY_SIZE_LARGE)
        assertEquals(UserPreferencesRepository.KEY_SIZE_LARGE, repo.getKeySizePreference())

        // Medium
        repo.setKeySizePreference(UserPreferencesRepository.KEY_SIZE_MEDIUM)
        assertEquals(UserPreferencesRepository.KEY_SIZE_MEDIUM, repo.getKeySizePreference())

        // Case-insensitive handling
        repo.setKeySizePreference("LARGE")
        assertEquals(UserPreferencesRepository.KEY_SIZE_LARGE, repo.getKeySizePreference())

        // Invalid fallback
        repo.setKeySizePreference("gigantic")
        assertEquals(UserPreferencesRepository.KEY_SIZE_MEDIUM, repo.getKeySizePreference())
    }

    @Test
    fun testShowKeyLabelsPreference() {
        // Default is enabled
        assertTrue("Key labels should be enabled by default", repo.isShowKeyLabelsEnabled())

        // Disable
        repo.setShowKeyLabelsEnabled(false)
        assertFalse("Key labels should be disabled", repo.isShowKeyLabelsEnabled())

        // Re-enable
        repo.setShowKeyLabelsEnabled(true)
        assertTrue("Key labels should be enabled", repo.isShowKeyLabelsEnabled())
    }

    @Test
    fun testDefaultScriptPreference() {
        // Default script is Greek
        assertEquals("greek", repo.getDefaultScriptId())

        // Set across all 7 supported choices
        val scripts = listOf("greek", "egyptian", "brahmi", "kharosthi", "phoenician", "runic", "english")
        for (s in scripts) {
            repo.setDefaultScriptId(s)
            assertEquals(s, repo.getDefaultScriptId())
        }

        // Case trimming and normalization
        repo.setDefaultScriptId("  PHOENICIAN  ")
        assertEquals("phoenician", repo.getDefaultScriptId())
    }

    @Test
    fun testConfirmScriptSwitchPreference() {
        // Default is false
        assertFalse("Confirm script switch should be false by default", repo.isConfirmScriptSwitchEnabled())

        // Enable
        repo.setConfirmScriptSwitchEnabled(true)
        assertTrue("Confirm script switch should be enabled", repo.isConfirmScriptSwitchEnabled())

        // Disable
        repo.setConfirmScriptSwitchEnabled(false)
        assertFalse("Confirm script switch should be disabled", repo.isConfirmScriptSwitchEnabled())
    }

    @Test
    fun testKeyboardHeightClampingAndPresets() {
        assertEquals(290, repo.getKeyboardHeightDp())

        // Standard presets
        repo.setKeyboardHeightDp(250)
        assertEquals(250, repo.getKeyboardHeightDp())

        repo.setKeyboardHeightDp(290)
        assertEquals(290, repo.getKeyboardHeightDp())

        repo.setKeyboardHeightDp(330)
        assertEquals(330, repo.getKeyboardHeightDp())

        // Clamping minimum
        repo.setKeyboardHeightDp(100)
        assertEquals(UserPreferencesRepository.MIN_KEYBOARD_HEIGHT_DP, repo.getKeyboardHeightDp())

        // Clamping maximum
        repo.setKeyboardHeightDp(500)
        assertEquals(UserPreferencesRepository.MAX_KEYBOARD_HEIGHT_DP, repo.getKeyboardHeightDp())
    }

    @Test
    fun testHapticsPreference() {
        assertTrue(repo.isHapticsEnabled())

        repo.setHapticsEnabled(false)
        assertFalse(repo.isHapticsEnabled())

        repo.setHapticsEnabled(true)
        assertTrue(repo.isHapticsEnabled())
    }
}

