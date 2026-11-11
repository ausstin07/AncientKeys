package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.data.BrahmiScript
import com.ancientkeys.keyboard.data.KharosthiScript
import com.ancientkeys.keyboard.data.RunicScript
import com.ancientkeys.keyboard.util.UnicodeMetadataHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UnicodeMetadataHelperTest {

    @Test
    fun testParseCodePointHex() {
        assertEquals(0x1313F, UnicodeMetadataHelper.parseCodePointHex("U+1313F"))
        assertEquals(0x1313F, UnicodeMetadataHelper.parseCodePointHex("1313F"))
        assertEquals(0x1313F, UnicodeMetadataHelper.parseCodePointHex("u+1313f"))
        assertEquals(0x0391, UnicodeMetadataHelper.parseCodePointHex("U+0391"))
        assertEquals(null, UnicodeMetadataHelper.parseCodePointHex("INVALID_HEX"))
    }

    @Test
    fun testFormatCodePoint() {
        assertEquals("U+0391", UnicodeMetadataHelper.formatCodePoint(0x0391))
        assertEquals("U+1313F", UnicodeMetadataHelper.formatCodePoint(0x1313F))
        assertEquals("U+16A0", UnicodeMetadataHelper.formatCodePoint(0x16A0))
    }

    @Test
    fun testGetCodePointFromCharacter() {
        assertEquals(0x16A0, UnicodeMetadataHelper.getCodePoint("ᚠ"))
        assertEquals(0x1313F, UnicodeMetadataHelper.getCodePoint("𓄿"))
        assertEquals(0x0391, UnicodeMetadataHelper.getCodePoint("Α"))
    }

    @Test
    fun testOfficialUnicodeNameLookup() {
        val fehuKey = RunicScript.keys.first { it.character == "ᚠ" }
        val name = UnicodeMetadataHelper.getOfficialUnicodeName(fehuKey)
        assertTrue("Name should mention FEHU", name.contains("FEHU"))
    }

    @Test
    fun testCombiningMarksDetection() {
        // Kharosthi vowel sign -i (U+10A01)
        val kharosthiSignI = KharosthiScript.keys.first { it.unicodeCodePoint == "U+10A01" }
        assertTrue("Kharosthi vowel sign -i must be combining", kharosthiSignI.isCombining)

        // Brahmi vowel sign -ā (U+11038)
        val brahmiSignAa = BrahmiScript.keys.first { it.unicodeCodePoint == "U+11038" }
        assertTrue("Brahmi vowel sign -ā must be combining", brahmiSignAa.isCombining)

        // Base consonants must not be combining
        val runicFehu = RunicScript.keys.first { it.character == "ᚠ" }
        assertFalse("Runic Fehu is not combining", runicFehu.isCombining)
    }

    @Test
    fun testCombiningMarksDoNotInsertDottedCircle() {
        // Requirement 16: Ensure combining marks do not insert placeholder dotted-circle characters
        for (key in KharosthiScript.keys) {
            if (key.isCombining) {
                assertFalse("Actual character string must not contain dotted circle", key.character.contains("◌"))
                assertFalse("Actual character string must not contain U+25CC", key.character.contains("\u25CC"))
            }
        }

        for (key in BrahmiScript.keys) {
            if (key.isCombining) {
                assertFalse("Actual character string must not contain dotted circle", key.character.contains("◌"))
                assertFalse("Actual character string must not contain U+25CC", key.character.contains("\u25CC"))
            }
        }
    }

    @Test
    fun testDisplayGlyphShowsDottedCircleForCombiningMarks() {
        val kharosthiSignI = KharosthiScript.keys.first { it.unicodeCodePoint == "U+10A01" }
        val display = UnicodeMetadataHelper.getDisplayGlyph(kharosthiSignI)
        assertTrue("Display representation of combining mark should contain base visual cue", display.contains("◌") || display.contains("\u25CC"))
    }
}

