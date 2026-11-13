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
        assertEquals("𑀓\uD804\uDC3A", BrahmiEasyPhoneticEngine.convert("ki"))
        assertEquals("𑀓\uD804\uDC3B", BrahmiEasyPhoneticEngine.convert("kii"))
        assertEquals("𑀓\uD804\uDC3B", BrahmiEasyPhoneticEngine.convert("kee"))
        assertEquals("𑀓\uD804\uDC3C", BrahmiEasyPhoneticEngine.convert("ku"))
        assertEquals("𑀓\uD804\uDC3D", BrahmiEasyPhoneticEngine.convert("kuu"))
        assertEquals("𑀓\uD804\uDC42", BrahmiEasyPhoneticEngine.convert("ke"))
        assertEquals("𑀓\uD804\uDC43", BrahmiEasyPhoneticEngine.convert("kai"))
        assertEquals("𑀓\uD804\uDC44", BrahmiEasyPhoneticEngine.convert("ko"))
        assertEquals("𑀓\uD804\uDC45", BrahmiEasyPhoneticEngine.convert("kau"))
    }

    @Test
    fun testWordsAndPhrases() {
        // "asoka" -> 𑀅 (a) + 𑀰𑁄 (sho) + 𑀓 (ka)
        assertEquals("𑀅𑀰𑁄𑀓", BrahmiEasyPhoneticEngine.convert("asoka"))
        // "dhamma" -> 𑀥 (dha) + 𑀫𑁆 (m cluster) + 𑀫 (ma)
        assertEquals("𑀥𑀫𑁆𑀫", BrahmiEasyPhoneticEngine.convert("dhamma"))
        // "namaste" -> 𑀦 (na) + 𑀫 (ma) + 𑀲𑁆 (s cluster) + 𑀢𑁂 (te)
        assertEquals("𑀦𑀫𑀲𑁆𑀢𑁂", BrahmiEasyPhoneticEngine.convert("namaste"))
        assertEquals("𑀦𑀫𑀲𑁆𑀢𑁂", BrahmiEasyPhoneticEngine.convert("namas_te"))
    }

    @Test
    fun testAustinPhonemicTransliterationAndBhattiproluExclusion() {
        // Standard phonemic transliteration of "Austin":
        // "au" -> 𑀒 (U+11012), "s" -> 𑀲 + 𑁆 (U+11046 virama cluster), "ti" -> 𑀢 + 𑀺 (U+1103A vowel sign i), "n" -> 𑀦 + 𑁆 (U+11046 final virama)
        val austin = BrahmiEasyPhoneticEngine.convert("Austin")
        assertEquals("𑀒𑀲𑁆𑀢𑀺𑀦𑁆", austin)

        // Strict verification: Bhattiprolu regional variant U+11039 must NEVER be present!
        assertFalse("Must NEVER emit Bhattiprolu AA (U+11039)", austin.contains("\uD804\uDC39"))
        assertTrue("Must emit standard Brahmi vowel sign I (U+1103A)", austin.contains("\uD804\uDC3A"))
        assertTrue("Must use virama (U+11046) for consonant cluster and final consonant", austin.contains("\uD804\uDC46"))

        // Explicit "Austina" produces final inherent 'a' without virama
        val austina = BrahmiEasyPhoneticEngine.convert("Austina")
        assertEquals("𑀒𑀲𑁆𑀢𑀺𑀦", austina)

        // Verify candidates for "austin"
        val candidates = BrahmiEasyPhoneticEngine.getCandidates("austin")
        assertTrue("Primary candidate must be 𑀒𑀲𑁆𑀢𑀺𑀦𑁆", candidates.any { it.isPrimary && it.scriptResult == "𑀒𑀲𑁆𑀢𑀺𑀦𑁆" })
        assertTrue("Must offer candidate with inherent final a (𑀒𑀲𑁆𑀢𑀺𑀦)", candidates.any { it.scriptResult == "𑀒𑀲𑁆𑀢𑀺𑀦" })
        assertTrue("Must offer retroflex ṭ variant (𑀒𑀲𑁆𑀝𑀺𑀦𑁆)", candidates.any { it.scriptResult == "𑀒𑀲𑁆𑀝𑀺𑀦𑁆" })
    }

    @Test
    fun testCandidateSuggestions() {
        val candidates = BrahmiEasyPhoneticEngine.getCandidates("k")
        assertTrue("Must include primary candidate", candidates.any { it.isPrimary })
        assertTrue("Must offer vowel variants", candidates.any { it.scriptResult == "𑀓\uD804\uDC38" })
        assertTrue("Must offer short i", candidates.any { it.scriptResult == "𑀓\uD804\uDC3A" })
        assertFalse("Must NOT offer Bhattiprolu U+11039", candidates.any { it.scriptResult == "𑀓\uD804\uDC39" })
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

