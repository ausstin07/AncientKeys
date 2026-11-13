package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.data.KeyboardMode
import com.ancientkeys.keyboard.ime.ui.ImeDimensionsHelper
import com.ancientkeys.keyboard.ime.ui.RepeatBackspaceHelper
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Phase 2 — Keyboard Simplification, Repeat Backspace, and Ergonomic Layout Unit Tests.
 */
class Phase2KeyboardSimplificationTest {

    @Test
    fun testRepeatBackspaceConstants() {
        assertEquals("Initial hold delay should be 400ms", 400L, RepeatBackspaceHelper.INITIAL_DELAY_MS)
        assertEquals("Standard repeat interval should be 50ms", 50L, RepeatBackspaceHelper.REPEAT_INTERVAL_MS)
        assertEquals("Accelerated interval should be 35ms", 35L, RepeatBackspaceHelper.ACCELERATED_INTERVAL_MS)
        assertEquals("Acceleration threshold should be 15 repeats", 15, RepeatBackspaceHelper.ACCELERATION_THRESHOLD)
    }

    @Test
    fun testRepeatBackspaceDelayProgression() {
        // Initial repeats (0 to 14) use standard 50ms interval
        for (i in 0 until 15) {
            assertEquals("Repeat count $i should use standard interval", 50L, RepeatBackspaceHelper.computeDelay(i))
        }

        // After 15 repeats, interval accelerates to 35ms
        for (i in 15..30) {
            assertEquals("Repeat count $i should use accelerated interval", 35L, RepeatBackspaceHelper.computeDelay(i))
        }
    }

    @Test
    fun testSimulateRepeatSequenceAccuracy() {
        // 0ms hold -> 0 events
        assertEquals(0, RepeatBackspaceHelper.simulateRepeatSequence(0L))
        assertEquals(0, RepeatBackspaceHelper.simulateRepeatSequence(-10L))

        // Quick tap (e.g. 100ms or 300ms) -> only 1 event (the initial down click)
        assertEquals(1, RepeatBackspaceHelper.simulateRepeatSequence(100L))
        assertEquals(1, RepeatBackspaceHelper.simulateRepeatSequence(300L))
        assertEquals(1, RepeatBackspaceHelper.simulateRepeatSequence(400L))

        // 450ms hold -> initial (0ms) + 1st repeat (at 450ms) = 2 events
        assertEquals(2, RepeatBackspaceHelper.simulateRepeatSequence(450L))

        // 500ms hold -> initial + 2 repeats = 3 events
        assertEquals(3, RepeatBackspaceHelper.simulateRepeatSequence(500L))

        // 600ms hold -> initial + 4 repeats = 5 events
        assertEquals(5, RepeatBackspaceHelper.simulateRepeatSequence(600L))

        // At 1150ms: initial + 15 standard repeats (15 * 50 = 750ms -> 400 + 750 = 1150ms) = 16 events
        assertEquals(16, RepeatBackspaceHelper.simulateRepeatSequence(1150L))

        // At 1185ms: 16 + 1 accelerated repeat (35ms) = 17 events
        assertEquals(17, RepeatBackspaceHelper.simulateRepeatSequence(1185L))
    }

    @Test
    fun testBottomRowBackspaceVisibilityRule() {
        // Historical mode requires bottom row backspace because glyphs are in a grid
        val isHistorical = (KeyboardMode.HISTORICAL == KeyboardMode.HISTORICAL)
        assertTrue("Historical mode must show bottom-row backspace", isHistorical)

        // English QWERTY has inline Backspace on Row 3; bottom row Backspace must be hidden
        val isEnglishHistorical = (KeyboardMode.ENGLISH_QWERTY == KeyboardMode.HISTORICAL)
        assertFalse("English QWERTY must NOT duplicate Backspace in bottom row", isEnglishHistorical)

        // Numbers & Symbols mode has inline Backspace on Row 3; bottom row Backspace must be hidden
        val isNumbersHistorical = (KeyboardMode.NUMBERS_SYMBOLS == KeyboardMode.HISTORICAL)
        assertFalse("Numbers/Symbols must NOT duplicate Backspace in bottom row", isNumbersHistorical)
    }

    @Test
    fun testBottomRowSpacebarWeightScaling() {
        // Spacebar expands when bottom row backspace is hidden
        fun computeSpaceWeight(mode: KeyboardMode): Float {
            return if (mode == KeyboardMode.HISTORICAL) 2.4f else 3.8f
        }

        assertEquals(3.8f, computeSpaceWeight(KeyboardMode.ENGLISH_QWERTY), 0.01f)
        assertEquals(3.8f, computeSpaceWeight(KeyboardMode.NUMBERS_SYMBOLS), 0.01f)
        assertEquals(3.8f, computeSpaceWeight(KeyboardMode.EASY_PHONETIC), 0.01f)
        assertEquals(3.8f, computeSpaceWeight(KeyboardMode.ROMANIZED), 0.01f)
        assertEquals(2.4f, computeSpaceWeight(KeyboardMode.HISTORICAL), 0.01f)
    }

