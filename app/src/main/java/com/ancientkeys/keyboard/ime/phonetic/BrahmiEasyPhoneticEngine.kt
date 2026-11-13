package com.ancientkeys.keyboard.ime.phonetic

/**
 * Conversational "Easy Phonetic" typing engine for the Brahmi script.
 *
 * Implements a natural, English-keyboard-friendly phonetic mapping inspired by
 * popular Indic transliteration ("Manglish" / "Hinglish"):
 * - Users type ordinary Roman characters without needing scholarly diacritics.
 * - Single and double vowels ("a", "aa", "i", "ii", "u", "uu", "e", "ai", "o", "au").
 * - Multi-character consonants ("kh", "gh", "ch", "chh", "jh", "th", "dh", "ph", "bh", "sh", "shh", "ng", "ny").
 * - Inherent vowel handling (consonant alone carries inherent 'a').
 * - Vowel signs attach automatically when typed after a consonant.
 * - Virama ("_" or trailing consonant) suppresses the inherent vowel.
 * - Generates live candidate suggestions for ambiguous phonemes (e.g. dental vs retroflex).
 */
object BrahmiEasyPhoneticEngine : EasyPhoneticEngine {

    override val scriptId: String = "brahmi"
    override val engineName: String = "Brahmi Easy Phonetic"
    override val description: String = "Conversational English-to-Brahmi phonetic input with inherent 'a' and live vowel-sign composition."

    private const val MAX_CACHE_ENTRIES = 256
    private val conversionCache = java.util.concurrent.ConcurrentHashMap<String, String>()
    private val candidateCache = java.util.concurrent.ConcurrentHashMap<String, List<PhoneticCandidate>>()

    // Common historical words & royal names where conversational English spelling uses 's' for Sanskrit 'ś'
    private val wordOverrides = mapOf(
        "asoka" to "𑀅𑀰𑁄𑀓",
        "ashoka" to "𑀅𑀰𑁄𑀓",
        "aśoka" to "𑀅𑀰𑁄𑀓"
    )

    // Independent Vowels
    private val independentVowels = mapOf(
        "a" to "𑀅",
        "aa" to "𑀆",
        "ā" to "𑀆",
        "i" to "𑀇",
        "ii" to "𑀈",
        "ī" to "𑀈",
        "ee" to "𑀈",
        "ea" to "𑀈",
        "u" to "𑀉",
        "uu" to "𑀊",
        "ū" to "𑀊",
        "oo" to "𑀊",
        "ri" to "𑀋",
        "ṛ" to "𑀋",
        "r̥" to "𑀋",
        "ree" to "𑀌",
        "ṝ" to "𑀌",
        "r̥̄" to "𑀌",
        "e" to "𑀏",
        "ai" to "𑀐",
        "ay" to "𑀏",
        "o" to "𑀑",
        "au" to "𑀒",
        "ou" to "𑀒",
        "aw" to "𑀒"
    )

    // Dependent Vowel Signs (Matras)
    // NOTE: Standard generic Brahmi vowel signs (U+11038..U+11045).
    // U+11039 is BRAHMI VOWEL SIGN BHATTIPROLU AA and must NEVER be used for generic Brahmi!
    private val dependentVowels = mapOf(
        "aa" to "\uD804\uDC38",  // U+11038 BRAHMI VOWEL SIGN AA
        "ā" to "\uD804\uDC38",
        "a" to "",               // Inherent 'a' requires no sign
        "i" to "\uD804\uDC3A",   // U+1103A BRAHMI VOWEL SIGN I
        "ii" to "\uD804\uDC3B",  // U+1103B BRAHMI VOWEL SIGN II
        "ī" to "\uD804\uDC3B",
        "ee" to "\uD804\uDC3B",
        "ea" to "\uD804\uDC3B",
        "u" to "\uD804\uDC3C",   // U+1103C BRAHMI VOWEL SIGN U
        "uu" to "\uD804\uDC3D",  // U+1103D BRAHMI VOWEL SIGN UU
        "ū" to "\uD804\uDC3D",
        "oo" to "\uD804\uDC3D",
        "ri" to "\uD804\uDC3E",  // U+1103E BRAHMI VOWEL SIGN VOCALIC R
        "ṛ" to "\uD804\uDC3E",
        "r̥" to "\uD804\uDC3E",
        "ree" to "\uD804\uDC3F", // U+1103F BRAHMI VOWEL SIGN VOCALIC RR
        "ṝ" to "\uD804\uDC3F",
        "r̥̄" to "\uD804\uDC3F",
        "e" to "\uD804\uDC42",   // U+11042 BRAHMI VOWEL SIGN E
        "ai" to "\uD804\uDC43",  // U+11043 BRAHMI VOWEL SIGN AI
        "ay" to "\uD804\uDC42",
        "o" to "\uD804\uDC44",   // U+11044 BRAHMI VOWEL SIGN O
        "au" to "\uD804\uDC45",  // U+11045 BRAHMI VOWEL SIGN AU
        "ou" to "\uD804\uDC45",
        "aw" to "\uD804\uDC45"
    )

