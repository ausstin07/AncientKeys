package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.phonetic.BrahmiEasyPhoneticEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the conversational "Easy Phonetic" Brahmi typing engine.
 */
class BrahmiEasyPhoneticTest {

    @Test
    fun testIndependentVowels() {
        assertEquals("𑀅", BrahmiEasyPhoneticEngine.convert("a"))
        assertEquals("𑀆", BrahmiEasyPhoneticEngine.convert("aa"))
        assertEquals("𑀆", BrahmiEasyPhoneticEngine.convert("ā"))
        assertEquals("𑀇", BrahmiEasyPhoneticEngine.convert("i"))
        assertEquals("𑀈", BrahmiEasyPhoneticEngine.convert("ii"))
        assertEquals("𑀈", BrahmiEasyPhoneticEngine.convert("ī"))
        assertEquals("𑀉", BrahmiEasyPhoneticEngine.convert("u"))
        assertEquals("𑀊", BrahmiEasyPhoneticEngine.convert("uu"))
        assertEquals("𑀊", BrahmiEasyPhoneticEngine.convert("ū"))
        assertEquals("𑀋", BrahmiEasyPhoneticEngine.convert("ri"))
        assertEquals("𑀏", BrahmiEasyPhoneticEngine.convert("e"))
        assertEquals("𑀐", BrahmiEasyPhoneticEngine.convert("ai"))
        assertEquals("𑀑", BrahmiEasyPhoneticEngine.convert("o"))
        assertEquals("𑀒", BrahmiEasyPhoneticEngine.convert("au"))
    }

    @Test
    fun testConsonantsInherentVowel() {
        assertEquals("𑀓", BrahmiEasyPhoneticEngine.convert("k"))
        assertEquals("𑀓", BrahmiEasyPhoneticEngine.convert("ka"))
        assertEquals("𑀔", BrahmiEasyPhoneticEngine.convert("kh"))
        assertEquals("𑀔", BrahmiEasyPhoneticEngine.convert("kha"))
        assertEquals("𑀕", BrahmiEasyPhoneticEngine.convert("ga"))
        assertEquals("𑀖", BrahmiEasyPhoneticEngine.convert("gha"))
        assertEquals("𑀘", BrahmiEasyPhoneticEngine.convert("ca"))
        assertEquals("𑀘", BrahmiEasyPhoneticEngine.convert("ch"))
        assertEquals("𑀙", BrahmiEasyPhoneticEngine.convert("chh"))
        assertEquals("𑀚", BrahmiEasyPhoneticEngine.convert("ja"))
        assertEquals("𑀢", BrahmiEasyPhoneticEngine.convert("ta"))
        assertEquals("𑀣", BrahmiEasyPhoneticEngine.convert("tha"))
        assertEquals("𑀤", BrahmiEasyPhoneticEngine.convert("da"))
        assertEquals("𑀥", BrahmiEasyPhoneticEngine.convert("dha"))
        assertEquals("𑀦", BrahmiEasyPhoneticEngine.convert("na"))
        assertEquals("𑀧", BrahmiEasyPhoneticEngine.convert("pa"))
        assertEquals("𑀨", BrahmiEasyPhoneticEngine.convert("pha"))
        assertEquals("𑀨", BrahmiEasyPhoneticEngine.convert("f"))
        assertEquals("𑀩", BrahmiEasyPhoneticEngine.convert("ba"))
        assertEquals("𑀪", BrahmiEasyPhoneticEngine.convert("bha"))
        assertEquals("𑀫", BrahmiEasyPhoneticEngine.convert("ma"))
        assertEquals("𑀬", BrahmiEasyPhoneticEngine.convert("ya"))
        assertEquals("𑀭", BrahmiEasyPhoneticEngine.convert("ra"))
        assertEquals("𑀮", BrahmiEasyPhoneticEngine.convert("la"))
        assertEquals("𑀯", BrahmiEasyPhoneticEngine.convert("va"))
        assertEquals("𑀰", BrahmiEasyPhoneticEngine.convert("sha"))
        assertEquals("𑀱", BrahmiEasyPhoneticEngine.convert("shh"))
        assertEquals("𑀲", BrahmiEasyPhoneticEngine.convert("sa"))
        assertEquals("𑀳", BrahmiEasyPhoneticEngine.convert("ha"))
    }

