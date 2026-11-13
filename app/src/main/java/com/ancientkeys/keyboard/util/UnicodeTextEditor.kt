package com.ancientkeys.keyboard.util

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import java.text.BreakIterator

/**
 * Safe Unicode text editing utility for historical scripts.
 * Correctly manages Supplementary Multilingual Plane (SMP) characters (surrogate pairs)
 * and combining diacritical marks (e.g. Brahmi and Kharosthi vowel signs).
 */
object UnicodeTextEditor {

    /**
     * Inserts [toInsert] at the current selection or cursor position.
     * If text is selected, the selected region is replaced.
     */
    fun insert(current: TextFieldValue, toInsert: String): TextFieldValue {
        val text = current.text
        val start = current.selection.min
        val end = current.selection.max

        val newText = buildString {
            append(text.substring(0, start))
            append(toInsert)
            append(text.substring(end))
        }
        val newCursor = start + toInsert.length
        return TextFieldValue(text = newText, selection = TextRange(newCursor))
    }

    /**
     * Safely deletes backward by one grapheme cluster or deletes the active selection.
     * Uses [BreakIterator.getCharacterInstance] to ensure that surrogate pairs (like
     * Egyptian Hieroglyphs, Phoenician, and Kharosthi) and combining marks (like Brahmi
     * vowel signs) are not split into corrupt surrogate code units.
     */
    fun deleteBackward(current: TextFieldValue): TextFieldValue {
        val text = current.text
        val start = current.selection.min
        val end = current.selection.max

        // If a selection exists, delete the selection
        if (start != end) {
            val newText = text.substring(0, start) + text.substring(end)
            return TextFieldValue(text = newText, selection = TextRange(start))
        }

        // If cursor is at the beginning, nothing to delete
        if (start <= 0 || text.isEmpty()) {
            return current
        }

        // Find the preceding grapheme cluster boundary
        val iterator = BreakIterator.getCharacterInstance()
        iterator.setText(text)
        val deleteFrom = iterator.preceding(start)

        val actualDeleteFrom = if (deleteFrom == BreakIterator.DONE || deleteFrom >= start) {
            // Safe fallback: check if previous 2 chars form a surrogate pair
            if (start >= 2 && Character.isSurrogatePair(text[start - 2], text[start - 1])) {
                start - 2
            } else {
                start - 1
            }
        } else {
            deleteFrom
        }

        val newText = text.substring(0, actualDeleteFrom) + text.substring(start)
        return TextFieldValue(text = newText, selection = TextRange(actualDeleteFrom))
    }

    /**
     * Inserts a standard whitespace character.
     */
    fun insertSpace(current: TextFieldValue): TextFieldValue {
        return insert(current, " ")
    }

    /**
     * Inserts a standard newline character at the current selection or cursor position.
     */
    fun insertNewline(current: TextFieldValue): TextFieldValue {
        return insert(current, "\n")
    }

    /**
     * Clears all text and resets cursor to start.
     */
    fun clear(): TextFieldValue {
        return TextFieldValue("", TextRange.Zero)
    }
}

