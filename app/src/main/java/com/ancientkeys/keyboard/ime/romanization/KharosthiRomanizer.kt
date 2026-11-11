package com.ancientkeys.keyboard.ime.romanization

/**
 * Documented transliteration engine for Kharosthi (Gāndhārī Prakrit) Romanized Input.
 *
 * Adheres strictly to standard epigraphic and academic transliteration conventions
 * established by Richard Salomon (1999) and Andrew Glass (2000).
 *
 * Distinct Principles:
 * - Deterministic, character-level transliteration into authentic Kharosthi Unicode points.
 * - Inherent vowel 'a' in consonants; vowels other than 'a' written with combining diacritics.
 * - Independent vowels (other than 'a') formed epigraphically using letter 'a' (𐨀) + vowel sign.
 * - No translation is attempted.
 */
object KharosthiRomanizer : ScriptRomanizer {

    override val scriptId: String = "kharosthi"
    override val schemeName: String = "Gāndhārī (Kharoṣṭhī)"
    override val academicConvention: String = "Gandhari Academic Epigraphic Transliteration (Salomon 1999)"

    // Base consonants (carrying inherent vowel 'a')
    private val consonants = mapOf(
        "k" to "𐨐",
        "kh" to "𐨑",
        "g" to "𐨒",
        "gh" to "𐨓",

        "c" to "𐨕",
        "ch" to "𐨖",
        "j" to "𐨗",
        "ñ" to "𐨙",
        "ny" to "𐨙",
        "n." to "𐨞",
        "ṇa" to "𐨞",
        "ṇ" to "𐨞",

        "ṭ" to "𐨚",
        "t." to "𐨚",
        "ṭh" to "𐨛",
        "th." to "𐨛",
        "ḍ" to "𐨜",
        "d." to "𐨜",
        "ḍh" to "𐨝",
        "dh." to "𐨝",

        "t" to "𐨟",
        "th" to "𐨠",
        "d" to "𐨡",
        "dh" to "𐨢",
        "n" to "𐨣",

        "p" to "𐨤",
        "ph" to "𐨥",
        "b" to "𐨦",
        "bh" to "𐨧",
        "m" to "𐨨",

        "y" to "𐨩",
        "r" to "𐨪",
        "l" to "𐨫",
        "v" to "𐨬",
        "w" to "𐨬",

        "ś" to "𐨭",
        "sh" to "𐨭",
        "ṣ" to "𐨮",
        "sh." to "𐨮",
        "s" to "𐨯",
        "z" to "𐨰",
        "h" to "𐨱"
    )

    // Dependent vowel signs (combining diacritics)
    private val dependentVowels = mapOf(
        "i" to "\uD802\uDE01",
        "ī" to "\uD802\uDE01",
        "ii" to "\uD802\uDE01",
        "u" to "\uD802\uDE02",
        "ū" to "\uD802\uDE02",
        "uu" to "\uD802\uDE02",
        "ṛ" to "\uD802\uDE03",
        "r̥" to "\uD802\uDE03",
        "e" to "\uD802\uDE05",
        "ai" to "\uD802\uDE05",
        "o" to "\uD802\uDE06",
        "au" to "\uD802\uDE06"
    )

    // Independent vowels (formed on base A: 𐨀)
    private val independentVowels = mapOf(
        "a" to "𐨀",
        "ā" to "𐨀",
        "aa" to "𐨀",
        "i" to "𐨀\uD802\uDE01",
        "ī" to "𐨀\uD802\uDE01",
        "ii" to "𐨀\uD802\uDE01",
        "u" to "𐨀\uD802\uDE02",
        "ū" to "𐨀\uD802\uDE02",
        "uu" to "𐨀\uD802\uDE02",
        "ṛ" to "𐨀\uD802\uDE03",
        "r̥" to "𐨀\uD802\uDE03",
        "e" to "𐨀\uD802\uDE05",
        "ai" to "𐨀\uD802\uDE05",
        "o" to "𐨀\uD802\uDE06",
        "au" to "𐨀\uD802\uDE06"
    )

    // Modifiers & Punctuation
    private val modifiers = mapOf(
        "ṃ" to "\uD802\uDE0E",
        "m." to "\uD802\uDE0E",
        "ḥ" to "\uD802\uDE0F",
        "h." to "\uD802\uDE0F",
        "_" to "\uD802\uDE3F", // Virama
        "|" to "𐩐",
        "||" to "𐩑"
    )

    // Numerals (Gandhāran numerals U+10A40..U+10A47)
    private val numerals = mapOf(
        "1" to "𐩀",
        "2" to "𐩁",
        "3" to "𐩂",
        "4" to "𐩃",
        "10" to "𐩄",
        "20" to "𐩅",
        "100" to "𐩆",
        "1000" to "𐩇"
    )

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

            // Check numerals (multi-digit like "1000", "100", "20", "10", then single digits)
            var matchedNum: String? = null
            var numLen = 0
            for (len in 4 downTo 1) {
                if (i + len <= lower.length) {
                    val cand = lower.substring(i, i + len)
                    if (numerals.containsKey(cand)) {
                        matchedNum = numerals[cand]
                        numLen = len
                        break
                    }
                }
            }
            if (matchedNum != null) {
                sb.append(matchedNum)
                i += numLen
                continue
            }

            // Check consonants (longest match first: e.g. "kh", "gh", "ch", "jh", "ṭh", "ḍh", "th", "dh", "ph", "bh", "sh")
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
                        if (vCandidate == "a" || vCandidate == "ā") {
                            // Inherent 'a' is already part of base consonant in Kharosthi
                            matchedVowel = ""
                            vowelLen = vLen
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

                // Check for modifier (anusvara, visarga, virama)
                if (i < lower.length) {
                    if (lower[i] == 'ṃ' || (lower[i] == 'm' && i + 1 < lower.length && lower[i+1] == '.')) {
                        sb.append(modifiers["ṃ"])
                        i += if (lower[i] == 'ṃ') 1 else 2
                    } else if (lower[i] == 'ḥ' || (lower[i] == 'h' && i + 1 < lower.length && lower[i+1] == '.')) {
                        sb.append(modifiers["ḥ"])
                        i += if (lower[i] == 'ḥ') 1 else 2
                    } else if (lower[i] == '_') {
                        sb.append(modifiers["_"])
                        i += 1
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
                    // Fallback: preserve literal
                    sb.append(lower[i])
                    i += 1
                }
            }
        }

        return sb.toString()
    }

    override fun getCompositionPreviews(prefix: String): List<RomanizedCompositionPreview> {
        val baseKharosthi = consonants[prefix.lowercase()] ?: return emptyList()
        return listOf(
            RomanizedCompositionPreview(prefix, baseKharosthi, "$baseKharosthi (inherent a)"),
            RomanizedCompositionPreview("${prefix}i", "$baseKharosthi\uD802\uDE01"),
            RomanizedCompositionPreview("${prefix}u", "$baseKharosthi\uD802\uDE02"),
            RomanizedCompositionPreview("${prefix}ṛ", "$baseKharosthi\uD802\uDE03"),
            RomanizedCompositionPreview("${prefix}e", "$baseKharosthi\uD802\uDE05"),
            RomanizedCompositionPreview("${prefix}o", "$baseKharosthi\uD802\uDE06"),
            RomanizedCompositionPreview("${prefix}ṃ", "$baseKharosthi\uD802\uDE0E"),
            RomanizedCompositionPreview("${prefix}ḥ", "$baseKharosthi\uD802\uDE0F"),
            RomanizedCompositionPreview("${prefix}_", "$baseKharosthi\uD802\uDE3F")
        )
    }
}