    // Consonants (inherent 'a' is assumed in Brahmi)
    private val consonants = mapOf(
        // Velars
        "kh" to "𑀔",
        "k" to "𑀓",
        "gh" to "𑀖",
        "g" to "𑀕",
        "ng" to "𑀗",
        "ṅ" to "𑀗",

        // Palatals
        "chh" to "𑀙",
        "ch" to "𑀘",
        "c" to "𑀘",
        "jh" to "𑀛",
        "j" to "𑀚",
        "ny" to "𑀜",
        "ñ" to "𑀜",

        // Retroflexes
        "ṭh" to "𑀞",
        "th." to "𑀞",
        "ṭ" to "𑀝",
        "t." to "𑀝",
        "ḍh" to "𑀠",
        "dh." to "𑀠",
        "ḍ" to "𑀟",
        "d." to "𑀟",
        "ṇ" to "𑀡",
        "n." to "𑀡",

        // Dentals
        "th" to "𑀣",
        "t" to "𑀢",
        "dh" to "𑀥",
        "d" to "𑀤",
        "n" to "𑀦",

        // Labials
        "ph" to "𑀨",
        "f" to "𑀨",
        "p" to "𑀧",
        "bh" to "𑀪",
        "b" to "𑀩",
        "m" to "𑀫",

        // Semivowels & Liquids
        "y" to "𑀬",
        "r" to "𑀭",
        "l" to "𑀮",
        "v" to "𑀯",
        "w" to "𑀯",

        // Sibilants & Aspirate
        "shh" to "𑀱",
        "ṣ" to "𑀱",
        "sh" to "𑀰",
        "ś" to "𑀰",
        "s" to "𑀲",
        "h" to "𑀳",
        "ḷ" to "𑀴",
        "l." to "𑀴"
    )

    // Modifiers & Punctuation
    private val modifiers = mapOf(
        "m." to "\uD804\uDC01",
        "ṃ" to "\uD804\uDC01",
        "h." to "\uD804\uDC02",
        "ḥ" to "\uD804\uDC02",
        "_" to "\uD804\uDC46", // Virama (U+11046)
        "|" to "𑁇",
        "||" to "𑁈"
    )

