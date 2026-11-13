package com.ancientkeys.keyboard.ime.phonetic

/**
 * Conversational "Easy Phonetic" typing engine for the Kharosthi script.
 *
 * Implements Gāndhārī Prakrit phonetic conventions established by Salomon (1999) and Glass (2000):
 * - Independent vowels built upon the base letter 'A' (𐨀, U+10A00).
 * - Single and double vowels ("a", "aa", "i", "ee", "u", "uu", "e", "o").
 * - Multi-character consonants ("kh", "gh", "ch", "th", "dh", "ph", "bh", "sh", "za").
 * - Inherent vowel 'a' in every consonant.
 * - Dependent vowel diacritics attaching directly to consonants.
 * - Live candidate generation for retroflex vs dental and sibilants.
 */
object KharosthiEasyPhoneticEngine : EasyPhoneticEngine {

    override val scriptId: String = "kharosthi"
    override val engineName: String = "Kharosthi Easy Phonetic"
    override val description: String = "Conversational English-to-Kharosthi phonetic input conforming to Gāndhārī Prakrit conventions."

    private val conversionCache = java.util.concurrent.ConcurrentHashMap<String, String>()
    private val candidateCache = java.util.concurrent.ConcurrentHashMap<String, List<PhoneticCandidate>>()

    // Independent Vowels (built upon base 𐨀)
    private val independentVowels = mapOf(
        "a" to "𐨀",
        "aa" to "𐨀",
        "ā" to "𐨀",
        "i" to "𐨀\uD802\uDE01",
        "ii" to "𐨀\uD802\uDE01",
        "ee" to "𐨀\uD802\uDE01",
        "ī" to "𐨀\uD802\uDE01",
        "u" to "𐨀\uD802\uDE02",
        "uu" to "𐨀\uD802\uDE02",
        "oo" to "𐨀\uD802\uDE02",
        "ū" to "𐨀\uD802\uDE02",
        "ri" to "𐨀\uD802\uDE03",
        "r̥" to "𐨀\uD802\uDE03",
        "e" to "𐨀\uD802\uDE05",
        "ai" to "𐨀\uD802\uDE05",
        "o" to "𐨀\uD802\uDE06",
        "au" to "𐨀\uD802\uDE06"
    )

    // Dependent Vowel Signs
    private val dependentVowels = mapOf(
        "aa" to "", // Kharosthi rarely distinguishes vowel length in script
        "ā" to "",
        "a" to "",
        "i" to "\uD802\uDE01",
        "ii" to "\uD802\uDE01",
        "ee" to "\uD802\uDE01",
        "ī" to "\uD802\uDE01",
        "u" to "\uD802\uDE02",
        "uu" to "\uD802\uDE02",
        "oo" to "\uD802\uDE02",
        "ū" to "\uD802\uDE02",
        "ri" to "\uD802\uDE03",
        "r̥" to "\uD802\uDE03",
        "e" to "\uD802\uDE05",
        "ai" to "\uD802\uDE05",
        "o" to "\uD802\uDE06",
        "au" to "\uD802\uDE06"
    )

    // Consonants
    private val consonants = mapOf(
        // Velars
        "kh" to "𐨑",
        "k" to "𐨐",
        "gh" to "𐨓",
        "g" to "𐨒",

        // Palatals
        "ch" to "𐨖",
        "c" to "𐨕",
        "j" to "𐨗",
        "ny" to "𐨙",
        "ñ" to "𐨙",

        // Retroflexes
        "ṭh" to "𐨛",
        "th." to "𐨛",
        "ṭ" to "𐨚",
        "t." to "𐨚",
        "ḍh" to "𐨝",
        "dh." to "𐨝",
        "ḍ" to "𐨜",
        "d." to "𐨜",
        "ṇ" to "𐨞",
        "n." to "𐨞",

        // Dentals
        "th" to "𐨠",
        "t" to "𐨟",
        "dh" to "𐨢",
        "d" to "𐨡",
        "n" to "𐨣",

        // Labials
        "ph" to "𐨥",
        "f" to "𐨥",
        "p" to "𐨤",
        "bh" to "𐨧",
        "b" to "𐨦",
        "m" to "𐨨",

        // Semivowels & Liquids
        "y" to "𐨩",
        "r" to "𐨪",
        "l" to "𐨫",
        "v" to "𐨬",
        "w" to "𐨬",

        // Sibilants, Spirants & Iranian Loans
        "shh" to "𐨮",
        "ṣ" to "𐨮",
        "sh" to "𐨭",
        "ś" to "𐨭",
        "s" to "𐨯",
        "z" to "𐨰",
        "h" to "𐨱"
    )

    // Modifiers & Punctuation
    private val modifiers = mapOf(
        "m." to "\uD802\uDE0E",
        "ṃ" to "\uD802\uDE0E",
        "h." to "\uD802\uDE0F",
        "ḥ" to "\uD802\uDE0F",
        "_" to "\uD802\uDE3F", // Virama
        "|" to "𐩐",
        "||" to "𐩑"
    )

    // Numerals (U+10A40..U+10A47)
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

