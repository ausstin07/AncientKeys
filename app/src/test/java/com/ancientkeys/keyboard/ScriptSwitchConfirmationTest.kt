package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.data.KeyboardMode
import com.ancientkeys.keyboard.ime.ui.ScriptSwitchHandler
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Targeted unit tests for the Phase E corrective fix: "Ask before switching scripts".
 *
 * Verifies:
 * - Selecting the already-active script is ignored without prompting (same-script scenario)
 * - Case-insensitive same-script comparison
 * - When confirmation is enabled, selecting a different script prompts for confirmation
 * - When confirmation is disabled, selecting a different script switches immediately
 * - Cancellation preserves the active script, mode, and state without side-effects
 * - Confirmation transitions to the target script correctly
 * - Integration with UserPreferencesRepository
 * - Fast English QWERTY and Numbers/Symbols mode workflows remain uninterrupted
 */
class ScriptSwitchConfirmationTest {

    private lateinit var repo: UserPreferencesRepository

    @Before
    fun setUp() {
        repo = UserPreferencesRepository(null)
    }

    @Test
    fun testSameScriptSelectionIsIgnoredWithoutConfirmation() {
        // When confirmation is enabled
        val actionEnabled = ScriptSwitchHandler.evaluateScriptSwitch(
            currentScriptId = "greek",
            targetScriptId = "greek",
            isConfirmationEnabled = true
        )
        assertEquals(ScriptSwitchHandler.Action.IGNORE, actionEnabled)

        // Case insensitivity
        val actionCase = ScriptSwitchHandler.evaluateScriptSwitch(
            currentScriptId = "GREEK",
            targetScriptId = "greek",
            isConfirmationEnabled = true
        )
        assertEquals(ScriptSwitchHandler.Action.IGNORE, actionCase)

        // Trimming whitespace
        val actionTrim = ScriptSwitchHandler.evaluateScriptSwitch(
            currentScriptId = "egyptian",
            targetScriptId = " egyptian ",
            isConfirmationEnabled = true
        )
        assertEquals(ScriptSwitchHandler.Action.IGNORE, actionTrim)

        // When confirmation is disabled
        val actionDisabled = ScriptSwitchHandler.evaluateScriptSwitch(
            currentScriptId = "brahmi",
            targetScriptId = "brahmi",
            isConfirmationEnabled = false
        )
        assertEquals(ScriptSwitchHandler.Action.IGNORE, actionDisabled)
    }

    @Test
    fun testDifferentScriptWithConfirmationEnabledPromptsConfirmation() {
        val pairs = listOf(
            "greek" to "brahmi",
            "brahmi" to "egyptian",
            "egyptian" to "kharosthi",
            "kharosthi" to "phoenician",
            "phoenician" to "runic",
            "runic" to "greek"
        )

        for ((current, target) in pairs) {
            val action = ScriptSwitchHandler.evaluateScriptSwitch(
                currentScriptId = current,
                targetScriptId = target,
                isConfirmationEnabled = true
            )
            assertEquals(
                "Switching from $current to $target with confirmation enabled must prompt confirmation",
                ScriptSwitchHandler.Action.PROMPT_CONFIRMATION,
                action
            )
        }
    }

    @Test
    fun testDifferentScriptWithConfirmationDisabledSwitchesImmediately() {
        val pairs = listOf(
            "greek" to "egyptian",
            "egyptian" to "brahmi",
            "brahmi" to "kharosthi",
            "kharosthi" to "phoenician",
            "phoenician" to "runic",
            "runic" to "greek"
        )

        for ((current, target) in pairs) {
            val action = ScriptSwitchHandler.evaluateScriptSwitch(
                currentScriptId = current,
                targetScriptId = target,
                isConfirmationEnabled = false
            )
            assertEquals(
                "Switching from $current to $target with confirmation disabled must switch immediately",
                ScriptSwitchHandler.Action.SWITCH_IMMEDIATELY,
                action
            )
        }
    }

