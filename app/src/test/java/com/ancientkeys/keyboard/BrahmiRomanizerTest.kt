package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.romanization.BrahmiRomanizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Brahmi Romanized Input transliteration engine.
 * Verifies academic IAST / Harvard-Kyoto mapping to authentic Brahmi Unicode code points.
 */
class BrahmiRomanizerTest {

    @Test
    fun testIndependentVowels() {
        assertEquals("𑀅", BrahmiRomanizer.transliterateToken("a"))
        assertEquals("𑀆", BrahmiRomanizer.transliterateToken("ā"))
        assertEquals("𑀆", BrahmiRomanizer.transliterateToken("aa"))
        assertEquals("𑀇", BrahmiRomanizer.transliterateToken("i"))
        assertEquals("𑀈", BrahmiRomanizer.transliterateToken("ī"))
        assertEquals("𑀈", BrahmiRomanizer.transliterateToken("ii"))
        assertEquals("𑀉", BrahmiRomanizer.transliterateToken("u"))
        assertEquals("𑀊", BrahmiRomanizer.transliterateToken("ū"))
        assertEquals("𑀊", BrahmiRomanizer.transliterateToken("uu"))
        assertEquals("𑀋", BrahmiRomanizer.transliterateToken("ṛ"))
        assertEquals("𑀏", BrahmiRomanizer.transliterateToken("e"))
        assertEquals("𑀐", BrahmiRomanizer.transliterateToken("ai"))
        assertEquals("𑀑", BrahmiRomanizer.transliterateToken("o"))
        assertEquals("𑀒", BrahmiRomanizer.transliterateToken("au"))
    }

    @Test
    fun testConsonantsWithInherentA() {
        assertEquals("𑀓", BrahmiRomanizer.transliterateToken("k"))
        assertEquals("𑀓", BrahmiRomanizer.transliterateToken("ka"))
        assertEquals("𑀔", BrahmiRomanizer.transliterateToken("kha"))
        assertEquals("𑀕", BrahmiRomanizer.transliterateToken("ga"))
        assertEquals("𑀖", BrahmiRomanizer.transliterateToken("gha"))
        assertEquals("𑀘", BrahmiRomanizer.transliterateToken("ca"))
        assertEquals("𑀙", BrahmiRomanizer.transliterateToken("cha"))
        assertEquals("𑀚", BrahmiRomanizer.transliterateToken("ja"))
        assertEquals("𑀛", BrahmiRomanizer.transliterateToken("jha"))
        assertEquals("𑀝", BrahmiRomanizer.transliterateToken("ṭa"))
        assertEquals("𑀞", BrahmiRomanizer.transliterateToken("ṭha"))
        assertEquals("𑀟", BrahmiRomanizer.transliterateToken("ḍa"))
        assertEquals("𑀠", BrahmiRomanizer.transliterateToken("ḍha"))
        assertEquals("𑀡", BrahmiRomanizer.transliterateToken("ṇa"))
        assertEquals("𑀢", BrahmiRomanizer.transliterateToken("ta"))
        assertEquals("𑀣", BrahmiRomanizer.transliterateToken("tha"))
        assertEquals("𑀤", BrahmiRomanizer.transliterateToken("da"))
        assertEquals("𑀥", BrahmiRomanizer.transliterateToken("dha"))
        assertEquals("𑀦", BrahmiRomanizer.transliterateToken("na"))
        assertEquals("𑀧", BrahmiRomanizer.transliterateToken("pa"))
        assertEquals("𑀨", BrahmiRomanizer.transliterateToken("pha"))
        assertEquals("𑀩", BrahmiRomanizer.transliterateToken("ba"))
        assertEquals("𑀪", BrahmiRomanizer.transliterateToken("bha"))
        assertEquals("𑀫", BrahmiRomanizer.transliterateToken("ma"))
        assertEquals("𑀬", BrahmiRomanizer.transliterateToken("ya"))
        assertEquals("𑀭", BrahmiRomanizer.transliterateToken("ra"))
        assertEquals("𑀮", BrahmiRomanizer.transliterateToken("la"))
        assertEquals("𑀯", BrahmiRomanizer.transliterateToken("va"))
        assertEquals("𑀰", BrahmiRomanizer.transliterateToken("śa"))
        assertEquals("𑀱", BrahmiRomanizer.transliterateToken("ṣa"))
        assertEquals("𑀲", BrahmiRomanizer.transliterateToken("sa"))
        assertEquals("𑀳", BrahmiRomanizer.transliterateToken("ha"))
    }

