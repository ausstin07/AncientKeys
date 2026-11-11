package com.ancientkeys.keyboard.data

import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptKey

/**
 * Kharoṣṭhī script (Unicode block: Kharoshthi, U+10A00..U+10A5F).
 *
 * Ancient right-to-left abugida writing system used primarily in Gandhāra
 * (modern northern Pakistan and eastern Afghanistan) and Central Asian Silk Road kingdoms
 * from c. 3rd century BCE to c. 4th century CE. Used extensively to write Gāndhārī Prakrit
 * and early Buddhist scriptures.
 *
 * Source References:
 * - Salomon, Richard (1999). Ancient Buddhist Scrolls from Gandhāra: The British Library Kharoṣṭhī Fragments. University of Washington Press.
 * - Glass, Andrew (2000). A Preliminary Study of Kharoṣṭhī Manuscript Paleography.
 * - The Unicode Standard, Chapter 14.2: Kharoshthi.
 */
val KharosthiScript = HistoricalScript(
    id = "kharosthi",
    name = "Kharosthi (right-to-left)",
    alternativeNames = listOf("Gandhara", "Gandhāra", "Gandhari", "Gāndhārī", "Kharoṣṭhī", "Indo-Bactrian", "Kabulian"),
    historicalPeriod = "c. 3rd century BCE – 4th century CE",
    region = "Gandhāra (Northwest Indian Subcontinent) & Silk Road",
    description = "Right-to-left abugida adapted from Aramaic script; used extensively for Gandhāran Buddhist texts.",
    historicalContext = "Adapted from the Aramaic script of the Achaemenid Empire in the 3rd century BCE, Kharosthi was engineered specifically to express the Indo-Aryan phonology of Gāndhārī Prakrit and Sanskrit. Written from right to left, it flourished under the Indo-Greek, Indo-Scythian, and Kushan empires, preserving some of the world's oldest surviving Buddhist manuscripts on birch-bark scrolls.",
    sourcesAndReferences = "Salomon, Ancient Buddhist Scrolls from Gandhāra (1999); Glass, Kharoṣṭhī Manuscript Paleography (2000); The Unicode Standard Version 16.0.",
    isRtl = true,
    sampleText = "𐨀 𐨐 𐨑 𐨒 𐨓 𐨕 𐨖 𐨗 𐩐",
    categories = listOf("Consonants", "Independent Vowels", "Dependent Vowels", "Combining Marks", "Punctuation", "Numerals"),
    keys = listOf(
        // Independent Vowel A
        ScriptKey("𐨀", label = "𐨀", phoneticHint = "a", unicodeCodePoint = "U+10A00", category = "Independent Vowels",
            unicodeName = "KHAROSHTHI LETTER A", transliteration = "a", pronunciationIpa = "[ɐ]", meaning = "Independent vowel a", historicalNotes = "Serves as the base for all other independent vowels when combined with diacritics."),

        // Consonants
        ScriptKey("𐨐", label = "𐨐", phoneticHint = "ka", unicodeCodePoint = "U+10A10", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER KA", transliteration = "ka", pronunciationIpa = "[kɐ]", meaning = "Velar unvoiced stop"),
        ScriptKey("𐨑", label = "𐨑", phoneticHint = "kha", unicodeCodePoint = "U+10A11", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER KHA", transliteration = "kha", pronunciationIpa = "[kʰɐ]", meaning = "Aspirated velar stop"),
        ScriptKey("𐨒", label = "𐨒", phoneticHint = "ga", unicodeCodePoint = "U+10A12", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER GA", transliteration = "ga", pronunciationIpa = "[ɡɐ]", meaning = "Voiced velar stop"),
        ScriptKey("𐨓", label = "𐨓", phoneticHint = "gha", unicodeCodePoint = "U+10A13", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER GHA", transliteration = "gha", pronunciationIpa = "[ɡʱɐ]", meaning = "Voiced aspirated velar stop"),

        ScriptKey("𐨕", label = "𐨕", phoneticHint = "ca", unicodeCodePoint = "U+10A15", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER CA", transliteration = "ca", pronunciationIpa = "[t͡ɕɐ]", meaning = "Palatal stop/affricate"),
        ScriptKey("𐨖", label = "𐨖", phoneticHint = "cha", unicodeCodePoint = "U+10A16", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER CHA", transliteration = "cha", pronunciationIpa = "[t͡ɕʰɐ]", meaning = "Aspirated palatal stop"),
        ScriptKey("𐨗", label = "𐨗", phoneticHint = "ja", unicodeCodePoint = "U+10A17", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER JA", transliteration = "ja", pronunciationIpa = "[d͡ʑɐ]", meaning = "Voiced palatal stop"),
        ScriptKey("𐨙", label = "𐨙", phoneticHint = "ña", unicodeCodePoint = "U+10A19", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER NYA", transliteration = "ña", pronunciationIpa = "[ɲɐ]", meaning = "Palatal nasal"),

        ScriptKey("𐨚", label = "𐨚", phoneticHint = "ṭa", unicodeCodePoint = "U+10A1A", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER TTA", transliteration = "ṭa", pronunciationIpa = "[ʈɐ]", meaning = "Retroflex unvoiced stop"),
        ScriptKey("𐨛", label = "𐨛", phoneticHint = "ṭha", unicodeCodePoint = "U+10A1B", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER TTHA", transliteration = "ṭha", pronunciationIpa = "[ʈʰɐ]", meaning = "Aspirated retroflex stop"),
        ScriptKey("𐨜", label = "𐨜", phoneticHint = "ḍa", unicodeCodePoint = "U+10A1C", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER DDA", transliteration = "ḍa", pronunciationIpa = "[ɖɐ]", meaning = "Voiced retroflex stop"),
        ScriptKey("𐨝", label = "𐨝", phoneticHint = "ḍha", unicodeCodePoint = "U+10A1D", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER DDHA", transliteration = "ḍha", pronunciationIpa = "[ɖʱɐ]", meaning = "Voiced aspirated retroflex stop"),
        ScriptKey("𐨞", label = "𐨞", phoneticHint = "ṇa", unicodeCodePoint = "U+10A1E", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER NNA", transliteration = "ṇa", pronunciationIpa = "[ɳɐ]", meaning = "Retroflex nasal"),

        ScriptKey("𐨟", label = "𐨟", phoneticHint = "ta", unicodeCodePoint = "U+10A1F", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER TA", transliteration = "ta", pronunciationIpa = "[t̪ɐ]", meaning = "Dental unvoiced stop"),
        ScriptKey("𐨠", label = "𐨠", phoneticHint = "tha", unicodeCodePoint = "U+10A20", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER THA", transliteration = "tha", pronunciationIpa = "[t̪ʰɐ]", meaning = "Aspirated dental stop"),
        ScriptKey("𐨡", label = "𐨡", phoneticHint = "da", unicodeCodePoint = "U+10A21", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER DA", transliteration = "da", pronunciationIpa = "[d̪ɐ]", meaning = "Voiced dental stop"),
        ScriptKey("𐨢", label = "𐨢", phoneticHint = "dha", unicodeCodePoint = "U+10A22", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER DHA", transliteration = "dha", pronunciationIpa = "[d̪ʱɐ]", meaning = "Voiced aspirated dental stop"),
        ScriptKey("𐨣", label = "𐨣", phoneticHint = "na", unicodeCodePoint = "U+10A23", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER NA", transliteration = "na", pronunciationIpa = "[nɐ]", meaning = "Dental nasal"),

        ScriptKey("𐨤", label = "𐨤", phoneticHint = "pa", unicodeCodePoint = "U+10A24", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER PA", transliteration = "pa", pronunciationIpa = "[pɐ]", meaning = "Bilabial unvoiced stop"),
        ScriptKey("𐨥", label = "𐨥", phoneticHint = "pha", unicodeCodePoint = "U+10A25", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER PHA", transliteration = "pha", pronunciationIpa = "[pʰɐ]", meaning = "Aspirated bilabial stop"),
        ScriptKey("𐨦", label = "𐨦", phoneticHint = "ba", unicodeCodePoint = "U+10A26", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER BA", transliteration = "ba", pronunciationIpa = "[bɐ]", meaning = "Voiced bilabial stop"),
        ScriptKey("𐨧", label = "𐨧", phoneticHint = "bha", unicodeCodePoint = "U+10A27", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER BHA", transliteration = "bha", pronunciationIpa = "[bʱɐ]", meaning = "Voiced aspirated bilabial stop"),
        ScriptKey("𐨨", label = "𐨨", phoneticHint = "ma", unicodeCodePoint = "U+10A28", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER MA", transliteration = "ma", pronunciationIpa = "[mɐ]", meaning = "Bilabial nasal"),

        ScriptKey("𐨩", label = "𐨩", phoneticHint = "ya", unicodeCodePoint = "U+10A29", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER YA", transliteration = "ya", pronunciationIpa = "[jɐ]", meaning = "Palatal approximant"),
        ScriptKey("𐨪", label = "𐨪", phoneticHint = "ra", unicodeCodePoint = "U+10A2A", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER RA", transliteration = "ra", pronunciationIpa = "[rɐ]", meaning = "Alveolar trill"),
        ScriptKey("𐨫", label = "𐨫", phoneticHint = "la", unicodeCodePoint = "U+10A2B", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER LA", transliteration = "la", pronunciationIpa = "[lɐ]", meaning = "Alveolar lateral"),
        ScriptKey("𐨬", label = "𐨬", phoneticHint = "va", unicodeCodePoint = "U+10A2C", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER VA", transliteration = "va", pronunciationIpa = "[ʋɐ]", meaning = "Labiodental approximant"),

        ScriptKey("𐨭", label = "𐨭", phoneticHint = "śa", unicodeCodePoint = "U+10A2D", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER SHA", transliteration = "śa", pronunciationIpa = "[ɕɐ]", meaning = "Voiceless palatal sibilant"),
        ScriptKey("𐨮", label = "𐨮", phoneticHint = "ṣa", unicodeCodePoint = "U+10A2E", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER SSA", transliteration = "ṣa", pronunciationIpa = "[ʂɐ]", meaning = "Voiceless retroflex sibilant"),
        ScriptKey("𐨯", label = "𐨯", phoneticHint = "sa", unicodeCodePoint = "U+10A2F", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER SA", transliteration = "sa", pronunciationIpa = "[sɐ]", meaning = "Voiceless dental/alveolar sibilant"),
        ScriptKey("𐨰", label = "𐨰", phoneticHint = "za", unicodeCodePoint = "U+10A30", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER ZA", transliteration = "za", pronunciationIpa = "[zɐ]", meaning = "Voiced alveolar sibilant (Iranian loan sound)"),
        ScriptKey("𐨱", label = "𐨱", phoneticHint = "ha", unicodeCodePoint = "U+10A31", category = "Consonants",
            unicodeName = "KHAROSHTHI LETTER HA", transliteration = "ha", pronunciationIpa = "[ɦɐ]", meaning = "Voiced glottal aspirate"),

        // Dependent Vowel Signs (Combining Diacritics) & Modifiers
        ScriptKey("\uD802\uDE01", label = "◌𐨁", phoneticHint = "-i", unicodeCodePoint = "U+10A01", category = "Dependent Vowels",
            unicodeName = "KHAROSHTHI VOWEL SIGN I", transliteration = "-i", pronunciationIpa = "[i]", meaning = "Dependent vowel sign i", isCombining = true),
        ScriptKey("\uD802\uDE02", label = "◌𐨂", phoneticHint = "-u", unicodeCodePoint = "U+10A02", category = "Dependent Vowels",
            unicodeName = "KHAROSHTHI VOWEL SIGN U", transliteration = "-u", pronunciationIpa = "[u]", meaning = "Dependent vowel sign u", isCombining = true),
        ScriptKey("\uD802\uDE03", label = "◌𐨃", phoneticHint = "-r̥", unicodeCodePoint = "U+10A03", category = "Dependent Vowels",
            unicodeName = "KHAROSHTHI VOWEL SIGN VOCALIC R", transliteration = "-r̥", pronunciationIpa = "[r̩]", meaning = "Dependent vocalic r sign", isCombining = true),
        ScriptKey("\uD802\uDE05", label = "◌𐨅", phoneticHint = "-e", unicodeCodePoint = "U+10A05", category = "Dependent Vowels",
            unicodeName = "KHAROSHTHI VOWEL SIGN E", transliteration = "-e", pronunciationIpa = "[eː]", meaning = "Dependent vowel sign e", isCombining = true),
        ScriptKey("\uD802\uDE06", label = "◌𐨆", phoneticHint = "-o", unicodeCodePoint = "U+10A06", category = "Dependent Vowels",
            unicodeName = "KHAROSHTHI VOWEL SIGN O", transliteration = "-o", pronunciationIpa = "[oː]", meaning = "Dependent vowel sign o", isCombining = true),

        // Combining Marks & Virama
        ScriptKey("\uD802\uDE0E", label = "◌𐨎", phoneticHint = "anusvāra", unicodeCodePoint = "U+10A0E", category = "Combining Marks",
            unicodeName = "KHAROSHTHI SIGN ANUSVARA", transliteration = "ṃ", pronunciationIpa = "[ŋ/̃]", meaning = "Nasalization mark", isCombining = true),
        ScriptKey("\uD802\uDE0F", label = "◌𐨏", phoneticHint = "visarga", unicodeCodePoint = "U+10A0F", category = "Combining Marks",
            unicodeName = "KHAROSHTHI SIGN VISARGA", transliteration = "ḥ", pronunciationIpa = "[h]", meaning = "Post-vocalic aspiration", isCombining = true),
        ScriptKey("\uD802\uDE3F", label = "◌𐨿", phoneticHint = "virāma", unicodeCodePoint = "U+10A3F", category = "Combining Marks",
            unicodeName = "KHAROSHTHI VIRAMA", transliteration = "virāma", pronunciationIpa = "Unavailable", meaning = "Vowel silencer / conjunct ligature marker", isCombining = true),

        // Punctuation
        ScriptKey("𐩐", label = "𐩐", phoneticHint = "daṇḍa", unicodeCodePoint = "U+10A50", category = "Punctuation",
            unicodeName = "KHAROSHTHI PUNCTUATION DOT", transliteration = "|", pronunciationIpa = "Unavailable", meaning = "Section punctuation dot / daṇḍa"),
        ScriptKey("𐩑", label = "𐩑", phoneticHint = "double daṇḍa", unicodeCodePoint = "U+10A51", category = "Punctuation",
            unicodeName = "KHAROSHTHI PUNCTUATION SMALL CIRCLE", transliteration = "||", pronunciationIpa = "Unavailable", meaning = "Verse closing punctuation / double daṇḍa"),
        ScriptKey("𐩒", label = "𐩒", phoneticHint = "line break mark", unicodeCodePoint = "U+10A52", category = "Punctuation",
            unicodeName = "KHAROSHTHI PUNCTUATION CRESCENT BAR", transliteration = "¶", pronunciationIpa = "Unavailable", meaning = "Line break / paragraph boundary mark"),
        ScriptKey("𐩖", label = "𐩖", phoneticHint = "daṇḍa line", unicodeCodePoint = "U+10A56", category = "Punctuation",
            unicodeName = "KHAROSHTHI PUNCTUATION DANDA", transliteration = "|", pronunciationIpa = "Unavailable", meaning = "Vertical bar / daṇḍa boundary"),
        ScriptKey("𐩗", label = "𐩗", phoneticHint = "double daṇḍa bar", unicodeCodePoint = "U+10A57", category = "Punctuation",
            unicodeName = "KHAROSHTHI PUNCTUATION DOUBLE DANDA", transliteration = "||", pronunciationIpa = "Unavailable", meaning = "Double vertical bar boundary"),

        // Numerals (U+10A40..U+10A47)
        ScriptKey("𐩀", label = "𐩀", phoneticHint = "Digit 1", unicodeCodePoint = "U+10A40", category = "Numerals",
            unicodeName = "KHAROSHTHI DIGIT ONE", transliteration = "1", pronunciationIpa = "Unavailable", meaning = "Kharosthi unit stroke 1"),
        ScriptKey("𐩁", label = "𐩁", phoneticHint = "Digit 2", unicodeCodePoint = "U+10A41", category = "Numerals",
            unicodeName = "KHAROSHTHI DIGIT TWO", transliteration = "2", pronunciationIpa = "Unavailable", meaning = "Kharosthi digit 2"),
        ScriptKey("𐩂", label = "𐩂", phoneticHint = "Digit 3", unicodeCodePoint = "U+10A42", category = "Numerals",
            unicodeName = "KHAROSHTHI DIGIT THREE", transliteration = "3", pronunciationIpa = "Unavailable", meaning = "Kharosthi digit 3"),
        ScriptKey("𐩃", label = "𐩃", phoneticHint = "Digit 4", unicodeCodePoint = "U+10A43", category = "Numerals",
            unicodeName = "KHAROSHTHI DIGIT FOUR", transliteration = "4", pronunciationIpa = "Unavailable", meaning = "Kharosthi digit 4 (cross form)"),
        ScriptKey("𐩄", label = "𐩄", phoneticHint = "Number 10", unicodeCodePoint = "U+10A44", category = "Numerals",
            unicodeName = "KHAROSHTHI NUMBER TEN", transliteration = "10", pronunciationIpa = "Unavailable", meaning = "Kharosthi number 10"),
        ScriptKey("𐩅", label = "𐩅", phoneticHint = "Number 20", unicodeCodePoint = "U+10A45", category = "Numerals",
            unicodeName = "KHAROSHTHI NUMBER TWENTY", transliteration = "20", pronunciationIpa = "Unavailable", meaning = "Kharosthi number 20"),
        ScriptKey("𐩆", label = "𐩆", phoneticHint = "Number 100", unicodeCodePoint = "U+10A46", category = "Numerals",
            unicodeName = "KHAROSHTHI NUMBER ONE HUNDRED", transliteration = "100", pronunciationIpa = "Unavailable", meaning = "Kharosthi number 100"),
        ScriptKey("𐩇", label = "𐩇", phoneticHint = "Number 1000", unicodeCodePoint = "U+10A47", category = "Numerals",
            unicodeName = "KHAROSHTHI NUMBER ONE THOUSAND", transliteration = "1000", pronunciationIpa = "Unavailable", meaning = "Kharosthi number 1000")
    )
)