    override fun convert(rawInput: String): String {
        if (rawInput.isEmpty()) return ""
        conversionCache[rawInput]?.let { return it }

        val sb = StringBuilder()
        var i = 0
        val input = rawInput.lowercase().trim()

        while (i < input.length) {
            val isStartOfWord = (i == 0 || input[i - 1] == ' ' || input[i - 1] == '_')

            // 1. Check word overrides at word boundary (e.g. "asoka" -> "𑀅𑀰𑁄𑀓")
            if (isStartOfWord) {
                var matchedWord: String? = null
                var matchedWordGlyphs: String? = null
                for ((word, glyphs) in wordOverrides) {
                    if (input.startsWith(word, i)) {
                        val nextIdx = i + word.length
                        if (nextIdx == input.length || input[nextIdx] == ' ' || input[nextIdx] == '_' || input[nextIdx] == '|') {
                            if (matchedWord == null || word.length > matchedWord.length) {
                                matchedWord = word
                                matchedWordGlyphs = glyphs
                            }
                        }
                    }
                }
                if (matchedWord != null && matchedWordGlyphs != null) {
                    sb.append(matchedWordGlyphs)
                    i += matchedWord.length
                    continue
                }
            }

            // 2. Check double punctuation or modifiers
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

            // 3. Independent vowel at start of word/token (e.g. "ri" -> 𑀋, "aa" -> 𑀆, "a" -> 𑀅)
            if (isStartOfWord) {
                var matchedIndepVowel: String? = null
                var matchedIndepKey: String? = null
                for (vLen in 3 downTo 1) {
                    if (i + vLen <= input.length) {
                        val candV = input.substring(i, i + vLen)
                        if (independentVowels.containsKey(candV)) {
                            matchedIndepVowel = independentVowels[candV]
                            matchedIndepKey = candV
                            break
                        }
                    }
                }
                if (matchedIndepVowel != null && matchedIndepKey != null) {
                    sb.append(matchedIndepVowel)
                    i += matchedIndepKey.length
                    continue
                }
            }

            // 4. Try matching consonant (longest prefix first: 3 chars, then 2 chars, then 1 char)
            var matchedConsonantKey: String? = null
            var matchedConsonantGlyph: String? = null
            for (cLen in 3 downTo 1) {
                if (i + cLen <= input.length) {
                    val cand = input.substring(i, i + cLen)
                    if (consonants.containsKey(cand)) {
                        matchedConsonantKey = cand
                        matchedConsonantGlyph = consonants[cand]
                        i += cLen
                        break
                    }
                }
            }

            if (matchedConsonantGlyph != null) {
                // We have a consonant. Now check if followed by a vowel sign
                var matchedVowelKey: String? = null
                var matchedVowelSign: String? = null
                for (vLen in 3 downTo 1) {
                    if (i + vLen <= input.length) {
                        val candV = input.substring(i, i + vLen)
                        if (dependentVowels.containsKey(candV)) {
                            matchedVowelKey = candV
                            matchedVowelSign = dependentVowels[candV]
                            i += vLen
                            break
                        }
                    }
                }

                sb.append(matchedConsonantGlyph)
                if (matchedVowelSign != null) {
                    sb.append(matchedVowelSign)
                } else {
                    // Consonant has NO dependent vowel following it
                    if (i < input.length && (input[i] == '_' || input[i] == '-')) {
                        sb.append("\uD804\uDC46") // Explicit virama
                        i += 1
                    } else {
                        // Check if followed by another consonant (consonant cluster e.g. "st", "kt", "pr", "nd")
                        val nextIsConsonant = consonants.keys.any { consKey ->
                            input.startsWith(consKey, i)
                        }
                        if (nextIsConsonant) {
                            sb.append("\uD804\uDC46") // Inherent vowel suppressed in consonant cluster
                        } else if (i >= input.length || input[i] == ' ' || input[i] == '|' || input[i] in setOf('.', ',', '!', '?', ';', ':')) {
                            // Word-final consonant without vowel (e.g. final 'n' in "austin")
                            // When user typed a multi-letter word ending in a consonant, attach virama
                            if (input.length > (matchedConsonantKey?.length ?: 1)) {
                                sb.append("\uD804\uDC46")
                            }
                        }
                    }
                }

                // Check modifier immediately following consonant+vowel (e.g. "kam", "kah")
                if (i < input.length) {
                    if (i + 1 < input.length && modifiers.containsKey(input.substring(i, i + 2))) {
                        sb.append(modifiers[input.substring(i, i + 2)])
                        i += 2
                    } else if (input[i] == 'm' && (i + 1 == input.length || input[i + 1] == ' ' || input[i + 1] == '_' || input[i + 1] == '|')) {
                        // Trailing 'm' interpreted as anusvara
                        sb.append("\uD804\uDC01")
                        i += 1
                    } else if (input[i] == 'h' && (i + 1 == input.length || input[i + 1] == ' ' || input[i + 1] == '_' || input[i + 1] == '|')) {
                        // Trailing 'h' interpreted as visarga
                        sb.append("\uD804\uDC02")
                        i += 1
                    }
                }
                continue
            }

            // 5. Independent vowel within word if any (e.g. diphthong or vowel hiatus)
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

            // 6. Fallback character (punctuation, space, unrecognized)
            sb.append(input[i])
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
        val trimmed = rawInput.lowercase().trim()
        if (trimmed.isEmpty()) return emptyList()
        candidateCache[rawInput]?.let { return it }

        val primary = convert(trimmed)
        val candidates = mutableListOf<PhoneticCandidate>()

        // 1. Primary candidate
        candidates.add(
            PhoneticCandidate(
                scriptResult = primary,
                displayLabel = primary,
                phoneticHint = "$trimmed",
                isPrimary = true
            )
        )

        // 2. If primary ends with virama, offer candidate with inherent 'a'
        if (primary.endsWith("\uD804\uDC46")) {
            val withoutVirama = primary.dropLast(2)
            candidates.add(
                PhoneticCandidate(
                    scriptResult = withoutVirama,
                    displayLabel = "$withoutVirama (${trimmed}a)",
                    phoneticHint = "inherent final a"
                )
            )
        }

        // 3. If input has 't' or 'th', offer retroflex variant (e.g. Austin with ṭa 𑀝)
        if (trimmed.contains("t") && !trimmed.contains("t.") && !trimmed.contains("ṭ")) {
            val retroflexInput = trimmed.replace("th", "ṭh").replace("t", "ṭ")
            val retroflexResult = convert(retroflexInput)
            if (retroflexResult != primary) {
                candidates.add(
                    PhoneticCandidate(
                        scriptResult = retroflexResult,
                        displayLabel = "$retroflexResult (retroflex ṭ)",
                        phoneticHint = "retroflex ṭ variant"
                    )
                )
            }
        }

        // 4. If input is a single or double consonant, offer vowel variations
        val baseConsonant = consonants[trimmed]
        if (baseConsonant != null) {
            candidates.add(PhoneticCandidate(baseConsonant, "$baseConsonant (ka)", "inherent a"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD804\uDC38", "$baseConsonant\uD804\uDC38 (${trimmed}aa)", "long ā"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD804\uDC3A", "$baseConsonant\uD804\uDC3A (${trimmed}i)", "short i"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD804\uDC3B", "$baseConsonant\uD804\uDC3B (${trimmed}ee)", "long ī"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD804\uDC3C", "$baseConsonant\uD804\uDC3C (${trimmed}u)", "short u"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD804\uDC3D", "$baseConsonant\uD804\uDC3D (${trimmed}uu)", "long ū"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD804\uDC42", "$baseConsonant\uD804\uDC42 (${trimmed}e)", "e"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD804\uDC43", "$baseConsonant\uD804\uDC43 (${trimmed}ai)", "ai"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD804\uDC44", "$baseConsonant\uD804\uDC44 (${trimmed}o)", "o"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD804\uDC45", "$baseConsonant\uD804\uDC45 (${trimmed}au)", "au"))
            candidates.add(PhoneticCandidate("$baseConsonant\uD804\uDC46", "$baseConsonant\uD804\uDC46 (${trimmed}-)", "virāma / halant"))
        }

        // 5. Ambiguity resolutions:
        // 't' ambiguity -> offer dental ta (𑀢) and retroflex ṭa (𑀝)
        if (trimmed == "t" || trimmed == "ta") {
            candidates.add(PhoneticCandidate("𑀢", "𑀢 (ta)", "dental"))
            candidates.add(PhoneticCandidate("𑀝", "𑀝 (ṭa)", "retroflex"))
            candidates.add(PhoneticCandidate("𑀣", "𑀣 (tha)", "dental aspirate"))
            candidates.add(PhoneticCandidate("𑀞", "𑀞 (ṭha)", "retroflex aspirate"))
        }
        // 'd' ambiguity -> dental da (𑀤) and retroflex ḍa (𑀟)
        if (trimmed == "d" || trimmed == "da") {
            candidates.add(PhoneticCandidate("𑀤", "𑀤 (da)", "dental"))
            candidates.add(PhoneticCandidate("𑀟", "𑀟 (ḍa)", "retroflex"))
            candidates.add(PhoneticCandidate("𑀥", "𑀥 (dha)", "dental aspirate"))
            candidates.add(PhoneticCandidate("𑀠", "𑀠 (ḍha)", "retroflex aspirate"))
        }
        // 'n' ambiguity -> dental na (𑀦) and retroflex ṇa (𑀡)
        if (trimmed == "n" || trimmed == "na") {
            candidates.add(PhoneticCandidate("𑀦", "𑀦 (na)", "dental"))
            candidates.add(PhoneticCandidate("𑀡", "𑀡 (ṇa)", "retroflex"))
            candidates.add(PhoneticCandidate("𑀜", "𑀜 (ña)", "palatal"))
            candidates.add(PhoneticCandidate("𑀗", "𑀗 (ṅa)", "velar"))
        }
        // 's' ambiguity -> dental sa (𑀲), palatal śa (𑀰), retroflex ṣa (𑀱)
        if (trimmed == "s" || trimmed == "sa") {
            candidates.add(PhoneticCandidate("𑀲", "𑀲 (sa)", "dental"))
            candidates.add(PhoneticCandidate("𑀰", "𑀰 (sha)", "palatal ś"))
            candidates.add(PhoneticCandidate("𑀱", "𑀱 (ṣa)", "retroflex ṣ"))
        }
        // 'sh' ambiguity -> palatal śa (𑀰) vs retroflex ṣa (𑀱)
        if (trimmed == "sh" || trimmed == "sha") {
            candidates.add(PhoneticCandidate("𑀰", "𑀰 (śa)", "palatal"))
            candidates.add(PhoneticCandidate("𑀱", "𑀱 (ṣa)", "retroflex"))
        }
        // 'k' ambiguity -> offer aspirated kha (𑀔)
        if (trimmed == "k" || trimmed == "ka") {
            candidates.add(PhoneticCandidate("𑀔", "𑀔 (kha)", "aspirated velar"))
        }
        // 'p' ambiguity -> offer aspirated pha (𑀨)
        if (trimmed == "p" || trimmed == "pa") {
            candidates.add(PhoneticCandidate("𑀨", "𑀨 (pha)", "aspirated labial"))
        }
        // 'a' vowel options
        if (trimmed == "a") {
            candidates.add(PhoneticCandidate("𑀅", "𑀅 (a)", "independent short a"))
            candidates.add(PhoneticCandidate("𑀆", "𑀆 (aa)", "independent long ā"))
            candidates.add(PhoneticCandidate("𑀇", "𑀇 (i)", "independent i"))
            candidates.add(PhoneticCandidate("𑀉", "𑀉 (u)", "independent u"))
        }

        // 'ri' options -> vocalic r vs consonant + vowel
        if (trimmed == "ri" || trimmed == "ṛ") {
            candidates.add(PhoneticCandidate("𑀋", "𑀋 (ṛ)", "independent vocalic r"))
            candidates.add(PhoneticCandidate("𑀭\uD804\uDC3A", "𑀭\uD804\uDC3A (ra + i)", "consonant + vowel"))
        }

        // 'asoka' royal name candidates
        if (trimmed == "asoka" || trimmed == "ashoka") {
            candidates.add(PhoneticCandidate("𑀅𑀰𑁄𑀓", "𑀅𑀰𑁄𑀓 (Aśoka)", "Ashoka (palatal ś)"))
            candidates.add(PhoneticCandidate("𑀅𑀲𑁄𑀓", "𑀅𑀲𑁄𑀓 (Asoka)", "Asoka (dental s)"))
        }

        // Return distinct candidates preserving order
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

