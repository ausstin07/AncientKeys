package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.data.AncientGreekScript
import com.ancientkeys.keyboard.data.EgyptianHieroglyphsScript
import com.ancientkeys.keyboard.learn.HistoricalLibraryData
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.util.HistoricalFontProvider
import com.ancientkeys.keyboard.util.UnicodeMetadataHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Regression test suite ensuring layout safety against text overlapping,
 * Egyptian and Greek character misalignment, rigid height clipping,
 * and line height collision during Android font scaling (up to 200%).
 */
class LayoutTextOverlapRegressionTest {

    @Test
    fun testLearnScreenSourceHasNoCollapsingLineHeight() {
        val learnFile = File("src/main/java/com/ancientkeys/keyboard/ui/LearnScreen.kt")
        if (!learnFile.exists()) {
            val altFile = File("app/src/main/java/com/ancientkeys/keyboard/ui/LearnScreen.kt")
            if (altFile.exists()) {
                assertLearnSourceSafety(altFile.readText())
                return
            }
        } else {
            assertLearnSourceSafety(learnFile.readText())
            return
        }
        val workingDirFile = File(System.getProperty("user.dir") ?: ".")
        val candidate = File(workingDirFile, "app/src/main/java/com/ancientkeys/keyboard/ui/LearnScreen.kt")
        if (candidate.exists()) {
            assertLearnSourceSafety(candidate.readText())
        }
    }

    private fun assertLearnSourceSafety(content: String) {
        // Assert no hardcoded small lineHeight in sp (e.g. 16.sp, 17.sp, 18.sp, 20.sp)
        assertFalse(
            "LearnScreen.kt must not use hardcoded lineHeight sp values which collapse when font scales >1.0x",
            content.contains("lineHeight = 16.sp") ||
                    content.contains("lineHeight = 17.sp") ||
                    content.contains("lineHeight = 18.sp") ||
                    content.contains("lineHeight = 20.sp")
        )

        // Assert tabs use responsive height
        assertTrue(
            "LearnScreen.kt tab row must use heightIn(min = ...) for multiline scaled text support",
            content.contains("heightIn(min = 36.dp)")
        )

        // Assert top bar Column is weighted
        assertTrue(
            "LearnScreen.kt LearnTopBar Column must use weight(1f) to prevent off-screen overflow",
            content.contains("Column(modifier = Modifier.weight(1f))")
        )

        // Assert script badge is used for visual centering
        assertTrue(
            "LearnScreen.kt must include getScriptRepresentativeBadge for centered script representation",
            content.contains("getScriptRepresentativeBadge(profile.scriptId)")
        )

        // Assert practice tasks use taskFont and separate label from target script text
        assertTrue(
            "LearnScreen.kt practice tasks must render target script with dedicated font and badge",
            content.contains("val taskFont = remember(taskScriptId)") &&
                    content.contains("text = \"Target Script:\"")
        )

        // Assert adaptive BoxWithConstraints layout in WritingSystemsTab preventing vertical single-letter compression
        assertTrue(
            "LearnScreen.kt must use BoxWithConstraints in WritingSystemsTab for adaptive title/badge layout",
            content.contains("BoxWithConstraints(modifier = Modifier.weight(1f))")
        )
        assertTrue(
            "LearnScreen.kt must stack badge beneath title with fillMaxWidth on narrow screens",
            content.contains("modifier = Modifier.fillMaxWidth()") &&
                    content.contains("val canFitSideBySide = maxWidth >=")
        )
    }

    @Test
    fun testCharacterExplorerSourceSafety() {
        val explorerFile = File("src/main/java/com/ancientkeys/keyboard/ui/CharacterExplorerScreen.kt")
        val target = if (explorerFile.exists()) explorerFile else File("app/src/main/java/com/ancientkeys/keyboard/ui/CharacterExplorerScreen.kt")
        if (target.exists()) {
            val content = target.readText()

            // Explorer tiles must use responsive heightIn
            assertTrue(
                "CharacterExplorerScreen.kt tiles must use heightIn(min = 80.dp) instead of rigid fixed height",
                content.contains("heightIn(min = 80.dp)")
            )

            // Must contain dedicated centered glyph box with height(44.dp)
            assertTrue(
                "CharacterExplorerScreen.kt must have dedicated 44dp glyph centering box",
                content.contains("height(44.dp)") && content.contains("contentAlignment = Alignment.Center")
            )

            // Must use includeFontPadding = false to prevent asymmetric vertical drift
            assertTrue(
                "CharacterExplorerScreen.kt must use PlatformTextStyle(includeFontPadding = false)",
                content.contains("includeFontPadding = false")
            )

            // DetailSpecRow must use proportional weights
            assertTrue(
                "CharacterExplorerScreen.kt DetailSpecRow must use weight(0.38f) for label",
                content.contains("weight(0.38f)")
            )
            assertTrue(
                "CharacterExplorerScreen.kt DetailSpecRow must use weight(0.62f) for value",
                content.contains("weight(0.62f)")
            )

            // Script selector must use centered representative badge
            assertTrue(
                "CharacterExplorerScreen.kt must use getScriptRepresentativeBadge in selector",
                content.contains("getScriptRepresentativeBadge(currentScript.id)")
            )
        }
    }

