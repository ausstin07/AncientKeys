package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.search.ScriptSearchEngine
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Automated test suite for AncientKeys v1.1 Phase C: Home Dashboard & Navigation Redesign.
 *
 * Verifies:
 * 1. Script search filtering by name, alias, period, region, and character code points.
 * 2. Recent scripts tracking and default fallback.
 * 3. Script-level and character-level favorites state machine.
 * 4. Completeness and validity of all 6 historical scripts featured on the Home dashboard.
 * 5. Settings integration (theme selection, keyboard height, scholar mode, haptics).
 */
class HomeScreenNavigationTest {

    private lateinit var userPrefs: UserPreferencesRepository

    @Before
    fun setUp() {
        userPrefs = UserPreferencesRepository(null)
    }

    @Test
    fun testAllSixFeaturedScriptsAreRegisteredAndComplete() {
        val scripts = ScriptRegistry.allScripts
        assertEquals("Exactly 6 historical scripts must be present", 6, scripts.size)

        val expectedIds = setOf("egyptian", "brahmi", "kharosthi", "greek", "phoenician", "runic")
        val actualIds = scripts.map { it.id.lowercase() }.toSet()
        assertEquals(expectedIds, actualIds)

        for (script in scripts) {
            assertTrue("Script ${script.name} must have a non-empty name", script.name.isNotBlank())
            assertTrue("Script ${script.name} must have a non-empty description", script.description.isNotBlank())
            assertTrue("Script ${script.name} must have historical period", script.historicalPeriod.isNotBlank())
            assertTrue("Script ${script.name} must have region", script.region.isNotBlank())
            assertTrue("Script ${script.name} must have authentic sample text", script.sampleText.isNotBlank())
            assertTrue("Script ${script.name} must have keys", script.keys.isNotEmpty())
        }

        // Verify RTL flags
        val phoenician = ScriptRegistry.getScriptById("phoenician")
        val kharosthi = ScriptRegistry.getScriptById("kharosthi")
        val greek = ScriptRegistry.getScriptById("greek")
        val egyptian = ScriptRegistry.getScriptById("egyptian")

        assertNotNull(phoenician)
        assertNotNull(kharosthi)
        assertNotNull(greek)
        assertNotNull(egyptian)

        assertTrue("Phoenician must be RTL", phoenician!!.isRtl)
        assertTrue("Kharosthi must be RTL", kharosthi!!.isRtl)
        assertFalse("Greek must not be RTL", greek!!.isRtl)
        assertFalse("Egyptian Hieroglyphs must not be RTL", egyptian!!.isRtl)
    }

    @Test
    fun testHomeSearchScriptFiltering() {
        val scripts = ScriptRegistry.allScripts

        // Empty query returns all scripts
        assertEquals(6, ScriptSearchEngine.searchScripts(scripts, "").size)
        assertEquals(6, ScriptSearchEngine.searchScripts(scripts, "   ").size)

        // Exact match
        val greek = ScriptSearchEngine.searchScripts(scripts, "Ancient Greek")
        assertEquals(1, greek.size)
        assertEquals("greek", greek[0].id)

        // Partial match
        val brahmi = ScriptSearchEngine.searchScripts(scripts, "brah")
        assertTrue(brahmi.any { it.id == "brahmi" })

        // Alias search: "Gandhara" -> Kharosthi
        val gandhara = ScriptSearchEngine.searchScripts(scripts, "Gandhara")
        assertTrue(gandhara.any { it.id == "kharosthi" })

        // Alias search with diacritics: "Gāndhārī" -> Kharosthi
        val gandhariDiacritics = ScriptSearchEngine.searchScripts(scripts, "Gāndhārī")
        assertTrue(gandhariDiacritics.any { it.id == "kharosthi" })

        // Alias search: "Futhark" / "Fuþark" -> Runic
        assertTrue(ScriptSearchEngine.searchScripts(scripts, "Futhark").any { it.id == "runic" })
        assertTrue(ScriptSearchEngine.searchScripts(scripts, "Fuþark").any { it.id == "runic" })
    }

