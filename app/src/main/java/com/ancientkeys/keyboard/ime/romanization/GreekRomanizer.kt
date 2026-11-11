package com.ancientkeys.keyboard.ime.romanization

/**
 * Documented transliteration engine for Classical Ancient Greek.
 *
 * Adheres to standard classical Greek academic transliteration and epigraphic conventions
 * (e.g. Jeffery 1990, Woodard 2010).
 *
 * Distinct Principles:
 * - Deterministic, rule-based transliteration into authentic Greek Unicode code points.
 * - Automatic contextual final sigma (ς) resolution at word boundaries.
 * - Support for both lowercase and classical uppercase epigraphic forms.
 * - Distinct transliteration (spelling representation), NOT semantic translation.
 */
object GreekRomanizer : ScriptRomanizer {

    override val scriptId: String = "greek"
    override val schemeName: String = "Classical Greek (Epigraphic)"
    override val academicConvention: String = "Standard Classical Academic Greek Transliteration (Jeffery 1990)"

    // Multi-character consonant clusters (checked before single letters)
    private val multiConsonantsLower = mapOf(
        "th" to "θ",
        "ph" to "φ",
        "ch" to "χ",
        "kh" to "χ",
        "ps" to "ψ",
        "ks" to "ξ"
    )

    private val multiConsonantsUpper = mapOf(
        "TH" to "Θ",
        "Th" to "Θ",
        "PH" to "Φ",
        "Ph" to "Φ",
        "CH" to "Χ",
        "Ch" to "Χ",
        "KH" to "Χ",
        "Kh" to "Χ",
        "PS" to "Ψ",
        "Ps" to "Ψ",
        "KS" to "Ξ",
        "Ks" to "Ξ"
    )

    // Single consonants
    private val singleConsonantsLower = mapOf(
        'b' to "β",
        'g' to "γ",
        'd' to "δ",
        'z' to "ζ",
        'k' to "κ",
        'c' to "κ",
        'l' to "λ",
        'm' to "μ",
        'n' to "ν",
        'x' to "ξ",
        'p' to "π",
        'r' to "ρ",
        's' to "σ", // Will be converted to ς if word-final
        't' to "τ",
        'f' to "φ",
        'w' to "ω",
        'v' to "ϝ", // Archaic digamma
        'q' to "ϙ"  // Archaic koppa
    )

    private val singleConsonantsUpper = mapOf(
        'B' to "Β",
        'G' to "Γ",
        'D' to "Δ",
        'Z' to "Ζ",
        'K' to "Κ",
        'C' to "Κ",
        'L' to "Λ",
        'M' to "Μ",
        'N' to "Ν",
        'X' to "Ξ",
        'P' to "Π",
        'R' to "Ρ",
        'S' to "Σ",
        'T' to "Τ",
        'F' to "Φ",
        'W' to "Ω",
        'V' to "Ϝ",
        'Q' to "Ϙ"
    )

    // Rough breathing vowel clusters (e.g. "ha" -> "ἁ")
    private val roughBreathingLower = mapOf(
        "ha" to "ἁ",
        "he" to "ἑ",
        "hē" to "ἡ",
        "hi" to "ἱ",
        "ho" to "ὁ",
        "hu" to "ὑ",
        "hy" to "ὑ",
        "hō" to "ὡ",
        "hw" to "ὡ",
        "hr" to "ῥ"
    )

    // Vowels
    private val vowelsLower = mapOf(
        "a" to "α",
        "ā" to "α",
        "á" to "ά",
        "à" to "ὰ",
        "e" to "ε",
        "é" to "έ",
        "è" to "ὲ",
        "ē" to "η",
        "h" to "η",
        "ḗ" to "ή",
        "i" to "ι",
        "ī" to "ι",
        "í" to "ί",
        "ì" to "ὶ",
        "o" to "ο",
        "ó" to "ό",
        "ò" to "ὸ",
        "u" to "υ",
        "y" to "υ",
        "ú" to "ύ",
        "ù" to "ὺ",
        "ō" to "ω",
        "ṓ" to "ώ",
        "ὼ" to "ὼ"
    )

    private val vowelsUpper = mapOf(
        "A" to "Α",
        "Ā" to "Α",
        "E" to "Ε",
        "Ē" to "Η",
        "H" to "Η",
        "I" to "Ι",
        "Ī" to "Ι",
        "O" to "Ο",
        "U" to "Υ",
        "Y" to "Υ",
        "Ō" to "Ω",
        "W" to "Ω"
    )

    // Punctuation
    private val punctuation = mapOf(
        ';' to ";", // Greek question mark U+037E
        '?' to ";",
        ':' to "·", // Greek ano teleia U+0387
        '.' to "·"
    )

