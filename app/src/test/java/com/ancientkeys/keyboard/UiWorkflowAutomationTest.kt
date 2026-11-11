package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.search.ScriptSearchEngine
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.ui.detectScriptsInText
import com.ancientkeys.keyboard.ui.getCodePointsList
import com.ancientkeys.keyboard.util.UnicodeMetadataHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Automated UI and workflow tests for AncientKeys:
 * - Home search filtering, case-insensitivity, and diacritic handling
 * - Character explorer categorization and metadata resolution
 * - Favorites toggling and persistence state machine
 * - Recent scripts MRU tracking and deduplication
 * - Settings state machine: height clamping, haptics toggle, and theme switching
 * - Test Lab diagnostics: script detection and code point formatting
 */
class UiWorkflowAutomationTest {

    private lateinit var userPrefs: UserPreferencesRepository

    @Before
    fun setUp() {
        userPrefs = UserPreferencesRepository(null)
    }

    // --- HOME SEARCH WORKFLOW TESTS ---

    @Test
    fun testHomeSearchEmptyQueryReturnsAllScripts() {
        val scripts = ScriptRegistry.allScripts
        val results = ScriptSearchEngine.searchScripts(scripts, "")
        assertEquals("Empty search query must return all scripts", scripts.size, results.size)

        val whitespaceResults = ScriptSearchEngine.searchScripts(scripts, "   ")
        assertEquals("Whitespace query must return all scripts", scripts.size, whitespaceResults.size)
    }

    @Test
    fun testHomeSearchExactAndPartialNames() {
        val scripts = ScriptRegistry.allScripts

        // Exact name
        val egyptian = ScriptSearchEngine.searchScripts(scripts, "Egyptian Hieroglyphs")
        assertEquals(1, egyptian.size)
        assertEquals("egyptian", egyptian[0].id)

        // Case-insensitive partial
        val greek = ScriptSearchEngine.searchScripts(scripts, "GREEK")
        assertTrue("Query 'GREEK' must match Ancient Greek", greek.any { it.id == "greek" })

        // Partial match
        val runic = ScriptSearchEngine.searchScripts(scripts, "run")
        assertTrue("Query 'run' must match Runic", runic.any { it.id == "runic" })
    }

    @Test
    fun testHomeSearchDiacriticsAndAliases() {
        val scripts = ScriptRegistry.allScripts

        // Gandhara without macron
        val gandhara = ScriptSearchEngine.searchScripts(scripts, "Gandhara")
        assertTrue("Gandhara should find Kharosthi", gandhara.any { it.id == "kharosthi" })

        // Gandhāra with macron
        val gandharaMacron = ScriptSearchEngine.searchScripts(scripts, "Gandhāra")
        assertTrue("Gandhāra should find Kharosthi", gandharaMacron.any { it.id == "kharosthi" })

        // Elder Futhark alias
        val futhark = ScriptSearchEngine.searchScripts(scripts, "Futhark")
        assertTrue("Futhark should find Runic", futhark.any { it.id == "runic" })

        // Fuþark with thorn
        val futharkThorn = ScriptSearchEngine.searchScripts(scripts, "Fuþark")
        assertTrue("Fuþark should find Runic", futharkThorn.any { it.id == "runic" })
    }

    @Test
    fun testHomeSearchNoMatchReturnsEmpty() {
        val scripts = ScriptRegistry.allScripts
        val results = ScriptSearchEngine.searchScripts(scripts, "NonExistentAncientScriptXYZ")
        assertTrue("Invalid query must return empty list", results.isEmpty())
    }

    // --- CHARACTER EXPLORER WORKFLOW TESTS ---

    @Test
    fun testCharacterExplorerCategoriesPerScript() {
        for (script in ScriptRegistry.allScripts) {
            assertTrue("${script.name} must have categories defined", script.categories.isNotEmpty())

            // Test filtering keys by each defined category
            for (category in script.categories) {
                val matchingKeys = script.keys.filter { it.category == category }
                assertTrue(
                    "Category '$category' in ${script.name} must contain at least one key",
                    matchingKeys.isNotEmpty()
                )
            }
        }
    }

    @Test
    fun testCharacterExplorerMetadataResolution() {
        val egyptian = ScriptRegistry.getScriptById("egyptian")!!
        val sunGodRa = egyptian.keys.first { it.unicodeCodePoint == "U+131F3" }
        assertEquals("𓇳", sunGodRa.character)
        assertTrue(sunGodRa.historicalNotes.contains("N5"))

        val charName = UnicodeMetadataHelper.getOfficialUnicodeName(sunGodRa)
        assertNotNull(charName)
        assertTrue(charName.contains("EGYPTIAN HIEROGLYPH N005"))
    }

    // --- FAVORITES WORKFLOW TESTS ---

    @Test
    fun testFavoritesWorkflowAddRemovePersistence() {
        val scriptId = "brahmi"
        val codePoint = "U+11005"

        // Initially not favorite
        assertFalse(userPrefs.isFavorite(scriptId, codePoint))
        assertTrue(userPrefs.getFavoriteKeysForScript(scriptId).isEmpty())

        // Toggle ON
        userPrefs.toggleFavorite(scriptId, codePoint)
        assertTrue(userPrefs.isFavorite(scriptId, codePoint))
        assertEquals(setOf(codePoint), userPrefs.getFavoriteKeysForScript(scriptId))

        // Toggle OFF
        userPrefs.toggleFavorite(scriptId, codePoint)
        assertFalse(userPrefs.isFavorite(scriptId, codePoint))
        assertTrue(userPrefs.getFavoriteKeysForScript(scriptId).isEmpty())
    }

