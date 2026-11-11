package com.ancientkeys.keyboard.ime.data

/**
 * Operating mode of the AncientKeys Input Method.
 */
enum class KeyboardMode {
    /**
     * Active historical script layout (Egyptian, Brahmi, Greek, Runic, Phoenician, Kharosthi).
     */
    HISTORICAL,

    /**
     * Full English QWERTY keyboard with Shift/Caps, punctuation, and word spacing.
     */
    ENGLISH_QWERTY,

    /**
     * Modern numeric digits 0–9 and standard punctuation/symbols.
     */
    NUMBERS_SYMBOLS,

    /**
     * Romanized input mode: types Latin transliteration (e.g. IAST for Brahmi)
     * which automatically composes into authentic historical script glyphs.
     */
    ROMANIZED,

    /**
     * Conversational Easy Phonetic mode ("Manglish" / natural English letters).
     * Types ordinary Roman letters (e.g. "ka", "kaa", "ki") with longest-match composition
     * and live multi-candidate suggestions.
     */
    EASY_PHONETIC
}

/**
 * State machine for the English keyboard Shift / Caps Lock key.
 */
enum class ShiftState {
    /** Lowercase input. */
    OFF,

    /** Next character typed will be uppercase; reverts to [OFF] immediately after. */
    ON_ONCE,

    /** Uppercase locked until manually toggled off. */
    CAPS_LOCK
}

