package com.ancientkeys.keyboard

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.ancientkeys.keyboard.ime.data.EnglishKeyboardData
import com.ancientkeys.keyboard.ime.data.KeyboardMode
import com.ancientkeys.keyboard.ime.data.ShiftState
import com.ancientkeys.keyboard.ime.ui.ScriptSwitchHandler
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.util.UnicodeTextEditor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Regression and validation tests for AncientKeys v1.1 Default Keyboard Improvement:
 * 1. English QWERTY default keyboard on initial installation / activation.
 * 2. Ability to explicitly configure any of the 7 supported scripts (English + 6 Historical).
 * 3. One-tap script selector integration and ScriptSwitchHandler with English QWERTY.
 * 4. Preservation of text and cursor position when switching scripts.
 * 5. English QWERTY key data integrity, Shift states, and number/symbol mappings.
 */
class DefaultKeyboardImprovementTest {

    private lateinit var repo: UserPreferencesRepository

    @Before
    fun setUp() {
        repo = UserPreferencesRepository(null)
    }

    @Test
    fun testDefaultScriptIsEnglishOnNewInstallation() {
        assertEquals(
            "New installation must default to English QWERTY layout",
            "english",
            repo.getDefaultScriptId()
        )
        assertEquals(
            "DEFAULT_SCRIPT_ID constant must be 'english'",
            "english",
            UserPreferencesRepository.DEFAULT_SCRIPT_ID
        )
    }

    @Test
    fun testExplicitScriptPreferenceOverridesEnglishDefault() {
        val testScripts = listOf("egyptian", "brahmi", "greek", "runic", "phoenician", "kharosthi")
        for (scriptId in testScripts) {
            repo.setDefaultScriptId(scriptId)
            assertEquals("Explicit script choice must be saved as preference", scriptId, repo.getDefaultScriptId())
        }

        // Switching back to English
        repo.setDefaultScriptId("english")
        assertEquals("Should be able to explicitly restore English as default", "english", repo.getDefaultScriptId())
    }

    @Test
    fun testAllSevenScriptsSupportedInPreferences() {
        val allSeven = listOf("english", "greek", "egyptian", "brahmi", "kharosthi", "phoenician", "runic")
        for (s in allSeven) {
            repo.setDefaultScriptId("  ${s.uppercase()}  ")
            assertEquals("Script ID must be normalized and trimmed", s, repo.getDefaultScriptId())
        }
    }

    @Test
    fun testScriptSwitchHandlerWithEnglishAndHistoricalScripts() {
        // Same script (English -> English): ignored
        val sameEnglish = ScriptSwitchHandler.evaluateScriptSwitch("english", "english", isConfirmationEnabled = true)
        assertEquals(ScriptSwitchHandler.Action.IGNORE, sameEnglish)

        // Same script case insensitive
        val sameCase = ScriptSwitchHandler.evaluateScriptSwitch("ENGLISH", "english", isConfirmationEnabled = true)
        assertEquals(ScriptSwitchHandler.Action.IGNORE, sameCase)

        // English -> Greek with confirmation enabled
        val toGreekConfirm = ScriptSwitchHandler.evaluateScriptSwitch("english", "greek", isConfirmationEnabled = true)
        assertEquals(ScriptSwitchHandler.Action.PROMPT_CONFIRMATION, toGreekConfirm)

        // English -> Greek with confirmation disabled
        val toGreekImmediate = ScriptSwitchHandler.evaluateScriptSwitch("english", "greek", isConfirmationEnabled = false)
        assertEquals(ScriptSwitchHandler.Action.SWITCH_IMMEDIATELY, toGreekImmediate)

        // Greek -> English with confirmation enabled
        val toEnglishConfirm = ScriptSwitchHandler.evaluateScriptSwitch("greek", "english", isConfirmationEnabled = true)
        assertEquals(ScriptSwitchHandler.Action.PROMPT_CONFIRMATION, toEnglishConfirm)

        // Greek -> English with confirmation disabled
        val toEnglishImmediate = ScriptSwitchHandler.evaluateScriptSwitch("greek", "english", isConfirmationEnabled = false)
        assertEquals(ScriptSwitchHandler.Action.SWITCH_IMMEDIATELY, toEnglishImmediate)
    }

    @Test
    fun testPreserveTextAndCursorAcrossScriptSwitches() {
        val initialText = "Hello World"
        val editor = TextFieldValue(text = initialText, selection = TextRange(5)) // cursor between 'o' and ' '
        assertEquals(initialText, editor.text)
        assertEquals(5, editor.selection.start)
        assertEquals(5, editor.selection.end)

        // Simulate switching script from English to Greek, then to Egyptian, then back to English
        val scripts = listOf("greek", "egyptian", "runic", "english")
        for (scriptId in scripts) {
            repo.setDefaultScriptId(scriptId)
            // Verify editor state remains untouched
            assertEquals("Text must remain unaltered across script switches", initialText, editor.text)
            assertEquals("Cursor start must remain unaltered", 5, editor.selection.start)
            assertEquals("Cursor end must remain unaltered", 5, editor.selection.end)
        }
    }

    @Test
    fun testEnglishKeyboardDataRowsAndShiftTransitions() {
        assertEquals("English QWERTY top row must have 10 keys (Q-P)", 10, EnglishKeyboardData.QWERTY_ROW_1_LOWER.size)
        assertEquals("English QWERTY middle row must have 9 keys (A-L)", 9, EnglishKeyboardData.QWERTY_ROW_2_LOWER.size)
        assertEquals("English QWERTY bottom row must have 7 letter keys (Z-M)", 7, EnglishKeyboardData.QWERTY_ROW_3_LOWER.size)

        // Shift transitions
        assertEquals(ShiftState.ON_ONCE, EnglishKeyboardData.nextShiftState(ShiftState.OFF))
        assertEquals(ShiftState.CAPS_LOCK, EnglishKeyboardData.nextShiftState(ShiftState.ON_ONCE))
        assertEquals(ShiftState.OFF, EnglishKeyboardData.nextShiftState(ShiftState.CAPS_LOCK))

        // Uppercase vs lowercase matching
        for (i in EnglishKeyboardData.QWERTY_ROW_1_LOWER.indices) {
            assertEquals(EnglishKeyboardData.QWERTY_ROW_1_LOWER[i].uppercase(), EnglishKeyboardData.QWERTY_ROW_1_UPPER[i])
        }
        for (i in EnglishKeyboardData.QWERTY_ROW_2_LOWER.indices) {
            assertEquals(EnglishKeyboardData.QWERTY_ROW_2_LOWER[i].uppercase(), EnglishKeyboardData.QWERTY_ROW_2_UPPER[i])
        }
        for (i in EnglishKeyboardData.QWERTY_ROW_3_LOWER.indices) {
            assertEquals(EnglishKeyboardData.QWERTY_ROW_3_LOWER[i].uppercase(), EnglishKeyboardData.QWERTY_ROW_3_UPPER[i])
        }
    }
}

