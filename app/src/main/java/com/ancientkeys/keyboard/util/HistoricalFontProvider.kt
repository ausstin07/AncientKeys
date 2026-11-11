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
     * Clears the in-memory typeface cache (useful for testing or memory reclamation).
     */
    fun clearCache() {
        fontFamilyCache.clear()
    }
}
