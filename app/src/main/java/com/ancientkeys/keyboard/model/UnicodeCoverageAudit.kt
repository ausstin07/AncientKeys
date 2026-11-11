package com.ancientkeys.keyboard.model

/**
 * Audit and comparison report documenting AncientKeys inventory coverage
 * versus complete official Unicode Standard character allocations.
 *
 * Epigraphically and linguistically distinguishes supported characters from
 * complete block allocations (including rare regional variants, Ptolemaic extensions,
 * and format controls).
 */
data class ScriptCoverageReport(
    val scriptId: String,
    val scriptName: String,
    val unicodeBlock: String,
    val unicodeBlockRange: String,
    val totalUnicodeAllocated: Int,
    val ancientKeysSupportedCount: Int,
    val coverageCategoryDescription: String,
    val omittedOrExtendedNotes: String
)

object UnicodeCoverageAudit {

    /**
     * Produces comprehensive coverage reports for all six historical scripts.
     */
    fun getFullCoverageAudit(): List<ScriptCoverageReport> {
        val allScripts = ScriptRegistry.allScripts.associateBy { it.id }

        return listOf(
            ScriptCoverageReport(
                scriptId = "brahmi",
                scriptName = "Brahmi",
                unicodeBlock = "Brahmi",
                unicodeBlockRange = "U+11000..U+1107F",
                totalUnicodeAllocated = 109,
                ancientKeysSupportedCount = allScripts["brahmi"]?.keys?.size ?: 0,
                coverageCategoryDescription = "Consonants, Independent Vowels, Dependent Vowels (Matras), Modifiers (Anusvara/Visarga), Virama, Dandas, Numerals (1-1000), and Decimal Digits (0-9).",
                omittedOrExtendedNotes = "Covers all standard Ashokan and classical Brahmi characters. Omitted: rare Bhattiprolu regional vowel variants and archaic ligature combinations."
            ),
            ScriptCoverageReport(
                scriptId = "kharosthi",
                scriptName = "Kharosthi",
                unicodeBlock = "Kharoshthi",
                unicodeBlockRange = "U+10A00..U+10A5F",
                totalUnicodeAllocated = 68,
                ancientKeysSupportedCount = allScripts["kharosthi"]?.keys?.size ?: 0,
                coverageCategoryDescription = "Core Consonants (including Iranian loan ZA), Independent Vowel A base, Dependent Vowel Signs (-i, -u, -r̥, -e, -o), Modifiers (Anusvara/Visarga), Virama, Section Punctuation, Dandas, and Gandhāran Numerals (1, 2, 3, 4, 10, 20, 100, 1000).",
                omittedOrExtendedNotes = "Covers all canonical Gandhāran Buddhist scroll characters. Omitted: rare half-fraction marks and specialized scribal florishes."
            ),
            ScriptCoverageReport(
                scriptId = "greek",
                scriptName = "Ancient Greek",
                unicodeBlock = "Greek and Coptic",
                unicodeBlockRange = "U+0370..U+03FF",
                totalUnicodeAllocated = 135,
                ancientKeysSupportedCount = allScripts["greek"]?.keys?.size ?: 0,
                coverageCategoryDescription = "24 Classical Uppercase, 25 Classical Lowercase (including contextual final sigma ς), Archaic Epigraphic letters (Digamma, Koppa, Sampi, Heta, San), and Punctuation (Ano Teleia, Greek Question Mark).",
                omittedOrExtendedNotes = "Full classical and archaic alphabet coverage. Omitted: Coptic additions and late Byzantine musical notations."
            ),
            ScriptCoverageReport(
                scriptId = "phoenician",
                scriptName = "Phoenician",
                unicodeBlock = "Phoenician",
                unicodeBlockRange = "U+10900..U+1091F",
                totalUnicodeAllocated = 29,
                ancientKeysSupportedCount = allScripts["phoenician"]?.keys?.size ?: 0,
                coverageCategoryDescription = "All 22 Consonant Letters (ʾālep through tāw), Monumental Word Separator Dot, and all 6 Phoenician Numerals (1, 2, 3, 10, 20, 100).",
                omittedOrExtendedNotes = "Complete 100% coverage of all 29 characters allocated in the official Unicode Phoenician block."
            ),
            ScriptCoverageReport(
                scriptId = "runic",
                scriptName = "Elder Futhark Runic",
                unicodeBlock = "Runic",
                unicodeBlockRange = "U+16A0..U+16FF",
                totalUnicodeAllocated = 89,
                ancientKeysSupportedCount = allScripts["runic"]?.keys?.size ?: 0,
                coverageCategoryDescription = "Complete 24-character Elder Futhark canonical inventory (Freyr's, Hagal's, and Týr's ættir) plus historical epigraphic punctuation (single dot, double colon, cross separator).",
                omittedOrExtendedNotes = "100% coverage of the historical Elder Futhark period. Omitted: Younger Futhark (Long-Branch/Short-Twig), Anglo-Saxon Futhorc expansions, and medieval dotted runes."
            ),
            ScriptCoverageReport(
                scriptId = "egyptian",
                scriptName = "Egyptian Hieroglyphs",
                unicodeBlock = "Egyptian Hieroglyphs",
                unicodeBlockRange = "U+13000..U+1342F",
                totalUnicodeAllocated = 1071,
                ancientKeysSupportedCount = allScripts["egyptian"]?.keys?.size ?: 0,
                coverageCategoryDescription = "Complete 24 Gardiner Uniliteral Phonetic Signs, Dual Reeds, High-Frequency Determinatives, Royal Ideograms (Ankh, Djed, Was, Hotep, Ra, Nefer), and Grammatical Strokes.",
                omittedOrExtendedNotes = "Curated core pedagogical and epigraphic inventory (~39 keys) optimized for mobile touch entry. The complete Unicode 1.0 block comprises 1,071 signs across Gardiner categories A-Z; Unicode 12.0+ format controls (U+13430..U+1343F) for quadrat stacking are omitted as standard mobile OpenType font renderers do not support complex hieroglyphic quadratic layout."
            )
        )
    }
}

