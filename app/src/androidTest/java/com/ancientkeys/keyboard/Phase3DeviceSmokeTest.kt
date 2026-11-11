package com.ancientkeys.keyboard

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.search.ScriptSearchEngine
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation test suite for Phase 3 features running in an Android runtime environment:
 * - Diacritic normalization and search in Android runtime.
 * - Preferences persistence via Android SharedPreferences.
 * - Script registry and character explorer metadata.
 */
@RunWith(AndroidJUnit4::class)
class Phase3DeviceSmokeTest {

    @Test
    fun testSearchAndAliasesOnDevice() {
        val scripts = ScriptRegistry.allScripts
        assertEquals(6, scripts.size)

        // Verify "Gandhara" search matches Kharosthi on Android runtime
        val gandharaMatches = ScriptSearchEngine.searchScripts(scripts, "Gandhara")
        assertTrue("Gandhara should find Kharosthi on Android", gandharaMatches.any { it.id == "kharosthi" })

        // Verify diacritic matching on device
        val macronMatches = ScriptSearchEngine.searchScripts(scripts, "Gandhāra")
        assertTrue("Gandhāra should find Kharosthi on Android", macronMatches.any { it.id == "kharosthi" })

        val futharkMatches = ScriptSearchEngine.searchScripts(scripts, "Fuþark")
        assertTrue("Fuþark should find Runic on Android", futharkMatches.any { it.id == "runic" })
    }

    @Test
    fun testPreferencesPersistenceWithRealSharedPreferences() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val repo1 = UserPreferencesRepository(appContext)

        // Record recent scripts
        repo1.recordScriptAccess("kharosthi")
        repo1.recordScriptAccess("runic")

        // Toggle favorite
        repo1.toggleFavorite("kharosthi", "U+10A00")
        assertTrue(repo1.isFavorite("kharosthi", "U+10A00"))

        // Set keyboard height
        repo1.setKeyboardHeightDp(310)

        // Instantiate second repo (simulating app relaunch)
        val repo2 = UserPreferencesRepository(appContext)
        assertEquals(listOf("runic", "kharosthi"), repo2.getRecentScriptIds().take(2))
        assertTrue("Favorite should persist in SharedPreferences", repo2.isFavorite("kharosthi", "U+10A00"))
        assertEquals(310, repo2.getKeyboardHeightDp())
    }

    @Test
    fun testScriptExplorerMetadataOnDevice() {
        for (script in ScriptRegistry.allScripts) {
            assertTrue("${script.name} should have keys", script.keys.isNotEmpty())
            assertTrue("${script.name} should have historical context", script.historicalContext.isNotBlank())
            assertTrue("${script.name} should have references", script.sourcesAndReferences.isNotBlank())
        }
    }
}

