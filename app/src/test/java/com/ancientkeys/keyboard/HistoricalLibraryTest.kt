package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.learn.HistoricalLibraryData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying data integrity, citations, and content of the Historical Learning Library.
 */
class HistoricalLibraryTest {

    @Test
    fun testAllSixHistoricalProfilesPresent() {
        val profiles = HistoricalLibraryData.profiles
        assertEquals(6, profiles.size)

        val ids = profiles.map { it.scriptId }.toSet()
        val expectedIds = setOf("egyptian", "brahmi", "greek", "runic", "phoenician", "kharosthi")
        assertEquals(expectedIds, ids)
    }

    @Test
    fun testProfilesContainCitationsAndInscriptions() {
        for (p in HistoricalLibraryData.profiles) {
            assertTrue("${p.name} must specify historical period", p.period.isNotBlank())
            assertTrue("${p.name} must specify geographic distribution", p.geographicDistribution.isNotBlank())
            assertTrue("${p.name} must specify writing direction", p.writingDirection.isNotBlank())
            assertTrue("${p.name} must specify system type", p.systemType.isNotBlank())
            assertTrue("${p.name} must specify languages written", p.languagesWritten.isNotEmpty())
            assertTrue("${p.name} must cite at least one primary inscription", p.importantInscriptions.isNotEmpty())
            assertTrue("${p.name} must explain consonant/vowel mechanics", p.consonantVowelMechanics.isNotBlank())
            assertTrue("${p.name} must describe numeral conventions", p.numeralConventions.isNotBlank())
            assertTrue("${p.name} must provide historical milestones", p.keyHistoricalDevelopments.isNotEmpty())
            assertTrue("${p.name} must provide interesting facts", p.interestingFacts.isNotEmpty())
            assertTrue("${p.name} must cite scholarly publications", p.scholarlySources.isNotEmpty())
            assertTrue("${p.name} must provide interactive examples", p.interactiveExamples.isNotEmpty())
        }
    }

    @Test
    fun testFoundationalArticlesIntegrity() {
        val articles = HistoricalLibraryData.foundationalArticles
        assertEquals("Must contain 3 educational foundation articles", 3, articles.size)

        val articleIds = articles.map { it.id }.toSet()
        assertTrue(articleIds.contains("script_vs_language"))
        assertTrue(articleIds.contains("phonetic_vs_transliteration"))
        assertTrue(articleIds.contains("transliteration_vs_translation"))

        for (a in articles) {
            assertTrue(a.title.isNotBlank())
            assertTrue(a.subtitle.isNotBlank())
            assertTrue(a.summary.isNotBlank())
            assertTrue(a.fullMarkdown.length > 100)
        }
    }

    @Test
    fun testGetProfileById() {
        assertNotNull(HistoricalLibraryData.getProfileById("brahmi"))
        assertNotNull(HistoricalLibraryData.getProfileById("BRAHMI"))
        assertNotNull(HistoricalLibraryData.getProfileById("egyptian"))
        assertNotNull(HistoricalLibraryData.getProfileById("greek"))
        assertNotNull(HistoricalLibraryData.getProfileById("kharosthi"))
        assertNotNull(HistoricalLibraryData.getProfileById("phoenician"))
        assertNotNull(HistoricalLibraryData.getProfileById("runic"))
        assertEquals(null, HistoricalLibraryData.getProfileById("unknown"))
    }
}