    override fun convert(rawInput: String): String {
        if (rawInput.isEmpty()) return ""
        conversionCache[rawInput]?.let { return it }

        val sb = StringBuilder()
        var i = 0
        val input = rawInput.lowercase().trim()

        while (i < input.length) {
            // Check double punctuation or modifiers
            if (i + 1 < input.length) {
                val pair = input.substring(i, i + 2)
                if (modifiers.containsKey(pair)) {
                    sb.append(modifiers[pair])
                    i += 2
                    continue
                }
            }
            if (modifiers.containsKey(input[i].toString())) {
                sb.append(modifiers[input[i].toString()])
                i += 1
                continue
            }

            // Check numerals (multi-digit like "1000", "100", "20", "10", then single digits)
            var matchedNum: String? = null
            var numLen = 0
            for (len in 4 downTo 1) {
                if (i + len <= input.length) {
                    val cand = input.substring(i, i + len)
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

            // Consonant match (longest prefix first)
            var matchedConsonantGlyph: String? = null
            for (cLen in 3 downTo 1) {
                if (i + cLen <= input.length) {
                    val cand = input.substring(i, i + cLen)
                    if (consonants.containsKey(cand)) {
                        matchedConsonantGlyph = consonants[cand]
                        i += cLen
                        break
                    }
                }
            }

            if (matchedConsonantGlyph != null) {
                // Check following vowel sign
                var matchedVowelSign: String? = null
                for (vLen in 3 downTo 1) {
                    if (i + vLen <= input.length) {
                        val candV = input.substring(i, i + vLen)
                        if (dependentVowels.containsKey(candV)) {
                            matchedVowelSign = dependentVowels[candV]
                            i += vLen
                            break
                        }
                    }
                }

                sb.append(matchedConsonantGlyph)
                if (matchedVowelSign != null) {
                    sb.append(matchedVowelSign)
                }

                // Check modifier
                if (i < input.length) {
                    if (input[i] == 'm' && (i + 1 == input.length || input[i + 1] == ' ' || input[i + 1] == '_')) {
                        sb.append("\uD802\uDE0E")
                        i += 1
                    } else if (input[i] == 'h' && (i + 1 == input.length || input[i + 1] == ' ' || input[i + 1] == '_')) {
                        sb.append("\uD802\uDE0F")
                        i += 1
                    }
                }
                continue
            }

            // Independent vowel match
            var matchedIndepVowel: String? = null
            for (vLen in 3 downTo 1) {
                if (i + vLen <= input.length) {
                    val candV = input.substring(i, i + vLen)
                    if (independentVowels.containsKey(candV)) {
                        matchedIndepVowel = independentVowels[candV]
                        i += vLen
                        break
                    }
                }
            }

            if (matchedIndepVowel != null) {
                sb.append(matchedIndepVowel)
                continue
            }

            // Fallback
            sb.append(input[i])
            i += 1
        }

        val result = sb.toString()
        conversionCache[rawInput] = result
        return result
    }

    override fun getCandidates(rawInput: String): List<PhoneticCandidate> {
        val trimmed = rawInput.lowercase().trim()
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

        val baseConsonant = consonants[trimmed]
        if (baseConsonant != null) {
            candidates.add(PhoneticCandidate(baseConsonant, "$baseConsonant (ka)", "inherent a"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD802\uDE01", "$baseConsonant\uD802\uDE01 (${trimmed}i)", "vowel i"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD802\uDE02", "$baseConsonant\uD802\uDE02 (${trimmed}u)", "vowel u"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD802\uDE03", "$baseConsonant\uD802\uDE03 (${trimmed}ri)", "vocalic r"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD802\uDE05", "$baseConsonant\uD802\uDE05 (${trimmed}e)", "vowel e"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD802\uDE06", "$baseConsonant\uD802\uDE06 (${trimmed}o)", "vowel o"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD802\uDE3F", "$baseConsonant\uD802\uDE3F (${trimmed}_)", "virāma"))
        }

        if (trimmed == "t" || trimmed == "ta") {
            candidates.add(PhoneticCandidate("𐨟", "𐨟 (ta)", "dental"))
            candidates.add(PhoneticCandidate("𐨚", "𐨚 (ṭa)", "retroflex"))
            candidates.add(PhoneticCandidate("𐨠", "𐨠 (tha)", "dental aspirate"))
            candidates.add(PhoneticCandidate("𐨛", "𐨛 (ṭha)", "retroflex aspirate"))
        }
        if (trimmed == "d" || trimmed == "da") {
            candidates.add(PhoneticCandidate("𐨡", "𐨡 (da)", "dental"))
            candidates.add(PhoneticCandidate("𐨜", "𐨜 (ḍa)", "retroflex"))
            candidates.add(PhoneticCandidate("𐨢", "𐨢 (dha)", "dental aspirate"))
            candidates.add(PhoneticCandidate("𐨝", "𐨝 (ḍha)", "retroflex aspirate"))
        }
        if (trimmed == "s" || trimmed == "sa") {
            candidates.add(PhoneticCandidate("𐨯", "𐨯 (sa)", "dental s"))
            candidates.add(PhoneticCandidate("𐨭", "𐨭 (sha)", "palatal ś"))
            candidates.add(PhoneticCandidate("𐨮", "𐨮 (ṣa)", "retroflex ṣ"))
            candidates.add(PhoneticCandidate("𐨰", "𐨰 (za)", "Iranian loan z"))
        }

        val distinct = candidates.distinctBy { it.scriptResult }
        candidateCache[rawInput] = distinct
        return distinct
    }
}