    // --- RECENT SCRIPTS WORKFLOW TESTS ---

    @Test
    fun testRecentScriptsMruOrderingAndDeduplication() {
        // Initial empty
        assertTrue(userPrefs.getRecentScriptIds().isEmpty())

        // Access scripts in sequence
        userPrefs.recordScriptAccess("greek")
        userPrefs.recordScriptAccess("runic")
        userPrefs.recordScriptAccess("egyptian")

        // Most recent first: egyptian, runic, greek
        assertEquals(listOf("egyptian", "runic", "greek"), userPrefs.getRecentScriptIds())

        // Re-access 'greek': should jump to front without duplicates
        userPrefs.recordScriptAccess("greek")
        assertEquals(listOf("greek", "egyptian", "runic"), userPrefs.getRecentScriptIds())
    }

    // --- SETTINGS WORKFLOW TESTS ---

    @Test
    fun testSettingsHeightPresetsAndClamping() {
        // Default height is 290 dp
        assertEquals(UserPreferencesRepository.DEFAULT_KEYBOARD_HEIGHT_DP, userPrefs.getKeyboardHeightDp())

        // Valid preset: 240 dp (Compact / Min)
        userPrefs.setKeyboardHeightDp(240)
        assertEquals(240, userPrefs.getKeyboardHeightDp())

        // Valid preset: 310 dp
        userPrefs.setKeyboardHeightDp(310)
        assertEquals(310, userPrefs.getKeyboardHeightDp())

        // Valid preset: 360 dp (Tall / Max)
        userPrefs.setKeyboardHeightDp(360)
        assertEquals(360, userPrefs.getKeyboardHeightDp())

        // Out of bounds: should clamp between 240 and 360
        userPrefs.setKeyboardHeightDp(100)
        assertEquals(UserPreferencesRepository.MIN_KEYBOARD_HEIGHT_DP, userPrefs.getKeyboardHeightDp())

        userPrefs.setKeyboardHeightDp(500)
        assertEquals(UserPreferencesRepository.MAX_KEYBOARD_HEIGHT_DP, userPrefs.getKeyboardHeightDp())
    }

    @Test
    fun testSettingsHapticsToggle() {
        // Default haptics is true
        assertTrue(userPrefs.isHapticsEnabled())

        userPrefs.setHapticsEnabled(false)
        assertFalse(userPrefs.isHapticsEnabled())

        userPrefs.setHapticsEnabled(true)
        assertTrue(userPrefs.isHapticsEnabled())
    }

    @Test
    fun testSettingsThemeSelection() {
        // Default theme is THEME_SYSTEM
        assertEquals(UserPreferencesRepository.THEME_SYSTEM, userPrefs.getThemePreference())

        userPrefs.setThemePreference(UserPreferencesRepository.THEME_LIGHT)
        assertEquals(UserPreferencesRepository.THEME_LIGHT, userPrefs.getThemePreference())

        userPrefs.setThemePreference(UserPreferencesRepository.THEME_DARK)
        assertEquals(UserPreferencesRepository.THEME_DARK, userPrefs.getThemePreference())
    }

    // --- TEST LAB DIAGNOSTICS TESTS ---

    @Test
    fun testImeTestLabScriptDetection() {
        val emptyDetected = detectScriptsInText("")
        assertTrue(emptyDetected.isEmpty())

        val latinText = "Hello World 123"
        val latinDetected = detectScriptsInText(latinText)
        assertTrue(latinDetected.contains("Latin / ASCII"))

        val egyptianText = "King 𓇳"
        val egyptianDetected = detectScriptsInText(egyptianText)
        assertTrue(egyptianDetected.contains("Latin / ASCII"))
        assertTrue(egyptianDetected.contains("Egyptian Hieroglyphs"))

        val phoenicianText = "𐤀𐤁𐤂"
        val phoenicianDetected = detectScriptsInText(phoenicianText)
        assertTrue(phoenicianDetected.contains("Phoenician (RTL)"))

        val kharosthiText = "𐨐𐨁"
        val kharosthiDetected = detectScriptsInText(kharosthiText)
        assertTrue(kharosthiDetected.contains("Kharosthi (RTL)"))
    }

    @Test
    fun testImeTestLabCodePointsList() {
        val text = "𓇳 A"
        val list = getCodePointsList(text, maxCount = 10)
        assertEquals(3, list.size)
        assertTrue("First code point should be 𓇳 with U+131F3", list[0].contains("U+131F3"))
        assertTrue("Second code point should be space with U+0020", list[1].contains("U+0020"))
        assertTrue("Third code point should be A with U+0041", list[2].contains("U+0041"))
    }

    @Test
    fun testScreenModeEnumCompleteness() {
        val modes = com.ancientkeys.keyboard.ScreenMode.values()
        assertTrue(modes.contains(com.ancientkeys.keyboard.ScreenMode.HOME))
        assertTrue(modes.contains(com.ancientkeys.keyboard.ScreenMode.PLAYGROUND))
        assertTrue(modes.contains(com.ancientkeys.keyboard.ScreenMode.EXPLORER))
        assertTrue(modes.contains(com.ancientkeys.keyboard.ScreenMode.TEST_LAB))
        assertTrue(modes.contains(com.ancientkeys.keyboard.ScreenMode.LEARN))
        assertTrue(modes.contains(com.ancientkeys.keyboard.ScreenMode.RESEARCH_WORKSPACE))
        assertTrue(modes.contains(com.ancientkeys.keyboard.ScreenMode.ABOUT))
        assertTrue(modes.contains(com.ancientkeys.keyboard.ScreenMode.SETTINGS))
        assertEquals(8, modes.size)
    }
}
