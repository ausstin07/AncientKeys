package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.util.UnicodeMetadataHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScriptRegistryVerificationTest {

    @Test
    fun testAllSixScriptsPresentAndPopulated() {
        val scripts = ScriptRegistry.allScripts
        assertEquals(6, scripts.size)

        for (script in scripts) {
            assertTrue("${script.name} must have keys", script.keys.isNotEmpty())
            assertTrue("${script.name} must specify historical period", script.historicalPeriod.isNotBlank())
            assertTrue("${script.name} must specify region", script.region.isNotBlank())
            assertTrue("${script.name} must provide historical context", script.historicalContext.isNotBlank())
            assertTrue("${script.name} must cite sources", script.sourcesAndReferences.isNotBlank())
            assertTrue("${script.name} must have categories", script.categories.isNotEmpty())
        }
    }

    @Test
    fun testCharacterCodePointHexConsistency() {
        for (script in ScriptRegistry.allScripts) {
            for (key in script.keys) {
                val hexCp = UnicodeMetadataHelper.parseCodePointHex(key.unicodeCodePoint)
                assertNotNull("Failed to parse hex code point for ${key.unicodeCodePoint} in ${script.name}", hexCp)

                val charCp = UnicodeMetadataHelper.getCodePoint(key.character)
                assertEquals(
                    "Character code point mismatch for '${key.character}' in ${script.name}",
                    hexCp,
                    charCp
                )
            }
        }
    }

    @Test
    fun testOfficialUnicodeBlocks() {
        for (script in ScriptRegistry.allScripts) {
            for (key in script.keys) {
                val cp = UnicodeMetadataHelper.getCodePoint(key.character)
                when (script.id) {
                    "egyptian" -> assertTrue("Egyptian character $cp out of range", cp in 0x13000..0x1342F)
                    "brahmi" -> assertTrue("Brahmi character $cp out of range", cp in 0x11000..0x1107F)
                    "greek" -> assertTrue("Greek character $cp out of range", cp in 0x0370..0x03FF)
                    "runic" -> assertTrue("Runic character $cp out of range", cp in 0x16A0..0x16FF)
                    "phoenician" -> assertTrue("Phoenician character $cp out of range", cp in 0x10900..0x1091F)
                    "kharosthi" -> assertTrue("Kharosthi character $cp out of range", cp in 0x10A00..0x10A5F)
                }
            }
        }
    }

    @Test
    fun testEgyptianGardinerCorrections() {
        // Verify that O34 (door bolt) and S29 (folded cloth) are correctly mapped
        val egyptian = ScriptRegistry.getScriptById("egyptian")!!
        val doorBolt = egyptian.keys.first { it.unicodeCodePoint == "U+13283" }
        assertEquals("𓊃", doorBolt.character)
        assertTrue("U+13283 is Gardiner O34", doorBolt.historicalNotes.contains("O34"))

        val foldedCloth = egyptian.keys.first { it.unicodeCodePoint == "U+132F4" }
        assertEquals("𓋴", foldedCloth.character)
        assertTrue("U+132F4 is Gardiner S29", foldedCloth.historicalNotes.contains("S29"))
    }

    @Test
    fun testRtlFlagsPreserved() {
        assertTrue(ScriptRegistry.getScriptById("phoenician")!!.isRtl)
        assertTrue(ScriptRegistry.getScriptById("kharosthi")!!.isRtl)
        assertFalse(ScriptRegistry.getScriptById("egyptian")!!.isRtl)
        assertFalse(ScriptRegistry.getScriptById("brahmi")!!.isRtl)
        assertFalse(ScriptRegistry.getScriptById("greek")!!.isRtl)
        assertFalse(ScriptRegistry.getScriptById("runic")!!.isRtl)
    }
}

