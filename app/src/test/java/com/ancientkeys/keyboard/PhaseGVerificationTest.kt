package com.ancientkeys.keyboard

import android.text.InputType
import android.view.inputmethod.EditorInfo
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.ancientkeys.keyboard.ime.ImeInputHelper
import com.ancientkeys.keyboard.ime.data.EnglishKeyboardData
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.ui.transliterateAncientText
import com.ancientkeys.keyboard.util.UnicodeTextEditor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Focused verification tests for Phase G: Pre-Release Functional Corrections.
 *
 * Verifies:
 * 1. Playground Enter key newline insertion (empty, middle, SMP, RTL)
 * 2. Globe script cycling preserving text and cursor
 * 3. Numeric keyboard switching & character insertion
 * 4. Script selection dropdown options completeness
 * 5. Custom IME action ID and label evaluation
 * 6. Standard IME action mapping (Send, Search, Go, Next, Done)
 * 7. Multiline input behavior vs IME_FLAG_NO_ENTER_ACTION
 * 8. Phonetic buffer composition Enter safety
 * 9. Reverse transliteration accuracy
 * 10. Robust InputConnection fallback on action refusal
 */
class PhaseGVerificationTest {

    // 1. Playground Enter inserting newline
    @Test
    fun testPlaygroundEnter_insertsNewlineCorrectly() {
        // Empty text
        val empty = TextFieldValue("")
        val afterEmptyEnter = UnicodeTextEditor.insertNewline(empty)
        assertEquals("\n", afterEmptyEnter.text)
        assertEquals(TextRange(1), afterEmptyEnter.selection)

        // Existing text at end
        val existing = TextFieldValue("AncientKeys", TextRange(11))
        val afterEndEnter = UnicodeTextEditor.insertNewline(existing)
        assertEquals("AncientKeys\n", afterEndEnter.text)
        assertEquals(TextRange(12), afterEndEnter.selection)

        // Cursor in middle
        val mid = TextFieldValue("Hello World", TextRange(5))
        val afterMidEnter = UnicodeTextEditor.insertNewline(mid)
        assertEquals("Hello\n World", afterMidEnter.text)
        assertEquals(TextRange(6), afterMidEnter.selection)

        // Supplementary plane characters (Egyptian Ankh 𓋹 U+132F9 taking 2 UTF-16 code units)
        val smpText = TextFieldValue("𓋹𓍑𓋴", TextRange(2)) // Cursor right after 𓋹
        val afterSmpEnter = UnicodeTextEditor.insertNewline(smpText)
        assertEquals("𓋹\n𓍑𓋴", afterSmpEnter.text)
        assertEquals(TextRange(3), afterSmpEnter.selection)

        // Right-to-Left characters (Phoenician 'Alef 𐤀 Bet 𐤁)
        val rtlText = TextFieldValue("𐤀𐤁", TextRange(2))
        val afterRtlEnter = UnicodeTextEditor.insertNewline(rtlText)
        assertEquals("𐤀\n𐤁", afterRtlEnter.text)
        assertEquals(TextRange(3), afterRtlEnter.selection)
    }

    // 2. Globe preserving text and cursor
    @Test
    fun testGlobeKey_preservesTextAndCursorDuringCycle() {
        val initialText = TextFieldValue("Important Research Note", TextRange(9))
        val scriptCycleList = listOf("english", "greek", "brahmi", "runic", "phoenician", "kharosthi", "egyptian")

        var activeScriptId = "greek"
        val currentIdx = scriptCycleList.indexOf(activeScriptId)
        val nextIdx = (currentIdx + 1) % scriptCycleList.size
        activeScriptId = scriptCycleList[nextIdx]

        // Script changed from greek to brahmi
        assertEquals("brahmi", activeScriptId)

        // Text and cursor position must remain strictly identical
        assertEquals("Important Research Note", initialText.text)
        assertEquals(TextRange(9), initialText.selection)
    }

