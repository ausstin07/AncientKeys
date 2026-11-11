package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.data.AncientGreekScript
import com.ancientkeys.keyboard.data.BrahmiScript
import com.ancientkeys.keyboard.data.EgyptianHieroglyphsScript
import com.ancientkeys.keyboard.data.KharosthiScript
import com.ancientkeys.keyboard.data.PhoenicianScript
import com.ancientkeys.keyboard.data.RunicScript
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.search.ScriptSearchEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScriptSearchEngineTest {

    // --- Part A: Gandhara & Kharosthi Search Defect Regression Tests ---

    @Test
    fun testSearchKharosthiByAllVariantNamesAndDiacritics() {
        val testQueries = listOf(
            "Gandhara",
            "Gandhāra",
            "gandhara",
            "gandhāra",
            "Gandhari",
            "Gāndhārī",
            "gandhari",
            "gāndhārī",
            "Kharosthi",
            "Kharoṣṭhī",
            "kharosthi",
            "kharoṣṭhī"
        )

        for (query in testQueries) {
            val results = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, query)
            assertTrue("Query '$query' should match Kharosthi", results.any { it.id == "kharosthi" })
        }
    }

    @Test
    fun testSearchOtherScripts() {
        // Greek
        val greekResults = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Greek")
        assertTrue(greekResults.any { it.id == "greek" })

        val ancientGreekResults = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Ancient Greek")
        assertEquals(1, ancientGreekResults.size)
        assertEquals("greek", ancientGreekResults[0].id)

        // Futhark & Runic
        val futharkResults = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Futhark")
        assertTrue(futharkResults.any { it.id == "runic" })

        val futharkThornResults = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Fuþark")
        assertTrue(futharkThornResults.any { it.id == "runic" })

        // Brahmi (plain and diacritic)
        val brahmiResults = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Brahmi")
        assertTrue(brahmiResults.any { it.id == "brahmi" })

        val brahmiMacronResults = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Brāhmī")
        assertTrue(brahmiMacronResults.any { it.id == "brahmi" })

        // Phoenician
        val phoenicianResults = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Phoenician")
        assertTrue(phoenicianResults.any { it.id == "phoenician" })

        val punicResults = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Punic")
        assertTrue(punicResults.any { it.id == "phoenician" })

        // Egyptian
        val egyptianResults = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Egyptian")
        assertTrue(egyptianResults.any { it.id == "egyptian" })

        val hieroglyphsResults = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Hieroglyphs")
        assertTrue(hieroglyphsResults.any { it.id == "egyptian" })
    }

    @Test
    fun testSearchScriptsByRegion() {
        val levant = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Levant")
        assertTrue(levant.any { it.id == "phoenician" })

        val nile = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Nile")
        assertTrue(nile.any { it.id == "egyptian" })

        val europe = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "Europe")
        assertTrue(europe.any { it.id == "runic" })
    }

    // --- Part B: Character Search Regression Tests ---

    @Test
    fun testSearchCharactersByCodePointVariations() {
        // Test U+0391 (Greek Capital Alpha)
        val greekKeys = AncientGreekScript.keys
        val matchU0391 = ScriptSearchEngine.searchCharacters(greekKeys, "U+0391")
        assertEquals(1, matchU0391.size)
        assertEquals("Α", matchU0391[0].character)

        val match0391 = ScriptSearchEngine.searchCharacters(greekKeys, "0391")
        assertEquals(1, match0391.size)
        assertEquals("Α", match0391[0].character)

        // Test U+11013 (Brahmi Letter Ka)
        val brahmiKeys = BrahmiScript.keys
        val matchU11013 = ScriptSearchEngine.searchCharacters(brahmiKeys, "U+11013")
        assertEquals(1, matchU11013.size)
        assertEquals("𑀓", matchU11013[0].character)

        val match11013 = ScriptSearchEngine.searchCharacters(brahmiKeys, "11013")
        assertEquals(1, match11013.size)
        assertEquals("𑀓", match11013[0].character)

        // Test U+16A0 (Runic Fehu)
        val runicKeys = RunicScript.keys
        val matchU16A0 = ScriptSearchEngine.searchCharacters(runicKeys, "U+16A0")
        assertEquals(1, matchU16A0.size)
        assertEquals("ᚠ", matchU16A0[0].character)

        val match16A0 = ScriptSearchEngine.searchCharacters(runicKeys, "16a0")
        assertEquals(1, match16A0.size)
        assertEquals("ᚠ", match16A0[0].character)
    }

    @Test
    fun testSearchCharactersByUnicodeName() {
        val greekKeys = AncientGreekScript.keys
        val alphaMatches = ScriptSearchEngine.searchCharacters(greekKeys, "ALPHA")
        assertTrue(alphaMatches.size >= 2)
        assertTrue(alphaMatches.any { it.character == "Α" })
        assertTrue(alphaMatches.any { it.character == "α" })

        val digammaMatches = ScriptSearchEngine.searchCharacters(greekKeys, "DIGAMMA")
        assertTrue(digammaMatches.any { it.character == "Ϝ" })
    }

    @Test
    fun testSearchCharactersByMeaningAndTransliteration() {
        val phoenicianKeys = PhoenicianScript.keys
        val oxMatches = ScriptSearchEngine.searchCharacters(phoenicianKeys, "ox")
        assertTrue(oxMatches.any { it.character == "𐤀" })

        // Diacritic insensitive transliteration search
        val alefWithDiacritic = ScriptSearchEngine.searchCharacters(phoenicianKeys, "ʾālep")
        assertTrue(alefWithDiacritic.any { it.character == "𐤀" })

        val alefPlain = ScriptSearchEngine.searchCharacters(phoenicianKeys, "alep")
        assertTrue(alefPlain.any { it.character == "𐤀" })
    }

    @Test
    fun testEmptySearchReturnsAll() {
        val allScripts = ScriptRegistry.allScripts
        assertEquals(allScripts.size, ScriptSearchEngine.searchScripts(allScripts, "").size)
        assertEquals(allScripts.size, ScriptSearchEngine.searchScripts(allScripts, "   ").size)

        val runicKeys = RunicScript.keys
        assertEquals(runicKeys.size, ScriptSearchEngine.searchCharacters(runicKeys, "").size)
    }

    @Test
    fun testNonMatchingSearchReturnsEmpty() {
        val results = ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, "xyz999nonexistent")
        assertTrue(results.isEmpty())

        val charResults = ScriptSearchEngine.searchCharacters(RunicScript.keys, "zzznonexistent")
        assertTrue(charResults.isEmpty())
    }
}
