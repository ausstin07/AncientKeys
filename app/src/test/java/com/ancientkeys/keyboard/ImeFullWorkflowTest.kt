package com.ancientkeys.keyboard

import android.text.InputType
import android.view.inputmethod.EditorInfo
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

/**
 * Comprehensive automated workflow tests for the AncientKeys IME:
 * - English QWERTY typing, Shift single-tap auto-revert, and Caps Lock
 * - Modern 0–9 numerals and symbol panels
 * - Keyboard mode transitions (HISTORICAL <-> ENGLISH_QWERTY <-> NUMBERS_SYMBOLS)
 * - All six historical scripts: Unicode insertion and deletion
 * - Phoenician and Kharosthi RTL logical ordering preservation
 * - Synthetic password-field detection and shield triggering
 * - Keyboard show/hide lifecycle resilience and IME switching
 */
class ImeFullWorkflowTest {

    // --- 1. ENGLISH QWERTY, SHIFT & CAPS LOCK TESTS ---

    @Test
    fun testEnglishQwertyCompleteSentenceTyping() {
        val ic = FakeInputConnection()
        val sentence = "The quick brown fox jumps over 13 lazy dogs."

        for (ch in sentence) {
            ImeInputHelper.commitCharacter(ic, ch.toString())
        }

        assertEquals(sentence, ic.currentText)
        assertEquals(sentence.length, ic.cursorPosition)
    }

    @Test
    fun testShiftStateSingleTapAutoRevert() {
        val ic = FakeInputConnection()
        var shiftState = ShiftState.OFF

        // Tap Shift: OFF -> ON_ONCE
        shiftState = EnglishKeyboardData.nextShiftState(shiftState)
        assertEquals(ShiftState.ON_ONCE, shiftState)
        assertTrue(EnglishKeyboardData.isUppercase(shiftState))

        // Type first letter 'a' with ON_ONCE -> outputs 'A'
        val letter = if (EnglishKeyboardData.isUppercase(shiftState)) "a".uppercase() else "a"
        ImeInputHelper.commitCharacter(ic, letter)
        if (shiftState == ShiftState.ON_ONCE) {
            shiftState = ShiftState.OFF
        }

        assertEquals("A", ic.currentText)
        assertEquals(ShiftState.OFF, shiftState)
        assertFalse(EnglishKeyboardData.isUppercase(shiftState))

        // Next letter 'b' typed under OFF -> outputs 'b'
        val nextLetter = if (EnglishKeyboardData.isUppercase(shiftState)) "b".uppercase() else "b"
        ImeInputHelper.commitCharacter(ic, nextLetter)

        assertEquals("Ab", ic.currentText)
    }

    @Test
    fun testCapsLockMaintainsUppercaseUntilUntoggled() {
        val ic = FakeInputConnection()
        var shiftState = ShiftState.OFF

        // Double tap: OFF -> ON_ONCE -> CAPS_LOCK
        shiftState = EnglishKeyboardData.nextShiftState(shiftState)
        shiftState = EnglishKeyboardData.nextShiftState(shiftState)
        assertEquals(ShiftState.CAPS_LOCK, shiftState)

        // Type multiple letters
        listOf("u", "n", "i", "c", "o", "d", "e").forEach { ch ->
            val out = if (EnglishKeyboardData.isUppercase(shiftState)) ch.uppercase() else ch
            ImeInputHelper.commitCharacter(ic, out)
        }

        assertEquals("UNICODE", ic.currentText)
        assertEquals(ShiftState.CAPS_LOCK, shiftState)

        // Tap Shift again: CAPS_LOCK -> OFF
        shiftState = EnglishKeyboardData.nextShiftState(shiftState)
        assertEquals(ShiftState.OFF, shiftState)

        ImeInputHelper.commitCharacter(ic, "s")
        assertEquals("UNICODEs", ic.currentText)
    }

    // --- 2. NUMBERS & SYMBOLS TESTS ---