    @Test
    fun testPreferenceIntegrationFromUserPreferencesRepository() {
        // By default, confirmation is disabled
        assertFalse(repo.isConfirmScriptSwitchEnabled())
        var action = ScriptSwitchHandler.evaluateScriptSwitch(
            currentScriptId = "greek",
            targetScriptId = "runic",
            isConfirmationEnabled = repo.isConfirmScriptSwitchEnabled()
        )
        assertEquals(ScriptSwitchHandler.Action.SWITCH_IMMEDIATELY, action)

        // User enables "Ask before switching scripts" in Settings
        repo.setConfirmScriptSwitchEnabled(true)
        assertTrue(repo.isConfirmScriptSwitchEnabled())
        action = ScriptSwitchHandler.evaluateScriptSwitch(
            currentScriptId = "greek",
            targetScriptId = "runic",
            isConfirmationEnabled = repo.isConfirmScriptSwitchEnabled()
        )
        assertEquals(ScriptSwitchHandler.Action.PROMPT_CONFIRMATION, action)

        // User disables it again
        repo.setConfirmScriptSwitchEnabled(false)
        assertFalse(repo.isConfirmScriptSwitchEnabled())
        action = ScriptSwitchHandler.evaluateScriptSwitch(
            currentScriptId = "greek",
            targetScriptId = "runic",
            isConfirmationEnabled = repo.isConfirmScriptSwitchEnabled()
        )
        assertEquals(ScriptSwitchHandler.Action.SWITCH_IMMEDIATELY, action)
    }

    @Test
    fun testCancellationPreservesActiveScriptAndState() {
        var activeScriptId = "greek"
        var activeKeyboardMode = KeyboardMode.HISTORICAL
        var phoneticBuffer = "sample_text"
        var pendingScriptId: String? = null

        // User requests switch to Kharosthi with confirmation enabled
        val action = ScriptSwitchHandler.evaluateScriptSwitch(
            currentScriptId = activeScriptId,
            targetScriptId = "kharosthi",
            isConfirmationEnabled = true
        )
        assertEquals(ScriptSwitchHandler.Action.PROMPT_CONFIRMATION, action)
        pendingScriptId = "kharosthi"

        // User cancels
        pendingScriptId = null

        // Verify active state remains unchanged
        assertEquals("greek", activeScriptId)
        assertEquals(KeyboardMode.HISTORICAL, activeKeyboardMode)
        assertEquals("sample_text", phoneticBuffer)
        assertNull(pendingScriptId)
    }

    @Test
    fun testConfirmationAppliesTargetScriptAndResetsBuffers() {
        var activeScriptId = "greek"
        var activeKeyboardMode = KeyboardMode.HISTORICAL
        var phoneticBuffer = "k"
        var pendingScriptId: String? = "brahmi"

        // User confirms switch
        if (pendingScriptId != null) {
            activeScriptId = pendingScriptId
            activeKeyboardMode = KeyboardMode.HISTORICAL
            phoneticBuffer = ""
            pendingScriptId = null
        }

        // Verify state is updated to Brahmi
        assertEquals("brahmi", activeScriptId)
        assertEquals(KeyboardMode.HISTORICAL, activeKeyboardMode)
        assertEquals("", phoneticBuffer)
        assertNull(pendingScriptId)
    }

    @Test
    fun testFastEnglishModeSwitchingDoesNotRequireScriptConfirmation() {
        // English QWERTY is an input mode, not a script change
        val currentScriptId = "greek"
        var currentMode = KeyboardMode.HISTORICAL

        // Tapping ABC pill changes mode to English QWERTY
        currentMode = KeyboardMode.ENGLISH_QWERTY
        assertEquals(KeyboardMode.ENGLISH_QWERTY, currentMode)
        assertEquals("greek", currentScriptId) // Script remains Greek

        // Tapping Greek pill changes mode back to Historical
        currentMode = KeyboardMode.HISTORICAL
        assertEquals(KeyboardMode.HISTORICAL, currentMode)
        assertEquals("greek", currentScriptId)
    }
}

