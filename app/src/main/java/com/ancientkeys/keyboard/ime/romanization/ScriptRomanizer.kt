package com.ancientkeys.keyboard.ime.romanization

/**
 * Standard composition preview model for historical Romanized input.
 */
data class RomanizedCompositionPreview(
    val latinInput: String,
    val scriptResult: String,
    val displayLabel: String = scriptResult
) {
    val brahmiResult: String get() = scriptResult
}

typealias BrahmiCompositionPreview = RomanizedCompositionPreview

/**
 * Common contract for historical script Romanized transliteration engines.
 *
 * Distinct Principles:
 * - Deterministic, rule-based transliteration based on published academic standards.
 * - Explicit distinction between transliteration (character representation) and translation (semantic rendering).
 * - No auto-translation and no invented historical mappings.
 */
interface ScriptRomanizer {
    val scriptId: String
    val schemeName: String
    val academicConvention: String

    /**
     * Deterministically maps a Latin transliteration token into authentic Unicode characters.
     */
    fun transliterateToken(token: String): String

    /**
     * Generates composition candidates/previews for a given input prefix or consonant.
     */
    fun getCompositionPreviews(prefix: String): List<RomanizedCompositionPreview>
}

/**
 * Provider registry for scripts supporting academic Romanized Input.
 *
 * Scripts strictly supported:
 * - Brahmi (IAST / epigraphic convention)
 * - Kharosthi (Gandhari Prakrit academic convention)
 * - Ancient Greek (Classical & epigraphic transliteration)
 *
 * Scripts unsupported (intentional; no standardized phonetic Romanizer exists):
 * - Egyptian Hieroglyphs (complex logographic/multiliteral nature)
 * - Phoenician (pure consonant abjad with varying unwritten vowels)
 * - Elder Futhark Runic (acrophonic runic alphabet without vowel mutation conventions)
 */
object ScriptRomanizerProvider {

    fun getRomanizer(scriptId: String): ScriptRomanizer? {
        return when (scriptId.lowercase().trim()) {
            "brahmi" -> BrahmiRomanizer
            "kharosthi" -> KharosthiRomanizer
            "greek" -> GreekRomanizer
            else -> null
        }
    }

    fun isRomanizationSupported(scriptId: String): Boolean {
        return getRomanizer(scriptId) != null
    }

    fun getAllSupportedScriptIds(): List<String> = listOf("brahmi", "kharosthi", "greek")
}