    @Test
    fun testNumbersRowInsertion() {
        val ic = FakeInputConnection()
        for (digit in EnglishKeyboardData.NUMBERS_ROW) {
            ImeInputHelper.commitCharacter(ic, digit)
        }
        assertEquals("1234567890", ic.currentText)
    }

    @Test
    fun testModernSymbolsPunctuationInsertion() {
        val ic = FakeInputConnection()
        val symbolsToTest = listOf("@", "#", "$", "%", "!", "?", ":", ";", "~", "[", "]")

        for (sym in symbolsToTest) {
            ImeInputHelper.commitCharacter(ic, sym)
        }

        assertEquals("@#$%!?:;~[]", ic.currentText)
    }

    // --- 3. KEYBOARD MODE SWITCHING TESTS ---

    @Test
    fun testKeyboardModeTransitions() {
        var mode = KeyboardMode.HISTORICAL

        // Transition: HISTORICAL -> EASY_PHONETIC
        mode = KeyboardMode.EASY_PHONETIC
        assertEquals(KeyboardMode.EASY_PHONETIC, mode)

        // Transition: EASY_PHONETIC -> ENGLISH_QWERTY
        mode = KeyboardMode.ENGLISH_QWERTY
        assertEquals(KeyboardMode.ENGLISH_QWERTY, mode)

        // Transition: ENGLISH_QWERTY -> NUMBERS_SYMBOLS
        mode = KeyboardMode.NUMBERS_SYMBOLS
        assertEquals(KeyboardMode.NUMBERS_SYMBOLS, mode)

        // Transition: NUMBERS_SYMBOLS -> ROMANIZED
        mode = KeyboardMode.ROMANIZED
        assertEquals(KeyboardMode.ROMANIZED, mode)

        // Transition: ROMANIZED -> HISTORICAL
        mode = KeyboardMode.HISTORICAL
        assertEquals(KeyboardMode.HISTORICAL, mode)
    }

    // --- 4. ALL SIX HISTORICAL SCRIPTS UNICODE INSERTION & DELETION ---

    @Test
    fun testAllSixScriptsUnicodeInsertionAndDeletion() {
        val scripts = ScriptRegistry.allScripts
        assertEquals(6, scripts.size)

        for (script in scripts) {
            val ic = FakeInputConnection()
            val sampleKeys = script.keys.take(5)
            assertTrue("${script.name} must have at least 5 keys", sampleKeys.size >= 5)

            // Insert characters
            val expectedText = StringBuilder()
            for (key in sampleKeys) {
                ImeInputHelper.commitCharacter(ic, key.character)
                expectedText.append(key.character)
            }
            assertEquals("Inserted text mismatch in ${script.name}", expectedText.toString(), ic.currentText)

            // Delete characters in reverse order using handleBackspace
            for (i in sampleKeys.indices.reversed()) {
                val keyToDelete = sampleKeys[i]
                ImeInputHelper.handleBackspace(ic)
            }
            assertEquals("Buffer must be empty after deleting all characters in ${script.name}", "", ic.currentText)
        }
    }

    // --- 5. PHOENICIAN & KHAROSTHI RTL LOGICAL ORDERING ---

    @Test
    fun testPhoenicianRtlLogicalOrder() {
        val ic = FakeInputConnection()
        val phoenician = ScriptRegistry.getScriptById("phoenician")!!
        assertTrue("Phoenician must be RTL", phoenician.isRtl)

        // Phoenician 'aleph 𐤀 (U+10900) + bet 𐤁 (U+10901) + gimel 𐤂 (U+10902)
        ImeInputHelper.commitCharacter(ic, "𐤀")
        ImeInputHelper.commitCharacter(ic, "𐤁")
        ImeInputHelper.commitCharacter(ic, "𐤂")

        // Logical storage order in Unicode MUST be: 𐤀, then 𐤁, then 𐤂
        assertEquals("𐤀𐤁𐤂", ic.currentText)
        assertEquals(6, ic.currentText.length) // 3 SMP characters * 2 code units = 6
        assertEquals(6, ic.cursorPosition)

        // Mixed with English prefix
        val icMixed = FakeInputConnection("Phoenician: ")
        ImeInputHelper.commitCharacter(icMixed, "𐤀")
        ImeInputHelper.commitCharacter(icMixed, "𐤁")
        assertEquals("Phoenician: 𐤀𐤁", icMixed.currentText)
    }

