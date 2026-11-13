package com.ancientkeys.keyboard

import androidx.compose.ui.text.font.FontFamily
import com.ancientkeys.keyboard.data.BrahmiScript
import com.ancientkeys.keyboard.data.KharosthiScript
import com.ancientkeys.keyboard.ime.data.EnglishKeyboardData
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
 * Unit & Regression test suite for keyboard layout constraints,
 * combining mark font rendering, and per-script canvas isolation.
 */
class KeyboardLayoutAndFontRenderingTest {

    @Test
    fun testNumericKeyboardRowLayoutAndKeyCount() {
        // Number row must contain exactly 10 keys: 1 through 0
        assertEquals(10, EnglishKeyboardData.NUMBERS_ROW.size)
        assertEquals(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"), EnglishKeyboardData.NUMBERS_ROW)

        // Symbols rows must be non-empty and well-formed
        assertEquals(10, EnglishKeyboardData.SYMBOLS_ROW_1.size)
        assertEquals(10, EnglishKeyboardData.SYMBOLS_ROW_2.size)
    }

    @Test
    fun testPlaygroundScreenLayoutSourceConstraints() {
        val playgroundFile = findSourceFile("app/src/main/java/com/ancientkeys/keyboard/ui/PlaygroundScreen.kt")
        assertNotNull("PlaygroundScreen.kt file should exist", playgroundFile)
        val content = playgroundFile!!.readText()

        // 1. Numeric rows must have fixed bounded height (height(46.dp)) and not unbounded fillMaxSize
        assertTrue(
            "Numeric rows must have bounded height (height(46.dp)) to prevent vertical stretching",
            content.contains(".height(46.dp)")
        )

        // 2. PlaygroundKeyCard must enforce heightIn(min = 40.dp, max = 54.dp)
        assertTrue(
            "PlaygroundKeyCard must define heightIn bounds to prevent card vertical explosion",
            content.contains("heightIn(min = 40.dp, max = 54.dp)")
        )

        // 3. PlaygroundKeyCard must use buildDisplayGlyphAnnotatedString for combining mark rendering
        assertTrue(
            "PlaygroundKeyCard must use buildDisplayGlyphAnnotatedString to handle dotted circle",
            content.contains("HistoricalFontProvider.buildDisplayGlyphAnnotatedString")
        )

        // 4. Per-script text buffer isolation must be present
        assertTrue(
            "PlaygroundScreen must isolate text values per script to prevent cross-script glyph tofu",
            content.contains("perScriptTextValues") && content.contains("currentTextValue")
        )
    }

    @Test
    fun testLearnScreenWritingSystemsAdaptiveHeader() {
        val learnFile = findSourceFile("app/src/main/java/com/ancientkeys/keyboard/ui/LearnScreen.kt")
        assertNotNull("LearnScreen.kt file should exist", learnFile)
        val content = learnFile!!.readText()

        // Verify adaptive BoxWithConstraints layout for writing systems tab
        assertTrue(
            "WritingSystemsTab must use BoxWithConstraints for responsive header layout",
            content.contains("BoxWithConstraints(modifier = Modifier.weight(1f))")
        )
        assertTrue(
            "WritingSystemsTab must have adaptive width threshold checking for side-by-side vs stacked badges",
            content.contains("canFitSideBySide") && content.contains("480.dp")
        )
    }

    @Test
    fun testBrahmiAndKharosthiCombiningMarksDottedCircle() {
        // Brahmi dependent vowels must have non-empty labels with '◌' (U+25CC)
        val brahmiVowels = BrahmiScript.keys.filter { it.category == "Dependent Vowels" }
        assertTrue("Brahmi should have dependent vowels", brahmiVowels.isNotEmpty())
        for (key in brahmiVowels) {
            assertTrue(
                "Brahmi vowel key ${key.unicodeCodePoint} must include dotted circle '◌' in label",
                key.label.startsWith("◌")
            )
            val display = UnicodeMetadataHelper.getDisplayGlyph(key)
            assertTrue("Display glyph must contain '◌'", display.contains("◌"))
        }

        // Kharosthi dependent vowels must have non-empty labels with '◌' (U+25CC)
        val kharosthiVowels = KharosthiScript.keys.filter { it.category == "Dependent Vowels" }
        assertTrue("Kharosthi should have dependent vowels", kharosthiVowels.isNotEmpty())
        for (key in kharosthiVowels) {
            assertTrue(
                "Kharosthi vowel key ${key.unicodeCodePoint} must include dotted circle '◌' in label",
                key.label.startsWith("◌")
            )
            val display = UnicodeMetadataHelper.getDisplayGlyph(key)
            assertTrue("Display glyph must contain '◌'", display.contains("◌"))
        }
    }

    @Test
    fun testBuildDisplayGlyphAnnotatedStringWithDottedCircle() {
        val mockFontFamily = FontFamily.Serif

        // Test label with dotted circle: "◌𑀸"
        val labelWithCircle = "◌𑀸"
        val annotated = HistoricalFontProvider.buildDisplayGlyphAnnotatedString(labelWithCircle, mockFontFamily)

        assertEquals("Text content must match original label", labelWithCircle, annotated.text)
        assertTrue("AnnotatedString must have span styles for dotted circle and script character", annotated.spanStyles.isNotEmpty())

        // The first span (0..1) for '◌' should use FontFamily.Default
        val circleSpan = annotated.spanStyles.find { it.start == 0 && it.end == 1 }
        assertNotNull("Dotted circle span must be present at 0..1", circleSpan)
        assertEquals("Dotted circle must use FontFamily.Default for system font fallback", FontFamily.Default, circleSpan!!.item.fontFamily)

        // The second span for the historical character should use mockFontFamily
        val scriptSpan = annotated.spanStyles.find { it.start == 1 }
        assertNotNull("Script glyph span must be present after dotted circle", scriptSpan)
        assertEquals("Script character must use specified script font", mockFontFamily, scriptSpan!!.item.fontFamily)
    }

    @Test
    fun testBuildDisplayGlyphAnnotatedStringWithoutDottedCircle() {
        val mockFontFamily = FontFamily.Serif

        // Test normal character without dotted circle: "𑀅"
        val normalChar = "𑀅"
        val annotated = HistoricalFontProvider.buildDisplayGlyphAnnotatedString(normalChar, mockFontFamily)

        assertEquals("Normal text must match", normalChar, annotated.text)
        assertTrue("Normal text without dotted circle requires no separate span splitting", annotated.spanStyles.isEmpty())
    }

    @Test
    fun testCanRenderGlyphStripsDottedCircle() {
        // canRenderGlyph should safely handle combining marks with dotted circle without throwing
        val canRender = HistoricalFontProvider.canRenderGlyph(null, "brahmi", "◌𑀸")
        assertTrue("canRenderGlyph should return true or valid boolean for Brahmi vowel sign", canRender)

        val canRenderKharosthi = HistoricalFontProvider.canRenderGlyph(null, "kharosthi", "◌𐨁")
        assertTrue("canRenderGlyph should return true or valid boolean for Kharosthi vowel sign", canRenderKharosthi)

        // Empty character returns false
        assertFalse(HistoricalFontProvider.canRenderGlyph(null, "brahmi", ""))
        assertFalse(HistoricalFontProvider.canRenderGlyph(null, "brahmi", "◌"))
    }

    @Test
    fun testAllHistoricalScriptsIntegrity() {
        val scripts = ScriptRegistry.allScripts
        assertEquals("AncientKeys must support 6 historical writing systems", 6, scripts.size)

        val scriptIds = scripts.map { it.id }.toSet()
        assertTrue(scriptIds.contains("greek"))
        assertTrue(scriptIds.contains("egyptian"))
        assertTrue(scriptIds.contains("brahmi"))
        assertTrue(scriptIds.contains("kharosthi"))
        assertTrue(scriptIds.contains("phoenician"))
        assertTrue(scriptIds.contains("runic"))

        // Ensure every script has keys and non-empty categories
        for (script in scripts) {
            assertTrue("${script.name} must have keys", script.keys.isNotEmpty())
            assertTrue("${script.name} must have categories", script.categories.isNotEmpty())
            for (key in script.keys) {
                assertTrue("Key label must not be blank in ${script.name}", key.label.isNotBlank())
                assertTrue("Key character must not be blank in ${script.name}", key.character.isNotBlank())
            }
        }
    }

    private fun findSourceFile(relativePath: String): File? {
        val candidates = listOf(
            File(relativePath),
            File(relativePath.removePrefix("app/")),
            File("app/$relativePath"),
            File(System.getProperty("user.dir") ?: ".", relativePath),
            File(System.getProperty("user.dir") ?: ".", relativePath.removePrefix("app/"))
        )
        return candidates.firstOrNull { it.exists() }
    }
}
