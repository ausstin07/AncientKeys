package com.ancientkeys.keyboard.util

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily

/**
 * Provides offline font families for ancient historical scripts from bundled assets.
 *
 * All bundled fonts are Google Noto fonts distributed under the SIL Open Font License 1.1.
 * If font loading is unavailable or running in unit tests, falls back cleanly to [FontFamily.Default].
 */
object HistoricalFontProvider {

    private val fontFamilyCache = mutableMapOf<String, FontFamily>()
    private val rawTypefaceCache = mutableMapOf<String, Typeface?>()
    private val glyphSupportCache = mutableMapOf<String, Boolean>()

    /**
     * Maps script IDs to bundled asset font paths.
     */
    fun getAssetPathForScript(scriptId: String): String? {
        return when (scriptId.lowercase().trim()) {
            "brahmi" -> "fonts/NotoSansBrahmi-Regular.ttf"
            "egyptian", "egyptian_hieroglyphs" -> "fonts/NotoSansEgyptianHieroglyphs-Regular.ttf"
            "kharosthi" -> "fonts/NotoSansKharosthi-Regular.ttf"
            "phoenician" -> "fonts/NotoSansPhoenician-Regular.ttf"
            "runic", "elder_futhark" -> "fonts/NotoSansRunic-Regular.ttf"
            else -> null
        }
    }

    /**
     * Resolves the [FontFamily] for a given script, caching the result in memory.
     */
    fun getFontFamily(context: Context?, scriptId: String): FontFamily {
        if (context == null) return FontFamily.Default

        val cleanId = scriptId.lowercase().trim()
        fontFamilyCache[cleanId]?.let { return it }

        if (cleanId == "greek" || cleanId == "ancient_greek") {
            val family = FontFamily.Serif
            fontFamilyCache[cleanId] = family
            return family
        }

        val path = getAssetPathForScript(cleanId) ?: return FontFamily.Default

        return try {
            val typeface = Typeface.createFromAsset(context.assets, path)
            val family = FontFamily(typeface)
            fontFamilyCache[cleanId] = family
            family
        } catch (_: Throwable) {
            FontFamily.Default
        }
    }

    /**
     * Checks if the character can be rendered by the device font or bundled typeface.
     * Uses android.graphics.Paint.hasGlyph(String) on Android M+ (API 23+).
     * Falls back to true if checking is unavailable or in unit tests.
     */
    fun canRenderGlyph(context: Context?, scriptId: String, character: String): Boolean {
        val testCharacter = if (character.contains('◌')) {
            character.replace("◌", "")
        } else {
            character
        }
        if (testCharacter.isEmpty()) return false
        val cleanId = scriptId.lowercase().trim()
        val cacheKey = "$cleanId:$testCharacter"
        glyphSupportCache[cacheKey]?.let { return it }

        val canRender = try {
            val paint = android.graphics.Paint()
            if (context != null) {
                val typeface = rawTypefaceCache.getOrPut(cleanId) {
                    val path = getAssetPathForScript(cleanId)
                    if (path != null) {
                        try {
                            Typeface.createFromAsset(context.assets, path)
                        } catch (_: Throwable) {
                            null
                        }
                    } else if (cleanId == "greek" || cleanId == "ancient_greek") {
                        Typeface.SERIF
                    } else null
                }
                if (typeface != null) {
                    paint.typeface = typeface
                }
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                paint.hasGlyph(testCharacter)
            } else {
                true
            }
        } catch (_: Throwable) {
            true
        }

        glyphSupportCache[cacheKey] = canRender
        return canRender
    }

    /**
     * Builds an [AnnotatedString] that safely styles combining mark labels.
     * When text contains the dotted circle placeholder ('◌', U+25CC), this method
     * ensures '◌' is styled with [FontFamily.Default] (where the glyph exists in system fonts)
     * while historical script characters are styled with [scriptFont].
     */
    fun buildDisplayGlyphAnnotatedString(
        text: String,
        scriptFont: FontFamily?
    ): AnnotatedString {
        if (!text.contains('◌')) {
            return AnnotatedString(text)
        }
        val builder = AnnotatedString.Builder()
        var i = 0
        while (i < text.length) {
            val cp = text.codePointAt(i)
            val charCount = Character.charCount(cp)
            val str = text.substring(i, i + charCount)
            if (cp == 0x25CC) { // Dotted circle
                builder.pushStyle(SpanStyle(fontFamily = FontFamily.Default))
                builder.append(str)
                builder.pop()
            } else {
                if (scriptFont != null) {
                    builder.pushStyle(SpanStyle(fontFamily = scriptFont))
                    builder.append(str)
                    builder.pop()
                } else {
                    builder.append(str)
                }
            }
            i += charCount
        }
        return builder.toAnnotatedString()
    }

    /**
     * Clears the in-memory typeface cache (useful for testing or memory reclamation).
     */
    fun clearCache() {
        fontFamilyCache.clear()
        rawTypefaceCache.clear()
        glyphSupportCache.clear()
    }
}
