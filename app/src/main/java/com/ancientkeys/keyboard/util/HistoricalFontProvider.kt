package com.ancientkeys.keyboard.util

import android.content.Context
import android.graphics.Typeface
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
        if (character.isEmpty()) return false
        val cleanId = scriptId.lowercase().trim()
        val cacheKey = "$cleanId:$character"
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
                    } else null
                }
                if (typeface != null) {
                    paint.typeface = typeface
                }
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                paint.hasGlyph(character)
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
     * Clears the in-memory typeface cache (useful for testing or memory reclamation).
     */
    fun clearCache() {
        fontFamilyCache.clear()
        rawTypefaceCache.clear()
        glyphSupportCache.clear()
    }
}
