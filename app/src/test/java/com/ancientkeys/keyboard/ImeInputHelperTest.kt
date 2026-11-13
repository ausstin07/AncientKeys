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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying safe Unicode insertion, deletion, sensitive field detection,
 * and error resilience across SMP code points, combining marks, and RTL scripts via [ImeInputHelper].
 */
class ImeInputHelperTest {

    // --- SENSITIVE / PASSWORD FIELD DETECTION TESTS ---

    @Test
    fun testSensitiveFieldDetection() {
        // Standard text password
        val textPassword = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        assertTrue("TYPE_TEXT_VARIATION_PASSWORD must be recognized as sensitive", ImeInputHelper.isSensitiveField(textPassword))

        // Visible password
        val visiblePassword = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        assertTrue("TYPE_TEXT_VARIATION_VISIBLE_PASSWORD must be recognized as sensitive", ImeInputHelper.isSensitiveField(visiblePassword))

        // Web password
        val webPassword = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        }
        assertTrue("TYPE_TEXT_VARIATION_WEB_PASSWORD must be recognized as sensitive", ImeInputHelper.isSensitiveField(webPassword))

        // Numeric password / PIN
        val numberPassword = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        assertTrue("TYPE_NUMBER_VARIATION_PASSWORD must be recognized as sensitive", ImeInputHelper.isSensitiveField(numberPassword))

