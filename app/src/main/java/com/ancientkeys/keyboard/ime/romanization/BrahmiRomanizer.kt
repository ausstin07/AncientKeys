package com.ancientkeys.keyboard.ime.romanization

/**
 * Documented transliteration engine for Brahmi Romanized Input.
 *
 * Adheres strictly to standard academic conventions (IAST: International Alphabet of
 * Sanskrit Transliteration and epigraphic Brahmi conventions).
 *
 * Clear Distinction:
 * - Romanization/Transliteration: Deterministic character-level mapping from Latin transliteration
 *   to authentic Brahmi Unicode code points.
 * - Translation: Semantic rendering of language meaning (NOT performed; never guessed).
 * - No invented historical phonetic mappings.
 */
object BrahmiRomanizer : ScriptRomanizer {

    override val scriptId: String = "brahmi"
    override val schemeName: String = "IAST (Brahmi)"
    override val academicConvention: String = "International Alphabet of Sanskrit Transliteration (IAST) & Ashokan Epigraphy"

    // Documented independent vowels
    private val independentVowels = mapOf(
        "a" to "𑀅",
        "ā" to "𑀆",
        "aa" to "𑀆",
        "i" to "𑀇",
        "ī" to "𑀈",
        "ii" to "𑀈",
        "u" to "𑀉",
        "ū" to "𑀊",
        "uu" to "𑀊",
        "ṛ" to "𑀋",
        "r̥" to "𑀋",
        "e" to "𑀏",
        "ai" to "𑀐",
        "o" to "𑀑",
        "au" to "𑀒"
    )

    // Documented base consonants (inherent vowel 'a')
    private val consonants = mapOf(
        "k" to "𑀓",
        "kh" to "𑀔",
        "g" to "𑀕",
        "gh" to "𑀖",
        "ṅ" to "𑀗",
        "ng" to "𑀗",

        "c" to "𑀘",
        "ch" to "𑀙",
        "j" to "𑀚",
        "jh" to "𑀛",
        "ñ" to "𑀜",
        "ny" to "𑀜",

        "ṭ" to "𑀝",
        "t." to "𑀝",
        "ṭh" to "𑀞",
        "th." to "𑀞",
        "ḍ" to "𑀟",
        "d." to "𑀟",
        "ḍh" to "𑀠",
        "dh." to "𑀠",
        "ṇ" to "𑀡",
        "n." to "𑀡",

        "t" to "𑀢",
        "th" to "𑀣",
        "d" to "𑀤",
        "dh" to "𑀥",
        "n" to "𑀦",

        "p" to "𑀧",
        "ph" to "𑀨",
        "b" to "𑀩",
        "bh" to "𑀪",
        "m" to "𑀫",

        "y" to "𑀬",
        "r" to "𑀭",
        "l" to "𑀮",
        "v" to "𑀯",
        "w" to "𑀯",

        "ś" to "𑀰",
        "sh" to "𑀰",
        "ṣ" to "𑀱",
        "sh." to "𑀱",
        "s" to "𑀲",
        "h" to "𑀳",
        "ḷ" to "𑀴",
        "l." to "𑀴"
    )

    // Documented dependent vowel signs (matras)
    private val dependentVowels = mapOf(
        "ā" to "\uD804\uDC38",  // U+11038 BRAHMI VOWEL SIGN AA
        "aa" to "\uD804\uDC38",
        "i" to "\uD804\uDC3A",   // U+1103A BRAHMI VOWEL SIGN I
        "ī" to "\uD804\uDC3B",   // U+1103B BRAHMI VOWEL SIGN II
        "ii" to "\uD804\uDC3B",
        "u" to "\uD804\uDC3C",   // U+1103C BRAHMI VOWEL SIGN U
        "ū" to "\uD804\uDC3D",   // U+1103D BRAHMI VOWEL SIGN UU
        "uu" to "\uD804\uDC3D",
        "e" to "\uD804\uDC42",   // U+11042 BRAHMI VOWEL SIGN E
        "ai" to "\uD804\uDC43",  // U+11043 BRAHMI VOWEL SIGN AI
        "o" to "\uD804\uDC44",   // U+11044 BRAHMI VOWEL SIGN O
        "au" to "\uD804\uDC45"   // U+11045 BRAHMI VOWEL SIGN AU
    )

    // Modifiers & Punctuation
    private val modifiers = mapOf(
        "ṃ" to "\uD804\uDC01",  // U+11001 Anusvara
        "m." to "\uD804\uDC01",
        "ḥ" to "\uD804\uDC02",  // U+11002 Visarga
        "h." to "\uD804\uDC02",
        "|" to "𑁇",             // U+11047 Danda
        "||" to "𑁈"             // U+11048 Double Danda
    )

