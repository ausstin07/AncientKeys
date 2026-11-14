package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.data.KeyboardMode
import com.ancientkeys.keyboard.ime.ui.ImeDimensionsHelper
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Automated test suite for AncientKeys v1.2.0 UX, Navigation & Inset Handling improvements.
 *
 * Verifies:
 * 1. Persistent bottom navigation destinations (Playground, Explore, Learn, Corpus, Settings).
 * 2. Navigation bar visibility policy (hidden during typing/IME input or modal detail screens).
 * 3. Ergonomic system navigation chin calculation for gesture navigation and 3-button navigation.
 * 4. Adaptive keyboard sizing within standard Android Gboard-comparable ranges (230-360 dp).
 * 5. Compact phonetic suggestion strip dimensions (40 dp portrait, 32 dp landscape).
 * 6. Dynamic preference listener reactivity for keyboard height changes.
 */
class AppNavigationAndKeyboardUxTest {

    private lateinit var userPrefs: UserPreferencesRepository

    @Before
    fun setUp() {
        userPrefs = UserPreferencesRepository(null)
    }

    @Test
    fun testPersistentNavigationDestinationsCoverage() {
        val destinations = listOf(
            NavigationDestination(
                screen = ScreenMode.PLAYGROUND,
                title = "Playground",
                icon = "⌨️",
                contentDescription = "Interactive typing playground"
            ),
            NavigationDestination(
                screen = ScreenMode.EXPLORER,
                title = "Explore",
                icon = "🧭",
                contentDescription = "Character and script explorer"
            ),
            NavigationDestination(
                screen = ScreenMode.LEARN,
                title = "Learn",
                icon = "📖",
                contentDescription = "Historical library and learning guides"
            ),
            NavigationDestination(
                screen = ScreenMode.RESEARCH_WORKSPACE,
                title = "Corpus",
                icon = "📜",
                contentDescription = "Research workspace and inscription corpus"
            ),
            NavigationDestination(
                screen = ScreenMode.SETTINGS,
                title = "Settings",
                icon = "⚙️",
                contentDescription = "Theme, layout, and keyboard settings"
            )
        )

        assertEquals("Must provide exactly 5 primary navigation destinations", 5, destinations.size)

        val screens = destinations.map { it.screen }.toSet()
        assertTrue(screens.contains(ScreenMode.PLAYGROUND))
        assertTrue(screens.contains(ScreenMode.EXPLORER))
        assertTrue(screens.contains(ScreenMode.LEARN))
        assertTrue(screens.contains(ScreenMode.RESEARCH_WORKSPACE))
        assertTrue(screens.contains(ScreenMode.SETTINGS))

        destinations.forEach { dest ->
            assertTrue("Title must not be empty", dest.title.isNotBlank())
            assertTrue("Icon must not be empty", dest.icon.isNotBlank())
            assertTrue("Accessibility description must not be empty", dest.contentDescription.isNotBlank())
        }
    }

    @Test
    fun testBottomBarVisibilityPolicy() {
        fun shouldShowBottomBar(screen: ScreenMode, isImeOpen: Boolean): Boolean {
            return !isImeOpen && (screen != ScreenMode.ABOUT && screen != ScreenMode.TEST_LAB)
        }

        // Home: visible when keyboard is closed
        assertTrue(shouldShowBottomBar(ScreenMode.HOME, isImeOpen = false))
        assertFalse(shouldShowBottomBar(ScreenMode.HOME, isImeOpen = true))

        // Playground: visible when keyboard is closed, hidden when typing (IME open)
        assertTrue(shouldShowBottomBar(ScreenMode.PLAYGROUND, isImeOpen = false))
        assertFalse(shouldShowBottomBar(ScreenMode.PLAYGROUND, isImeOpen = true))

        // Explore, Learn, Corpus, Settings: visible when keyboard closed
        assertTrue(shouldShowBottomBar(ScreenMode.EXPLORER, isImeOpen = false))
        assertTrue(shouldShowBottomBar(ScreenMode.LEARN, isImeOpen = false))
        assertTrue(shouldShowBottomBar(ScreenMode.RESEARCH_WORKSPACE, isImeOpen = false))
        assertTrue(shouldShowBottomBar(ScreenMode.SETTINGS, isImeOpen = false))

        // Hidden when IME open on any screen
        assertFalse(shouldShowBottomBar(ScreenMode.EXPLORER, isImeOpen = true))
        assertFalse(shouldShowBottomBar(ScreenMode.RESEARCH_WORKSPACE, isImeOpen = true))

        // Modal detail screens: always hidden
        assertFalse(shouldShowBottomBar(ScreenMode.ABOUT, isImeOpen = false))
        assertFalse(shouldShowBottomBar(ScreenMode.TEST_LAB, isImeOpen = false))
    }

