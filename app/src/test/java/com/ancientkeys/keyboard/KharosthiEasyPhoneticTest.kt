package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.phonetic.KharosthiEasyPhoneticEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the conversational "Easy Phonetic" Kharosthi typing engine.
 */
class KharosthiEasyPhoneticTest {

    @Test
    fun testIndependentVowels() {
        assertEquals("𐨀", KharosthiEasyPhoneticEngine.convert("a"))
        assertEquals("𐨀", KharosthiEasyPhoneticEngine.convert("aa"))
        assertEquals("𐨀\uD802\uDE01", KharosthiEasyPhoneticEngine.convert("i"))
        assertEquals("𐨀\uD802\uDE01", KharosthiEasyPhoneticEngine.convert("ii"))
        assertEquals("𐨀\uD802\uDE01", KharosthiEasyPhoneticEngine.convert("ee"))
        assertEquals("𐨀\uD802\uDE02", KharosthiEasyPhoneticEngine.convert("u"))
        assertEquals("𐨀\uD802\uDE02", KharosthiEasyPhoneticEngine.convert("uu"))
        assertEquals("𐨀\uD802\uDE05", KharosthiEasyPhoneticEngine.convert("e"))
        assertEquals("𐨀\uD802\uDE06", KharosthiEasyPhoneticEngine.convert("o"))
    }

    @Test
    fun testConsonantsInherentVowel() {
        assertEquals("𐨐", KharosthiEasyPhoneticEngine.convert("k"))
        assertEquals("𐨐", KharosthiEasyPhoneticEngine.convert("ka"))
        assertEquals("𐨑", KharosthiEasyPhoneticEngine.convert("kh"))
        assertEquals("𐨑", KharosthiEasyPhoneticEngine.convert("kha"))
        assertEquals("𐨒", KharosthiEasyPhoneticEngine.convert("ga"))
        assertEquals("𐨓", KharosthiEasyPhoneticEngine.convert("gha"))
        assertEquals("𐨕", KharosthiEasyPhoneticEngine.convert("ca"))
        assertEquals("𐨖", KharosthiEasyPhoneticEngine.convert("ch"))
        assertEquals("𐨗", KharosthiEasyPhoneticEngine.convert("ja"))
        assertEquals("𐨟", KharosthiEasyPhoneticEngine.convert("ta"))
        assertEquals("𐨠", KharosthiEasyPhoneticEngine.convert("tha"))
        assertEquals("𐨡", KharosthiEasyPhoneticEngine.convert("da"))
        assertEquals("𐨢", KharosthiEasyPhoneticEngine.convert("dha"))
        assertEquals("𐨣", KharosthiEasyPhoneticEngine.convert("na"))
        assertEquals("𐨤", KharosthiEasyPhoneticEngine.convert("pa"))
        assertEquals("𐨥", KharosthiEasyPhoneticEngine.convert("pha"))
        assertEquals("𐨦", KharosthiEasyPhoneticEngine.convert("ba"))
        assertEquals("𐨧", KharosthiEasyPhoneticEngine.convert("bha"))
        assertEquals("𐨨", KharosthiEasyPhoneticEngine.convert("ma"))
        assertEquals("𐨰", KharosthiEasyPhoneticEngine.convert("za"))
        assertEquals("𐨱", KharosthiEasyPhoneticEngine.convert("ha"))
    }

    @Test
    fun testDependentVowels() {
        assertEquals("𐨐\uD802\uDE01", KharosthiEasyPhoneticEngine.convert("ki"))
        assertEquals("𐨐\uD802\uDE02", KharosthiEasyPhoneticEngine.convert("ku"))
        assertEquals("𐨐\uD802\uDE05", KharosthiEasyPhoneticEngine.convert("ke"))
        assertEquals("𐨐\uD802\uDE06", KharosthiEasyPhoneticEngine.convert("ko"))
    }

    @Test
    fun testGandharanNumerals() {
        assertEquals("𐩀", KharosthiEasyPhoneticEngine.convert("1"))
        assertEquals("𐩁", KharosthiEasyPhoneticEngine.convert("2"))
        assertEquals("𐩂", KharosthiEasyPhoneticEngine.convert("3"))
        assertEquals("𐩃", KharosthiEasyPhoneticEngine.convert("4"))
        assertEquals("𐩄", KharosthiEasyPhoneticEngine.convert("10"))
        assertEquals("𐩅", KharosthiEasyPhoneticEngine.convert("20"))
        assertEquals("𐩆", KharosthiEasyPhoneticEngine.convert("100"))
        assertEquals("𐩇", KharosthiEasyPhoneticEngine.convert("1000"))
    }

    @Test
    fun testCandidates() {
        val candidates = KharosthiEasyPhoneticEngine.getCandidates("k")
        assertTrue(candidates.any { it.isPrimary })
        assertTrue(candidates.any { it.scriptResult == "𐨐\uD802\uDE01" })
        assertTrue(candidates.any { it.scriptResult == "𐨐\uD802\uDE02" })

        val sCandidates = KharosthiEasyPhoneticEngine.getCandidates("s")
        assertTrue(sCandidates.any { it.scriptResult == "𐨯" })
        assertTrue(sCandidates.any { it.scriptResult == "𐨭" })
        assertTrue(sCandidates.any { it.scriptResult == "𐨮" })
        assertTrue(sCandidates.any { it.scriptResult == "𐨰" })
    }

    @Test
    fun testEdgeCasesIncompleteAndUnknownInput() {
        // Empty and whitespace input
        assertEquals("", KharosthiEasyPhoneticEngine.convert(""))
        assertEquals("", KharosthiEasyPhoneticEngine.convert("   "))

        // Case insensitivity
        assertEquals("𐨐", KharosthiEasyPhoneticEngine.convert("K"))
        assertEquals("𐨐", KharosthiEasyPhoneticEngine.convert("KA"))
        assertEquals("𐨐\uD802\uDE01", KharosthiEasyPhoneticEngine.convert("KI"))

        // Multi-word sequence: "dhama"
        assertEquals("𐨢𐨨", KharosthiEasyPhoneticEngine.convert("dhama"))

        // Unknown punctuation and numbers fallback
        assertEquals("𐨐, 𐨑!", KharosthiEasyPhoneticEngine.convert("ka, kha!"))
    }
}