    /**
     * Converts a single Romanized syllable/token (e.g. "ka", "kā", "ki", "kha", "a", "asoka")
     * into authentic Brahmi Unicode characters.
     */
    override fun transliterateToken(token: String): String {
        val lower = token.lowercase().trim()
        if (lower.isEmpty()) return ""

        val sb = StringBuilder()
        var i = 0

        while (i < lower.length) {
            // Check double-character punctuation ("||")
            if (i + 2 <= lower.length && lower.substring(i, i + 2) == "||") {
                sb.append(modifiers["||"])
                i += 2
                continue
            }
            if (lower[i] == '|') {
                sb.append(modifiers["|"])
                i += 1
                continue
            }

            // Check if match longest consonant first (e.g. "kh", "gh", "ch", "jh", "ṭh", "ḍh", "th", "dh", "ph", "bh", "sh")
            var matchedConsonant: String? = null
            var consLen = 0

            for (len in 3 downTo 1) {
                if (i + len <= lower.length) {
                    val candidate = lower.substring(i, i + len)
                    if (consonants.containsKey(candidate)) {
                        matchedConsonant = consonants[candidate]
                        consLen = len
                        break
                    }
                }
            }

            if (matchedConsonant != null) {
                sb.append(matchedConsonant)
                i += consLen

                // Check for subsequent dependent vowel sign
                var matchedVowel: String? = null
                var vowelLen = 0
                for (vLen in 2 downTo 1) {
                    if (i + vLen <= lower.length) {
                        val vCandidate = lower.substring(i, i + vLen)
                        if (vCandidate == "a") {
                            // Inherent vowel 'a': base consonant already has it!
                            matchedVowel = ""
                            vowelLen = 1
                            break
                        } else if (dependentVowels.containsKey(vCandidate)) {
                            matchedVowel = dependentVowels[vCandidate]
                            vowelLen = vLen
                            break
                        }
                    }
                }

                if (matchedVowel != null) {
                    sb.append(matchedVowel)
                    i += vowelLen
                }

                // Check for subsequent anusvara or visarga
                if (i < lower.length) {
                    if (lower[i] == 'ṃ' || (lower[i] == 'm' && i + 1 < lower.length && lower[i+1] == '.')) {
                        sb.append(modifiers["ṃ"])
                        i += if (lower[i] == 'ṃ') 1 else 2
                    } else if (lower[i] == 'ḥ' || (lower[i] == 'h' && i + 1 < lower.length && lower[i+1] == '.')) {
                        sb.append(modifiers["ḥ"])
                        i += if (lower[i] == 'ḥ') 1 else 2
                    }
                }
            } else {
                // Check independent vowels
                var matchedIndep: String? = null
                var indepLen = 0
                for (len in 2 downTo 1) {
                    if (i + len <= lower.length) {
                        val candidate = lower.substring(i, i + len)
                        if (independentVowels.containsKey(candidate)) {
                            matchedIndep = independentVowels[candidate]
                            indepLen = len
                            break
                        }
                    }
                }

                if (matchedIndep != null) {
                    sb.append(matchedIndep)
                    i += indepLen
                } else {
                    // Fallback: character not in Brahmi phonetic map, preserve literally
                    sb.append(lower[i])
                    i += 1
                }
            }
        }

        return sb.toString()
    }

    /**
     * Generates a character-composition preview for a base consonant key and its vowel signs.
     */
    override fun getCompositionPreviews(prefix: String): List<BrahmiCompositionPreview> {
        val baseBrahmi = consonants[prefix.lowercase()] ?: return emptyList()
        return listOf(
            BrahmiCompositionPreview(prefix, baseBrahmi, "$baseBrahmi (inherent a)"),
            BrahmiCompositionPreview("${prefix}ā", "$baseBrahmi\uD804\uDC38"),
            BrahmiCompositionPreview("${prefix}i", "$baseBrahmi\uD804\uDC3A"),
            BrahmiCompositionPreview("${prefix}ī", "$baseBrahmi\uD804\uDC3B"),
            BrahmiCompositionPreview("${prefix}u", "$baseBrahmi\uD804\uDC3C"),
            BrahmiCompositionPreview("${prefix}ū", "$baseBrahmi\uD804\uDC3D"),
            BrahmiCompositionPreview("${prefix}e", "$baseBrahmi\uD804\uDC42"),
            BrahmiCompositionPreview("${prefix}ai", "$baseBrahmi\uD804\uDC43"),
            BrahmiCompositionPreview("${prefix}o", "$baseBrahmi\uD804\uDC44"),
            BrahmiCompositionPreview("${prefix}au", "$baseBrahmi\uD804\uDC45"),
            BrahmiCompositionPreview("${prefix}ṃ", "$baseBrahmi\uD804\uDC01"),
            BrahmiCompositionPreview("${prefix}ḥ", "$baseBrahmi\uD804\uDC02")
        )
    }
}
