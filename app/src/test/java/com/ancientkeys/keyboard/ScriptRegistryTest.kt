package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.model.ScriptRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScriptRegistryTest {

    @Test
    fun testAllSixScriptsRegistered() {
        val scripts = ScriptRegistry.allScripts
        assertEquals(6, scripts.size)

        val scriptIds = scripts.map { it.id }.toSet()
        val expectedIds = setOf("egyptian", "brahmi", "greek", "runic", "phoenician", "kharosthi")
        assertEquals(expectedIds, scriptIds)
    }

    @Test
    fun testScriptLookupById() {
        assertNotNull(ScriptRegistry.getScriptById("egyptian"))
        assertNotNull(ScriptRegistry.getScriptById("EGYPTIAN"))
        assertNotNull(ScriptRegistry.getScriptById("brahmi"))
        assertNotNull(ScriptRegistry.getScriptById("greek"))
        assertNotNull(ScriptRegistry.getScriptById("runic"))
        assertNotNull(ScriptRegistry.getScriptById("phoenician"))
        assertNotNull(ScriptRegistry.getScriptById("kharosthi"))
    }

    @Test
    fun testRtlFlags() {
        val phoenician = ScriptRegistry.getScriptById("phoenician")
        assertNotNull(phoenician)
        assertTrue("Phoenician must be RTL", phoenician!!.isRtl)

        val kharosthi = ScriptRegistry.getScriptById("kharosthi")
        assertNotNull(kharosthi)
        assertTrue("Kharosthi must be RTL", kharosthi!!.isRtl)

        val egyptian = ScriptRegistry.getScriptById("egyptian")
        assertNotNull(egyptian)
        assertFalse("Egyptian default layout is LTR", egyptian!!.isRtl)

        val greek = ScriptRegistry.getScriptById("greek")
        assertNotNull(greek)
        assertFalse("Greek is LTR", greek!!.isRtl)

        val runic = ScriptRegistry.getScriptById("runic")
        assertNotNull(runic)
        assertFalse("Runic is LTR", runic!!.isRtl)

        val brahmi = ScriptRegistry.getScriptById("brahmi")
        assertNotNull(brahmi)
        assertFalse("Brahmi is LTR", brahmi!!.isRtl)
    }

    @Test
    fun testAllKeysValid() {
        for (script in ScriptRegistry.allScripts) {
            assertTrue("${script.name} should have keys", script.keys.isNotEmpty())
            for (key in script.keys) {
                assertTrue("Key character in ${script.name} should not be empty", key.character.isNotEmpty())
                assertTrue("Key Unicode point in ${script.name} should not be empty", key.unicodeCodePoint.startsWith("U+"))
            }
        }
    }
}