    @Test
    fun testDependentVowelSigns() {
        assertEquals("𑀓\uD804\uDC38", BrahmiEasyPhoneticEngine.convert("kaa"))
        assertEquals("𑀓\uD804\uDC38", BrahmiEasyPhoneticEngine.convert("kā"))
        assertEquals("𑀓\uD804\uDC39", BrahmiEasyPhoneticEngine.convert("ki"))
        assertEquals("𑀓\uD804\uDC3A", BrahmiEasyPhoneticEngine.convert("kii"))
        assertEquals("𑀓\uD804\uDC3A", BrahmiEasyPhoneticEngine.convert("kee"))
        assertEquals("𑀓\uD804\uDC3B", BrahmiEasyPhoneticEngine.convert("ku"))
        assertEquals("𑀓\uD804\uDC3C", BrahmiEasyPhoneticEngine.convert("kuu"))
        assertEquals("𑀓\uD804\uDC3E", BrahmiEasyPhoneticEngine.convert("ke"))
        assertEquals("𑀓\uD804\uDC3F", BrahmiEasyPhoneticEngine.convert("kai"))
        assertEquals("𑀓\uD804\uDC40", BrahmiEasyPhoneticEngine.convert("ko"))
        assertEquals("𑀓\uD804\uDC41", BrahmiEasyPhoneticEngine.convert("kau"))
    }

    @Test
    fun testWordsAndPhrases() {
        // "asoka" -> 𑀅 (a) + 𑀰𑁄 (sho) + 𑀓 (ka)
        assertEquals("𑀅𑀰𑁄𑀓", BrahmiEasyPhoneticEngine.convert("asoka"))
        // "dhamma" -> 𑀥 (dha) + 𑀫 (ma) + 𑀫 (ma)
        assertEquals("𑀥𑀫𑀫", BrahmiEasyPhoneticEngine.convert("dhamma"))
        // "namaste" -> 𑀦 (na) + 𑀫 (ma) + 𑀲\uD804\uDC46 (s with virama) + 𑀢\uD804\uDC3E (te)
        assertEquals("𑀦𑀫𑀲𑁆𑀢\uD804\uDC3E", BrahmiEasyPhoneticEngine.convert("namas_te"))
    }

    @Test
    fun testCandidateSuggestions() {
        val candidates = BrahmiEasyPhoneticEngine.getCandidates("k")
        assertTrue("Must include primary candidate", candidates.any { it.isPrimary })
        assertTrue("Must offer vowel variants", candidates.any { it.scriptResult == "𑀓\uD804\uDC38" })
        assertTrue("Must offer short i", candidates.any { it.scriptResult == "𑀓\uD804\uDC39" })
        assertTrue("Must offer virama", candidates.any { it.scriptResult == "𑀓\uD804\uDC46" })
        assertTrue("Must offer aspirated kha", candidates.any { it.scriptResult == "𑀔" })
    }

    @Test
    fun testAmbiguityResolutions() {
        // Dental vs retroflex t
        val tCandidates = BrahmiEasyPhoneticEngine.getCandidates("t")
        assertTrue(tCandidates.any { it.scriptResult == "𑀢" }) // dental ta
        assertTrue(tCandidates.any { it.scriptResult == "𑀝" }) // retroflex ṭa

        // Sibilant ambiguity
        val sCandidates = BrahmiEasyPhoneticEngine.getCandidates("s")
        assertTrue(sCandidates.any { it.scriptResult == "𑀲" }) // dental sa
        assertTrue(sCandidates.any { it.scriptResult == "𑀰" }) // palatal sha
        assertTrue(sCandidates.any { it.scriptResult == "𑀱" }) // retroflex sha
    }

    @Test
    fun testEdgeCasesIncompleteSequencesAndUnknownInput() {
        // Empty and whitespace input
        assertEquals("", BrahmiEasyPhoneticEngine.convert(""))
        assertEquals("", BrahmiEasyPhoneticEngine.convert("   "))

        // Case insensitivity
        assertEquals("𑀓", BrahmiEasyPhoneticEngine.convert("KA"))
        assertEquals("𑀓\uD804\uDC38", BrahmiEasyPhoneticEngine.convert("KAA"))
        assertEquals("𑀅𑀰𑁄𑀓", BrahmiEasyPhoneticEngine.convert("ASOKA"))

        // Consonant + Virama
        assertEquals("𑀓\uD804\uDC46", BrahmiEasyPhoneticEngine.convert("k_"))

        // Fallback for numbers, spaces and unknown punctuation
        assertEquals("123", BrahmiEasyPhoneticEngine.convert("123"))
        assertEquals("𑀓, 𑀔!", BrahmiEasyPhoneticEngine.convert("ka, kha!"))

        // Modifiers: Anusvara and Visarga
        assertEquals("𑀓\uD804\uDC01", BrahmiEasyPhoneticEngine.convert("kam"))
        assertEquals("𑀓\uD804\uDC02", BrahmiEasyPhoneticEngine.convert("kah"))
        assertEquals("𑀓\uD804\uDC01", BrahmiEasyPhoneticEngine.convert("kaṃ"))
        assertEquals("𑀓\uD804\uDC02", BrahmiEasyPhoneticEngine.convert("kaḥ"))
    }
}

