package com.ancientkeys.keyboard

import androidx.compose.ui.graphics.Color
import com.ancientkeys.keyboard.ime.data.EnglishKeyboardData
import com.ancientkeys.keyboard.ime.data.LongPressAlternativesData
import com.ancientkeys.keyboard.ime.ui.ImeDimensionsHelper
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.ui.theme.AncientKeysCustomColors
import com.ancientkeys.keyboard.ui.theme.AncientKeysDesignTokens
import com.ancientkeys.keyboard.ui.theme.DarkCustomColors
import com.ancientkeys.keyboard.ui.theme.LightCustomColors
import com.ancientkeys.keyboard.ui.theme.SageCustomColors
import com.ancientkeys.keyboard.ui.theme.SepiaCustomColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Automated test suite for AncientKeys v1.1 Phase B: Responsive Keyboard Visual Redesign.
 *
 * Verifies:
 * 1. Palette color tokens for IME across Minimal Sage, Light, Dark, and Scholar/Sepia themes.
 * 2. Visual design tokens (shapes, touch target minimums, key elevations).
 * 3. IME dimensions and responsive key sizing constraints.
 * 4. Key inventory coverage for English QWERTY, Numbers/Symbols, and all 6 historical scripts.
 * 5. Theme change notification mechanism powering reactive IME recomposition.
 */
class ImeKeyboardThemeStylingTest {

    @Test
    fun testMinimalSageImePaletteTokens() {
        val sage = SageCustomColors
        // Target Reference Screen 2 specs:
        // Keyboard background: #EDF3EE (SageSurfaceVariant)
        // Normal keys: #FFFFFF (SageSurface)
        // Action keys (Shift, Backspace, ?123): #DEE7E0 (SageSecondaryContainer)
        // Primary action key (Enter): #4A7C59 (SagePrimary)
        // Key text: #1C2B20 (SageOnSurface)
        // Key border: #D1DDD4 (SageOutline)
        assertEquals(Color(0xFFEDF3EE), sage.keyboardBackground)
        assertEquals(Color(0xFFFFFFFF), sage.keyBackground)
        assertEquals(Color(0xFFDEE7E0), sage.keyActionBackground)
        assertEquals(Color(0xFF4A7C59), sage.keyPrimaryActionBackground)
        assertEquals(Color(0xFF1C2B20), sage.keyText)
        assertEquals(Color(0xFFFFFFFF), sage.keyPrimaryActionText)
        assertEquals(Color(0xFFD1DDD4), sage.keyBorder)
        assertEquals(Color(0xFF4A7C59), sage.glyphAccent)
    }

    @Test
    fun testLightAndDarkImePalettes() {
        val light = LightCustomColors
        val dark = DarkCustomColors

        // Light Theme: clean off-white keyboard
        assertEquals(Color(0xFFEFF0F1), light.keyboardBackground)
        assertEquals(Color(0xFFFFFFFF), light.keyBackground)
        assertEquals(Color(0xFFD7E8D9), light.keyActionBackground)
        assertEquals(Color(0xFF2E6A44), light.keyPrimaryActionBackground)

        // Dark Theme: deep slate keyboard
        assertEquals(Color(0xFF1E2520), dark.keyboardBackground)
        assertEquals(Color(0xFF252D27), dark.keyBackground)
        assertEquals(Color(0xFF394C3E), dark.keyActionBackground)
        assertEquals(Color(0xFF8FD8A0), dark.keyPrimaryActionBackground)
        assertEquals(Color(0xFFE1E3DF), dark.keyText)
    }

    @Test
    fun testSepiaScholarImePalette() {
        val sepia = SepiaCustomColors

        // Scholar / Sepia Theme: parchment keyboard
        assertEquals(Color(0xFFF2EADA), sepia.keyboardBackground)
        assertEquals(Color(0xFFFFFDF8), sepia.keyBackground)
        assertEquals(Color(0xFFEADECC), sepia.keyActionBackground)
        assertEquals(Color(0xFF7A5C38), sepia.keyPrimaryActionBackground)
        assertEquals(Color(0xFF2C2218), sepia.keyText)
        assertEquals(Color(0xFF7A5C38), sepia.glyphAccent)
    }

    @Test
    fun testImeKeyDesignTokens() {
        // Corner radius: 7.dp for standard keys and action keys
        val keyShape = AncientKeysDesignTokens.Shapes.keyShape
        val actionKeyShape = AncientKeysDesignTokens.Shapes.actionKeyShape
        assertNotNull(keyShape)
        assertNotNull(actionKeyShape)

        // Elevation: 1.dp default key elevation, 2.dp active elevation
        assertEquals(1f, AncientKeysDesignTokens.Elevation.keyElevation.value)
        assertEquals(2f, AncientKeysDesignTokens.Elevation.activeElevation.value)

        // Minimum accessibility touch targets: Enter >= 44dp, Backspace >= 42dp, Space >= 70dp
        assertTrue(ImeDimensionsHelper.MIN_ENTER_KEY_WIDTH_DP >= 44)
        assertTrue(ImeDimensionsHelper.MIN_BACKSPACE_KEY_WIDTH_DP >= 42)
        assertTrue(ImeDimensionsHelper.MIN_SPACE_KEY_WIDTH_DP >= 70)
    }

