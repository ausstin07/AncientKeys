package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.data.LongPressAlternativesData
import com.ancientkeys.keyboard.ui.TestLabValidator
import com.ancientkeys.keyboard.util.HistoricalFontProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for bundled fallback font mapping, scholarly long-press diacritics,
 * and Test Lab validator diagnostics.
 */
class HistoricalFontAndAlternativesTest {

    @Test
    fun testFontAssetPaths() {
        assertEquals("fonts/NotoSansBrahmi-Regular.ttf", HistoricalFontProvider.getAssetPathForScript("brahmi"))
        assertEquals("fonts/NotoSansEgyptianHieroglyphs-Regular.ttf", HistoricalFontProvider.getAssetPathForScript("egyptian"))
        assertEquals("fonts/NotoSansEgyptianHieroglyphs-Regular.ttf", HistoricalFontProvider.getAssetPathForScript("egyptian_hieroglyphs"))
        assertEquals("fonts/NotoSansKharosthi-Regular.ttf", HistoricalFontProvider.getAssetPathForScript("kharosthi"))
        assertEquals("fonts/NotoSansPhoenician-Regular.ttf", HistoricalFontProvider.getAssetPathForScript("phoenician"))
        assertEquals("fonts/NotoSansRunic-Regular.ttf", HistoricalFontProvider.getAssetPathForScript("runic"))

        // Ancient Greek relies on standard system Noto Sans / Roboto serif which has full Greek coverage
        assertNull(HistoricalFontProvider.getAssetPathForScript("ancient_greek"))
        assertNull(HistoricalFontProvider.getAssetPathForScript("unknown_script"))
    }

    @Test
    fun testLongPressScholarlyDiacriticsLatin() {
        // Vowels with macrons
        assertTrue(LongPressAlternativesData.hasAlternatives("a"))
        val aAlts = LongPressAlternativesData.getAlternatives("a")
        assertTrue("Expected macron 'ā' in alternatives for 'a'", aAlts.contains("ā"))

        assertTrue(LongPressAlternativesData.hasAlternatives("i"))
        val iAlts = LongPressAlternativesData.getAlternatives("i")
        assertTrue("Expected macron 'ī' in alternatives for 'i'", iAlts.contains("ī"))

        assertTrue(LongPressAlternativesData.hasAlternatives("u"))
        val uAlts = LongPressAlternativesData.getAlternatives("u")
        assertTrue("Expected macron 'ū' in alternatives for 'u'", uAlts.contains("ū"))

        // Consonants with retroflex underdots and scholarly diacritics
        assertTrue(LongPressAlternativesData.hasAlternatives("t"))
        assertTrue("Expected underdot 'ṭ' for 't'", LongPressAlternativesData.getAlternatives("t").contains("ṭ"))

        assertTrue(LongPressAlternativesData.hasAlternatives("d"))
        assertTrue("Expected underdot 'ḍ' for 'd'", LongPressAlternativesData.getAlternatives("d").contains("ḍ"))

        assertTrue(LongPressAlternativesData.hasAlternatives("n"))
        val nAlts = LongPressAlternativesData.getAlternatives("n")
        assertTrue("Expected retroflex 'ṇ'", nAlts.contains("ṇ"))
        assertTrue("Expected velar 'ṅ'", nAlts.contains("ṅ"))
        assertTrue("Expected palatal 'ñ'", nAlts.contains("ñ"))

        assertTrue(LongPressAlternativesData.hasAlternatives("s"))
        val sAlts = LongPressAlternativesData.getAlternatives("s")
        assertTrue("Expected retroflex 'ṣ'", sAlts.contains("ṣ"))
        assertTrue("Expected palatal 'ś'", sAlts.contains("ś"))

        assertTrue(LongPressAlternativesData.hasAlternatives("m"))
        assertTrue("Expected anusvara 'ṃ'", LongPressAlternativesData.getAlternatives("m").contains("ṃ"))

        assertTrue(LongPressAlternativesData.hasAlternatives("h"))
        assertTrue("Expected visarga 'ḥ'", LongPressAlternativesData.getAlternatives("h").contains("ḥ"))

        assertTrue(LongPressAlternativesData.hasAlternatives("r"))
        assertTrue("Expected syllabic 'ṛ'", LongPressAlternativesData.getAlternatives("r").contains("ṛ"))

        assertTrue(LongPressAlternativesData.hasAlternatives("l"))
        assertTrue("Expected 'ḷ'", LongPressAlternativesData.getAlternatives("l").contains("ḷ"))
    }

    @Test
    fun testLongPressGreekPolytonic() {
        assertTrue(LongPressAlternativesData.hasAlternatives("α"))
        val alphaAlts = LongPressAlternativesData.getAlternatives("α")
        assertTrue("Expected oxia 'ά'", alphaAlts.contains("ά"))
        assertTrue("Expected varia 'ὰ'", alphaAlts.contains("ὰ"))
        assertTrue("Expected perispomeni 'ᾶ'", alphaAlts.contains("ᾶ"))

        assertTrue(LongPressAlternativesData.hasAlternatives("ω"))
        val omegaAlts = LongPressAlternativesData.getAlternatives("ω")
        assertTrue("Expected 'ώ'", omegaAlts.contains("ώ"))
        assertTrue("Expected 'ῶ'", omegaAlts.contains("ῶ"))
    }

    @Test
    fun testUppercaseAlternativePreservation() {
        val upperA = LongPressAlternativesData.getAlternatives("A")
        assertTrue("Expected uppercase macron 'Ā'", upperA.contains("Ā"))
        val upperT = LongPressAlternativesData.getAlternatives("T")
        assertTrue("Expected uppercase underdot 'Ṭ'", upperT.contains("Ṭ"))
    }

    @Test
    fun testTestLabValidatorFullAudit() {
        val results = TestLabValidator.runFullAudit()
        assertNotNull(results)
        assertTrue("Audit should evaluate at least 5 subsystem checks", results.size >= 5)

        val failedChecks = results.filter { !it.passed }
        assertTrue("All self-diagnostic checks must pass, but failed: ${failedChecks.map { it.name }}", failedChecks.isEmpty())
    }
}
