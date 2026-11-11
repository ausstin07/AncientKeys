package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.data.EnglishKeyboardData
import com.ancientkeys.keyboard.ime.data.KeyboardMode
import com.ancientkeys.keyboard.ime.data.ShiftState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EnglishKeyboardTest {

    @Test
    fun testQwertyLayoutCompleteness() {
        // 26 English letters in 3 rows
        assertEquals(10, EnglishKeyboardData.QWERTY_ROW_1_LOWER.size)
        assertEquals(9, EnglishKeyboardData.QWERTY_ROW_2_LOWER.size)
        assertEquals(7, EnglishKeyboardData.QWERTY_ROW_3_LOWER.size)

        val allLower = EnglishKeyboardData.QWERTY_ROW_1_LOWER +
                EnglishKeyboardData.QWERTY_ROW_2_LOWER +
                EnglishKeyboardData.QWERTY_ROW_3_LOWER
        assertEquals(26, allLower.size)
        assertEquals(26, allLower.toSet().size) // All distinct

        // Uppercase matches lowercase
        val allUpper = EnglishKeyboardData.QWERTY_ROW_1_UPPER +
                EnglishKeyboardData.QWERTY_ROW_2_UPPER +
                EnglishKeyboardData.QWERTY_ROW_3_UPPER
        assertEquals(26, allUpper.size)
        for (i in allLower.indices) {
            assertEquals(allLower[i].uppercase(), allUpper[i])
        }
    }

    @Test
    fun testShiftStateMachineTransitions() {
        var state = ShiftState.OFF
        assertFalse("OFF should be lowercase", EnglishKeyboardData.isUppercase(state))

        // Single tap: OFF -> ON_ONCE
        state = EnglishKeyboardData.nextShiftState(state)
        assertEquals(ShiftState.ON_ONCE, state)
        assertTrue("ON_ONCE should be uppercase", EnglishKeyboardData.isUppercase(state))

        // Second tap: ON_ONCE -> CAPS_LOCK
        state = EnglishKeyboardData.nextShiftState(state)
        assertEquals(ShiftState.CAPS_LOCK, state)
        assertTrue("CAPS_LOCK should be uppercase", EnglishKeyboardData.isUppercase(state))

        // Third tap: CAPS_LOCK -> OFF
        state = EnglishKeyboardData.nextShiftState(state)
        assertEquals(ShiftState.OFF, state)
        assertFalse("OFF should be lowercase", EnglishKeyboardData.isUppercase(state))
    }

    @Test
    fun testNumeralsRowHasAllDigits() {
        val digits = EnglishKeyboardData.NUMBERS_ROW
        assertEquals(10, digits.size)
        assertEquals(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"), digits)
    }

    @Test
    fun testModernSymbolsCompleteness() {
        val sym1 = EnglishKeyboardData.SYMBOLS_ROW_1
        val sym2 = EnglishKeyboardData.SYMBOLS_ROW_2
        val sym3 = EnglishKeyboardData.SYMBOLS_ROW_3

        assertTrue("Row 1 symbols must have standard punctuation", sym1.contains("@") && sym1.contains("#") && sym1.contains("$"))
        assertTrue("Row 2 symbols must have standard punctuation", sym2.contains("!") && sym2.contains("?") && sym2.contains(":"))
        assertTrue("Row 3 symbols must have extended symbols", sym3.contains("~") && sym3.contains("[") && sym3.contains("]"))
    }

    @Test
    fun testKeyboardModesExist() {
        val modes = KeyboardMode.values()
        assertTrue(modes.contains(KeyboardMode.HISTORICAL))
        assertTrue(modes.contains(KeyboardMode.ENGLISH_QWERTY))
        assertTrue(modes.contains(KeyboardMode.NUMBERS_SYMBOLS))
    }
}