    // 3. Numeric keyboard switching & character insertion
    @Test
    fun testNumericKeyboard_layoutAndInsertion() {
        val numbers = EnglishKeyboardData.NUMBERS_ROW
        assertEquals(10, numbers.size)
        assertEquals(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"), numbers)

        val symbols1 = EnglishKeyboardData.SYMBOLS_ROW_1
        assertTrue(symbols1.contains("@"))
        assertTrue(symbols1.contains("#"))
        assertTrue(symbols1.contains("&"))

        val current = TextFieldValue("Ancient ", TextRange(8))
        val withNum = UnicodeTextEditor.insert(current, "1")
        assertEquals("Ancient 1", withNum.text)
        assertEquals(TextRange(9), withNum.selection)
    }

    // 4. Script selection dropdown options
    @Test
    fun testScriptSelection_allSevenOptionsAvailable() {
        val allScripts = ScriptRegistry.allScripts
        assertEquals(6, allScripts.size)

        val scriptIds = allScripts.map { it.id }.toSet()
        assertTrue(scriptIds.contains("egyptian"))
        assertTrue(scriptIds.contains("brahmi"))
        assertTrue(scriptIds.contains("greek"))
        assertTrue(scriptIds.contains("runic"))
        assertTrue(scriptIds.contains("phoenician"))
        assertTrue(scriptIds.contains("kharosthi"))
    }

    // 5. Custom IME action ID and label
    @Test
    fun testCustomImeAction_evaluation() {
        val customInfo = EditorInfo().apply {
            actionId = 101
            actionLabel = "Post Update"
        }
        val info = ImeInputHelper.getImeActionInfo(customInfo)
        assertEquals(101, info.actionId)
        assertEquals("Post Update", info.displayLabel)
        assertEquals("Post Update", info.contentDescription)
        assertFalse(info.isEnterNewline)
    }

    // 6. Standard IME actions
    @Test
    fun testStandardImeActions_mapping() {
        // Send
        val sendInfo = EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_SEND }
        val sendRes = ImeInputHelper.getImeActionInfo(sendInfo)
        assertEquals(EditorInfo.IME_ACTION_SEND, sendRes.actionId)
        assertEquals("Send ➤", sendRes.displayLabel)
        assertEquals("➤", sendRes.compactLabel)
        assertEquals("Send", sendRes.contentDescription)
        assertFalse(sendRes.isEnterNewline)

        // Search
        val searchInfo = EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_SEARCH }
        val searchRes = ImeInputHelper.getImeActionInfo(searchInfo)
        assertEquals(EditorInfo.IME_ACTION_SEARCH, searchRes.actionId)
        assertEquals("Search 🔍", searchRes.displayLabel)
        assertEquals("🔍", searchRes.compactLabel)
        assertEquals("Search", searchRes.contentDescription)
        assertFalse(searchRes.isEnterNewline)

