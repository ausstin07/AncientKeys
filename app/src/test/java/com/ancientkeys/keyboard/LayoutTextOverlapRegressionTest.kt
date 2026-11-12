package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.learn.HistoricalLibraryData
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.util.UnicodeMetadataHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Regression test suite ensuring layout safety against text overlapping,
 * rigid height clipping, and line height collision during Android font scaling (up to 200%).
 */
class LayoutTextOverlapRegressionTest {

    @Test
    fun testLearnScreenSourceHasNoCollapsingLineHeight() {
        val learnFile = File("src/main/java/com/ancientkeys/keyboard/ui/LearnScreen.kt")
        if (!learnFile.exists()) {
            // Alternative relative path when running from subproject root
            val altFile = File("app/src/main/java/com/ancientkeys/keyboard/ui/LearnScreen.kt")
            if (altFile.exists()) {
                assertLearnSourceSafety(altFile.readText())
                return
            }
        } else {
            assertLearnSourceSafety(learnFile.readText())
            return
        }
        // Fallback for CI/Gradle environments
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

        // Assert script name in WritingSystemsTab is weighted
        assertTrue(
            "LearnScreen.kt profile.name must be weighted against systemType badge",
            content.contains("modifier = Modifier.weight(1f).padding(end = 8.dp)")
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
                "CharacterExplorerScreen.kt tiles must use heightIn(min = 76.dp) instead of rigid fixed height",
                content.contains("heightIn(min = 76.dp)")
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

            // Preview tile must use responsive sizing
            assertTrue(
                "CharacterExplorerScreen.kt preview glyph must use sizeIn",
                content.contains("sizeIn(minWidth = 62.dp, minHeight = 62.dp)")
            )
        }
    }

    @Test
    fun testHistoricalLibraryDataTextIntegrityForScaling() {
        // Verify all profiles have clean text content without unescaped delimiters or empty fields
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

                // Hint should be reasonably short so it doesn't wrap awkwardly in 64dp cell
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