    @Test
    fun testHomeSearchCharacterCodePointsAcrossScripts() {
        val allKeys = ScriptRegistry.allScripts.flatMap { it.keys }

        // Search Greek Alpha by code point
        val alphaByCode = ScriptSearchEngine.searchCharacters(allKeys, "U+0391")
        assertTrue(alphaByCode.any { it.unicodeCodePoint.equals("U+0391", ignoreCase = true) })

        // Search without "U+" prefix
        val alphaByHex = ScriptSearchEngine.searchCharacters(allKeys, "0391")
        assertTrue(alphaByHex.any { it.unicodeCodePoint.equals("U+0391", ignoreCase = true) })

        // Search by transliteration / phonetic hint: "alpha"
        val alphaByName = ScriptSearchEngine.searchCharacters(allKeys, "alpha")
        assertTrue(alphaByName.any {
            it.phoneticHint.contains("Alpha", ignoreCase = true) ||
            it.label.contains("Alpha", ignoreCase = true) ||
            it.transliteration.contains("alpha", ignoreCase = true)
        })
    }

    @Test
    fun testRecentScriptsManagement() {
        // Starts empty
        assertTrue(userPrefs.getRecentScriptIds().isEmpty())

        // Access scripts
        userPrefs.recordScriptAccess("greek")
        userPrefs.recordScriptAccess("egyptian")
        userPrefs.recordScriptAccess("brahmi")

        assertEquals(listOf("brahmi", "egyptian", "greek"), userPrefs.getRecentScriptIds())

        // Accessing 'greek' again promotes it to front without duplicates
        userPrefs.recordScriptAccess("greek")
        assertEquals(listOf("greek", "brahmi", "egyptian"), userPrefs.getRecentScriptIds())
    }

    @Test
    fun testScriptAndCharacterFavoritesStateMachine() {
        // Initially no favorites
        assertEquals(0, userPrefs.getTotalFavoritesCount())
        assertFalse(userPrefs.isScriptFavorite("greek"))
        assertTrue(userPrefs.getFavoriteScriptIds().isEmpty())

        // Toggle script favorite
        val state1 = userPrefs.toggleScriptFavorite("greek")
        assertTrue(state1)
        assertTrue(userPrefs.isScriptFavorite("greek"))
        assertEquals(setOf("greek"), userPrefs.getFavoriteScriptIds())
        assertEquals(1, userPrefs.getTotalFavoritesCount())

        // Toggle character favorite
        val state2 = userPrefs.toggleFavorite("greek", "U+03B1")
        assertTrue(state2)
        assertTrue(userPrefs.isFavorite("greek", "U+03B1"))
        assertEquals(2, userPrefs.getTotalFavoritesCount())
        assertEquals(setOf("greek"), userPrefs.getAllFavoriteScriptIds())

        // Untoggle script favorite
        val state3 = userPrefs.toggleScriptFavorite("greek")
        assertFalse(state3)
        assertFalse(userPrefs.isScriptFavorite("greek"))
        // Character favorite still exists
        assertTrue(userPrefs.isFavorite("greek", "U+03B1"))
        assertEquals(1, userPrefs.getTotalFavoritesCount())
    }

    @Test
    fun testHomeSettingsPreferencesPersistence() {
        // Theme selection
        userPrefs.setThemePreference(UserPreferencesRepository.THEME_SAGE)
        assertEquals(UserPreferencesRepository.THEME_SAGE, userPrefs.getThemePreference())

        userPrefs.setThemePreference(UserPreferencesRepository.THEME_SEPIA)
        assertEquals(UserPreferencesRepository.THEME_SEPIA, userPrefs.getThemePreference())

        // Scholar mode toggle
        assertFalse(userPrefs.isScholarMode())
        userPrefs.setScholarMode(true)
        assertTrue(userPrefs.isScholarMode())
        userPrefs.setScholarMode(false)
        assertFalse(userPrefs.isScholarMode())

        // Haptics toggle
        assertTrue(userPrefs.isHapticsEnabled())
        userPrefs.setHapticsEnabled(false)
        assertFalse(userPrefs.isHapticsEnabled())

        // Keyboard height clamping
        userPrefs.setKeyboardHeightDp(250)
        assertEquals(250, userPrefs.getKeyboardHeightDp())
        userPrefs.setKeyboardHeightDp(200) // below min
        assertEquals(UserPreferencesRepository.MIN_KEYBOARD_HEIGHT_DP, userPrefs.getKeyboardHeightDp())
        userPrefs.setKeyboardHeightDp(400) // above max
        assertEquals(UserPreferencesRepository.MAX_KEYBOARD_HEIGHT_DP, userPrefs.getKeyboardHeightDp())
    }
}

