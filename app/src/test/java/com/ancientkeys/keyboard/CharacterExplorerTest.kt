package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.search.ScriptSearchEngine
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.util.HistoricalFontProvider
import com.ancientkeys.keyboard.util.UnicodeMetadataHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Automated test suite for AncientKeys v1.1 Phase D: Character Explorer Redesign.
 *
 * Verifies:
 * 1. Script switching and category consistency across all 6 historical scripts.
 * 2. Glyph metadata completeness, code point formatting, and official Unicode naming.
 * 3. Character-level favorites toggling and cross-script isolation.
 * 4. Safe combining mark visualization (dotted circle ◌) preserving raw characters.
 * 5. Bundled font asset path resolution.
 * 6. Multi-attribute character search (hex codes, names, transliterations, phonetics).
 */
class CharacterExplorerTest {

    private lateinit var userPrefs: UserPreferencesRepository

    @Before
    fun setUp() {
        userPrefs = UserPreferencesRepository(null)
    }

    @Test
    fun testAllSixScriptsHaveValidCategoriesAndKeys() {
        val scripts = ScriptRegistry.allScripts
        assertEquals(6, scripts.size)

        for (script in scripts) {
            assertTrue("Script ${script.name} must define categories", script.categories.isNotEmpty())
            assertTrue("Script ${script.name} must have keys", script.keys.isNotEmpty())

            // Test category partition validity
            for (category in script.categories) {
                val categoryKeys = script.keys.filter { it.category == category }
                assertTrue("Category '$category' in script '${script.name}' must have keys", categoryKeys.isNotEmpty())
            }
        }
    }

    @Test
    fun testUnicodeCodePointsAndDisplayGlyphsCompleteness() {
        for (script in ScriptRegistry.allScripts) {
            for (key in script.keys) {
                // Must start with U+ and have 4 or 5 hex digits
                assertTrue("Code point '${key.unicodeCodePoint}' in ${script.name} must be valid",
                    key.unicodeCodePoint.matches("^U\\+[0-9A-Fa-f]{4,5}$".toRegex()))

                val displayGlyph = UnicodeMetadataHelper.getDisplayGlyph(key)
                assertTrue("Display glyph for ${key.unicodeCodePoint} must not be blank", displayGlyph.isNotBlank())

                val officialName = UnicodeMetadataHelper.getOfficialUnicodeName(key)
                assertTrue("Official name for ${key.unicodeCodePoint} must not be blank", officialName.isNotBlank())
            }
        }
    }

    @Test
    fun testCharacterLevelFavoritesIsolationAcrossScripts() {
        val greek = ScriptRegistry.getScriptById("greek")!!
        val brahmi = ScriptRegistry.getScriptById("brahmi")!!

        val greekKey = greek.keys.first()
        val brahmiKey = brahmi.keys.first()

        // Initially neither is favorite
        assertFalse(userPrefs.isFavorite(greek.id, greekKey.unicodeCodePoint))
        assertFalse(userPrefs.isFavorite(brahmi.id, brahmiKey.unicodeCodePoint))

        // Favorite Greek key
        userPrefs.toggleFavorite(greek.id, greekKey.unicodeCodePoint)
        assertTrue(userPrefs.isFavorite(greek.id, greekKey.unicodeCodePoint))
        assertFalse(userPrefs.isFavorite(brahmi.id, brahmiKey.unicodeCodePoint))

        assertEquals(setOf(greekKey.unicodeCodePoint), userPrefs.getFavoriteKeysForScript(greek.id))
        assertTrue(userPrefs.getFavoriteKeysForScript(brahmi.id).isEmpty())

        // Favorite Brahmi key
        userPrefs.toggleFavorite(brahmi.id, brahmiKey.unicodeCodePoint)
        assertTrue(userPrefs.isFavorite(greek.id, greekKey.unicodeCodePoint))
        assertTrue(userPrefs.isFavorite(brahmi.id, brahmiKey.unicodeCodePoint))

        assertEquals(2, userPrefs.getTotalFavoritesCount())

        // Unfavorite Greek key
        userPrefs.toggleFavorite(greek.id, greekKey.unicodeCodePoint)
        assertFalse(userPrefs.isFavorite(greek.id, greekKey.unicodeCodePoint))
        assertTrue(userPrefs.isFavorite(brahmi.id, brahmiKey.unicodeCodePoint))
    }

