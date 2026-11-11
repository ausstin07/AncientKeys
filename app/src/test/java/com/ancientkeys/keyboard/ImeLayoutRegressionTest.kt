package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.data.KeyboardMode
import com.ancientkeys.keyboard.ime.ui.ImeDimensionsHelper
import com.ancientkeys.keyboard.model.ScriptRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Automated regression test suite for AncientKeys v1.1 Phase 1 Responsive Keyboard Layout.
 *
 * Verifies:
 * - Dynamic adaptive height calculation across portrait and landscape orientations.
 * - Upper and lower clamping limits preventing keyboard overflow or text field occlusion.
 * - Mode-specific height expansion for composition strips (Easy Phonetic & Romanized).
 * - Compact script name resolution preventing toolbar overflow on narrow displays (320-360 dp).
 * - Minimum touch target widths and row heights for critical controls (Enter, Backspace, Space).
 */
class ImeLayoutRegressionTest {

    @Test
    fun testPortraitAdaptiveHeightClamping() {
        // Standard phone portrait: 800 dp height, default preference (290 dp)
        val defaultPortrait = ImeDimensionsHelper.computeAdaptiveKeyboardHeight(
            screenHeightDp = 800,
            isLandscape = false,
            keyboardMode = KeyboardMode.HISTORICAL,
            userPreferenceHeightDp = 290
        )
        assertEquals(290, defaultPortrait)

        // Very small screen phone portrait: 400 dp screen height
        // Max allowed is 400 * 0.48 = 192 dp, but clamped to MIN_PORTRAIT_HEIGHT_DP (240 dp)
        val smallScreen = ImeDimensionsHelper.computeAdaptiveKeyboardHeight(
            screenHeightDp = 400,
            isLandscape = false,
            keyboardMode = KeyboardMode.HISTORICAL,
            userPreferenceHeightDp = 290
        )
        assertEquals(ImeDimensionsHelper.MIN_PORTRAIT_HEIGHT_DP, smallScreen)

        // Large tablet/tall phone portrait: user requests excessive 500 dp
        val clampedMax = ImeDimensionsHelper.computeAdaptiveKeyboardHeight(
            screenHeightDp = 1000,
            isLandscape = false,
            keyboardMode = KeyboardMode.HISTORICAL,
            userPreferenceHeightDp = 500
        )
        // User preference is clamped to MAX_PORTRAIT_HEIGHT_DP (360 dp)
        assertEquals(ImeDimensionsHelper.MAX_PORTRAIT_HEIGHT_DP, clampedMax)
    }

    @Test
    fun testCompositionModesReceiveVerticalExpansionInPortrait() {
        // Standard phone portrait: 800 dp height
        val historicalHeight = ImeDimensionsHelper.computeAdaptiveKeyboardHeight(
            screenHeightDp = 800,
            isLandscape = false,
            keyboardMode = KeyboardMode.HISTORICAL,
            userPreferenceHeightDp = 290
        )
        val phoneticHeight = ImeDimensionsHelper.computeAdaptiveKeyboardHeight(
            screenHeightDp = 800,
            isLandscape = false,
            keyboardMode = KeyboardMode.EASY_PHONETIC,
            userPreferenceHeightDp = 290
        )
        val romanizedHeight = ImeDimensionsHelper.computeAdaptiveKeyboardHeight(
            screenHeightDp = 800,
            isLandscape = false,
            keyboardMode = KeyboardMode.ROMANIZED,
            userPreferenceHeightDp = 290
        )

        // Phonetic and Romanized receive +28 dp for the candidate/composition strip
        assertEquals(290, historicalHeight)
        assertEquals(318, phoneticHeight)
        assertEquals(318, romanizedHeight)
        assertTrue(phoneticHeight > historicalHeight)
    }