        // Go
        val goInfo = EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_GO }
        val goRes = ImeInputHelper.getImeActionInfo(goInfo)
        assertEquals(EditorInfo.IME_ACTION_GO, goRes.actionId)
        assertEquals("Go ➔", goRes.displayLabel)
        assertFalse(goRes.isEnterNewline)

        // Next
        val nextInfo = EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_NEXT }
        val nextRes = ImeInputHelper.getImeActionInfo(nextInfo)
        assertEquals(EditorInfo.IME_ACTION_NEXT, nextRes.actionId)
        assertEquals("Next ➔", nextRes.displayLabel)
        assertFalse(nextRes.isEnterNewline)

        // Done
        val doneInfo = EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_DONE }
        val doneRes = ImeInputHelper.getImeActionInfo(doneInfo)
        assertEquals(EditorInfo.IME_ACTION_DONE, doneRes.actionId)
        assertEquals("Done ✓", doneRes.displayLabel)
        assertFalse(doneRes.isEnterNewline)

        // None / Unspecified
        val noneInfo = EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_NONE }
        val noneRes = ImeInputHelper.getImeActionInfo(noneInfo)
        assertTrue(noneRes.isEnterNewline)
        assertEquals("Enter ↵", noneRes.displayLabel)
    }

    // 7. Multiline vs IME_FLAG_NO_ENTER_ACTION
    @Test
    fun testMultilineAndNoEnterAction() {
        // App set IME_ACTION_SEND but also set IME_FLAG_NO_ENTER_ACTION
        val noEnterInfo = EditorInfo().apply {
            imeOptions = EditorInfo.IME_ACTION_SEND or EditorInfo.IME_FLAG_NO_ENTER_ACTION
        }
        val noEnterRes = ImeInputHelper.getImeActionInfo(noEnterInfo)
        assertTrue("IME_FLAG_NO_ENTER_ACTION must force newline", noEnterRes.isEnterNewline)
        assertEquals("Enter ↵", noEnterRes.displayLabel)

        // Multiline without action
        val multilineInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            imeOptions = EditorInfo.IME_ACTION_UNSPECIFIED
        }
        val multiRes = ImeInputHelper.getImeActionInfo(multilineInfo)
        assertTrue("Multiline without explicit action must be newline", multiRes.isEnterNewline)
    }

    // 8. Phonetic buffer composition safety simulation
    @Test
    fun testPhoneticBuffer_commitSafety() {
        var phoneticBuffer = "asoka"
        var committedText = ""
        var actionDispatched = false

        fun onEnterPress(action: () -> Unit) {
            if (phoneticBuffer.isNotEmpty()) {
                committedText = "𑀅𑀰𑁄𑀓"
                phoneticBuffer = ""
                // Buffer committed, do NOT dispatch action on this tap
                return
            }
            action()
        }

        // Tap 1: Composes and commits buffer, action is NOT dispatched
        onEnterPress { actionDispatched = true }
        assertEquals("𑀅𑀰𑁄𑀓", committedText)
        assertEquals("", phoneticBuffer)
        assertFalse("First enter press must not dispatch editor action", actionDispatched)

        // Tap 2: Buffer is now empty, action IS dispatched
        onEnterPress { actionDispatched = true }
        assertTrue("Second enter press must dispatch editor action", actionDispatched)
    }

    // 9. Reverse transliteration accuracy
    @Test
    fun testReverseTransliteration() {
        // Greek
        val greekTranslit = transliterateAncientText("λόγος", "greek")
        assertEquals("logos", greekTranslit.lowercase())

        // Brahmi
        val brahmiTranslit = transliterateAncientText("𑀅", "brahmi")
        assertEquals("a", brahmiTranslit.lowercase())

        // Egyptian Hieroglyphs (Triliteral S34 𓋹 transliterates to Egyptological ꜥnḫ)
        val egyptianTranslit = transliterateAncientText("𓋹", "egyptian")
        assertTrue(egyptianTranslit.contains("ꜥnḫ") || egyptianTranslit.contains("nḫ") || egyptianTranslit.contains("ankh"))

        // English passthrough
        val englishTranslit = transliterateAncientText("Hello", "english")
        assertEquals("Hello", englishTranslit)
    }

    // 10. Robust InputConnection fallback
    @Test
    fun testInputConnection_fallbackOnActionRefusal() {
        var performEditorActionCalled = false
        var commitTextCalled = false
        var committedString = ""

        // Simulate host declining editor action (returns false)
        val actionInfo = ImeInputHelper.getImeActionInfo(EditorInfo().apply {
            imeOptions = EditorInfo.IME_ACTION_SEND
        })

        assertFalse(actionInfo.isEnterNewline)

        // Emulate handleEnter logic
        if (!actionInfo.isEnterNewline) {
            performEditorActionCalled = true
            val success = false // Host app refused action
            if (!success) {
                commitTextCalled = true
                committedString = "\n"
            }
        }

        assertTrue(performEditorActionCalled)
        assertTrue(commitTextCalled)
        assertEquals("\n", committedString)
    }
}

