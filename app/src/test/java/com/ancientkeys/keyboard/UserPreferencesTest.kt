package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserPreferencesTest {

    private lateinit var repo: UserPreferencesRepository

    @Before
    fun setUp() {
        // Instantiate without Android context to use in-memory testing backend
        repo = UserPreferencesRepository(null)
    }

    @Test
    fun testRecordScriptAccessAndOrdering() {
        repo.recordScriptAccess("egyptian")
        repo.recordScriptAccess("runic")
        repo.recordScriptAccess("greek")

        val recents = repo.getRecentScriptIds()
        assertEquals(listOf("greek", "runic", "egyptian"), recents)

        // Accessing egyptian again moves it to top
        repo.recordScriptAccess("egyptian")
        val updated = repo.getRecentScriptIds()
        assertEquals(listOf("egyptian", "greek", "runic"), updated)
    }

    @Test
    fun testRecentsCappedAtSix() {
        val scripts = listOf("egyptian", "runic", "greek", "brahmi", "phoenician", "kharosthi", "extra")
        for (s in scripts) {
            repo.recordScriptAccess(s)
        }
        val recents = repo.getRecentScriptIds()
        assertTrue("Recent scripts should not exceed 6", recents.size <= 6)
    }

    @Test
    fun testFavoriteToggleAndStorage() {
        assertFalse(repo.isFavorite("runic", "U+16A0"))

        val added = repo.toggleFavorite("runic", "U+16A0")
        assertTrue(added)
        assertTrue(repo.isFavorite("runic", "U+16A0"))

        val favs = repo.getFavoriteKeysForScript("runic")
        assertEquals(setOf("U+16A0"), favs)

        // Toggling again removes favorite
        val removed = repo.toggleFavorite("runic", "U+16A0")
        assertFalse(removed)
        assertFalse(repo.isFavorite("runic", "U+16A0"))
    }

    @Test
    fun testFavoritesPerScriptIsolation() {
        repo.toggleFavorite("runic", "U+16A0")
        repo.toggleFavorite("egyptian", "U+1313F")

        val runicFavs = repo.getFavoriteKeysForScript("runic")
        val egyptianFavs = repo.getFavoriteKeysForScript("egyptian")

        assertEquals(setOf("U+16A0"), runicFavs)
        assertEquals(setOf("U+1313F"), egyptianFavs)
    }

    @Test
    fun testOnlyIdentifiersStoredNeverTypedText() {
        // Ensure that stored favorite format is strictly scriptId:U+XXXX
        repo.toggleFavorite("phoenician", "U+10900")
        val favs = repo.getFavoriteKeysForScript("phoenician")
        assertEquals(1, favs.size)
        val favKey = favs.first()
        assertTrue("Favorite must be a code point identifier starting with U+", favKey.startsWith("U+"))
        assertFalse("Favorite must never be raw text", favKey.contains(" "))
    }

    @Test
    fun testKeyboardHeightPresetsAndClamping() {
        // Default height (Standard preset = 290dp)
        assertEquals(UserPreferencesRepository.DEFAULT_KEYBOARD_HEIGHT_DP, repo.getKeyboardHeightDp())

        // Compact preset (250dp)
        repo.setKeyboardHeightDp(250)
        assertEquals(250, repo.getKeyboardHeightDp())

        // Standard preset (290dp)
        repo.setKeyboardHeightDp(290)
        assertEquals(290, repo.getKeyboardHeightDp())

        // Tall preset (330dp)
        repo.setKeyboardHeightDp(330)
        assertEquals(330, repo.getKeyboardHeightDp())

        // Out of bounds minimum clamp
        repo.setKeyboardHeightDp(150)
        assertEquals(UserPreferencesRepository.MIN_KEYBOARD_HEIGHT_DP, repo.getKeyboardHeightDp())

        // Out of bounds maximum clamp
        repo.setKeyboardHeightDp(500)
        assertEquals(UserPreferencesRepository.MAX_KEYBOARD_HEIGHT_DP, repo.getKeyboardHeightDp())
    }

    @Test
    fun testScholarModeToggle() {
        assertFalse(repo.isScholarMode())
        repo.setScholarMode(true)
        assertTrue(repo.isScholarMode())
        repo.setScholarMode(false)
        assertFalse(repo.isScholarMode())
    }

    @Test
    fun testOnboardingCompletedToggle() {
        assertFalse(repo.isOnboardingCompleted())
        repo.setOnboardingCompleted(true)
        assertTrue(repo.isOnboardingCompleted())
    }
}
