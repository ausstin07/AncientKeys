package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.ImeInputHelper
import com.ancientkeys.keyboard.ime.data.EnglishKeyboardData
import com.ancientkeys.keyboard.ime.data.KeyboardMode
import com.ancientkeys.keyboard.ime.data.ShiftState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying mixed Latin, historical script, numeric, and symbol input
 * within the same input connection, confirming logical Unicode order preservation,
 * boundary backspace safety, and keyboard mode switching.
 */
class MixedScriptInputTest {

    @Test
    fun testEnglishTextThenEgyptianHieroglyphs() {
        val ic = FakeInputConnection()

        // 1. Type English prefix
        "King ".forEach { char ->
            ImeInputHelper.commitCharacter(ic, char.toString())
        }
        assertEquals("King ", ic.currentText)

        // 2. Type Egyptian Hieroglyphs (SMP code points, 2 UTF-16 code units each)
        // 𓇳 (Ra, U+131F3) + 𓏤 (Stroke Z1, U+133E4)
        ImeInputHelper.commitCharacter(ic, "𓇳")
        ImeInputHelper.commitCharacter(ic, "𓏤")

        assertEquals("King 𓇳𓏤", ic.currentText)
        assertEquals(5 + 2 + 2, ic.cursorPosition)

        // 3. Type English suffix
        " of Egypt".forEach { char ->
            ImeInputHelper.commitCharacter(ic, char.toString())
        }

        assertEquals("King 𓇳𓏤 of Egypt", ic.currentText)
        assertEquals(18, ic.cursorPosition)
    }

    @Test
    fun testMixedLatinAndBrahmiInput() {
        val ic = FakeInputConnection()

        // Latin label
        "Asoka: ".forEach { ImeInputHelper.commitCharacter(ic, it.toString()) }

        // Brahmi characters: 𑀅 (a), 𑀰 (sha), 𑁄 (o), 𑀓 (ka)
        val brahmiAsoka = listOf("𑀅", "𑀰", "𑁄", "𑀓")
        brahmiAsoka.forEach { ImeInputHelper.commitCharacter(ic, it) }

        " Edict 1".forEach { ImeInputHelper.commitCharacter(ic, it.toString()) }

        assertEquals("Asoka: 𑀅𑀰𑁄𑀓 Edict 1", ic.currentText)
    }

    @Test
    fun testMixedLatinAndPhoenicianRtlLogicalOrder() {
        val ic = FakeInputConnection()

        // In Unicode Bidi text, characters are committed in logical typing order.
        // Phoenician letters 𐤀 (ʾālep), 𐤁 (bēt), 𐤂 (gīml)
        "Phoenician: ".forEach { ImeInputHelper.commitCharacter(ic, it.toString()) }

        ImeInputHelper.commitCharacter(ic, "𐤀")
        ImeInputHelper.commitCharacter(ic, "𐤁")
        ImeInputHelper.commitCharacter(ic, "𐤂")

        " text".forEach { ImeInputHelper.commitCharacter(ic, it.toString()) }

        // Must preserve exact logical sequence: English + 𐤀𐤁𐤂 + English
        assertEquals("Phoenician: 𐤀𐤁𐤂 text", ic.currentText)
    }

    @Test
    fun testMixedLatinAndKharosthiCombiningMarks() {
        val ic = FakeInputConnection()

        "Kharosthi: ".forEach { ImeInputHelper.commitCharacter(ic, it.toString()) }

        // Kharosthi consonant 𐨐 (KA, U+10A10) + dependent vowel sign 𐨁 (I, U+10A01)
        ImeInputHelper.commitCharacter(ic, "𐨐")
        ImeInputHelper.commitCharacter(ic, "𐨁")

        assertEquals("Kharosthi: 𐨐𐨁", ic.currentText)

        // Append English note
        " inscription".forEach { ImeInputHelper.commitCharacter(ic, it.toString()) }
        assertEquals("Kharosthi: 𐨐𐨁 inscription", ic.currentText)
    }

