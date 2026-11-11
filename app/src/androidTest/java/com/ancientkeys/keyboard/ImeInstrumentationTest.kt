package com.ancientkeys.keyboard

import android.content.Context
import android.text.InputType
import android.view.inputmethod.EditorInfo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ancientkeys.keyboard.ime.AncientKeysIME
import com.ancientkeys.keyboard.ime.ImeInputHelper
import com.ancientkeys.keyboard.ime.data.EnglishKeyboardData
import com.ancientkeys.keyboard.ime.data.KeyboardMode
import com.ancientkeys.keyboard.ime.data.ShiftState
import com.ancientkeys.keyboard.model.ScriptRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android Instrumentation test suite for the AncientKeys IME runtime environment:
 * - English QWERTY keyboard layout rows and numbers
 * - Shift and Caps Lock state machine transitions
 * - Keyboard modes availability
 * - All six historical script inventories and Unicode ranges on Android
 * - Phoenician and Kharosthi RTL flags on Android
 * - Real Android EditorInfo sensitive/password field detection
 * - AncientKeysIME service availability
 */
@RunWith(AndroidJUnit4::class)
class ImeInstrumentationTest {

    @Test
    fun testImeServiceRegistration() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        assertNotNull(appContext)
        assertEquals("com.ancientkeys.keyboard", appContext.packageName)

        // Verify AncientKeysIME class can be resolved
        val imeClass = AncientKeysIME::class.java
        assertEquals("com.ancientkeys.keyboard.ime.AncientKeysIME", imeClass.name)
    }

    @Test
    fun testEnglishQwertyOnAndroidRuntime() {
        val row1 = EnglishKeyboardData.QWERTY_ROW_1_LOWER
        val row2 = EnglishKeyboardData.QWERTY_ROW_2_LOWER
        val row3 = EnglishKeyboardData.QWERTY_ROW_3_LOWER

        assertEquals(10, row1.size)
        assertEquals(9, row2.size)
        assertEquals(7, row3.size)

        // Numbers 0-9
        val numbers = EnglishKeyboardData.NUMBERS_ROW
        assertEquals(10, numbers.size)
        assertEquals("1", numbers.first())
        assertEquals("0", numbers.last())

        // Modern symbols
        val sym1 = EnglishKeyboardData.SYMBOLS_ROW_1
        assertTrue(sym1.contains("@"))
        assertTrue(sym1.contains("#"))
    }

    @Test
    fun testShiftStateTransitionsOnAndroidRuntime() {
        var state = ShiftState.OFF
        assertFalse(EnglishKeyboardData.isUppercase(state))

        // Single tap -> ON_ONCE
        state = EnglishKeyboardData.nextShiftState(state)
        assertEquals(ShiftState.ON_ONCE, state)
        assertTrue(EnglishKeyboardData.isUppercase(state))

        // Tap again -> CAPS_LOCK
        state = EnglishKeyboardData.nextShiftState(state)
        assertEquals(ShiftState.CAPS_LOCK, state)
        assertTrue(EnglishKeyboardData.isUppercase(state))

        // Tap again -> OFF
        state = EnglishKeyboardData.nextShiftState(state)
        assertEquals(ShiftState.OFF, state)
        assertFalse(EnglishKeyboardData.isUppercase(state))
    }

    @Test
    fun testKeyboardModesOnAndroidRuntime() {
        val modes = KeyboardMode.values()
        assertEquals(5, modes.size)
        assertTrue(modes.contains(KeyboardMode.HISTORICAL))
        assertTrue(modes.contains(KeyboardMode.ENGLISH_QWERTY))
        assertTrue(modes.contains(KeyboardMode.NUMBERS_SYMBOLS))
        assertTrue(modes.contains(KeyboardMode.EASY_PHONETIC))
        assertTrue(modes.contains(KeyboardMode.ROMANIZED))
    }

    @Test
    fun testAllSixHistoricalScriptsOnAndroidRuntime() {
        val scripts = ScriptRegistry.allScripts
        assertEquals(6, scripts.size)

        for (script in scripts) {
            assertTrue("${script.name} must have keys on device", script.keys.isNotEmpty())
            assertTrue("${script.name} must have categories on device", script.categories.isNotEmpty())

            // Verify code point strings
            for (key in script.keys) {
                assertTrue(key.unicodeCodePoint.startsWith("U+"))
                assertTrue(key.character.isNotEmpty())
            }
        }
    }

    @Test
    fun testPhoenicianAndKharosthiRtlFlagsOnAndroidRuntime() {
        val phoenician = ScriptRegistry.getScriptById("phoenician")!!
        assertTrue("Phoenician must be RTL on device", phoenician.isRtl)

        val kharosthi = ScriptRegistry.getScriptById("kharosthi")!!
        assertTrue("Kharosthi must be RTL on device", kharosthi.isRtl)

        val egyptian = ScriptRegistry.getScriptById("egyptian")!!
        assertFalse("Egyptian must be LTR on device", egyptian.isRtl)
    }

    @Test
    fun testRealEditorInfoSensitiveFieldDetection() {
        // Standard Android password field
        val passwordEditorInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        assertTrue("Password field must trigger shield on device", ImeInputHelper.isSensitiveField(passwordEditorInfo))

        // Web password
        val webPasswordEditorInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        }
        assertTrue("Web password must trigger shield on device", ImeInputHelper.isSensitiveField(webPasswordEditorInfo))

        // Normal text field
        val normalEditorInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        }
        assertFalse("Normal text field must not trigger shield on device", ImeInputHelper.isSensitiveField(normalEditorInfo))
    }
}

