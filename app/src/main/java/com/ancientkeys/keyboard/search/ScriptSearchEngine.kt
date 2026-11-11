package com.ancientkeys.keyboard.search

import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptKey
import com.ancientkeys.keyboard.util.UnicodeMetadataHelper
import java.text.Normalizer

/**
 * Offline, fast in-memory search and filtering engine for historical scripts and characters.
 *
 * Provides diacritic-insensitive, case-folded matching so users can search using either
 * plain ASCII input (e.g. "Gandhara", "Kharosthi", "Brahmi") or academic transliterations
 * with macrons, underdots, and accents (e.g. "Gandhāra", "Kharoṣṭhī", "Brāhmī").
 */
object ScriptSearchEngine {

    private val DIACRITICS_REGEX = "\\p{M}+".toRegex()

    /**
     * Normalizes a string by decomposing Unicode characters and stripping combining diacritical marks.
     * Also folds case, strips punctuation variants, and replaces common Germanic/historical equivalents
     * (e.g. thorn 'þ' -> 'th', eth 'ð' -> 'dh').
     */
    fun normalizeForSearch(input: String): String {
        if (input.isEmpty()) return ""
        val decomposed = Normalizer.normalize(input.trim().lowercase(), Normalizer.Form.NFD)
        return DIACRITICS_REGEX.replace(decomposed, "")
            .replace("þ", "th")
            .replace("ð", "dh")
            .replace("·", "")
    }

    private fun matches(target: String, query: String, normalizedQuery: String): Boolean {
        if (target.contains(query, ignoreCase = true)) return true
        if (normalizedQuery.isNotEmpty()) {
            val normalizedTarget = normalizeForSearch(target)
            if (normalizedTarget.contains(normalizedQuery)) return true
        }
        return false
    }

    /**
     * Filters scripts case-insensitively and diacritic-insensitively by:
     * - Canonical name (e.g. "Kharosthi (right-to-left)")
     * - Alternative spellings and aliases (e.g. "Gandhara", "Gandhāra", "Gāndhārī", "Kharoṣṭhī")
     * - Historical region (e.g. "Gandhāra", "Levant", "Egypt", "Europe")
     * - Historical period
     * - Description
     */
    fun searchScripts(scripts: List<HistoricalScript>, rawQuery: String): List<HistoricalScript> {
        val query = rawQuery.trim()
        if (query.isEmpty()) return scripts

        val normalizedQuery = normalizeForSearch(query)

        return scripts.filter { script ->
            matches(script.name, query, normalizedQuery) ||
                    script.alternativeNames.any { matches(it, query, normalizedQuery) } ||
                    matches(script.region, query, normalizedQuery) ||
                    matches(script.historicalPeriod, query, normalizedQuery) ||
                    matches(script.description, query, normalizedQuery)
        }
    }

    /**
     * Filters characters within a script case-insensitively and diacritic-insensitively by:
     * - Unicode hexadecimal code point (e.g. "U+1313F", "1313F", "0391", "U+0391", "U+11013")
     * - Official Unicode character name (e.g. "FEHU", "ALPHA", "HIEROGLYPH G001", "KHAROSHTHI LETTER KA")
     * - Character glyph or label
     * - Scholarly transliteration (e.g. "ʾālep", "Fehu", "ꜣ", "ka")
     * - Phonetic hint / Gardiner code (e.g. "G1", "A1", "kʰ")
     * - Documented meaning / English gloss (e.g. "vulture", "cattle", "house")
     * - Historical notes
     * - Category name
     */
    fun searchCharacters(keys: List<ScriptKey>, rawQuery: String): List<ScriptKey> {
        val query = rawQuery.trim()
        if (query.isEmpty()) return keys

        val cleanQuery = query.removePrefix("U+").removePrefix("u+").removePrefix("0x").removePrefix("0X").trim()
        val normalizedQuery = normalizeForSearch(query)

        return keys.filter { key ->
            // Code point matches
            key.unicodeCodePoint.contains(query, ignoreCase = true) ||
                    (cleanQuery.isNotEmpty() && key.unicodeCodePoint.removePrefix("U+").contains(cleanQuery, ignoreCase = true)) ||
                    // Character or label matches
                    key.character.contains(query, ignoreCase = true) ||
                    key.label.contains(query, ignoreCase = true) ||
                    // Official Unicode name matches
                    UnicodeMetadataHelper.getOfficialUnicodeName(key).contains(query, ignoreCase = true) ||
                    // Metadata matches (direct and diacritic-normalized)
                    matches(key.transliteration, query, normalizedQuery) ||
                    matches(key.phoneticHint, query, normalizedQuery) ||
                    matches(key.meaning, query, normalizedQuery) ||
                    matches(key.historicalNotes, query, normalizedQuery) ||
                    matches(key.category, query, normalizedQuery)
        }
    }
}
