package com.ancientkeys.keyboard

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.ancientkeys.keyboard.util.UnicodeTextEditor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class UnicodeTextEditorTest {

    @Test
    fun testBasicInsertion() {
        val initial = TextFieldValue("", TextRange.Zero)
        val result = UnicodeTextEditor.insert(initial, "Α") // Greek Alpha

        assertEquals("Α", result.text)
        assertEquals(1, result.selection.start)
        assertEquals(1, result.selection.end)
    }

    @Test
    fun testSMPCharacterInsertion() {
        // Egyptian Hieroglyph: 𓀀 (U+13000, 2 UTF-16 code units)
        val initial = TextFieldValue("", TextRange.Zero)
        val result = UnicodeTextEditor.insert(initial, "𓀀")

        assertEquals("𓀀", result.text)
        assertEquals(2, result.text.length)
        assertEquals(2, result.selection.start)
    }

    @Test
    fun testSelectionReplacement() {
        // Replace "BC" in "ABCD" with Phoenician 𐤀
        val initial = TextFieldValue("ABCD", TextRange(1, 3))
        val result = UnicodeTextEditor.insert(initial, "𐤀")

        assertEquals("A𐤀D", result.text)
        // Cursor should be right after the inserted 𐤀 (index 1 + 2 = 3)
        assertEquals(3, result.selection.start)
        assertEquals(3, result.selection.end)
    }

    @Test
    fun testBasicDeletion() {
        val initial = TextFieldValue("AB", TextRange(2))
        val result = UnicodeTextEditor.deleteBackward(initial)

        assertEquals("A", result.text)
        assertEquals(1, result.selection.start)
    }

    @Test
    fun testSMPDeletionRemovesBothSurrogatesSafely() {
        // "𓀀𓁐" has length 4 (two surrogate pairs)
        val initial = TextFieldValue("𓀀𓁐", TextRange(4))
        val result = UnicodeTextEditor.deleteBackward(initial)

        assertEquals("𓀀", result.text)
        assertEquals(2, result.text.length)
        assertEquals(2, result.selection.start)

        // Ensure no lone/orphan surrogates remain
        for (ch in result.text) {
            assertFalse("Result should not contain un-paired surrogate chars", Character.isHighSurrogate(ch) && result.text.length == 1)
        }
    }

    @Test
    fun testCombiningMarkDeletion() {
        // Brahmi KA (𑀓) + Vowel Sign AA (𑀸)
        val text = "𑀓\uD804\uDC38"
        val initial = TextFieldValue(text, TextRange(text.length))
        val result = UnicodeTextEditor.deleteBackward(initial)

        // Deleting backward must remove the diacritic or cluster without cutting surrogate code units
        for (i in 0 until result.text.length) {
            val ch = result.text[i]
            if (Character.isHighSurrogate(ch)) {
                assertFalse("High surrogate must have following low surrogate", i + 1 >= result.text.length || !Character.isLowSurrogate(result.text[i + 1]))
            }
        }
    }

    @Test
    fun testDeletionWithSelection() {
        val initial = TextFieldValue("𐤀𐤁𐤂", TextRange(2, 4)) // Selecting 𐤁
        val result = UnicodeTextEditor.deleteBackward(initial)

        assertEquals("𐤀𐤂", result.text)
        assertEquals(2, result.selection.start)
    }

    @Test
    fun testDeletionAtBeginningIsNoOp() {
        val initial = TextFieldValue("𓀀", TextRange(0))
        val result = UnicodeTextEditor.deleteBackward(initial)

        assertEquals("𓀀", result.text)
        assertEquals(0, result.selection.start)
    }

    @Test
    fun testSpaceAndClear() {
        val initial = TextFieldValue("𐨀", TextRange(2))
        val withSpace = UnicodeTextEditor.insertSpace(initial)

        assertEquals("𐨀 ", withSpace.text)
        assertEquals(3, withSpace.selection.start)

        val cleared = UnicodeTextEditor.clear()
        assertEquals("", cleared.text)
        assertEquals(0, cleared.selection.start)
    }
}

