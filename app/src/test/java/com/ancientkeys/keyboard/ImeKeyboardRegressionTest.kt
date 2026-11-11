package com.ancientkeys.keyboard

import android.text.InputType
import android.view.inputmethod.EditorInfo
import com.ancientkeys.keyboard.ime.ImeInputHelper
import com.ancientkeys.keyboard.model.ScriptRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ImeKeyboardRegressionTest {

    @Test
    fun testAllSixScriptsHaveKeysForIme() {
        val scripts = ScriptRegistry.allScripts
        assertEquals(6, scripts.size)

        for (script in scripts) {
            assertTrue("${script.name} must have keys for IME layout", script.keys.isNotEmpty())
            for (key in script.keys) {
                assertTrue("Character in ${script.name} must not be empty", key.character.isNotEmpty())
                assertTrue("Code point in ${script.name} must be formatted as U+", key.unicodeCodePoint.startsWith("U+"))
            }
        }
    }

    @Test
    fun testRtlFlagsForImeLayoutDirection() {
        val phoenician = ScriptRegistry.getScriptById("phoenician")!!
        assertTrue("Phoenician IME must be RTL", phoenician.isRtl)

        val kharosthi = ScriptRegistry.getScriptById("kharosthi")!!
        assertTrue("Kharosthi IME must be RTL", kharosthi.isRtl)

        val egyptian = ScriptRegistry.getScriptById("egyptian")!!
        assertFalse("Egyptian IME must be LTR", egyptian.isRtl)

        val greek = ScriptRegistry.getScriptById("greek")!!
        assertFalse("Greek IME must be LTR", greek.isRtl)

        val runic = ScriptRegistry.getScriptById("runic")!!
        assertFalse("Runic IME must be LTR", runic.isRtl)

        val brahmi = ScriptRegistry.getScriptById("brahmi")!!
        assertFalse("Brahmi IME must be LTR", brahmi.isRtl)
    }

    @Test
    fun testSensitiveFieldDetectionForPasswordShield() {
        // Sensitive password field: must trigger shield fallback
        val passwordField = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        assertTrue("Password field must be identified as sensitive", ImeInputHelper.isSensitiveField(passwordField))

        val webPasswordField = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        }
        assertTrue("Web password field must be identified as sensitive", ImeInputHelper.isSensitiveField(webPasswordField))

        val numericPasswordField = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        assertTrue("Numeric password field must be identified as sensitive", ImeInputHelper.isSensitiveField(numericPasswordField))

        // Normal text field: must NOT trigger shield
        val normalField = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        }
        assertFalse("Normal text field must not be sensitive", ImeInputHelper.isSensitiveField(normalField))

        // Null field: safe fallback
        assertFalse("Null EditorInfo must not crash and should not be sensitive", ImeInputHelper.isSensitiveField(null))
    }

    @Test
    fun testImeCategoriesAreValid() {
        for (script in ScriptRegistry.allScripts) {
            assertTrue("${script.name} must define at least one category", script.categories.isNotEmpty())
            val keyCategories = script.keys.map { it.category }.toSet()
            for (keyCat in keyCategories) {
                assertTrue("Key category '$keyCat' in ${script.name} must belong to script categories",
                    script.categories.contains(keyCat))
            }
        }
    }
}