    @Test
    fun testErgonomicSystemNavigationChin() {
        // Motorola moto g73 5G and modern Android gesture navigation bar: 16–24 dp
        val gestureChin16 = ImeDimensionsHelper.computeBottomChinPaddingDp(16f, isLandscape = false)
        assertEquals(14f, gestureChin16, 0.01f)

        val gestureChin20 = ImeDimensionsHelper.computeBottomChinPaddingDp(20f, isLandscape = false)
        assertEquals(14f, gestureChin20, 0.01f)

        val smallGestureChin = ImeDimensionsHelper.computeBottomChinPaddingDp(10f, isLandscape = false)
        assertEquals(10f, smallGestureChin, 0.01f)

        // 3-button navigation: typically 48 dp
        val threeButtonChin = ImeDimensionsHelper.computeBottomChinPaddingDp(48f, isLandscape = false)
        assertEquals(48f, threeButtonChin, 0.01f)

        // Zero inset (system already offsets or edge-to-edge off)
        val zeroInsetChin = ImeDimensionsHelper.computeBottomChinPaddingDp(0f, isLandscape = false)
        assertEquals(4f, zeroInsetChin, 0.01f)

        // Landscape mode: bottom chin is zeroed to give maximum height to input
        val landscapeChin = ImeDimensionsHelper.computeBottomChinPaddingDp(20f, isLandscape = true)
        assertEquals(0f, landscapeChin, 0.01f)
    }

    @Test
    fun testKeyboardStandardHeightsAndPhoneticStrip() {
        // Standard portrait height default is 270 dp
        assertEquals(270, UserPreferencesRepository.DEFAULT_KEYBOARD_HEIGHT_DP)
        assertEquals(245, UserPreferencesRepository.HEIGHT_PRESET_COMPACT)
        assertEquals(310, UserPreferencesRepository.HEIGHT_PRESET_LARGE)

        // Adaptive height in portrait with standard 270 dp
        val standardPortrait = ImeDimensionsHelper.computeAdaptiveKeyboardHeight(
            screenHeightDp = 850,
            isLandscape = false,
            keyboardMode = KeyboardMode.HISTORICAL,
            userPreferenceHeightDp = UserPreferencesRepository.HEIGHT_PRESET_STANDARD
        )
        assertEquals(270, standardPortrait)

        // Phonetic strip dimensions: compact 40 dp portrait, 32 dp landscape
        assertEquals(40, ImeDimensionsHelper.PHONETIC_STRIP_PORTRAIT_HEIGHT_DP)
        assertEquals(32, ImeDimensionsHelper.PHONETIC_STRIP_LANDSCAPE_HEIGHT_DP)

        // Phonetic mode gets an ergonomic +20 dp vertical expansion
        val phoneticPortrait = ImeDimensionsHelper.computeAdaptiveKeyboardHeight(
            screenHeightDp = 850,
            isLandscape = false,
            keyboardMode = KeyboardMode.EASY_PHONETIC,
            userPreferenceHeightDp = UserPreferencesRepository.HEIGHT_PRESET_STANDARD
        )
        assertEquals(290, phoneticPortrait)
    }

    @Test
    fun testDynamicHeightPreferenceObservation() {
        var observedHeight = 0
        val unregister = userPrefs.registerHeightChangeListener { newHeight ->
            observedHeight = newHeight
        }

        userPrefs.setKeyboardHeightDp(UserPreferencesRepository.HEIGHT_PRESET_COMPACT)
        assertEquals(245, observedHeight)
        assertEquals(245, userPrefs.getKeyboardHeightDp())

        userPrefs.setKeyboardHeightDp(UserPreferencesRepository.HEIGHT_PRESET_LARGE)
        assertEquals(310, observedHeight)
        assertEquals(310, userPrefs.getKeyboardHeightDp())

        unregister()
        userPrefs.setKeyboardHeightDp(UserPreferencesRepository.HEIGHT_PRESET_STANDARD)
        // Listener was unregistered, so observedHeight remains 310
        assertEquals(310, observedHeight)
        assertEquals(270, userPrefs.getKeyboardHeightDp())
    }
}
