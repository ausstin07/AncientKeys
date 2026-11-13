package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.learn.HistoricalLibraryData
import com.ancientkeys.keyboard.model.ResearchRecord
import com.ancientkeys.keyboard.ui.detectScriptsInText
import com.ancientkeys.keyboard.ui.getCodePointsList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit verification tests for Phase F UI/UX modernization across all application screens.
 *
 * Validates:
 * - Script detection engine for IME Test Lab
 * - Unicode code point inspection and hex formatting
 * - Historical Library Data integrity, profiles, articles, and practice tasks
 * - Research Record model and search query filtering logic
 * - ScreenMode navigation enumeration consistency
 */
class PhaseFModernizationTest {

    @Test
    fun testDetectScriptsInText_allHistoricalScripts() {
        // Latin / ASCII
        val latin = detectScriptsInText("Hello Ancient World 123")
        assertTrue(latin.contains("Latin / ASCII"))

        // Ancient Greek
        val greek = detectScriptsInText("λόγος")
        assertTrue(greek.contains("Ancient Greek"))

        // Elder Futhark Runic
        val runic = detectScriptsInText("ᚠᚢᚦᚨᚱᚲ")
        assertTrue(runic.contains("Elder Futhark Runic"))

        // Phoenician
        val phoenician = detectScriptsInText("𐤀𐤁𐤂")
        assertTrue(phoenician.contains("Phoenician (RTL)"))

        // Kharosthi
        val kharosthi = detectScriptsInText("𐨦\uD802\uDE02𐨢")
        assertTrue(kharosthi.contains("Kharosthi (RTL)"))

        // Brahmi
        val brahmi = detectScriptsInText("𑀅𑀰𑁄𑀓")
        assertTrue(brahmi.contains("Brahmi"))

        // Egyptian Hieroglyphs
        val egyptian = detectScriptsInText("𓋹𓍑𓋴")
        assertTrue(egyptian.contains("Egyptian Hieroglyphs"))
    }

    @Test
    fun testDetectScriptsInText_emptyAndMixed() {
        // Empty
        assertTrue(detectScriptsInText("").isEmpty())

        // Mixed Latin + Greek + Hieroglyphs
        val mixed = detectScriptsInText("Ancient 🏛️ φ 𓋹")
        assertTrue(mixed.contains("Latin / ASCII"))
        assertTrue(mixed.contains("Ancient Greek"))
        assertTrue(mixed.contains("Egyptian Hieroglyphs"))
    }

    @Test
    fun testGetCodePointsList_formattingAndTruncation() {
        // Empty
        assertTrue(getCodePointsList("").isEmpty())

        // Single BMP character
        val singleBmp = getCodePointsList("A")
        assertEquals(listOf("A (U+0041)"), singleBmp)

        // Supplementary plane character (Egyptian Ankh U+132F9)
        val supplementary = getCodePointsList("𓋹")
        assertEquals(listOf("𓋹 (U+132F9)"), supplementary)

        // Truncation at maxCount
        val longText = "ABCDEFGHIJKLMNO"
        val truncated = getCodePointsList(longText, maxCount = 5)
        assertEquals(6, truncated.size) // 5 items + "…"
        assertEquals("…", truncated.last())
        assertEquals("A (U+0041)", truncated[0])
        assertEquals("E (U+0045)", truncated[4])
    }

    @Test
    fun testHistoricalLibraryData_completeness() {
        val profiles = HistoricalLibraryData.profiles
        assertEquals(6, profiles.size)

        val scriptIds = setOf("egyptian", "brahmi", "kharosthi", "greek", "phoenician", "runic")
        profiles.forEach { profile ->
            assertTrue(scriptIds.contains(profile.scriptId))
            assertTrue("Name must not be empty", profile.name.isNotBlank())
            assertTrue("Period must not be empty", profile.period.isNotBlank())
            assertTrue("Geographic distribution must not be empty", profile.geographicDistribution.isNotBlank())
            assertTrue("Writing direction must not be empty", profile.writingDirection.isNotBlank())
            assertTrue("System type must not be empty", profile.systemType.isNotBlank())
            assertTrue("Consonant mechanics must not be empty", profile.consonantVowelMechanics.isNotBlank())
            assertTrue("Numeral conventions must not be empty", profile.numeralConventions.isNotBlank())
            assertTrue("Important inscriptions must be non-empty", profile.importantInscriptions.isNotEmpty())
            assertTrue("Key developments must be non-empty", profile.keyHistoricalDevelopments.isNotEmpty())
            assertTrue("Interesting facts must be non-empty", profile.interestingFacts.isNotEmpty())
            assertTrue("Interactive examples must be non-empty", profile.interactiveExamples.isNotEmpty())
        }
    }

    @Test
    fun testHistoricalLibraryData_lookupAndArticles() {
        assertNotNull(HistoricalLibraryData.getProfileById("GREEK"))
        assertNotNull(HistoricalLibraryData.getProfileById("egyptian"))
        assertNotNull(HistoricalLibraryData.getProfileById("BRAHMI"))
        assertNull(HistoricalLibraryData.getProfileById("non_existent_script"))

        val articles = HistoricalLibraryData.foundationalArticles
        assertTrue(articles.size >= 3)
        articles.forEach { article ->
            assertTrue(article.id.isNotBlank())
            assertTrue(article.title.isNotBlank())
            assertTrue(article.subtitle.isNotBlank())
            assertTrue(article.summary.isNotBlank())
            assertTrue(article.fullMarkdown.isNotBlank())
        }
    }

    @Test
    fun testResearchRecord_searchFiltering() {
        val record = ResearchRecord(
            id = "rec_001",
            title = "Rosetta Inscription Line 1",
            originalText = "𓋹𓍑𓋴",
            scriptId = "egyptian",
            academicTransliteration = "ankh wedja seneb",
            researcherTranslation = "Life, Prosperity, Health",
            sourceReference = "Urk. IV, 123",
            notes = "Standard royal epithet"
        )

        // Case-insensitive query matching
        fun matchesQuery(rec: ResearchRecord, q: String): Boolean {
            val lower = q.lowercase()
            return rec.title.lowercase().contains(lower) ||
                    rec.originalText.lowercase().contains(lower) ||
                    rec.academicTransliteration.lowercase().contains(lower) ||
                    rec.researcherTranslation.lowercase().contains(lower) ||
                    rec.sourceReference.lowercase().contains(lower) ||
                    rec.notes.lowercase().contains(lower)
        }

        assertTrue(matchesQuery(record, "rosetta"))
        assertTrue(matchesQuery(record, "𓋹"))
        assertTrue(matchesQuery(record, "ANKH"))
        assertTrue(matchesQuery(record, "prosperity"))
        assertTrue(matchesQuery(record, "urk. iv"))
        assertTrue(matchesQuery(record, "epithet"))
        assertTrue(!matchesQuery(record, "Linear B"))
    }

    @Test
    fun testScreenMode_completeCoverage() {
        val expectedModes = setOf(
            ScreenMode.HOME,
            ScreenMode.PLAYGROUND,
            ScreenMode.LEARN,
            ScreenMode.EXPLORER,
            ScreenMode.RESEARCH_WORKSPACE,
            ScreenMode.TEST_LAB,
            ScreenMode.ABOUT,
            ScreenMode.SETTINGS
        )
        val allModes = ScreenMode.values().toSet()
        assertEquals(expectedModes, allModes)
        assertEquals(8, allModes.size)
    }
}