    @Test
    fun testCombiningMarksDisplayWithDottedCircleWithoutCorruptingCharacter() {
        val brahmi = ScriptRegistry.getScriptById("brahmi")!!
        val combiningKeys = brahmi.keys.filter { it.isCombining }

        assertTrue("Brahmi must have combining vowel signs", combiningKeys.isNotEmpty())

        for (combKey in combiningKeys) {
            val displayGlyph = UnicodeMetadataHelper.getDisplayGlyph(combKey)
            // Visual presentation must contain dotted circle or base indicator
            assertTrue("Display glyph for combining mark ${combKey.unicodeCodePoint} must show visualization",
                displayGlyph.contains("◌") || (combKey.label.isNotBlank() && combKey.label != combKey.character))

            // Raw character must NOT contain dotted circle
            assertFalse("Raw typing character must not be modified", combKey.character.contains("◌"))
        }
    }

    @Test
    fun testBundledFontAssetPathsResolution() {
        val brahmiPath = HistoricalFontProvider.getAssetPathForScript("brahmi")
        val egyptianPath = HistoricalFontProvider.getAssetPathForScript("egyptian")
        val kharosthiPath = HistoricalFontProvider.getAssetPathForScript("kharosthi")
        val phoenicianPath = HistoricalFontProvider.getAssetPathForScript("phoenician")
        val runicPath = HistoricalFontProvider.getAssetPathForScript("runic")

        assertNotNull(brahmiPath)
        assertNotNull(egyptianPath)
        assertNotNull(kharosthiPath)
        assertNotNull(phoenicianPath)
        assertNotNull(runicPath)

        assertTrue(brahmiPath!!.endsWith(".ttf"))
        assertTrue(egyptianPath!!.endsWith(".ttf"))
        assertTrue(kharosthiPath!!.endsWith(".ttf"))
        assertTrue(phoenicianPath!!.endsWith(".ttf"))
        assertTrue(runicPath!!.endsWith(".ttf"))
    }

    @Test
    fun testCharacterSearchInsideExplorer() {
        val greek = ScriptRegistry.getScriptById("greek")!!

        // Search by hex code point with U+
        val searchByUCode = ScriptSearchEngine.searchCharacters(greek.keys, "U+03A6")
        assertTrue("U+03A6 must match Phi", searchByUCode.any { it.unicodeCodePoint == "U+03A6" })

        // Search by raw hex
        val searchByRawHex = ScriptSearchEngine.searchCharacters(greek.keys, "03A6")
        assertTrue("03A6 must match Phi", searchByRawHex.any { it.unicodeCodePoint == "U+03A6" })

        // Search by transliteration: "ph"
        val searchByTranslit = ScriptSearchEngine.searchCharacters(greek.keys, "ph")
        assertTrue(searchByTranslit.any { it.unicodeCodePoint == "U+03A6" })

        // Search by Gardiner code or description in Egyptian Hieroglyphs
        val egyptian = ScriptRegistry.getScriptById("egyptian")!!
        val searchGardiner = ScriptSearchEngine.searchCharacters(egyptian.keys, "G1")
        assertTrue(searchGardiner.any { it.phoneticHint.contains("G1", ignoreCase = true) || it.label.contains("G1", ignoreCase = true) })
    }

    @Test
    fun testCanRenderGlyphFallbackAndCaching() {
        // Empty character should return false
        assertFalse(HistoricalFontProvider.canRenderGlyph(null, "greek", ""))

        // Non-empty should return true safely even in JVM/unit test environment without throwing
        val canRenderAlpha = HistoricalFontProvider.canRenderGlyph(null, "greek", "α")
        assertTrue(canRenderAlpha)

        val canRenderRunic = HistoricalFontProvider.canRenderGlyph(null, "runic", "ᚠ")
        assertTrue(canRenderRunic)

        // Clear cache should reset cleanly without exception
        HistoricalFontProvider.clearCache()
        assertTrue(HistoricalFontProvider.canRenderGlyph(null, "phoenician", "𐤀"))
    }
}

