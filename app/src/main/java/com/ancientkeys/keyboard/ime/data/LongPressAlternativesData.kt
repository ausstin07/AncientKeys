package com.ancientkeys.keyboard.ime.data

/**
 * Provides linguistically documented long-press character alternatives for:
 * - Scholarly Latin transliteration (IAST macrons, underdots for retroflexes, vocalic r/l, anusvara/visarga)
 * - Greek polytonic accents and diacritics (oxia, perispomeni, psili, dasia, final sigma)
 * - Standard Latin accents (acute, grave, circumflex, umlaut)
 */
object LongPressAlternativesData {

    private val alternativesMap: Map<String, List<String>> = mapOf(
        // Latin lower-case vowels with scholarly macrons & diacritics
        "a" to listOf("ā", "á", "à", "ä", "â", "ã", "å", "æ"),
        "e" to listOf("ē", "é", "è", "ë", "ê"),
        "i" to listOf("ī", "í", "ì", "ï", "î"),
        "o" to listOf("ō", "ó", "ò", "ö", "ô", "õ", "œ"),
        "u" to listOf("ū", "ú", "ù", "ü", "û"),

        // Scholarly consonants for transliterating ancient scripts (Brahmi, Kharosthi, Old English/Runic)
        "r" to listOf("ṛ", "ṝ"),
        "l" to listOf("ḷ", "ḹ"),
        "m" to listOf("ṃ", "ṁ"),
        "h" to listOf("ḥ"),
        "n" to listOf("ñ", "ṅ", "ṇ"),
        "s" to listOf("ś", "ṣ", "ß"),
        "t" to listOf("ṭ", "þ"),
        "d" to listOf("ḍ", "ð"),

        // Latin upper-case vowels and consonants
        "A" to listOf("Ā", "Á", "À", "Ä", "Â", "Ã", "Å", "Æ"),
        "E" to listOf("Ē", "É", "È", "Ë", "Ê"),
        "I" to listOf("Ī", "Í", "Ì", "Ï", "Î"),
        "O" to listOf("Ō", "Ó", "Ò", "Ö", "Ô", "Õ", "Œ"),
        "U" to listOf("Ū", "Ú", "Ù", "Ü", "Û"),
        "R" to listOf("Ṛ", "Ṝ"),
        "L" to listOf("Ḷ", "Ḹ"),
        "M" to listOf("Ṃ", "Ṁ"),
        "H" to listOf("Ḥ"),
        "N" to listOf("Ñ", "Ṅ", "Ṇ"),
        "S" to listOf("Ś", "Ṣ"),
        "T" to listOf("Ṭ", "Þ"),
        "D" to listOf("Ḍ", "Ð"),

        // Greek polytonic & epigraphic alternatives
        "α" to listOf("ά", "ᾶ", "ὰ", "ἀ", "ἁ", "ἆ", "ἇ", "ἂ", "ἃ"),
        "ε" to listOf("έ", "ὲ", "ἐ", "ἑ", "ἒ", "ἓ"),
        "η" to listOf("ή", "ῆ", "ὴ", "ἠ", "ἡ", "ἦ", "ἧ"),
        "ι" to listOf("ί", "ῖ", "ὶ", "ἰ", "ἱ", "ἶ", "ἷ", "ϊ"),
        "ο" to listOf("ό", "ὸ", "ὀ", "ὁ", "ὂ", "ὃ"),
        "υ" to listOf("ύ", "ῦ", "ὺ", "ὐ", "ὑ", "ὖ", "ὗ", "ϋ"),
        "ω" to listOf("ώ", "ῶ", "ὼ", "ὠ", "ὡ", "ὦ", "ὧ"),
        "ρ" to listOf("ῥ", "ῤ"),
        "σ" to listOf("ς"), // Word-final sigma

        // Greek uppercase polytonic
        "Α" to listOf("Ά", "Ἀ", "Ἁ", "Ἆ", "Ἇ"),
        "Ε" to listOf("Έ", "Ἐ", "Ἑ"),
        "Η" to listOf("Ή", "Ἠ", "Ἡ", "Ἦ", "Ἧ"),
        "Ι" to listOf("Ί", "Ἰ", "Ἱ", "Ἶ", "Ἷ"),
        "Ο" to listOf("Ό", "Ὀ", "Ὁ"),
        "Υ" to listOf("Ύ", "ὐ", "Ὑ", "Ὗ"),
        "Ω" to listOf("Ώ", "Ὠ", "Ὡ", "Ὦ", "Ὧ"),
        "Ρ" to listOf("Ῥ")
    )

    /**
     * Returns documented alternatives for a given character key, or empty if none.
     */
    fun getAlternatives(key: String): List<String> {
        return alternativesMap[key] ?: emptyList()
    }

    /**
     * Whether a key has long-press alternatives available.
     */
    fun hasAlternatives(key: String): Boolean {
        return alternativesMap.containsKey(key)
    }
}