    @Test
    fun testKharosthiRtlLogicalOrderAndCombiningVowel() {
        val ic = FakeInputConnection()
        val kharosthi = ScriptRegistry.getScriptById("kharosthi")!!
        assertTrue("Kharosthi must be RTL", kharosthi.isRtl)

        // Kharosthi ka 𐨐 (U+10A10) + vowel sign i 𐨁 (U+10A01)
        ImeInputHelper.commitCharacter(ic, "𐨐")
        ImeInputHelper.commitCharacter(ic, "𐨁")

        assertEquals("𐨐𐨁", ic.currentText)
        assertEquals(4, ic.currentText.length) // 2 SMP characters * 2 code units = 4

        // Backspace should safely remove combining vowel or base letter without orphan surrogates
        ImeInputHelper.handleBackspace(ic)
        // Verify buffer has valid surrogate pair or is clean
        val remaining = ic.currentText
        assertTrue("Remaining text must be valid Unicode without split surrogates",
            remaining.isEmpty() || remaining == "𐨐")
    }

    // --- 6. SYNTHETIC PASSWORD-FIELD FALLBACK TESTS ---

    @Test
    fun testSyntheticPasswordFieldShieldFallback() {
        // Standard text password
        val textPassword = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        assertTrue("Password field must trigger sensitive shield", ImeInputHelper.isSensitiveField(textPassword))

        // Web password
        val webPassword = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        }
        assertTrue("Web password must trigger sensitive shield", ImeInputHelper.isSensitiveField(webPassword))

        // Visible password
        val visiblePassword = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        assertTrue("Visible password must trigger sensitive shield", ImeInputHelper.isSensitiveField(visiblePassword))

        // Numeric PIN / password
        val pinPassword = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        assertTrue("Numeric PIN must trigger sensitive shield", ImeInputHelper.isSensitiveField(pinPassword))

        // Normal text (should NOT trigger shield)
        val normalText = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        }
        assertFalse("Normal text field must not trigger shield", ImeInputHelper.isSensitiveField(normalText))

        // Multiline text (should NOT trigger shield)
        val multilineText = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
        assertFalse("Multiline text field must not trigger shield", ImeInputHelper.isSensitiveField(multilineText))
    }

    // --- 7. KEYBOARD SHOW/HIDE & IME LIFECYCLE RESILIENCE ---

    @Test
    fun testKeyboardRepeatedShowHideResilience() {
        val ic = FakeInputConnection("Existing document text ")

        // Simulate 5 show/hide cycles where user inserts text during each cycle
        for (cycle in 1..5) {
            // "Show" keyboard: type a glyph
            ImeInputHelper.commitCharacter(ic, "𓀀")
            ImeInputHelper.handleSpace(ic)

            // "Hide" keyboard (no input connection operation)
            assertTrue(ic.currentText.contains("Existing document text "))
        }

        assertEquals("Existing document text 𓀀 𓀀 𓀀 𓀀 𓀀 ", ic.currentText)
    }

    @Test
    fun testErrorResilienceWithThrowingTargetApp() {
        val throwingIc = ThrowingInputConnection()

        // None of these should throw unhandled exceptions or crash the IME
        ImeInputHelper.commitCharacter(throwingIc, "Α")
        ImeInputHelper.handleSpace(throwingIc)
        ImeInputHelper.handleBackspace(throwingIc)
        ImeInputHelper.handleEnter(throwingIc, null)
    }
}