    @Test
    fun testImeRowHeights() {
        val portraitKeyRow = ImeDimensionsHelper.computeKeyRowHeight(isLandscape = false)
        val landscapeKeyRow = ImeDimensionsHelper.computeKeyRowHeight(isLandscape = true)
        val portraitBottomRow = ImeDimensionsHelper.computeBottomRowHeight(isLandscape = false)
        val landscapeBottomRow = ImeDimensionsHelper.computeBottomRowHeight(isLandscape = true)

        assertEquals(44, portraitKeyRow)
        assertEquals(34, landscapeKeyRow)
        assertEquals(44, portraitBottomRow)
        assertEquals(36, landscapeBottomRow)
    }

    @Test
    fun testEnglishQwertyAndNumbersInventoryCompleteness() {
        // Rows 1, 2, 3 must have 10, 9, 7 keys respectively
        assertEquals(10, EnglishKeyboardData.QWERTY_ROW_1_LOWER.size)
        assertEquals(9, EnglishKeyboardData.QWERTY_ROW_2_LOWER.size)
        assertEquals(7, EnglishKeyboardData.QWERTY_ROW_3_LOWER.size)

        assertEquals(10, EnglishKeyboardData.QWERTY_ROW_1_UPPER.size)
        assertEquals(9, EnglishKeyboardData.QWERTY_ROW_2_UPPER.size)
        assertEquals(7, EnglishKeyboardData.QWERTY_ROW_3_UPPER.size)

        // Numbers row must have 10 digits
        assertEquals(10, EnglishKeyboardData.NUMBERS_ROW.size)

        // Symbol rows must be non-empty
        assertTrue(EnglishKeyboardData.SYMBOLS_ROW_1.isNotEmpty())
        assertTrue(EnglishKeyboardData.SYMBOLS_ROW_2.isNotEmpty())
        assertTrue(EnglishKeyboardData.SYMBOLS_ROW_3.isNotEmpty())
    }

    @Test
    fun testHistoricalScriptInventoriesHaveCompactNamesAndKeys() {
        val scripts = ScriptRegistry.allScripts
        assertEquals(6, scripts.size)

        for (script in scripts) {
            assertTrue("Script ${script.name} should have keys", script.keys.isNotEmpty())
            val compactName = ImeDimensionsHelper.getCompactScriptName(script)
            assertTrue("Compact name for ${script.name} must not be blank", compactName.isNotBlank())
            assertTrue("Compact name should be short (<= 12 chars)", compactName.length <= 12)
        }
    }

    @Test
    fun testLongPressAlternativesExistForCommonVowels() {
        assertTrue(LongPressAlternativesData.hasAlternatives("a"))
        assertTrue(LongPressAlternativesData.hasAlternatives("e"))
        assertTrue(LongPressAlternativesData.hasAlternatives("i"))
        assertTrue(LongPressAlternativesData.hasAlternatives("o"))
        assertTrue(LongPressAlternativesData.hasAlternatives("u"))

        val aAlts = LongPressAlternativesData.getAlternatives("a")
        assertTrue(aAlts.contains("ā") || aAlts.contains("á") || aAlts.contains("ä"))
    }

    @Test
    fun testReactiveThemeChangeListenerNotifiesOnPreferenceChange() {
        val repo = UserPreferencesRepository(null)

        var notifiedTheme: String? = null
        val unregister = repo.registerThemeChangeListener { newTheme ->
            notifiedTheme = newTheme
        }

        // Setting a new theme should immediately notify registered listener
        repo.setThemePreference(UserPreferencesRepository.THEME_SEPIA)
        assertEquals(UserPreferencesRepository.THEME_SEPIA, notifiedTheme)

        repo.setThemePreference(UserPreferencesRepository.THEME_SAGE)
        assertEquals(UserPreferencesRepository.THEME_SAGE, notifiedTheme)

        // Reload theme should also notify
        notifiedTheme = null
        repo.reloadTheme()
        assertEquals(UserPreferencesRepository.THEME_SAGE, notifiedTheme)

        // Unregister listener and ensure no further calls
        unregister()
        notifiedTheme = null
        repo.setThemePreference(UserPreferencesRepository.THEME_DARK)
        assertEquals(null, notifiedTheme)
    }
}
