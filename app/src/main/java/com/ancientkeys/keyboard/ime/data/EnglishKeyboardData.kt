package com.ancientkeys.keyboard.ime.data

/**
 * Key definitions and layout rows for the English QWERTY keyboard and modern numbers/symbols panels.
 */
object EnglishKeyboardData {

    val QWERTY_ROW_1_LOWER = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val QWERTY_ROW_2_LOWER = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val QWERTY_ROW_3_LOWER = listOf("z", "x", "c", "v", "b", "n", "m")

    val QWERTY_ROW_1_UPPER = QWERTY_ROW_1_LOWER.map { it.uppercase() }
    val QWERTY_ROW_2_UPPER = QWERTY_ROW_2_LOWER.map { it.uppercase() }
    val QWERTY_ROW_3_UPPER = QWERTY_ROW_3_LOWER.map { it.uppercase() }

    // Numbers & Modern Symbols
    val NUMBERS_ROW = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    val SYMBOLS_ROW_1 = listOf("@", "#", "$", "%", "&", "*", "-", "+", "(", ")")
    val SYMBOLS_ROW_2 = listOf("!", "?", "\"", "'", ":", ";", "/", "\\", "=", "_")
    val SYMBOLS_ROW_3 = listOf("~", "`", "|", "^", "{", "}", "[", "]", "<", ">")

    /**
     * Computes the next [ShiftState] when the Shift key is tapped.
     * Single tap: OFF -> ON_ONCE
     * Tap while ON_ONCE: ON_ONCE -> CAPS_LOCK
     * Tap while CAPS_LOCK: CAPS_LOCK -> OFF
     */
    fun nextShiftState(current: ShiftState): ShiftState {
        return when (current) {
            ShiftState.OFF -> ShiftState.ON_ONCE
            ShiftState.ON_ONCE -> ShiftState.CAPS_LOCK
            ShiftState.CAPS_LOCK -> ShiftState.OFF
        }
    }

    /**
     * Determines whether the active shift state requires uppercase letter output.
     */
    fun isUppercase(state: ShiftState): Boolean {
        return state == ShiftState.ON_ONCE || state == ShiftState.CAPS_LOCK
    }
}
