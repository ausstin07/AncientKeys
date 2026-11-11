package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.romanization.KharosthiRomanizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Kharosthi (Gāndhārī Prakrit) Romanized Input transliteration engine.
 * Verifies academic epigraphic conventions established by Salomon (1999) and Glass (2000).
 */
class KharosthiRomanizerTest {

    @Test
    fun testIndependentVowels() {
        assertEquals("𐨀", KharosthiRomanizer.transliterateToken("a"))
        assertEquals("𐨀", KharosthiRomanizer.transliterateToken("ā"))
        assertEquals("𐨀\uD802\uDE01", KharosthiRomanizer.transliterateToken("i"))
        assertEquals("𐨀\uD802\uDE01", KharosthiRomanizer.transliterateToken("ī"))
        assertEquals("𐨀\uD802\uDE02", KharosthiRomanizer.transliterateToken("u"))
        assertEquals("𐨀\uD802\uDE02", KharosthiRomanizer.transliterateToken("ū"))
        assertEquals("𐨀\uD802\uDE03", KharosthiRomanizer.transliterateToken("ṛ"))
        assertEquals("𐨀\uD802\uDE05", KharosthiRomanizer.transliterateToken("e"))
        assertEquals("𐨀\uD802\uDE06", KharosthiRomanizer.transliterateToken("o"))
    }

    @Test
    fun testConsonantsWithInherentA() {
        assertEquals("𐨐", KharosthiRomanizer.transliterateToken("k"))
        assertEquals("𐨐", KharosthiRomanizer.transliterateToken("ka"))
        assertEquals("𐨑", KharosthiRomanizer.transliterateToken("kha"))
        assertEquals("𐨒", KharosthiRomanizer.transliterateToken("ga"))
        assertEquals("𐨓", KharosthiRomanizer.transliterateToken("gha"))
        assertEquals("𐨕", KharosthiRomanizer.transliterateToken("ca"))
        assertEquals("𐨖", KharosthiRomanizer.transliterateToken("cha"))
        assertEquals("𐨗", KharosthiRomanizer.transliterateToken("ja"))
        assertEquals("𐨙", KharosthiRomanizer.transliterateToken("ña"))
        assertEquals("𐨚", KharosthiRomanizer.transliterateToken("ṭa"))
        assertEquals("𐨛", KharosthiRomanizer.transliterateToken("ṭha"))
        assertEquals("𐨜", KharosthiRomanizer.transliterateToken("ḍa"))
        assertEquals("𐨝", KharosthiRomanizer.transliterateToken("ḍha"))
        assertEquals("𐨞", KharosthiRomanizer.transliterateToken("ṇa"))
        assertEquals("𐨟", KharosthiRomanizer.transliterateToken("ta"))
        assertEquals("𐨠", KharosthiRomanizer.transliterateToken("tha"))
        assertEquals("𐨡", KharosthiRomanizer.transliterateToken("da"))
        assertEquals("𐨢", KharosthiRomanizer.transliterateToken("dha"))
        assertEquals("𐨣", KharosthiRomanizer.transliterateToken("na"))
        assertEquals("𐨤", KharosthiRomanizer.transliterateToken("pa"))
        assertEquals("𐨥", KharosthiRomanizer.transliterateToken("pha"))
        assertEquals("𐨦", KharosthiRomanizer.transliterateToken("ba"))
        assertEquals("𐨧", KharosthiRomanizer.transliterateToken("bha"))
        assertEquals("𐨨", KharosthiRomanizer.transliterateToken("ma"))
        assertEquals("𐨩", KharosthiRomanizer.transliterateToken("ya"))
        assertEquals("𐨪", KharosthiRomanizer.transliterateToken("ra"))
        assertEquals("𐨫", KharosthiRomanizer.transliterateToken("la"))
        assertEquals("𐨬", KharosthiRomanizer.transliterateToken("va"))
        assertEquals("𐨭", KharosthiRomanizer.transliterateToken("śa"))
        assertEquals("𐨮", KharosthiRomanizer.transliterateToken("ṣa"))
        assertEquals("𐨯", KharosthiRomanizer.transliterateToken("sa"))
        assertEquals("𐨰", KharosthiRomanizer.transliterateToken("za"))
        assertEquals("𐨱", KharosthiRomanizer.transliterateToken("ha"))
    }

    @Test
    fun testDependentVowelSigns() {
        assertEquals("𐨐\uD802\uDE01", KharosthiRomanizer.transliterateToken("ki"))
        assertEquals("𐨐\uD802\uDE02", KharosthiRomanizer.transliterateToken("ku"))
        assertEquals("𐨐\uD802\uDE03", KharosthiRomanizer.transliterateToken("kṛ"))
        assertEquals("𐨐\uD802\uDE05", KharosthiRomanizer.transliterateToken("ke"))
        assertEquals("𐨐\uD802\uDE06", KharosthiRomanizer.transliterateToken("ko"))
    }

    @Test
    fun testModifiersAndPunctuation() {
        assertEquals("𐨐\uD802\uDE0E", KharosthiRomanizer.transliterateToken("kaṃ"))
        assertEquals("𐨐\uD802\uDE0F", KharosthiRomanizer.transliterateToken("kaḥ"))
        assertEquals("𐨐\uD802\uDE3F", KharosthiRomanizer.transliterateToken("k_"))
        assertEquals("𐩐", KharosthiRomanizer.transliterateToken("|"))
        assertEquals("𐩑", KharosthiRomanizer.transliterateToken("||"))
    }

    @Test
    fun testGandharanNumerals() {
        assertEquals("𐩀", KharosthiRomanizer.transliterateToken("1"))
        assertEquals("𐩁", KharosthiRomanizer.transliterateToken("2"))
        assertEquals("𐩂", KharosthiRomanizer.transliterateToken("3"))
        assertEquals("𐩃", KharosthiRomanizer.transliterateToken("4"))
        assertEquals("𐩄", KharosthiRomanizer.transliterateToken("10"))
        assertEquals("𐩅", KharosthiRomanizer.transliterateToken("20"))
        assertEquals("𐩆", KharosthiRomanizer.transliterateToken("100"))
        assertEquals("𐩇", KharosthiRomanizer.transliterateToken("1000"))
    }

    @Test
    fun testGandharanWords() {
        // "dharma" -> 'dha' (𐨢) + 'r' (𐨪) + 'ma' (𐨨)
        assertEquals("𐨢𐨪𐨨", KharosthiRomanizer.transliterateToken("dharma"))
        // "budha" -> 'bu' (𐨦\uD802\uDE02) + 'dha' (𐨢)
        assertEquals("𐨦\uD802\uDE02𐨢", KharosthiRomanizer.transliterateToken("budha"))
    }

    @Test
    fun testCompositionPreviews() {
        val previews = KharosthiRomanizer.getCompositionPreviews("k")
        assertEquals(9, previews.size)
        assertEquals("k", previews[0].latinInput)
        assertEquals("𐨐", previews[0].scriptResult)
        assertEquals("ki", previews[1].latinInput)
        assertEquals("𐨐\uD802\uDE01", previews[1].scriptResult)
    }
}