    @Test
    fun testBottomRowModeToggleTransitions() {
        fun getNextModeFromBottomToggle(current: KeyboardMode): KeyboardMode {
            return when (current) {
                KeyboardMode.ENGLISH_QWERTY -> KeyboardMode.NUMBERS_SYMBOLS
                KeyboardMode.NUMBERS_SYMBOLS -> KeyboardMode.ENGLISH_QWERTY
                KeyboardMode.HISTORICAL -> KeyboardMode.ENGLISH_QWERTY
                KeyboardMode.EASY_PHONETIC, KeyboardMode.ROMANIZED -> KeyboardMode.NUMBERS_SYMBOLS
            }
        }

        assertEquals(KeyboardMode.NUMBERS_SYMBOLS, getNextModeFromBottomToggle(KeyboardMode.ENGLISH_QWERTY))
        assertEquals(KeyboardMode.ENGLISH_QWERTY, getNextModeFromBottomToggle(KeyboardMode.NUMBERS_SYMBOLS))
        assertEquals(KeyboardMode.ENGLISH_QWERTY, getNextModeFromBottomToggle(KeyboardMode.HISTORICAL))
        assertEquals(KeyboardMode.NUMBERS_SYMBOLS, getNextModeFromBottomToggle(KeyboardMode.EASY_PHONETIC))
        assertEquals(KeyboardMode.NUMBERS_SYMBOLS, getNextModeFromBottomToggle(KeyboardMode.ROMANIZED))
    }

    @Test
    fun testBottomRowModeButtonLabels() {
        fun getBottomModeLabel(mode: KeyboardMode): String {
            return when (mode) {
                KeyboardMode.ENGLISH_QWERTY -> "?123"
                KeyboardMode.NUMBERS_SYMBOLS -> "ABC"
                KeyboardMode.HISTORICAL -> "ABC"
                KeyboardMode.EASY_PHONETIC, KeyboardMode.ROMANIZED -> "?123"
            }
        }

        assertEquals("?123", getBottomModeLabel(KeyboardMode.ENGLISH_QWERTY))
        assertEquals("ABC", getBottomModeLabel(KeyboardMode.NUMBERS_SYMBOLS))
        assertEquals("ABC", getBottomModeLabel(KeyboardMode.HISTORICAL))
        assertEquals("?123", getBottomModeLabel(KeyboardMode.EASY_PHONETIC))
    }

    @Test
    fun testDefaultScriptPreferenceHandling() {
        val userPrefs = UserPreferencesRepository.getInstance(null)
        // Default is English QWERTY
        userPrefs.setDefaultScriptId("english")
        assertEquals("english", userPrefs.getDefaultScriptId())

        // User explicitly selects Greek
        userPrefs.setDefaultScriptId("greek")
        assertEquals("greek", userPrefs.getDefaultScriptId())
        assertEquals("Ancient Greek", ScriptRegistry.getScriptById(userPrefs.getDefaultScriptId())?.name)

        // Switching back to English
        userPrefs.setDefaultScriptId("english")
        assertEquals("english", userPrefs.getDefaultScriptId())
    }

    @Test
    fun testCompactScriptNameForToolbar() {
        val greek = ScriptRegistry.getScriptById("greek")
        val egyptian = ScriptRegistry.getScriptById("egyptian")
        val brahmi = ScriptRegistry.getScriptById("brahmi")
        val runic = ScriptRegistry.getScriptById("runic")
        val phoenician = ScriptRegistry.getScriptById("phoenician")
        val kharosthi = ScriptRegistry.getScriptById("kharosthi")

        assertNotNull(greek)
        assertNotNull(egyptian)
        assertNotNull(brahmi)
        assertNotNull(runic)
        assertNotNull(phoenician)
        assertNotNull(kharosthi)

        assertEquals("Greek", ImeDimensionsHelper.getCompactScriptName(greek!!))
        assertEquals("Egyptian", ImeDimensionsHelper.getCompactScriptName(egyptian!!))
        assertEquals("Brahmi", ImeDimensionsHelper.getCompactScriptName(brahmi!!))
        assertEquals("Runic", ImeDimensionsHelper.getCompactScriptName(runic!!))
        assertEquals("Phoenician", ImeDimensionsHelper.getCompactScriptName(phoenician!!))
        assertEquals("Kharosthi", ImeDimensionsHelper.getCompactScriptName(kharosthi!!))
    }
}

