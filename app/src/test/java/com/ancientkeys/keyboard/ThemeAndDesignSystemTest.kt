package com.ancientkeys.keyboard

import androidx.compose.ui.graphics.Color
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.ui.theme.AncientKeysDesignTokens
import com.ancientkeys.keyboard.ui.theme.AncientKeysThemeVariant
import com.ancientkeys.keyboard.ui.theme.DarkPrimary
import com.ancientkeys.keyboard.ui.theme.DarkSurface
import com.ancientkeys.keyboard.ui.theme.DarkOnSurface
import com.ancientkeys.keyboard.ui.theme.LightPrimary
import com.ancientkeys.keyboard.ui.theme.LightSurface
import com.ancientkeys.keyboard.ui.theme.LightOnSurface
import com.ancientkeys.keyboard.ui.theme.SageOnPrimary
import com.ancientkeys.keyboard.ui.theme.SageOnSurface
import com.ancientkeys.keyboard.ui.theme.SageOnSurfaceVariant
import com.ancientkeys.keyboard.ui.theme.SagePrimary
import com.ancientkeys.keyboard.ui.theme.SageSurface
import com.ancientkeys.keyboard.ui.theme.SepiaOnSurface
import com.ancientkeys.keyboard.ui.theme.SepiaPrimary
import com.ancientkeys.keyboard.ui.theme.SepiaSurface
import com.ancientkeys.keyboard.ui.theme.resolveThemeVariant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.pow

/**
 * Focused unit test suite for AncientKeys v1.1 Phase A Design System Foundation.
 *
 * Verifies:
 * - Theme selection, persistence, and fallback defaults.
 * - Backward compatibility with legacy theme preferences ("system", "light", "dark").
 * - Multi-theme variant resolution across orientations and system settings.
 * - WCAG contrast mathematical safety across Minimal Sage, Light, Dark, and Sepia palettes.
 * - Design tokens and minimum accessibility touch targets.
 */
class ThemeAndDesignSystemTest {

    private lateinit var repo: UserPreferencesRepository

    @Before
    fun setUp() {
        // Instantiate without Android context to use in-memory testing backend
        repo = UserPreferencesRepository(null)
    }

    @Test
    fun testDefaultThemeIsSystemResolvingToSage() {
        assertEquals(UserPreferencesRepository.THEME_SYSTEM, repo.getThemePreference())
        assertEquals(UserPreferencesRepository.DEFAULT_THEME, repo.getThemePreference())
        // In default light appearance, system resolves to Minimal Sage
        assertEquals(AncientKeysThemeVariant.SAGE, resolveThemeVariant(repo.getThemePreference(), isSystemDark = false))
    }

    @Test
    fun testThemeSelectionAndPersistence() {
        // Sage
        repo.setThemePreference(UserPreferencesRepository.THEME_SAGE)
        assertEquals(UserPreferencesRepository.THEME_SAGE, repo.getThemePreference())

        // Light
        repo.setThemePreference(UserPreferencesRepository.THEME_LIGHT)
        assertEquals(UserPreferencesRepository.THEME_LIGHT, repo.getThemePreference())

        // Dark
        repo.setThemePreference(UserPreferencesRepository.THEME_DARK)
        assertEquals(UserPreferencesRepository.THEME_DARK, repo.getThemePreference())

        // Sepia
        repo.setThemePreference(UserPreferencesRepository.THEME_SEPIA)
        assertEquals(UserPreferencesRepository.THEME_SEPIA, repo.getThemePreference())

        // System
        repo.setThemePreference(UserPreferencesRepository.THEME_SYSTEM)
        assertEquals(UserPreferencesRepository.THEME_SYSTEM, repo.getThemePreference())
    }

    @Test
    fun testInvalidOrGarbageThemeFallsBackToDefault() {
        repo.setThemePreference("unknown_custom_theme")
        assertEquals(UserPreferencesRepository.DEFAULT_THEME, repo.getThemePreference())

        repo.setThemePreference("")
        assertEquals(UserPreferencesRepository.DEFAULT_THEME, repo.getThemePreference())

        repo.setThemePreference("   ")
        assertEquals(UserPreferencesRepository.DEFAULT_THEME, repo.getThemePreference())
    }

    @Test
    fun testBackwardCompatibilityWithLegacyThemeValues() {
        val legacyValues = listOf("light", "dark", "system")
        for (legacy in legacyValues) {
            repo.setThemePreference(legacy)
            assertEquals("Legacy preference '$legacy' must be accepted and preserved", legacy, repo.getThemePreference())
        }
    }

    @Test
    fun testAvailableThemesList() {
        val available = repo.getAvailableThemes()
        assertEquals(5, available.size)
        assertTrue(available.contains(UserPreferencesRepository.THEME_SAGE))
        assertTrue(available.contains(UserPreferencesRepository.THEME_LIGHT))
        assertTrue(available.contains(UserPreferencesRepository.THEME_DARK))
        assertTrue(available.contains(UserPreferencesRepository.THEME_SEPIA))
        assertTrue(available.contains(UserPreferencesRepository.THEME_SYSTEM))
    }

    @Test
    fun testReloadThemeReturnsCurrentState() {
        repo.setThemePreference(UserPreferencesRepository.THEME_SEPIA)
        assertEquals(UserPreferencesRepository.THEME_SEPIA, repo.reloadTheme())
    }

