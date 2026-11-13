package com.ancientkeys.keyboard.ime.phonetic

/**
 * Provider registry for scripts supporting conversational Easy Phonetic typing.
 *
 * Scripts strictly supported:
 * - Brahmi (Conversational Indic / Manglish-style phonetic input)
 * - Kharosthi (Gāndhārī Prakrit phonetic input)
 * - Ancient Greek (Classical transliteration with digraphs and contextual final sigma)
 *
 * Scripts strictly unsupported (no standardized conversational phonetic mapping):
 * - Egyptian Hieroglyphs (complex logographic and multicategorical sign system)
 * - Phoenician (pure consonant abjad with unwritten vowels)
 * - Elder Futhark Runic (acrophonic rune row without vowel-sign mutations)
 */
object EasyPhoneticProvider {

    fun getEngine(scriptId: String): EasyPhoneticEngine? {
        return when (scriptId.lowercase().trim()) {
            "brahmi" -> BrahmiEasyPhoneticEngine
            "kharosthi" -> KharosthiEasyPhoneticEngine
            "greek" -> GreekEasyPhoneticEngine
            else -> null
        }
    }

    fun isPhoneticSupported(scriptId: String): Boolean {
        return getEngine(scriptId) != null
    }

    fun getAllSupportedScriptIds(): List<String> = listOf("brahmi", "kharosthi", "greek")

    fun clearAllCaches() {
        BrahmiEasyPhoneticEngine.clearCache()
        KharosthiEasyPhoneticEngine.clearCache()
        GreekEasyPhoneticEngine.clearCache()
    }
}