        // Standard text (not sensitive)
        val normalText = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        }
        assertFalse("Normal text must not be recognized as sensitive", ImeInputHelper.isSensitiveField(normalText))

        // Email address (not password)
        val email = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        assertFalse("Email field must not be treated as password", ImeInputHelper.isSensitiveField(email))

        // Null EditorInfo
        assertFalse("Null EditorInfo must return false safely", ImeInputHelper.isSensitiveField(null))
    }

    // --- UNICODE COMMIT TESTS ---

    @Test
    fun testCommitSingleCharacter() {
        val ic = FakeInputConnection()
        ImeInputHelper.commitCharacter(ic, "Α") // Greek Alpha

        assertEquals("Α", ic.currentText)
        assertEquals(1, ic.cursorPosition)
    }

    @Test
    fun testCommitSMPCharacter() {
        val ic = FakeInputConnection()
        // Egyptian Hieroglyph 𓀀 (U+13000, 2 UTF-16 code units)
        ImeInputHelper.commitCharacter(ic, "𓀀")

        assertEquals("𓀀", ic.currentText)
        assertEquals(2, ic.currentText.length)
        assertEquals(2, ic.cursorPosition)
    }

    @Test
    fun testRtlScriptLogicalOrderPreservation() {
        val ic = FakeInputConnection()
        // Phoenician 𐤀 (ʾālep) followed by 𐤁 (bēt)
        ImeInputHelper.commitCharacter(ic, "𐤀")
        ImeInputHelper.commitCharacter(ic, "𐤁")

        // In Unicode and Android text engines, RTL characters must be stored in
        // logical typing order (𐤀 then 𐤁). Android Bidi layout displays them RTL.
        assertEquals("𐤀𐤁", ic.currentText)
        assertEquals(4, ic.cursorPosition)
    }

    @Test
    fun testHandleSpace() {
        val ic = FakeInputConnection("𐨀", 2)
        ImeInputHelper.handleSpace(ic)

        assertEquals("𐨀 ", ic.currentText)
        assertEquals(3, ic.cursorPosition)
    }

    // --- SAFE BACKSPACE & DELETION TESTS ---

    @Test
    fun testBackspaceStandard() {
        val ic = FakeInputConnection("AB", 2)
        ImeInputHelper.handleBackspace(ic)

        assertEquals("A", ic.currentText)
        assertEquals(1, ic.cursorPosition)
    }

    @Test
    fun testBackspaceSMPCharacterRemovesBothSurrogates() {
        // "𓀀𓁐" has 4 UTF-16 code units (two 2-unit SMP characters)
        val ic = FakeInputConnection("𓀀𓁐", 4)
        ImeInputHelper.handleBackspace(ic)

        assertEquals("𓀀", ic.currentText)
        assertEquals(2, ic.currentText.length)
        assertEquals(2, ic.cursorPosition)

        // Verify remaining text has no orphan/severed surrogates
        for (ch in ic.currentText) {
            assertFalse(Character.isHighSurrogate(ch) && ic.currentText.length == 1)
        }
    }

    @Test
    fun testBackspaceCombiningDiacritic() {
        // Brahmi KA (𑀓) + Vowel Sign AA (𑀸)
        val text = "𑀓\uD804\uDC38"
        val ic = FakeInputConnection(text, text.length)
        ImeInputHelper.handleBackspace(ic)

        // Must safely delete without splitting surrogate pairs
        for (i in 0 until ic.currentText.length) {
            val ch = ic.currentText[i]
            if (Character.isHighSurrogate(ch)) {
                assertTrue("High surrogate must have trailing low surrogate",
                    i + 1 < ic.currentText.length && Character.isLowSurrogate(ic.currentText[i + 1]))
            }
        }
    }

    @Test
    fun testBackspaceWithSelection() {
        val ic = FakeInputConnection("ABCD", 3)
        ic.selectionStart = 1
        ic.selectionEnd = 3 // Selecting "BC"

        ImeInputHelper.handleBackspace(ic)

        assertEquals("AD", ic.currentText)
        assertEquals(1, ic.cursorPosition)
    }

    @Test
    fun testBackspaceAtStartWithEmptyTextIsSafe() {
        val ic = FakeInputConnection("", 0)
        ImeInputHelper.handleBackspace(ic)

        assertEquals("", ic.currentText)
        assertEquals(0, ic.cursorPosition)
    }

    @Test
    fun testBackspaceAtStartWithNonEmptyTextIsSafe() {
        val ic = FakeInputConnection("𓀀", 0)
        ImeInputHelper.handleBackspace(ic)

        // When cursor is at index 0, text before cursor is empty; backspace must not delete forward text
        assertEquals("𓀀", ic.currentText)
        assertEquals(0, ic.cursorPosition)
    }

    @Test
    fun testBackspaceFallbackWhenGetTextBeforeCursorReturnsNull() {
        // Simulate a custom editor that returns null for getTextBeforeCursor
        val ic = NullTextBeforeInputConnection("ABC", 3)
        ImeInputHelper.handleBackspace(ic)

        // Should trigger fallback sendKeyEvent
        assertEquals("AB", ic.currentText)
        assertEquals(2, ic.cursorPosition)
    }

    @Test
    fun testInputConnectionExceptionResilience() {
        // A buggy target app connection that throws RuntimeExceptions must not crash AncientKeys
        val throwingIc = ThrowingInputConnection()
        ImeInputHelper.commitCharacter(throwingIc, "𓀀")
        ImeInputHelper.handleSpace(throwingIc)
        ImeInputHelper.handleBackspace(throwingIc)
        ImeInputHelper.handleEnter(throwingIc, null)
        // If execution reaches here without unhandled exception, test passes
        assertTrue(true)
    }

    // --- ENTER & ACTION TESTS ---

    @Test
    fun testEnterAction() {
        val ic = FakeInputConnection("test", 4)
        val editorInfo = EditorInfo().apply {
            imeOptions = EditorInfo.IME_ACTION_SEARCH
        }
        ImeInputHelper.handleEnter(ic, editorInfo)

        assertEquals(EditorInfo.IME_ACTION_SEARCH, ic.lastPerformedAction)
    }

    @Test
    fun testAllImeActionsAndDisplayInfo() {
        // Search
        val searchInfo = ImeInputHelper.getImeActionInfo(EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_SEARCH })
        assertEquals(EditorInfo.IME_ACTION_SEARCH, searchInfo.actionId)
        assertEquals("Search 🔍", searchInfo.displayLabel)
        assertEquals("🔍", searchInfo.compactLabel)
        assertFalse(searchInfo.isEnterNewline)

        // Go
        val goInfo = ImeInputHelper.getImeActionInfo(EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_GO })
        assertEquals(EditorInfo.IME_ACTION_GO, goInfo.actionId)
        assertEquals("Go ➔", goInfo.displayLabel)
        assertEquals("➔", goInfo.compactLabel)
        assertFalse(goInfo.isEnterNewline)

        // Send
        val sendInfo = ImeInputHelper.getImeActionInfo(EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_SEND })
        assertEquals(EditorInfo.IME_ACTION_SEND, sendInfo.actionId)
        assertEquals("Send ➤", sendInfo.displayLabel)
        assertFalse(sendInfo.isEnterNewline)

        // Next
        val nextInfo = ImeInputHelper.getImeActionInfo(EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_NEXT })
        assertEquals(EditorInfo.IME_ACTION_NEXT, nextInfo.actionId)
        assertEquals("Next ➔", nextInfo.displayLabel)
        assertFalse(nextInfo.isEnterNewline)

        // Done
        val doneInfo = ImeInputHelper.getImeActionInfo(EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_DONE })
        assertEquals(EditorInfo.IME_ACTION_DONE, doneInfo.actionId)
        assertEquals("Done ✓", doneInfo.displayLabel)
        assertFalse(doneInfo.isEnterNewline)

        // Custom action ID & label
        val customInfo = ImeInputHelper.getImeActionInfo(EditorInfo().apply {
            actionId = 42
            actionLabel = "Custom"
        })
        assertEquals(42, customInfo.actionId)
        assertEquals("Custom", customInfo.displayLabel)
        assertFalse(customInfo.isEnterNewline)

        // IME_FLAG_NO_ENTER_ACTION
        val noEnterActionInfo = ImeInputHelper.getImeActionInfo(EditorInfo().apply {
            imeOptions = EditorInfo.IME_ACTION_SEARCH or EditorInfo.IME_FLAG_NO_ENTER_ACTION
        })
        assertTrue(noEnterActionInfo.isEnterNewline)
    }

    @Test
    fun testActionFallbackToSendDefaultActionAndKeyEvents() {
        // Test connection that refuses performEditorAction
        var sendDefaultCalled = false
        var downUpKeyCalled = false
        var lastKeyCode = -1

        val refusingIc = object : FakeInputConnection("search query", 12) {
            override fun performEditorAction(editorAction: Int): Boolean = false
        }

        val editorInfo = EditorInfo().apply {
            imeOptions = EditorInfo.IME_ACTION_SEARCH
        }

        ImeInputHelper.handleEnter(
            refusingIc,
            editorInfo,
            sendDefaultAction = {
                sendDefaultCalled = true
                false // Simulate sendDefaultEditorAction also returning false
            },
            sendDownUpKey = { keyCode ->
                downUpKeyCalled = true
                lastKeyCode = keyCode
            }
        )

        assertTrue("Must fallback to sendDefaultEditorAction", sendDefaultCalled)
        assertTrue("Must fallback to sending Enter key event when host refuses action", downUpKeyCalled)
        assertEquals(KeyEvent.KEYCODE_ENTER, lastKeyCode)
    }

    @Test
    fun testEnterNewlineFallback() {
        val ic = FakeInputConnection("test", 4)
        val editorInfo = EditorInfo().apply {
            imeOptions = EditorInfo.IME_ACTION_NONE
        }
        ImeInputHelper.handleEnter(ic, editorInfo)

        assertEquals("test\n", ic.currentText)
    }

    // --- COMPOSING TEXT LIFECYCLE TESTS ---

    @Test
    fun testComposingTextSafeExecution() {
        val ic = FakeInputConnection("hello", 5)

        // Safe calls on valid connection
        ImeInputHelper.setComposingText(ic, "𑀓", 1)
        ImeInputHelper.finishComposingText(ic)
        ImeInputHelper.commitComposingText(ic, "𑀓")
        ImeInputHelper.cancelComposingText(ic)

        // Safe calls on null connection
        ImeInputHelper.setComposingText(null, "𑀓", 1)
        ImeInputHelper.finishComposingText(null)
        ImeInputHelper.commitComposingText(null, "𑀓")
        ImeInputHelper.cancelComposingText(null)

        // Safe calls on throwing connection
        val throwing = ThrowingInputConnection()
        ImeInputHelper.setComposingText(throwing, "𑀓", 1)
        ImeInputHelper.finishComposingText(throwing)
        ImeInputHelper.commitComposingText(throwing, "𑀓")
        ImeInputHelper.cancelComposingText(throwing)

        assertTrue(true)
    }
}