    @Test
    fun testResolveThemeVariant() {
        // Explicit variants
        assertEquals(AncientKeysThemeVariant.SAGE, resolveThemeVariant("sage", isSystemDark = false))
        assertEquals(AncientKeysThemeVariant.SAGE, resolveThemeVariant("sage", isSystemDark = true))

        assertEquals(AncientKeysThemeVariant.LIGHT, resolveThemeVariant("light", isSystemDark = false))
        assertEquals(AncientKeysThemeVariant.LIGHT, resolveThemeVariant("light", isSystemDark = true))

        assertEquals(AncientKeysThemeVariant.DARK, resolveThemeVariant("dark", isSystemDark = false))
        assertEquals(AncientKeysThemeVariant.DARK, resolveThemeVariant("dark", isSystemDark = true))

        assertEquals(AncientKeysThemeVariant.SEPIA, resolveThemeVariant("sepia", isSystemDark = false))
        assertEquals(AncientKeysThemeVariant.SEPIA, resolveThemeVariant("sepia", isSystemDark = true))

        // System follows system dark mode
        assertEquals(AncientKeysThemeVariant.SAGE, resolveThemeVariant("system", isSystemDark = false))
        assertEquals(AncientKeysThemeVariant.DARK, resolveThemeVariant("system", isSystemDark = true))

        // Null or unknown fall back to Sage
        assertEquals(AncientKeysThemeVariant.SAGE, resolveThemeVariant(null, isSystemDark = false))
        assertEquals(AncientKeysThemeVariant.SAGE, resolveThemeVariant("unknown", isSystemDark = false))
    }

    // --- WCAG Contrast Verification ---

    private fun calculateLuminance(color: Color): Double {
        fun channelLuminance(c: Float): Double {
            return if (c <= 0.03928f) {
                c / 12.92
            } else {
                ((c + 0.055) / 1.055).pow(2.4)
            }
        }
        val r = channelLuminance(color.red)
        val g = channelLuminance(color.green)
        val b = channelLuminance(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun calculateContrast(foreground: Color, background: Color): Double {
        val l1 = calculateLuminance(foreground)
        val l2 = calculateLuminance(background)
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    @Test
    fun testMinimalSageContrastMeetsWcagStandards() {
        // Primary text on surface (White): target >= 7.0 for AAA
        val textContrast = calculateContrast(SageOnSurface, SageSurface)
        assertTrue("Minimal Sage text contrast ($textContrast) must exceed WCAG AAA (7.0)", textContrast >= 7.0)

        // Secondary text on surface: target >= 4.5 for AA
        val secondaryContrast = calculateContrast(SageOnSurfaceVariant, SageSurface)
        assertTrue("Minimal Sage secondary text contrast ($secondaryContrast) must exceed WCAG AA (4.5)", secondaryContrast >= 4.5)

        // Primary action button (white text on sage): target >= 4.5 for AA
        val buttonContrast = calculateContrast(SageOnPrimary, SagePrimary)
        assertTrue("Minimal Sage primary button contrast ($buttonContrast) must exceed WCAG AA (4.5)", buttonContrast >= 4.5)
    }

    @Test
    fun testLightAndDarkPalettesContrastMeetWcagStandards() {
        val lightTextContrast = calculateContrast(LightOnSurface, LightSurface)
        assertTrue("Light theme text contrast ($lightTextContrast) must exceed WCAG AAA (7.0)", lightTextContrast >= 7.0)

        val darkTextContrast = calculateContrast(DarkOnSurface, DarkSurface)
        assertTrue("Dark theme text contrast ($darkTextContrast) must exceed WCAG AAA (7.0)", darkTextContrast >= 7.0)

        val sepiaTextContrast = calculateContrast(SepiaOnSurface, SepiaSurface)
        assertTrue("Sepia theme text contrast ($sepiaTextContrast) must exceed WCAG AAA (7.0)", sepiaTextContrast >= 7.0)
    }

    @Test
    fun testDesignTokensAccessibilityBounds() {
        // Touch targets must be >= 44 dp for compact keys and >= 48 dp for general components
        assertTrue("Min key width must be >= 42 dp", AncientKeysDesignTokens.TouchTargets.minKeyWidth.value >= 42f)
        assertTrue("Min key height must be >= 44 dp", AncientKeysDesignTokens.TouchTargets.minKeyHeight.value >= 44f)
        assertTrue("Min general touch target must be >= 48 dp", AncientKeysDesignTokens.TouchTargets.minTouchTarget.value >= 48f)
        assertTrue("Min icon button must be >= 40 dp", AncientKeysDesignTokens.TouchTargets.minIconButton.value >= 40f)

        // Spacing scale integrity
        assertTrue(AncientKeysDesignTokens.Spacing.none.value == 0f)
        assertTrue(AncientKeysDesignTokens.Spacing.xxs.value < AncientKeysDesignTokens.Spacing.xs.value)
        assertTrue(AncientKeysDesignTokens.Spacing.xs.value < AncientKeysDesignTokens.Spacing.sm.value)
        assertTrue(AncientKeysDesignTokens.Spacing.sm.value < AncientKeysDesignTokens.Spacing.md.value)
        assertTrue(AncientKeysDesignTokens.Spacing.md.value < AncientKeysDesignTokens.Spacing.lg.value)
        assertTrue(AncientKeysDesignTokens.Spacing.lg.value < AncientKeysDesignTokens.Spacing.xl.value)
    }
}