    @Test
    fun testDependentVowelSigns() {
        assertEquals("𑀓\uD804\uDC38", BrahmiRomanizer.transliterateToken("kā"))
        assertEquals("𑀓\uD804\uDC38", BrahmiRomanizer.transliterateToken("kaa"))
        assertEquals("𑀓\uD804\uDC39", BrahmiRomanizer.transliterateToken("ki"))
        assertEquals("𑀓\uD804\uDC3A", BrahmiRomanizer.transliterateToken("kī"))
        assertEquals("𑀓\uD804\uDC3A", BrahmiRomanizer.transliterateToken("kii"))
        assertEquals("𑀓\uD804\uDC3B", BrahmiRomanizer.transliterateToken("ku"))
        assertEquals("𑀓\uD804\uDC3C", BrahmiRomanizer.transliterateToken("kū"))
        assertEquals("𑀓\uD804\uDC3C", BrahmiRomanizer.transliterateToken("kuu"))
        assertEquals("𑀓\uD804\uDC3E", BrahmiRomanizer.transliterateToken("ke"))
        assertEquals("𑀓\uD804\uDC3F", BrahmiRomanizer.transliterateToken("kai"))
        assertEquals("𑀓\uD804\uDC40", BrahmiRomanizer.transliterateToken("ko"))
        assertEquals("𑀓\uD804\uDC41", BrahmiRomanizer.transliterateToken("kau"))
    }

    @Test
    fun testModifiersAndPunctuation() {
        assertEquals("𑀓\uD804\uDC01", BrahmiRomanizer.transliterateToken("kaṃ"))
        assertEquals("𑀓\uD804\uDC02", BrahmiRomanizer.transliterateToken("kaḥ"))
        assertEquals("𑁇", BrahmiRomanizer.transliterateToken("|"))
        assertEquals("𑁈", BrahmiRomanizer.transliterateToken("||"))
    }

    @Test
    fun testHistoricalWords() {
        // "asoka" -> 'a' (𑀅) + 'so' (𑀲𑁄) + 'ka' (𑀓)
        val asoka = BrahmiRomanizer.transliterateToken("asoka")
        assertEquals("𑀅𑀲\uD804\uDC40𑀓", asoka)

        // "deva" -> 'de' (𑀤𑀾) + 'va' (𑀯)
        val deva = BrahmiRomanizer.transliterateToken("deva")
        assertEquals("𑀤\uD804\uDC3E𑀯", deva)
    }

    @Test
    fun testCompositionPreviews() {
        val previews = BrahmiRomanizer.getCompositionPreviews("k")
        assertEquals(12, previews.size)
        assertEquals("k", previews[0].latinInput)
        assertEquals("𑀓", previews[0].brahmiResult)
        assertEquals("kā", previews[1].latinInput)
        assertEquals("𑀓\uD804\uDC38", previews[1].brahmiResult)

        // Verify destructuring support
        val (rom, glyph) = previews[0]
        assertEquals("k", rom)
        assertEquals("𑀓", glyph)

        // Invalid key returns empty list
        assertTrue(BrahmiRomanizer.getCompositionPreviews("invalid").isEmpty())
    }

    @Test
    fun testCaseInsensitiveAndEmptyHandling() {
        assertEquals("", BrahmiRomanizer.transliterateToken(""))
        assertEquals("", BrahmiRomanizer.transliterateToken("   "))
        assertEquals("𑀓", BrahmiRomanizer.transliterateToken("KA"))
        assertEquals("𑀓\uD804\uDC39", BrahmiRomanizer.transliterateToken("Ki"))
    }
}
