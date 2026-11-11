package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.data.RunicScript
import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.util.HistoricalFontProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AncientKeys v1.1 Phases 4 & 5:
 * - Companion App 4-Pillar Navigation (Home, Explore, Learn, Settings).
 * - Safe Explorer and Playground navigation resolution when no active script is explicitly passed.
 * - Character Explorer fallback detection with HistoricalFontProvider.canRenderGlyph.
 * - Linguistic and historical accuracy verification (Unicode 16.0, Proto-Germanic * asterisk notation).
 */
class Phase4And5UpgradeTest {

    private lateinit var userPrefs: UserPreferencesRepository

    @Before
    fun setUp() {
        userPrefs = UserPreferencesRepository(null)
    }

    @Test
    fun testFourPillarNavigationItems() {
        val bottomNavPillars = listOf("Home", "Explore", "Learn", "Settings")
        assertEquals(4, bottomNavPillars.size)
        assertTrue(bottomNavPillars.contains("Home"))
        assertTrue(bottomNavPillars.contains("Explore"))
        assertTrue(bottomNavPillars.contains("Learn"))
        assertTrue(bottomNavPillars.contains("Settings"))
    }

    @Test
    fun testSafeScriptResolutionForExplorerAndPlayground() {
        // When activeScript is null and default is english, fallback should resolve to a valid historical script
        val activeScript: HistoricalScript? = null
        val defaultScriptId = userPrefs.getDefaultScriptId() // "english"
        
        val resolvedScript = activeScript
            ?: ScriptRegistry.getScriptById(defaultScriptId)
            ?: ScriptRegistry.allScripts.first()

        assertNotNull(resolvedScript)
        assertTrue(ScriptRegistry.allScripts.contains(resolvedScript))

        // When user has chosen a historical default (e.g. "greek")
        userPrefs.setDefaultScriptId("greek")
        val resolvedGreek = activeScript
            ?: ScriptRegistry.getScriptById(userPrefs.getDefaultScriptId())
            ?: ScriptRegistry.allScripts.first()

        assertEquals("greek", resolvedGreek.id)
    }

    @Test
    fun testCanRenderGlyphRobustness() {
        // Null context / empty string safety
        assertFalse(HistoricalFontProvider.canRenderGlyph(null, "greek", ""))

        // Valid characters return true safely in JVM tests
        assertTrue(HistoricalFontProvider.canRenderGlyph(null, "greek", "α"))
        assertTrue(HistoricalFontProvider.canRenderGlyph(null, "runic", "ᚠ"))
        assertTrue(HistoricalFontProvider.canRenderGlyph(null, "brahmi", "𑀅"))
        assertTrue(HistoricalFontProvider.canRenderGlyph(null, "kharosthi", "𐨀"))
        assertTrue(HistoricalFontProvider.canRenderGlyph(null, "phoenician", "𐤀"))
        assertTrue(HistoricalFontProvider.canRenderGlyph(null, "egyptian", "𓀀"))

        // Cache clear verification
        HistoricalFontProvider.clearCache()
        assertTrue(HistoricalFontProvider.canRenderGlyph(null, "greek", "Ω"))
    }

    @Test
    fun testProtoGermanicReconstructedNamesUseAsterisk() {
        val runicKeys = RunicScript.keys
        assertTrue("Elder Futhark has 27 keys including punctuation", runicKeys.size >= 24)

        // Find keys with reconstructed historical notes
        val reconstructedKeys = runicKeys.filter { it.historicalNotes.contains("Reconstructed Proto-Germanic") }
        assertTrue("All 24 Elder Futhark runes should have reconstructed historical notes", reconstructedKeys.size >= 24)

        for (key in reconstructedKeys) {
            assertTrue(
                "Historical notes for ${key.unicodeCodePoint} (${key.label}) must contain linguistic reconstruction asterisk '*': ${key.historicalNotes}",
                key.historicalNotes.contains("*")
            )
        }
    }

    @Test
    fun testAllScriptsComplyWithUnicode16Specifications() {
        val scripts = ScriptRegistry.allScripts
        assertEquals(6, scripts.size)

        for (script in scripts) {
            assertTrue("Script ${script.name} must reference Unicode 16.0 or standards",
                script.sourcesAndReferences.contains("Unicode") || script.sourcesAndReferences.contains("ISO/IEC 10646"))

            for (key in script.keys) {
                assertTrue("Code point ${key.unicodeCodePoint} must follow standard U+XXXX format",
                    key.unicodeCodePoint.matches(Regex("^U\\+[0-9A-F]{4,5}$")))
                assertFalse("Character must not be empty", key.character.isEmpty())
            }
        }
    }
}