    @Test
    fun testLandscapeAdaptiveHeightProportionsAndLimits() {
        // Standard phone landscape: screen height = 400 dp (e.g. 800x400 landscape)
        // Proportional = 400 * 0.52 = 208 dp -> clamped to MAX_LANDSCAPE_HEIGHT_DP (205 dp)
        val standardLandscape = ImeDimensionsHelper.computeAdaptiveKeyboardHeight(
            screenHeightDp = 400,
            isLandscape = true,
            keyboardMode = KeyboardMode.HISTORICAL
        )
        assertEquals(ImeDimensionsHelper.MAX_LANDSCAPE_HEIGHT_DP, standardLandscape)

        // Narrow landscape (e.g. 300 dp height): 300 * 0.52 = 156 dp -> clamped to MIN_LANDSCAPE_HEIGHT_DP (160 dp)
        val narrowLandscape = ImeDimensionsHelper.computeAdaptiveKeyboardHeight(
            screenHeightDp = 300,
            isLandscape = true,
            keyboardMode = KeyboardMode.HISTORICAL
        )
        assertEquals(ImeDimensionsHelper.MIN_LANDSCAPE_HEIGHT_DP, narrowLandscape)

        // Mid landscape: 360 dp height -> 360 * 0.52 = 187 dp
        val midLandscape = ImeDimensionsHelper.computeAdaptiveKeyboardHeight(
            screenHeightDp = 360,
            isLandscape = true,
            keyboardMode = KeyboardMode.HISTORICAL
        )
        assertEquals(187, midLandscape)
        assertTrue(midLandscape in ImeDimensionsHelper.MIN_LANDSCAPE_HEIGHT_DP..ImeDimensionsHelper.MAX_LANDSCAPE_HEIGHT_DP)
    }

    @Test
    fun testCompactScriptNamesForAllScripts() {
        val scripts = ScriptRegistry.allScripts
        assertEquals("Expected all 6 historical scripts in registry", 6, scripts.size)

        for (script in scripts) {
            val compactName = ImeDimensionsHelper.getCompactScriptName(script)
            assertTrue("Compact name for ${script.name} should not be empty", compactName.isNotEmpty())
            assertTrue(
                "Compact name '$compactName' must be concise (<= 11 chars) to avoid utility bar overflow",
                compactName.length <= 11
            )
        }

        val egyptian = ScriptRegistry.getScriptById("egyptian")!!
        assertEquals("Egyptian", ImeDimensionsHelper.getCompactScriptName(egyptian))

        val greek = ScriptRegistry.getScriptById("greek")!!
        assertEquals("Greek", ImeDimensionsHelper.getCompactScriptName(greek))

        val runic = ScriptRegistry.getScriptById("runic")!!
        assertEquals("Runic", ImeDimensionsHelper.getCompactScriptName(runic))

        val brahmi = ScriptRegistry.getScriptById("brahmi")!!
        assertEquals("Brahmi", ImeDimensionsHelper.getCompactScriptName(brahmi))

        val kharosthi = ScriptRegistry.getScriptById("kharosthi")!!
        assertEquals("Kharosthi", ImeDimensionsHelper.getCompactScriptName(kharosthi))

        val phoenician = ScriptRegistry.getScriptById("phoenician")!!
        assertEquals("Phoenician", ImeDimensionsHelper.getCompactScriptName(phoenician))
    }

    @Test
    fun testMinimumControlTouchTargetsMeetAccessibilityStandards() {
        // Material and Android guidelines recommend min 44-48 dp for interactive touch targets
        assertTrue(
            "Enter key width constraint (${ImeDimensionsHelper.MIN_ENTER_KEY_WIDTH_DP} dp) must be >= 44 dp",
            ImeDimensionsHelper.MIN_ENTER_KEY_WIDTH_DP >= 44
        )
        assertTrue(
            "Backspace key width constraint (${ImeDimensionsHelper.MIN_BACKSPACE_KEY_WIDTH_DP} dp) must be >= 42 dp",
            ImeDimensionsHelper.MIN_BACKSPACE_KEY_WIDTH_DP >= 42
        )
        assertTrue(
            "Space key width constraint (${ImeDimensionsHelper.MIN_SPACE_KEY_WIDTH_DP} dp) must be >= 70 dp",
            ImeDimensionsHelper.MIN_SPACE_KEY_WIDTH_DP >= 70
        )

        // Bottom row heights
        assertEquals(44, ImeDimensionsHelper.computeBottomRowHeight(isLandscape = false))
        assertEquals(36, ImeDimensionsHelper.computeBottomRowHeight(isLandscape = true))

        // Key row heights
        assertEquals(44, ImeDimensionsHelper.computeKeyRowHeight(isLandscape = false))
        assertEquals(34, ImeDimensionsHelper.computeKeyRowHeight(isLandscape = true))
    }
}

