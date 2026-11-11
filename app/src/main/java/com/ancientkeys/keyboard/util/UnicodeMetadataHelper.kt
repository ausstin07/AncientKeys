package com.ancientkeys.keyboard.util

import com.ancientkeys.keyboard.model.ScriptKey

/**
 * Utility functions for parsing, formatting, and inspecting Unicode metadata
 * across historical writing systems according to the Unicode Standard.
 */
object UnicodeMetadataHelper {

    /**
     * Extracts the primary 32-bit Unicode code point from a string.
     */
    fun getCodePoint(text: String): Int {
        if (text.isEmpty()) return 0
        return text.codePointAt(0)
    }

    /**
     * Parses a hex string formatted as "U+1313F", "1313F", "0391", etc. into an integer code point.
     * Returns null if unparseable.
     */
    fun parseCodePointHex(hexStr: String): Int? {
        val clean = hexStr.trim().removePrefix("U+").removePrefix("u+").removePrefix("0x").removePrefix("0X")
        return clean.toIntOrNull(16)
    }

    /**
     * Formats an integer code point into standard "U+XXXX" or "U+XXXXX" uppercase format.
     */
    fun formatCodePoint(codePoint: Int): String {
        return "U+%04X".format(codePoint)
    }

    /**
     * Returns the official Unicode Character Database name for a character.
     * Uses [ScriptKey.unicodeName] if populated, falling back to Java's built-in [Character.getName].
     */
    fun getOfficialUnicodeName(key: ScriptKey): String {
        if (key.unicodeName.isNotBlank()) {
            return key.unicodeName
        }
        val cp = parseCodePointHex(key.unicodeCodePoint) ?: getCodePoint(key.character)
        if (cp > 0) {
            val name = Character.getName(cp)
            if (!name.isNullOrBlank()) {
                return name
            }
        }
        return "UNASSIGNED OR UNDOCUMENTED (${key.unicodeCodePoint})"
    }

    /**
     * Determines whether a code point is a Unicode combining mark (diacritic / dependent vowel sign).
     */
    fun isCombiningMark(codePoint: Int): Boolean {
        val type = Character.getType(codePoint)
        return type == Character.NON_SPACING_MARK.toInt() ||
                type == Character.COMBINING_SPACING_MARK.toInt() ||
                type == Character.ENCLOSING_MARK.toInt()
    }

    /**
     * Returns a safe display representation for a character.
     * If the character is a combining mark and doesn't already start with a base glyph or dotted circle,
     * prepends the standard Unicode dotted circle (U+25CC) for visual clarity.
     * Note: This is strictly for visual presentation; the actual typing character remains unmodified.
     */
    fun getDisplayGlyph(key: ScriptKey): String {
        if (key.label.isNotBlank() && key.label != key.character) {
            return key.label
        }
        val cp = getCodePoint(key.character)
        return if (key.isCombining || isCombiningMark(cp)) {
            "◌${key.character}"
        } else {
            key.character
        }
    }
}

