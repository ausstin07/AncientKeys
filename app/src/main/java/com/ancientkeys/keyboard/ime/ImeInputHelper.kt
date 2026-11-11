package com.ancientkeys.keyboard.ime

import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import java.text.BreakIterator

/**
 * Handles communication with the Android [InputConnection] for the AncientKeys IME.
 *
 * Implements safe Unicode code point and grapheme cluster insertion and deletion,
 * ensuring Supplementary Multilingual Plane (SMP) surrogate pairs and combining
 * marks are never split or corrupted.
 *
 * Also provides detection for sensitive/password input fields to prevent accidental
 * entry of passwords in historical scripts.
 */
object ImeInputHelper {

    /**
     * Determines whether the given [EditorInfo] represents a password or sensitive input field.
     * Checks text passwords, visible passwords, web passwords, and numeric PINs/passwords.
     */
    fun isSensitiveField(editorInfo: EditorInfo?): Boolean {
        if (editorInfo == null) return false
        val inputType = editorInfo.inputType
        val inputClass = inputType and InputType.TYPE_MASK_CLASS
        val variation = inputType and InputType.TYPE_MASK_VARIATION

        return when (inputClass) {
            InputType.TYPE_CLASS_TEXT -> {
                variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
            }
            InputType.TYPE_CLASS_NUMBER -> {
                variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD
            }
            else -> false
        }
    }

    /**
     * Commits a Unicode character sequence to the target application.
     * Note: In Unicode and Android text handling, text is always committed in logical
     * order. Right-to-Left (RTL) scripts (e.g. Phoenician, Kharosthi) are rendered RTL
     * automatically by the system text engine based on their Unicode character properties;
     * character order must never be reversed.
     */
    fun commitCharacter(inputConnection: InputConnection?, character: String) {
        if (inputConnection == null || character.isEmpty()) return
        try {
            inputConnection.commitText(character, 1)
        } catch (_: Exception) {
            // Silently swallow exceptions from misbehaving third-party target applications
        }
    }

    /**
     * Inserts a standard whitespace character.
     */
    fun handleSpace(inputConnection: InputConnection?) {
        if (inputConnection == null) return
        try {
            inputConnection.commitText(" ", 1)
        } catch (_: Exception) {
        }
    }

    /**
     * Safely deletes text backward by one grapheme cluster or deletes the active selection.
     *
     * Correctly handles:
     * - Active text selection (deletes the selected region)
     * - SMP supplementary code points (surrogate pairs taking 2 UTF-16 chars)
     * - Combining diacritical marks (e.g. Brahmi / Kharosthi vowel signs)
     * - Empty input / cursor at position 0 (safe no-op)
     * - Truncated or unusual input connection responses
     */
    fun handleBackspace(inputConnection: InputConnection?) {
        if (inputConnection == null) return

        try {
            // 1. If text is currently selected, replace the selection with an empty string
            val selectedText = inputConnection.getSelectedText(0)
            if (!selectedText.isNullOrEmpty()) {
                inputConnection.commitText("", 1)
                return
            }

            // 2. Inspect up to 16 characters before cursor to accurately detect grapheme clusters
            val textBefore = inputConnection.getTextBeforeCursor(16, 0)?.toString()

            if (textBefore != null) {
                if (textBefore.isEmpty()) {
                    // Cursor is explicitly at position 0; nothing before cursor to delete
                    return
                }

                val iterator = BreakIterator.getCharacterInstance()
                iterator.setText(textBefore)
                val prev = iterator.preceding(textBefore.length)

                var charsToDelete = if (prev != BreakIterator.DONE && prev < textBefore.length) {
                    textBefore.length - prev
                } else if (textBefore.length >= 2 &&
                    Character.isSurrogatePair(textBefore[textBefore.length - 2], textBefore.last())
                ) {
                    2
                } else {
                    1
                }

                // Extra safety: ensure we do not leave an un-paired high surrogate before deletion point
                val remainingLen = textBefore.length - charsToDelete
                if (remainingLen > 0 && Character.isHighSurrogate(textBefore[remainingLen - 1])) {
                    charsToDelete++
                }

                inputConnection.deleteSurroundingText(charsToDelete, 0)
            } else {
                // Fallback for custom editors that return null for getTextBeforeCursor
                inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            }
        } catch (_: Exception) {
            // Prevent crashes caused by third-party apps throwing unexpected runtime exceptions
        }
    }

    /**
     * Handles the Enter / Action key based on the target editor's [EditorInfo.imeOptions].
     */
    fun handleEnter(inputConnection: InputConnection?, editorInfo: EditorInfo?) {
        if (inputConnection == null) return
        try {
            val imeOptions = editorInfo?.imeOptions ?: 0
            val action = imeOptions and EditorInfo.IME_MASK_ACTION
            val noEnterAction = (imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0

            if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED && !noEnterAction) {
                inputConnection.performEditorAction(action)
            } else {
                inputConnection.commitText("\n", 1)
            }
        } catch (_: Exception) {
        }
    }

    /**
     * Updates the active composing span in the target editor without finalizing text.
     * Used for live phonetic typing feedback.
     */
    fun setComposingText(inputConnection: InputConnection?, text: CharSequence, newCursorPosition: Int = 1) {
        if (inputConnection == null) return
        try {
            inputConnection.setComposingText(text, newCursorPosition)
        } catch (_: Exception) {
        }
    }

    /**
     * Finishes composing text, cementing whatever text is currently inside the composing span.
     */
    fun finishComposingText(inputConnection: InputConnection?) {
        if (inputConnection == null) return
        try {
            inputConnection.finishComposingText()
        } catch (_: Exception) {
        }
    }

    /**
     * Commits a resolved composition candidate and terminates the composing span.
     */
    fun commitComposingText(inputConnection: InputConnection?, text: String) {
        if (inputConnection == null) return
        try {
            inputConnection.commitText(text, 1)
        } catch (_: Exception) {
        }
    }

    /**
     * Safely cancels any active composing span, reverting or clearing uncommitted composing text.
     */
    fun cancelComposingText(inputConnection: InputConnection?) {
        if (inputConnection == null) return
        try {
            inputConnection.setComposingText("", 1)
            inputConnection.finishComposingText()
        } catch (_: Exception) {
        }
    }
}
