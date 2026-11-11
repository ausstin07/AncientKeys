package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.romanization.GreekRomanizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Classical Ancient Greek Romanized Input transliteration engine.
 * Verifies academic epigraphic conventions and final sigma contextual rules.
 */
class GreekRomanizerTest {

    @Test
    fun testConsonants() {
        assertEquals("β", GreekRomanizer.transliterateToken("b"))
        assertEquals("γ", GreekRomanizer.transliterateToken("g"))
        assertEquals("δ", GreekRomanizer.transliterateToken("d"))
        assertEquals("ζ", GreekRomanizer.transliterateToken("z"))
        assertEquals("θ", GreekRomanizer.transliterateToken("th"))
        assertEquals("κ", GreekRomanizer.transliterateToken("k"))
        assertEquals("κ", GreekRomanizer.transliterateToken("c"))
        assertEquals("λ", GreekRomanizer.transliterateToken("l"))
        assertEquals("μ", GreekRomanizer.transliterateToken("m"))
        assertEquals("ν", GreekRomanizer.transliterateToken("n"))
        assertEquals("ξ", GreekRomanizer.transliterateToken("x"))
        assertEquals("ξ", GreekRomanizer.transliterateToken("ks"))
        assertEquals("π", GreekRomanizer.transliterateToken("p"))
        assertEquals("ρ", GreekRomanizer.transliterateToken("r"))
        assertEquals("τ", GreekRomanizer.transliterateToken("t"))
        assertEquals("φ", GreekRomanizer.transliterateToken("ph"))
        assertEquals("χ", GreekRomanizer.transliterateToken("ch"))
        assertEquals("χ", GreekRomanizer.transliterateToken("kh"))
        assertEquals("ψ", GreekRomanizer.transliterateToken("ps"))
    }

    @Test
    fun testContextualFinalSigma() {
        // Word-final 's' must become 'ς'
        assertEquals("λογος", GreekRomanizer.transliterateToken("logos"))
        assertEquals("θεος", GreekRomanizer.transliterateToken("theos"))

        // Word-internal 's' must remain 'σ'
        assertEquals("σιγμα", GreekRomanizer.transliterateToken("sigma"))
        assertEquals("σται", GreekRomanizer.transliterateToken("stai"))
    }

    @Test
    fun testVowels() {
        assertEquals("α", GreekRomanizer.transliterateToken("a"))
        assertEquals("ε", GreekRomanizer.transliterateToken("e"))
        assertEquals("η", GreekRomanizer.transliterateToken("ē"))
        assertEquals("η", GreekRomanizer.transliterateToken("h"))
        assertEquals("ι", GreekRomanizer.transliterateToken("i"))
        assertEquals("ο", GreekRomanizer.transliterateToken("o"))
        assertEquals("υ", GreekRomanizer.transliterateToken("u"))
        assertEquals("υ", GreekRomanizer.transliterateToken("y"))
        assertEquals("ω", GreekRomanizer.transliterateToken("ō"))
        assertEquals("ω", GreekRomanizer.transliterateToken("w"))
    }

    @Test
    fun testRoughBreathing() {
        assertEquals("ἁ", GreekRomanizer.transliterateToken("ha"))
        assertEquals("ἑ", GreekRomanizer.transliterateToken("he"))
        assertEquals("ἡ", GreekRomanizer.transliterateToken("hē"))
        assertEquals("ἱ", GreekRomanizer.transliterateToken("hi"))
        assertEquals("ὁ", GreekRomanizer.transliterateToken("ho"))
        assertEquals("ὑ", GreekRomanizer.transliterateToken("hu"))
        assertEquals("ὡ", GreekRomanizer.transliterateToken("hō"))
    }

    @Test
    fun testUppercaseTransliteration() {
        assertEquals("ΛΟΓΟΣ", GreekRomanizer.transliterateToken("LOGOS"))
        assertEquals("Θεος", GreekRomanizer.transliterateToken("Theos"))
        assertEquals("Α", GreekRomanizer.transliterateToken("A"))
        assertEquals("Ω", GreekRomanizer.transliterateToken("W"))
    }

    @Test
    fun testPunctuation() {
        assertEquals(";", GreekRomanizer.transliterateToken("?"))
        assertEquals(";", GreekRomanizer.transliterateToken(";"))
        assertEquals("·", GreekRomanizer.transliterateToken(":"))
        assertEquals("·", GreekRomanizer.transliterateToken("."))
    }

    @Test
    fun testClassicalWords() {
        // "philosophia" -> φ + ι + λ + ο + σ + ο + φ + ι + α = φιλοσοφια
        assertEquals("φιλοσοφια", GreekRomanizer.transliterateToken("philosophia"))
        // "demokratia" -> δ + η + μ + ο + κ + ρ + α + τ + ι + α = δημοκρατια
        assertEquals("δημοκρατια", GreekRomanizer.transliterateToken("dēmokratia"))
    }

    @Test
    fun testCompositionPreviews() {
        val previews = GreekRomanizer.getCompositionPreviews("a")
        assertTrue(previews.isNotEmpty())
        assertEquals("a", previews[0].latinInput)
        assertEquals("α", previews[0].scriptResult)
    }
}

