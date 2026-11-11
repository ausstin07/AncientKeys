package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.phonetic.EasyPhoneticProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying safety guardrails and provider registration for Easy Phonetic typing.
 */
class EasyPhoneticProviderTest {

    @Test
    fun testSupportedScripts() {
        val supported = EasyPhoneticProvider.getAllSupportedScriptIds()
        assertEquals(listOf("brahmi", "kharosthi", "greek"), supported)

        assertTrue(EasyPhoneticProvider.isPhoneticSupported("brahmi"))
        assertTrue(EasyPhoneticProvider.isPhoneticSupported("kharosthi"))
        assertTrue(EasyPhoneticProvider.isPhoneticSupported("greek"))
        assertTrue(EasyPhoneticProvider.isPhoneticSupported("BRAHMI"))
        assertTrue(EasyPhoneticProvider.isPhoneticSupported("Greek"))

        assertNotNull(EasyPhoneticProvider.getEngine("brahmi"))
        assertNotNull(EasyPhoneticProvider.getEngine("kharosthi"))
        assertNotNull(EasyPhoneticProvider.getEngine("greek"))
    }

    @Test
    fun testUnsupportedScriptsStrictlyRejected() {
        // Egyptian Hieroglyphs, Phoenician, Runic must NOT have invented phonetic mappings
        assertFalse(EasyPhoneticProvider.isPhoneticSupported("egyptian"))
        assertFalse(EasyPhoneticProvider.isPhoneticSupported("phoenician"))
        assertFalse(EasyPhoneticProvider.isPhoneticSupported("runic"))
        assertFalse(EasyPhoneticProvider.isPhoneticSupported("english"))
        assertFalse(EasyPhoneticProvider.isPhoneticSupported("latin"))
        assertFalse(EasyPhoneticProvider.isPhoneticSupported("unknown"))

        assertNull(EasyPhoneticProvider.getEngine("egyptian"))
        assertNull(EasyPhoneticProvider.getEngine("phoenician"))
        assertNull(EasyPhoneticProvider.getEngine("runic"))
        assertNull(EasyPhoneticProvider.getEngine("unknown"))
    }
}