/**
 * Stub for testing [InputConnection] text manipulation.
 */
open class FakeInputConnection(
    initialText: String = "",
    initialCursor: Int = initialText.length
) : InputConnection {

    protected val buffer = StringBuilder(initialText)
    var cursorPosition: Int = initialCursor
    var selectionStart: Int = initialCursor
    var selectionEnd: Int = initialCursor
    var lastPerformedAction: Int = -1

    val currentText: String
        get() = buffer.toString()

    override fun getTextBeforeCursor(n: Int, flags: Int): CharSequence? {
        val start = (cursorPosition - n).coerceAtLeast(0)
        return buffer.substring(start, cursorPosition)
    }

    override fun getTextAfterCursor(n: Int, flags: Int): CharSequence? {
        val end = (cursorPosition + n).coerceAtMost(buffer.length)
        return buffer.substring(cursorPosition, end)
    }

    override fun getSelectedText(flags: Int): CharSequence? {
        val min = minOf(selectionStart, selectionEnd)
        val max = maxOf(selectionStart, selectionEnd)
        return if (min != max) buffer.substring(min, max) else null
    }

    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
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
        val deleteStart = (cursorPosition - beforeLength).coerceAtLeast(0)
        val deleteEnd = (cursorPosition + afterLength).coerceAtMost(buffer.length)

        buffer.delete(deleteStart, deleteEnd)
        cursorPosition = deleteStart
        selectionStart = cursorPosition
        selectionEnd = cursorPosition
        return true
    }

    override fun performEditorAction(editorAction: Int): Boolean {
        lastPerformedAction = editorAction
        return true
    }

    private var keyEventCount = 0

    override fun sendKeyEvent(event: KeyEvent?): Boolean {
        keyEventCount++
        // Pair of ACTION_DOWN + ACTION_UP; execute delete on first event of pair
        if (keyEventCount % 2 == 1 && cursorPosition > 0) {
            buffer.deleteCharAt(cursorPosition - 1)
            cursorPosition--
            selectionStart = cursorPosition
            selectionEnd = cursorPosition
        }
        return true
    }

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
    override fun beginBatchEdit(): Boolean = true
    override fun endBatchEdit(): Boolean = true
    override fun clearMetaKeyStates(states: Int): Boolean = true
    override fun performPrivateCommand(action: String?, data: Bundle?): Boolean = true
    override fun requestCursorUpdates(cursorUpdateMode: Int): Boolean = true
    override fun getHandler(): Handler? = null
    override fun closeConnection() {}
    override fun commitContent(inputContentInfo: InputContentInfo, flags: Int, opts: Bundle?): Boolean = false
}

/**
 * Stub simulating an editor that returns null for getTextBeforeCursor.
 */
class NullTextBeforeInputConnection(
    initialText: String = "",
    initialCursor: Int = initialText.length
) : FakeInputConnection(initialText, initialCursor) {
    override fun getTextBeforeCursor(n: Int, flags: Int): CharSequence? = null
}

/**
 * Stub simulating a misbehaving target app that throws exceptions on every call.
 */
class ThrowingInputConnection : FakeInputConnection() {
    override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean =
        throw RuntimeException("Target app crash simulation")

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean =
        throw RuntimeException("Target app crash simulation")

    override fun performEditorAction(editorAction: Int): Boolean =
        throw RuntimeException("Target app crash simulation")
}
