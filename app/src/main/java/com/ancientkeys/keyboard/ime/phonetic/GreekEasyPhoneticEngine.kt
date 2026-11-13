package com.ancientkeys.keyboard.ime.phonetic

/**
 * Conversational "Easy Phonetic" typing engine for Ancient Greek.
 *
 * Implements intuitive English-to-Greek mapping for Classical Greek:
 * - Digraphs ("th" -> θ, "ph" -> φ, "ch" -> χ, "ps" -> ψ, "ks" -> ξ).
 * - Long vowel shorthands ("ee" / "h" -> η, "oo" / "w" -> ω).
 * - Rough breathing ("ha" -> ἁ, "he" -> ἑ, "hi" -> ἱ, "ho" -> ὁ, "hu" -> ὑ).
 * - Contextual final sigma: 's' converts to ς at end of word/token or before punctuation, and σ medially.
 * - Retains uppercase casing when capital letters are typed.
 * - Greek punctuation (";" / "?" -> ;, ":" -> ·).
 */
object GreekEasyPhoneticEngine : EasyPhoneticEngine {

    override val scriptId: String = "greek"
    override val engineName: String = "Ancient Greek Easy Phonetic"
    override val description: String = "Conversational English-to-Greek phonetic typing with digraphs, vowel pairs, and contextual final sigma."

    private const val MAX_CACHE_ENTRIES = 256
    private val conversionCache = java.util.concurrent.ConcurrentHashMap<String, String>()
    private val candidateCache = java.util.concurrent.ConcurrentHashMap<String, List<PhoneticCandidate>>()

    private val multiCharMap = mapOf(
        "th" to "θ", "Th" to "Θ", "TH" to "Θ",
        "ph" to "φ", "Ph" to "Φ", "PH" to "Φ",
        "ch" to "χ", "Ch" to "Χ", "CH" to "Χ",
        "ps" to "ψ", "Ps" to "Ψ", "PS" to "Ψ",
        "ks" to "ξ", "Ks" to "Ξ", "KS" to "Ξ",
        "ee" to "η", "Ee" to "Η", "EE" to "Η",
        "oo" to "ω", "Oo" to "Ω", "OO" to "Ω",

        // Rough breathing digraphs
        "ha" to "ἁ", "Ha" to "Ἁ", "HA" to "Ἁ",
        "he" to "ἑ", "He" to "Ἑ", "HE" to "Ἑ",
        "hi" to "ἱ", "Hi" to "Ἱ", "HI" to "Ἱ",
        "ho" to "ὁ", "Ho" to "Ὁ", "HO" to "Ὁ",
        "hu" to "ὑ", "Hu" to "Ὑ", "HU" to "Ὑ",
        "hy" to "ὑ", "Hy" to "Ὑ", "HY" to "Ὑ",
        "hw" to "ὡ", "Hw" to "Ὡ", "HW" to "Ὡ"
    )

    private val singleCharMap = mapOf(
        'a' to "α", 'A' to "Α",
        'b' to "β", 'B' to "Β",
        'g' to "γ", 'G' to "Γ",
        'd' to "δ", 'D' to "Δ",
        'e' to "ε", 'E' to "Ε",
        'z' to "ζ", 'Z' to "Ζ",
        'h' to "η", 'H' to "Η",
        'i' to "ι", 'I' to "Ι",
        'k' to "κ", 'K' to "Κ",
        'c' to "κ", 'C' to "Κ",
        'l' to "λ", 'L' to "Λ",
        'm' to "μ", 'M' to "Μ",
        'n' to "ν", 'N' to "Ν",
        'x' to "ξ", 'X' to "Ξ",
        'o' to "ο", 'O' to "Ο",
        'p' to "π", 'P' to "Π",
        'r' to "ρ", 'R' to "Ρ",
        't' to "τ", 'T' to "Τ",
        'u' to "υ", 'U' to "Υ",
        'y' to "υ", 'Y' to "Υ",
        'w' to "ω", 'W' to "Ω",
        ';' to ";", '?' to ";",
        ':' to "·"
    )

