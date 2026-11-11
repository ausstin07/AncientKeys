package com.ancientkeys.keyboard.model

/**
 * Represents a single key or character entry in a historical script.
 *
 * @property character The actual Unicode character(s) to insert when pressed.
 * @property label The display text shown on the key face (defaults to [character]).
 * @property phoneticHint Scholarly phonetic designation or Gardiner classification.
 * @property unicodeCodePoint Hexadecimal representation of Unicode code point(s) (e.g. "U+1313F").
 * @property category Category name for organizing keys into tabs/sections.
 * @property unicodeName Official Unicode character name (e.g. "EGYPTIAN HIEROGLYPH G001").
 * @property transliteration Scholarly transliteration or transcription (e.g. "ꜣ", "Fehu", "ʾālep").
 * @property pronunciationIpa Reconstructed phonetic pronunciation in IPA, or "Unavailable" if uncertain.
 * @property meaning Documented historical meaning or symbol depiction, or "Unavailable" if unknown.
 * @property historicalNotes Scholarly notes, epigraphic variants, or catalog references (e.g. Gardiner sign code).
 * @property isCombining Whether this character is a combining diacritic / vowel sign.
 */
data class ScriptKey(
    val character: String,
    val label: String = character,
    val phoneticHint: String = "",
    val unicodeCodePoint: String = "",
    val category: String = "General",
    val unicodeName: String = "",
    val transliteration: String = "",
    val pronunciationIpa: String = "",
    val meaning: String = "",
    val historicalNotes: String = "",
    val isCombining: Boolean = false
)