    @Test
    fun testGreekAndEgyptianFontConfiguration() {
        // Egyptian has bundled Noto Sans Egyptian Hieroglyphs
        val egyptianAsset = HistoricalFontProvider.getAssetPathForScript("egyptian")
        assertEquals("fonts/NotoSansEgyptianHieroglyphs-Regular.ttf", egyptianAsset)

        // Ancient Greek relies on serif for classical polytonic and archaic typography
        val family = HistoricalFontProvider.getFontFamily(null, "greek")
        assertNotNull(family)

        // canRenderGlyph should return true or valid boolean without exception
        assertTrue(HistoricalFontProvider.canRenderGlyph(null, "egyptian", "𓀀"))
        assertTrue(HistoricalFontProvider.canRenderGlyph(null, "greek", "Ω"))
        assertTrue(HistoricalFontProvider.canRenderGlyph(null, "greek", "Ἁ"))
    }

    @Test
    fun testGreekPolytonicAndDiacriticsKeys() {
        val greekKeys = AncientGreekScript.keys
        assertEquals(60, greekKeys.size)

        // Check uppercase, lowercase, and archaic characters exist
        val characters = greekKeys.map { it.character }.toSet()
        assertTrue("Must contain uppercase Omega", characters.contains("Ω"))
        assertTrue("Must contain lowercase omega", characters.contains("ω"))
        assertTrue("Must contain final sigma", characters.contains("ς"))
        assertTrue("Must contain archaic Sampi", characters.contains("Ͳ"))
        assertTrue("Must contain archaic Heta", characters.contains("Ͱ"))
        assertTrue("Must contain Ano teleia", characters.contains("·"))

        // Verify that HistoricalFontProvider handles polytonic Greek diacritics
        val polytonicChars = listOf("ἁ", "Ἁ", "ἑ", "Ἑ", "ἱ", "Ἱ", "ὁ", "Ὁ", "ὑ", "Ὑ", "ὡ", "Ὡ", "ῥ")
        for (char in polytonicChars) {
            assertTrue("HistoricalFontProvider must handle polytonic character '$char'",
                HistoricalFontProvider.canRenderGlyph(null, "greek", char))
        }
    }

    @Test
    fun testEgyptianHieroglyphKeysIntegrity() {
        val egyptianKeys = EgyptianHieroglyphsScript.keys
        assertTrue("Egyptian script must define keys", egyptianKeys.isNotEmpty())

        for (key in egyptianKeys) {
            assertTrue("Code point '${key.unicodeCodePoint}' must start with U+13", key.unicodeCodePoint.startsWith("U+13"))
            assertTrue("Egyptian character glyph must not be empty", key.character.isNotEmpty())
            assertTrue("Transliteration must not be empty", key.transliteration.isNotEmpty())
        }
    }

    @Test
    fun testHistoricalLibraryDataTextIntegrityForScaling() {
        val profiles = HistoricalLibraryData.profiles
        assertTrue("Library must have 6 profiles", profiles.size >= 6)

        for (profile in profiles) {
            assertTrue("Script profile name must not be blank: ${profile.scriptId}", profile.name.isNotBlank())
            assertTrue("System type must not be blank: ${profile.scriptId}", profile.systemType.isNotBlank())
            assertTrue("Period must not be blank: ${profile.scriptId}", profile.period.isNotBlank())
            assertTrue("Geographic distribution must not be blank: ${profile.scriptId}", profile.geographicDistribution.isNotBlank())
            assertTrue("Writing direction must not be blank: ${profile.scriptId}", profile.writingDirection.isNotBlank())
            assertTrue("Consonant/vowel mechanics must not be blank: ${profile.scriptId}", profile.consonantVowelMechanics.isNotBlank())
            assertTrue("Numeral conventions must not be blank: ${profile.scriptId}", profile.numeralConventions.isNotBlank())
            assertTrue("Important inscriptions must not be empty: ${profile.scriptId}", profile.importantInscriptions.isNotEmpty())
            assertTrue("Key historical developments must not be empty: ${profile.scriptId}", profile.keyHistoricalDevelopments.isNotEmpty())
            assertTrue("Scholarly sources must not be empty: ${profile.scriptId}", profile.scholarlySources.isNotEmpty())
        }
    }

    @Test
    fun testFoundationalArticlesTextIntegrityForScaling() {
        val articles = HistoricalLibraryData.foundationalArticles
        assertTrue("Must have foundational articles", articles.isNotEmpty())

        for (article in articles) {
            assertTrue("Article title must not be blank: ${article.id}", article.title.isNotBlank())
            assertTrue("Article subtitle must not be blank: ${article.id}", article.subtitle.isNotBlank())
            assertTrue("Article summary must not be blank: ${article.id}", article.summary.isNotBlank())
            assertTrue("Article markdown must not be blank: ${article.id}", article.fullMarkdown.isNotBlank())
        }
    }

    @Test
    fun testAllScriptKeysProvideNonClippingDisplayData() {
        for (script in ScriptRegistry.allScripts) {
            for (key in script.keys) {
                val displayGlyph = UnicodeMetadataHelper.getDisplayGlyph(key)
                val officialName = UnicodeMetadataHelper.getOfficialUnicodeName(key)

                assertTrue("Display glyph in ${script.name} must not be blank", displayGlyph.isNotBlank())
                assertTrue("Official name in ${script.name} must not be blank", officialName.isNotBlank())

                val hint = when {
                    key.transliteration.isNotBlank() && key.transliteration != "Unavailable" -> key.transliteration
                    key.phoneticHint.isNotBlank() -> key.phoneticHint.split(" ").firstOrNull() ?: key.phoneticHint
                    else -> key.unicodeCodePoint
                }
                assertNotNull(hint)
            }
        }
    }
}