    override fun convert(rawInput: String): String {
        if (rawInput.isEmpty()) return ""
        conversionCache[rawInput]?.let { return it }

        val sb = StringBuilder()
        var i = 0
        val input = rawInput.trim()

        while (i < input.length) {
            // Check 2-char multi-char map
            if (i + 1 < input.length) {
                val pair = input.substring(i, i + 2)
                if (multiCharMap.containsKey(pair)) {
                    sb.append(multiCharMap[pair])
                    i += 2
                    continue
                }
            }

            val c = input[i]

            // Sigma handling: check if 's'/'S' is at end of token or followed by space/punct
            if (c == 's' || c == 'S') {
                val isFinal = (i + 1 == input.length) || input[i + 1].isWhitespace() ||
                        input[i + 1] in setOf('.', ',', ';', ':', '!', '?', '-', '—')
                if (c == 'S') {
                    sb.append("Σ")
                } else {
                    sb.append(if (isFinal) "ς" else "σ")
                }
                i += 1
                continue
            }

            if (singleCharMap.containsKey(c)) {
                sb.append(singleCharMap[c])
                i += 1
                continue
            }

            // Fallback
            sb.append(c)
            i += 1
        }

        val result = sb.toString()
        if (conversionCache.size >= MAX_CACHE_ENTRIES) {
            conversionCache.clear()
        }
        conversionCache[rawInput] = result
        return result
    }

    override fun getCandidates(rawInput: String): List<PhoneticCandidate> {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) return emptyList()
        candidateCache[rawInput]?.let { return it }

        val primary = convert(trimmed)
        val candidates = mutableListOf<PhoneticCandidate>()

        candidates.add(
            PhoneticCandidate(
                scriptResult = primary,
                displayLabel = primary,
                phoneticHint = trimmed,
                isPrimary = true
            )
        )

        val lower = trimmed.lowercase()

        // Alternatives for ambiguous vowels and consonants
        if (lower == "e") {
            candidates.add(PhoneticCandidate("ε", "ε (epsilon)", "short e"))
            candidates.add(PhoneticCandidate("η", "η (eta)", "long e"))
        }
        if (lower == "o") {
            candidates.add(PhoneticCandidate("ο", "ο (omicron)", "short o"))
            candidates.add(PhoneticCandidate("ω", "ω (omega)", "long o"))
        }
        if (lower == "s") {
            candidates.add(PhoneticCandidate("σ", "σ (medial)", "medial sigma"))
            candidates.add(PhoneticCandidate("ς", "ς (final)", "final sigma"))
            candidates.add(PhoneticCandidate("Σ", "Σ (upper)", "capital sigma"))
        }
        if (lower == "th") {
            candidates.add(PhoneticCandidate("θ", "θ (theta)", "aspirated dental"))
            candidates.add(PhoneticCandidate("Θ", "Θ (Theta)", "capital theta"))
        }
        if (lower == "ph") {
            candidates.add(PhoneticCandidate("φ", "φ (phi)", "aspirated labial"))
            candidates.add(PhoneticCandidate("Φ", "Φ (Phi)", "capital phi"))
        }
        if (lower == "ch") {
            candidates.add(PhoneticCandidate("χ", "χ (chi)", "aspirated velar"))
            candidates.add(PhoneticCandidate("Χ", "Χ (Chi)", "capital chi"))
        }
        if (lower == "ps") {
            candidates.add(PhoneticCandidate("ψ", "ψ (psi)", "consonant cluster ps"))
            candidates.add(PhoneticCandidate("Ψ", "Ψ (Psi)", "capital psi"))
        }
        if (lower == "x" || lower == "ks") {
            candidates.add(PhoneticCandidate("ξ", "ξ (xi)", "consonant cluster ks"))
            candidates.add(PhoneticCandidate("χ", "χ (chi)", "aspirated velar"))
        }

        val distinct = candidates.distinctBy { it.scriptResult }
        if (candidateCache.size >= MAX_CACHE_ENTRIES) {
            candidateCache.clear()
        }
        candidateCache[rawInput] = distinct
        return distinct
    }

    override fun clearCache() {
        conversionCache.clear()
        candidateCache.clear()
    }
}