    override fun transliterateToken(token: String): String {
        val trimmed = token.trim()
        if (trimmed.isEmpty()) return ""

        val sb = StringBuilder()
        var i = 0

        while (i < trimmed.length) {
            val ch = trimmed[i]

            // Check punctuation
            if (punctuation.containsKey(ch)) {
                sb.append(punctuation[ch])
                i += 1
                continue
            }

            // Check rough breathing (only at word start or after non-letter)
            if (i == 0 || !trimmed[i - 1].isLetter()) {
                var matchedBreathing: String? = null
                var brLen = 0
                for (len in 2 downTo 2) {
                    if (i + len <= trimmed.length) {
                        val cand = trimmed.substring(i, i + len).lowercase()
                        if (roughBreathingLower.containsKey(cand)) {
                            matchedBreathing = roughBreathingLower[cand]
                            brLen = len
                            break
                        }
                    }
                }
                if (matchedBreathing != null) {
                    sb.append(matchedBreathing)
                    i += brLen
                    continue
                }
            }

            // Check multi-consonants (e.g. "th", "ph", "ch", "ps", "ks")
            var matchedMulti: String? = null
            var multiLen = 0
            for (len in 2 downTo 2) {
                if (i + len <= trimmed.length) {
                    val cand = trimmed.substring(i, i + len)
                    if (multiConsonantsUpper.containsKey(cand)) {
                        matchedMulti = multiConsonantsUpper[cand]
                        multiLen = len
                        break
                    } else if (multiConsonantsLower.containsKey(cand.lowercase())) {
                        matchedMulti = multiConsonantsLower[cand.lowercase()]
                        multiLen = len
                        break
                    }
                }
            }
            if (matchedMulti != null) {
                sb.append(matchedMulti)
                i += multiLen
                continue
            }

            // Check single consonants
            if (ch.isUpperCase() && singleConsonantsUpper.containsKey(ch)) {
                sb.append(singleConsonantsUpper[ch])
                i += 1
                continue
            } else if (singleConsonantsLower.containsKey(ch)) {
                // Special final sigma handling: if 's' is followed by non-letter or end of string, use ς
                if (ch == 's') {
                    val isFinal = (i + 1 >= trimmed.length) || !trimmed[i + 1].isLetter()
                    sb.append(if (isFinal) "ς" else "σ")
                } else {
                    sb.append(singleConsonantsLower[ch])
                }
                i += 1
                continue
            }

            // Check vowels
            var matchedVowel: String? = null
            var vLen = 0
            for (len in 2 downTo 1) {
                if (i + len <= trimmed.length) {
                    val cand = trimmed.substring(i, i + len)
                    if (vowelsUpper.containsKey(cand)) {
                        matchedVowel = vowelsUpper[cand]
                        vLen = len
                        break
                    } else if (vowelsLower.containsKey(cand)) {
                        matchedVowel = vowelsLower[cand]
                        vLen = len
                        break
                    }
                }
            }
            if (matchedVowel != null) {
                sb.append(matchedVowel)
                i += vLen
                continue
            }

            // Fallback: preserve literal character
            sb.append(ch)
            i += 1
        }

        return sb.toString()
    }

    override fun getCompositionPreviews(prefix: String): List<RomanizedCompositionPreview> {
        val lower = prefix.lowercase().trim()
        if (lower.isEmpty()) return emptyList()

        return when (lower) {
            "a" -> listOf(
                RomanizedCompositionPreview("a", "α"),
                RomanizedCompositionPreview("á", "ά"),
                RomanizedCompositionPreview("à", "ὰ"),
                RomanizedCompositionPreview("ha", "ἁ"),
                RomanizedCompositionPreview("ā", "ᾱ")
            )
            "e" -> listOf(
                RomanizedCompositionPreview("e", "ε"),
                RomanizedCompositionPreview("é", "έ"),
                RomanizedCompositionPreview("he", "ἑ"),
                RomanizedCompositionPreview("ē", "η")
            )
            "i" -> listOf(
                RomanizedCompositionPreview("i", "ι"),
                RomanizedCompositionPreview("í", "ί"),
                RomanizedCompositionPreview("hi", "ἱ")
            )
            "o" -> listOf(
                RomanizedCompositionPreview("o", "ο"),
                RomanizedCompositionPreview("ó", "ό"),
                RomanizedCompositionPreview("ho", "ὁ"),
                RomanizedCompositionPreview("ō", "ω")
            )
            "u", "y" -> listOf(
                RomanizedCompositionPreview("u", "υ"),
                RomanizedCompositionPreview("ú", "ύ"),
                RomanizedCompositionPreview("hu", "ὑ")
            )
            "s" -> listOf(
                RomanizedCompositionPreview("s", "σ"),
                RomanizedCompositionPreview("s (final)", "ς"),
                RomanizedCompositionPreview("sh", "σχ")
            )
            "t" -> listOf(
                RomanizedCompositionPreview("t", "τ"),
                RomanizedCompositionPreview("th", "θ")
            )
            "p" -> listOf(
                RomanizedCompositionPreview("p", "π"),
                RomanizedCompositionPreview("ph", "φ"),
                RomanizedCompositionPreview("ps", "ψ")
            )
            "k", "c" -> listOf(
                RomanizedCompositionPreview("k", "κ"),
                RomanizedCompositionPreview("ks", "ξ"),
                RomanizedCompositionPreview("kh", "χ")
            )
            else -> {
                val transliterated = transliterateToken(prefix)
                if (transliterated.isNotEmpty() && transliterated != prefix) {
                    listOf(RomanizedCompositionPreview(prefix, transliterated))
                } else {
                    emptyList()
                }
            }
        }
    }
}

