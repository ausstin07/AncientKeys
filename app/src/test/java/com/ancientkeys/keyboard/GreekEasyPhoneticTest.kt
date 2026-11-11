package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.phonetic.GreekEasyPhoneticEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for conversational "Easy Phonetic" Ancient Greek typing engine.
 */
class GreekEasyPhoneticTest {

    @Test
    fun testDigraphs() {
        assertEquals("θ", GreekEasyPhoneticEngine.convert("th"))
        assertEquals("φ", GreekEasyPhoneticEngine.convert("ph"))
        assertEquals("χ", GreekEasyPhoneticEngine.convert("ch"))
        assertEquals("ψ", GreekEasyPhoneticEngine.convert("ps"))
        assertEquals("ξ", GreekEasyPhoneticEngine.convert("ks"))
        assertEquals("η", GreekEasyPhoneticEngine.convert("ee"))
        assertEquals("ω", GreekEasyPhoneticEngine.convert("oo"))
    }

    @Test
    fun testRoughBreathing() {
        assertEquals("ἁ", GreekEasyPhoneticEngine.convert("ha"))
        assertEquals("ἑ", GreekEasyPhoneticEngine.convert("he"))
        assertEquals("ἱ", GreekEasyPhoneticEngine.convert("hi"))
        assertEquals("ὁ", GreekEasyPhoneticEngine.convert("ho"))
        assertEquals("ὑ", GreekEasyPhoneticEngine.convert("hu"))
    }

    @Test
    fun testContextualFinalSigma() {
        // 's' at the end of a word converts to final sigma ς
        assertEquals("λογος", GreekEasyPhoneticEngine.convert("logos"))
        // 's' in the middle remains medial sigma σ
        assertEquals("σοφια", GreekEasyPhoneticEngine.convert("sophia"))
    }

    @Test
    fun testUppercaseRetention() {
        assertEquals("Θ", GreekEasyPhoneticEngine.convert("Th"))
        assertEquals("Φ", GreekEasyPhoneticEngine.convert("Ph"))
        assertEquals("Χ", GreekEasyPhoneticEngine.convert("Ch"))
        assertEquals("Α", GreekEasyPhoneticEngine.convert("A"))
        assertEquals("Β", GreekEasyPhoneticEngine.convert("B"))
    }

    @Test
    fun testPunctuation() {
        assertEquals(";", GreekEasyPhoneticEngine.convert(";"))
        assertEquals(";", GreekEasyPhoneticEngine.convert("?"))
        assertEquals("·", GreekEasyPhoneticEngine.convert(":"))
    }

    @Test
    fun testCandidates() {
        val eCandidates = GreekEasyPhoneticEngine.getCandidates("e")
        assertTrue(eCandidates.any { it.scriptResult == "ε" })
        assertTrue(eCandidates.any { it.scriptResult == "η" })

        val oCandidates = GreekEasyPhoneticEngine.getCandidates("o")
        assertTrue(oCandidates.any { it.scriptResult == "ο" })
        assertTrue(oCandidates.any { it.scriptResult == "ω" })

        val sCandidates = GreekEasyPhoneticEngine.getCandidates("s")
        assertTrue(sCandidates.any { it.scriptResult == "σ" })
        assertTrue(sCandidates.any { it.scriptResult == "ς" })
    }

    @Test
    fun testEdgeCasesEmptyAndUnknownInput() {
        assertEquals("", GreekEasyPhoneticEngine.convert(""))
        assertEquals("", GreekEasyPhoneticEngine.convert("   "))

        // Final sigma before punctuation
        assertEquals("λογος, και", GreekEasyPhoneticEngine.convert("logos, kai"))

        // Full phrase with casing
        assertEquals("Ἐν ἀρχῇ ἦν ὁ Λόγος", GreekEasyPhoneticEngine.convert("Ἐν ἀρχῇ ἦν ὁ Λόγος"))
    }
}

