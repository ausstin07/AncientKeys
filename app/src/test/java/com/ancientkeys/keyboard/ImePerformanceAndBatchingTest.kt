package com.ancientkeys.keyboard

import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.CompletionInfo
import android.view.inputmethod.CorrectionInfo
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedText
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputContentInfo
import com.ancientkeys.keyboard.ime.ImeInputHelper
import com.ancientkeys.keyboard.ime.ui.RepeatBackspaceHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Performance, IPC Batching, and Responsiveness Unit Tests for AncientKeys IME v1.1.
 *
 * Verifies:
 * - Matched beginBatchEdit / endBatchEdit calls for all text mutations
 * - Repeat backspace IPC bypass of redundant getSelectedText calls
 * - Grapheme cluster deletion and SMP surrogate pair integrity within batch transactions
 * - Exception resilience ensuring endBatchEdit always executes even if target app throws
 * - Haptic cadence throttling to prevent physical motor thrashing
 */
class ImePerformanceAndBatchingTest {

    /**
     * Instrumented InputConnection tracking batch edit call counts and IPC metrics.
     */
    private open class TrackingInputConnection(
        initialText: String = "",
        initialCursor: Int = initialText.length
    ) : InputConnection {

        val buffer = StringBuilder(initialText)
        var cursorPosition: Int = initialCursor
        var selectionStart: Int = initialCursor
        var selectionEnd: Int = initialCursor

        var beginBatchCount = 0
        var endBatchCount = 0
        var getSelectedTextCount = 0
        var getTextBeforeCursorCount = 0
        var deleteSurroundingTextCount = 0
        var commitTextCount = 0

        val currentText: String
            get() = buffer.toString()

        val isBatchBalanced: Boolean
            get() = beginBatchCount == endBatchCount && beginBatchCount > 0

        override fun beginBatchEdit(): Boolean {
            beginBatchCount++
            return true
        }

        override fun endBatchEdit(): Boolean {
            endBatchCount++
            return true
        }

        override fun getSelectedText(flags: Int): CharSequence? {
            getSelectedTextCount++
            val min = minOf(selectionStart, selectionEnd)
            val max = maxOf(selectionStart, selectionEnd)
            return if (min != max) buffer.substring(min, max) else null
        }

        override fun getTextBeforeCursor(n: Int, flags: Int): CharSequence? {
            getTextBeforeCursorCount++
            val start = (cursorPosition - n).coerceAtLeast(0)
            return buffer.substring(start, cursorPosition)
        }

        override fun getTextAfterCursor(n: Int, flags: Int): CharSequence? {
            val end = (cursorPosition + n).coerceAtMost(buffer.length)
            return buffer.substring(cursorPosition, end)
        }

        override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
            commitTextCount++
            if (text == null) return false
            val min = minOf(selectionStart, selectionEnd)
            val max = maxOf(selectionStart, selectionEnd)
            buffer.replace(min, max, text.toString())
            cursorPosition = min + text.length
            selectionStart = cursorPosition
            selectionEnd = cursorPosition
            return true
        }

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            deleteSurroundingTextCount++
            val deleteStart = (cursorPosition - beforeLength).coerceAtLeast(0)
            val deleteEnd = (cursorPosition + afterLength).coerceAtMost(buffer.length)
            buffer.delete(deleteStart, deleteEnd)
            cursorPosition = deleteStart
            selectionStart = cursorPosition
            selectionEnd = cursorPosition
            return true
        }

        override fun performEditorAction(editorAction: Int): Boolean = true
        override fun sendKeyEvent(event: KeyEvent?): Boolean = true
        override fun reportFullscreenMode(enabled: Boolean): Boolean = false
        override fun getCursorCapsMode(reqModes: Int): Int = 0
        override fun getExtractedText(request: ExtractedTextRequest?, flags: Int): ExtractedText? = null
        override fun deleteSurroundingTextInCodePoints(beforeLength: Int, afterLength: Int): Boolean = false
        override fun setComposingText(text: CharSequence?, newCursorPosition: Int): Boolean = true
        override fun setComposingRegion(start: Int, end: Int): Boolean = true
        override fun finishComposingText(): Boolean = true
        override fun commitCompletion(text: CompletionInfo?): Boolean = true
        override fun commitCorrection(correctionInfo: CorrectionInfo?): Boolean = true
        override fun setSelection(start: Int, end: Int): Boolean {
            selectionStart = start
            selectionEnd = end
            cursorPosition = end
            return true
        }
        override fun performContextMenuAction(id: Int): Boolean = true
        override fun clearMetaKeyStates(states: Int): Boolean = true
        override fun performPrivateCommand(action: String?, data: Bundle?): Boolean = true
        override fun requestCursorUpdates(cursorUpdateMode: Int): Boolean = true
        override fun getHandler(): Handler? = null
        override fun closeConnection() {}
        override fun commitContent(inputContentInfo: InputContentInfo, flags: Int, opts: Bundle?): Boolean = false
    }

    private class ThrowingOnCommitInputConnection : TrackingInputConnection() {
        override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
            super.commitText(text, newCursorPosition)
            throw RuntimeException("Host app simulated exception on commitText")
        }
    }

    // --- BATCH EDITING TESTS ---

    @Test
    fun testCommitCharacterWrapsInBatchEdit() {
        val ic = TrackingInputConnection()
        ImeInputHelper.commitCharacter(ic, "a")

        assertEquals("a", ic.currentText)
        assertEquals(1, ic.beginBatchCount)
        assertEquals(1, ic.endBatchCount)
        assertTrue("Batch edit must be balanced", ic.isBatchBalanced)
    }

    @Test
    fun testHandleSpaceWrapsInBatchEdit() {
        val ic = TrackingInputConnection("word", 4)
        ImeInputHelper.handleSpace(ic)

        assertEquals("word ", ic.currentText)
        assertEquals(1, ic.beginBatchCount)
        assertEquals(1, ic.endBatchCount)
        assertTrue("Batch edit must be balanced", ic.isBatchBalanced)
    }

    @Test
    fun testHandleBackspaceWrapsInBatchEdit() {
        val ic = TrackingInputConnection("hello", 5)
        ImeInputHelper.handleBackspace(ic, isRepeating = false)

        assertEquals("hell", ic.currentText)
        assertEquals(1, ic.beginBatchCount)
        assertEquals(1, ic.endBatchCount)
        assertTrue("Batch edit must be balanced", ic.isBatchBalanced)
    }

    @Test
    fun testHandleEnterWrapsInBatchEdit() {
        val ic = TrackingInputConnection()
        val editorInfo = EditorInfo().apply {
            imeOptions = EditorInfo.IME_ACTION_NONE
        }
        ImeInputHelper.handleEnter(ic, editorInfo)

        assertEquals("\n", ic.currentText)
        assertEquals(1, ic.beginBatchCount)
        assertEquals(1, ic.endBatchCount)
        assertTrue("Batch edit must be balanced", ic.isBatchBalanced)
    }

    // --- REPEAT BACKSPACE IPC BYPASS TESTS ---

    @Test
    fun testSingleBackspaceChecksSelection() {
        val ic = TrackingInputConnection("sample", 6)
        ImeInputHelper.handleBackspace(ic, isRepeating = false)

        assertEquals(1, ic.getSelectedTextCount)
        assertEquals("sampl", ic.currentText)
    }

    @Test
    fun testRepeatingBackspaceBypassesGetSelectedTextIPC() {
        val ic = TrackingInputConnection("continuous_deletion", 19)

        // Simulate 5 repeated backspace pulses
        for (i in 1..5) {
            ImeInputHelper.handleBackspace(ic, isRepeating = true)
        }

        // Repeating backspace must NOT invoke getSelectedText (0 IPC calls to getSelectedText)
        assertEquals(0, ic.getSelectedTextCount)
        // 5 characters should have been cleanly deleted
        assertEquals("continuous_del", ic.currentText)
        // Batch edits must be balanced for all 5 pulses
        assertEquals(5, ic.beginBatchCount)
        assertEquals(5, ic.endBatchCount)
    }

    // --- SMP & DIACRITIC UNICODE DELETION WITHIN BATCH EDIT ---

    @Test
    fun testSMPHieroglyphBatchBackspacePreservesSurrogates() {
        // Egyptian Hieroglyphic phrase: 𓀀𓁐 (2 SMP glyphs, 4 UTF-16 code units)
        val ic = TrackingInputConnection("𓀀𓁐", 4)
        ImeInputHelper.handleBackspace(ic, isRepeating = false)

        assertEquals("𓀀", ic.currentText)
        assertEquals(2, ic.currentText.length)
        assertTrue("Must be balanced batch edit", ic.isBatchBalanced)

        // Second backspace deletes remaining SMP glyph
        ImeInputHelper.handleBackspace(ic, isRepeating = true)
        assertEquals("", ic.currentText)
        assertEquals(2, ic.beginBatchCount)
        assertEquals(2, ic.endBatchCount)
    }

    // --- EXCEPTION RESILIENCE & FINALLY EXECUTION ---

    @Test
    fun testEndBatchEditExecutedEvenIfHostThrows() {
        val ic = ThrowingOnCommitInputConnection()
        // Host app throws inside commitText, but ImeInputHelper must silently handle it
        ImeInputHelper.commitCharacter(ic, "x")

        // endBatchEdit must have been reliably called in finally block
        assertEquals(1, ic.beginBatchCount)
        assertEquals(1, ic.endBatchCount)
        assertTrue("Batch edit must balance even upon exceptions", ic.isBatchBalanced)
    }

    // --- HAPTIC THROTTLING TIMING CHECKS ---

    @Test
    fun testHapticPulseCadenceConstants() {
        // Verifies cadence interval prevents vibrator motor swamp
        assertEquals(3, RepeatBackspaceHelper.HAPTIC_PULSE_CADENCE)
        val initialDelay = RepeatBackspaceHelper.INITIAL_DELAY_MS
        val repeatInterval = RepeatBackspaceHelper.REPEAT_INTERVAL_MS
        assertEquals(400L, initialDelay)
        assertEquals(50L, repeatInterval)

        // At cadence 3, haptic pulses occur roughly every 150ms (50ms * 3), matching phone motor response
        val pulseInterval = repeatInterval * RepeatBackspaceHelper.HAPTIC_PULSE_CADENCE
        assertTrue("Haptic pulse interval ($pulseInterval ms) must be between 100ms and 200ms", pulseInterval in 100L..200L)
    }
}