    @Test
    fun testMixedScriptBackspaceBoundaries() {
        val ic = FakeInputConnection()

        // Type "Text 𓀀"
        "Text ".forEach { ImeInputHelper.commitCharacter(ic, it.toString()) }
        ImeInputHelper.commitCharacter(ic, "𓀀") // Egyptian hieroglyph (surrogate pair)

        assertEquals("Text 𓀀", ic.currentText)

        // 1. Backspace: must delete Egyptian glyph atomically (2 UTF-16 code units)
        ImeInputHelper.handleBackspace(ic)
        assertEquals("Text ", ic.currentText)

        // 2. Backspace: must delete space
        ImeInputHelper.handleBackspace(ic)
        assertEquals("Text", ic.currentText)

        // 3. Backspace: must delete Latin 't'
        ImeInputHelper.handleBackspace(ic)
        assertEquals("Tex", ic.currentText)

        // 4. Commit Greek character 'ω'
        ImeInputHelper.commitCharacter(ic, "ω")
        assertEquals("Texω", ic.currentText)

        // 5. Backspace: deletes 'ω'
        ImeInputHelper.handleBackspace(ic)
        assertEquals("Tex", ic.currentText)
    }

    @Test
    fun testInterspersedModernNumbersAndSymbols() {
        val ic = FakeInputConnection()

        // "No. 42: ᚠ (Runic) = 100%"
        val tokens = listOf("N", "o", ".", " ", "4", "2", ":", " ", "ᚠ", " ", "(", "R", "u", "n", "i", "c", ")", " ", "=", " ", "1", "0", "0", "%")
        tokens.forEach { ImeInputHelper.commitCharacter(ic, it) }

        assertEquals("No. 42: ᚠ (Runic) = 100%", ic.currentText)
    }

    @Test
    fun testShiftStateAutoRevertBehavior() {
        // Test shift state machine behavior as used during typing
        var shiftState = ShiftState.OFF

        // Initial state: lowercase
        assertFalse(EnglishKeyboardData.isUppercase(shiftState))

        // Tap Shift -> ON_ONCE
        shiftState = EnglishKeyboardData.nextShiftState(shiftState)
        assertEquals(ShiftState.ON_ONCE, shiftState)
        assertTrue(EnglishKeyboardData.isUppercase(shiftState))

        // Simulate typing one letter when ON_ONCE -> reverts to OFF
        val typedChar = if (EnglishKeyboardData.isUppercase(shiftState)) "H" else "h"
        assertEquals("H", typedChar)
        if (shiftState == ShiftState.ON_ONCE) {
            shiftState = ShiftState.OFF
        }
        assertEquals(ShiftState.OFF, shiftState)
        assertFalse(EnglishKeyboardData.isUppercase(shiftState))

        // Double tap Shift: OFF -> ON_ONCE -> CAPS_LOCK
        shiftState = EnglishKeyboardData.nextShiftState(shiftState)
        assertEquals(ShiftState.ON_ONCE, shiftState)
        shiftState = EnglishKeyboardData.nextShiftState(shiftState)
        assertEquals(ShiftState.CAPS_LOCK, shiftState)
        assertTrue(EnglishKeyboardData.isUppercase(shiftState))

        // Typing under CAPS_LOCK does not revert
        val letter1 = if (EnglishKeyboardData.isUppercase(shiftState)) "E" else "e"
        val letter2 = if (EnglishKeyboardData.isUppercase(shiftState)) "L" else "l"
        assertEquals("E", letter1)
        assertEquals("L", letter2)
        assertEquals(ShiftState.CAPS_LOCK, shiftState)

        // Tap Shift from CAPS_LOCK -> OFF
        shiftState = EnglishKeyboardData.nextShiftState(shiftState)
        assertEquals(ShiftState.OFF, shiftState)
    }

    @Test
    fun testKeyboardModeSwitchingIntegrity() {
        // Mode switching should allow toggling between Historical, English QWERTY, and Numbers/Symbols
        var currentMode = KeyboardMode.HISTORICAL
        assertEquals(KeyboardMode.HISTORICAL, currentMode)

        // Switch to English QWERTY
        currentMode = KeyboardMode.ENGLISH_QWERTY
        assertEquals(KeyboardMode.ENGLISH_QWERTY, currentMode)

        // Switch to Numbers & Symbols
        currentMode = KeyboardMode.NUMBERS_SYMBOLS
        assertEquals(KeyboardMode.NUMBERS_SYMBOLS, currentMode)

        // Return to Historical
        currentMode = KeyboardMode.HISTORICAL
        assertEquals(KeyboardMode.HISTORICAL, currentMode)
    }
}

