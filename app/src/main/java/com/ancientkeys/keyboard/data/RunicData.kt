package com.ancientkeys.keyboard.data

import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptKey

/**
 * Elder Futhark Runic Script (Unicode block: Runic, U+16A0..U+16FF).
 *
 * Oldest recorded form of the Germanic runic alphabet, used during the Migration Period
 * (c. 2nd to 8th centuries CE) across Northern, Central, and Western Europe to write
 * Proto-Germanic, Proto-Norse, and early Germanic dialects.
 *
 * Source References:
 * - Page, R. I. (1987). Runes (Reading the Past). University of California Press / British Museum.
 * - Düwel, Klaus (2001). Runenkunde. Stuttgart: J.B. Metzler.
 * - Barnes, Michael P. (2012). Runes: A Handbook. Boydell Press.
 * - The Unicode Standard, Chapter 8.7: Runic.
 */
val RunicScript = HistoricalScript(
    id = "runic",
    name = "Elder Futhark Runic",
    alternativeNames = listOf("Elder Futhark", "Futhark", "Fuþark", "Germanic Runes", "Runes", "Old Futhark"),
    historicalPeriod = "c. 2nd – 8th century CE",
    region = "Northern, Central & Western Europe",
    description = "Ancient Germanic runic alphabet traditionally grouped into three ættir of eight runes each.",
    historicalContext = "The Elder Futhark is the earliest writing system of Germanic-speaking peoples, attested on artifacts such as the Kylver Stone (Gotland, Sweden), the Vadstena bracteate, and the Vimose comb (Denmark). Comprising 24 characters arranged in a fixed order and grouped into three families ('ættir', singular 'ætt') of eight runes, each rune bore an acrophonic name rooted in early Germanic culture.",
    sourcesAndReferences = "Page, Runes (1987); Düwel, Runenkunde (2001); Barnes, Runes: A Handbook (2012); The Unicode Standard Version 16.0.",
    isRtl = false,
    sampleText = "ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ ᚷ ᚹ ᛫ ᛬",
    categories = listOf("Freyr's Ætt", "Hagal's Ætt", "Týr's Ætt", "Punctuation"),
    keys = listOf(
        // First Ætt (Freyr's Ætt)
        ScriptKey("ᚠ", label = "ᚠ", phoneticHint = "Fehu [f] (cattle)", unicodeCodePoint = "U+16A0", category = "Freyr's Ætt",
            unicodeName = "RUNIC LETTER FEHU FEOH FE F", transliteration = "f", pronunciationIpa = "[f]", meaning = "Cattle / movable wealth", historicalNotes = "Reconstructed Proto-Germanic *fehu."),
        ScriptKey("ᚢ", label = "ᚢ", phoneticHint = "Uruz [u] (aurochs)", unicodeCodePoint = "U+16A2", category = "Freyr's Ætt",
            unicodeName = "RUNIC LETTER URUZ UR U", transliteration = "u", pronunciationIpa = "[uː]", meaning = "Aurochs (wild ox)", historicalNotes = "Reconstructed Proto-Germanic *ūruz."),
        ScriptKey("ᚦ", label = "ᚦ", phoneticHint = "Thurisaz [þ] (giant)", unicodeCodePoint = "U+16A6", category = "Freyr's Ætt",
            unicodeName = "RUNIC LETTER THURISAZ THURS THORN", transliteration = "þ / th", pronunciationIpa = "[θ/ð]", meaning = "Giant / Thurs / thorn", historicalNotes = "Reconstructed Proto-Germanic *þurisaz."),
        ScriptKey("ᚨ", label = "ᚨ", phoneticHint = "Ansuz [a] (god)", unicodeCodePoint = "U+16A8", category = "Freyr's Ætt",
            unicodeName = "RUNIC LETTER ANSUZ A", transliteration = "a", pronunciationIpa = "[aː]", meaning = "God / ancestral deity (Aesir)", historicalNotes = "Reconstructed Proto-Germanic *ansuz."),
        ScriptKey("ᚱ", label = "ᚱ", phoneticHint = "Raidho [r] (ride)", unicodeCodePoint = "U+16B1", category = "Freyr's Ætt",
            unicodeName = "RUNIC LETTER RAIDO RAD REID R", transliteration = "r", pronunciationIpa = "[r]", meaning = "Ride / journey / wagon", historicalNotes = "Reconstructed Proto-Germanic *raidō."),
        ScriptKey("ᚲ", label = "ᚲ", phoneticHint = "Kaunan [k] (torch)", unicodeCodePoint = "U+16B2", category = "Freyr's Ætt",
            unicodeName = "RUNIC LETTER KAUNA", transliteration = "k", pronunciationIpa = "[k]", meaning = "Torch / ulcer (disputed)", historicalNotes = "Reconstructed Proto-Germanic *kaunan."),
        ScriptKey("ᚷ", label = "ᚷ", phoneticHint = "Gebo [g] (gift)", unicodeCodePoint = "U+16B7", category = "Freyr's Ætt",
            unicodeName = "RUNIC LETTER GEBO GYFU G", transliteration = "g", pronunciationIpa = "[ɡ/ɣ]", meaning = "Gift / partnership", historicalNotes = "Reconstructed Proto-Germanic *gebō."),
        ScriptKey("ᚹ", label = "ᚹ", phoneticHint = "Wunjo [w] (joy)", unicodeCodePoint = "U+16B9", category = "Freyr's Ætt",
            unicodeName = "RUNIC LETTER WUNJO WYNN W", transliteration = "w", pronunciationIpa = "[w]", meaning = "Joy / delight / perfection", historicalNotes = "Reconstructed Proto-Germanic *wunjō."),

        // Second Ætt (Hagal's Ætt)
        ScriptKey("ᚺ", label = "ᚺ", phoneticHint = "Hagalaz [h] (hail)", unicodeCodePoint = "U+16BA", category = "Hagal's Ætt",
            unicodeName = "RUNIC LETTER HAGL H", transliteration = "h", pronunciationIpa = "[h/x]", meaning = "Hail (weather precipitation)", historicalNotes = "Reconstructed Proto-Germanic *hagalaz."),
        ScriptKey("ᚾ", label = "ᚾ", phoneticHint = "Nauthiz [n] (need)", unicodeCodePoint = "U+16BE", category = "Hagal's Ætt",
            unicodeName = "RUNIC LETTER NAUDIZ NYD NAUD N", transliteration = "n", pronunciationIpa = "[n]", meaning = "Need / distress / constraint", historicalNotes = "Reconstructed Proto-Germanic *naudiz."),
        ScriptKey("ᛁ", label = "ᛁ", phoneticHint = "Isa [i] (ice)", unicodeCodePoint = "U+16C1", category = "Hagal's Ætt",
            unicodeName = "RUNIC LETTER ISAZ IS ISS I", transliteration = "i", pronunciationIpa = "[iː]", meaning = "Ice", historicalNotes = "Reconstructed Proto-Germanic *īsaz."),
        ScriptKey("ᛃ", label = "ᛃ", phoneticHint = "Jera [j] (year)", unicodeCodePoint = "U+16C3", category = "Hagal's Ætt",
            unicodeName = "RUNIC LETTER JERAN J", transliteration = "j", pronunciationIpa = "[j]", meaning = "Year / good season / harvest", historicalNotes = "Reconstructed Proto-Germanic *jēran."),
        ScriptKey("ᛇ", label = "ᛇ", phoneticHint = "Eihwaz [ï] (yew)", unicodeCodePoint = "U+16C7", category = "Hagal's Ætt",
            unicodeName = "RUNIC LETTER IWAZ EOH", transliteration = "ï / æ", pronunciationIpa = "[æː/ï]", meaning = "Yew tree", historicalNotes = "Reconstructed Proto-Germanic *īwaz."),
        ScriptKey("ᛈ", label = "ᛈ", phoneticHint = "Perthro [p] (lot cup)", unicodeCodePoint = "U+16C8", category = "Hagal's Ætt",
            unicodeName = "RUNIC LETTER PERTHO P", transliteration = "p", pronunciationIpa = "[p]", meaning = "Lot cup / pear tree (disputed)", historicalNotes = "Reconstructed Proto-Germanic *perþō, exact meaning historically uncertain."),
        ScriptKey("ᛉ", label = "ᛉ", phoneticHint = "Algiz [z] (protection)", unicodeCodePoint = "U+16C9", category = "Hagal's Ætt",
            unicodeName = "RUNIC LETTER ALGIZ EOLHX", transliteration = "z / R", pronunciationIpa = "[z]", meaning = "Elk / protection", historicalNotes = "Reconstructed Proto-Germanic *algiz."),
        ScriptKey("ᛊ", label = "ᛊ", phoneticHint = "Sowilo [s] (sun)", unicodeCodePoint = "U+16CA", category = "Hagal's Ætt",
            unicodeName = "RUNIC LETTER SOWILO S", transliteration = "s", pronunciationIpa = "[s]", meaning = "Sun", historicalNotes = "Reconstructed Proto-Germanic *sōwilō."),

        // Third Ætt (Týr's Ætt)
        ScriptKey("ᛏ", label = "ᛏ", phoneticHint = "Tiwaz [t] (god Týr)", unicodeCodePoint = "U+16CF", category = "Týr's Ætt",
            unicodeName = "RUNIC LETTER TIWAZ TIR TYR T", transliteration = "t", pronunciationIpa = "[t]", meaning = "God Týr / sky deity", historicalNotes = "Reconstructed Proto-Germanic *tīwaz."),
        ScriptKey("ᛒ", label = "ᛒ", phoneticHint = "Berkanan [b] (birch)", unicodeCodePoint = "U+16D2", category = "Týr's Ætt",
            unicodeName = "RUNIC LETTER BERKANAN BEORC BJARKAN B", transliteration = "b", pronunciationIpa = "[b/β]", meaning = "Birch twig / rebirth", historicalNotes = "Reconstructed Proto-Germanic *berkanan."),
        ScriptKey("ᛖ", label = "ᛖ", phoneticHint = "Ehwaz [e] (horse)", unicodeCodePoint = "U+16D6", category = "Týr's Ætt",
            unicodeName = "RUNIC LETTER EHWAZ E", transliteration = "e", pronunciationIpa = "[eː]", meaning = "Horse / steed", historicalNotes = "Reconstructed Proto-Germanic *ehwaz."),
        ScriptKey("ᛗ", label = "ᛗ", phoneticHint = "Mannaz [m] (man)", unicodeCodePoint = "U+16D7", category = "Týr's Ætt",
            unicodeName = "RUNIC LETTER MANNAZ M", transliteration = "m", pronunciationIpa = "[m]", meaning = "Human / man", historicalNotes = "Reconstructed Proto-Germanic *mannaz."),
        ScriptKey("ᛚ", label = "ᛚ", phoneticHint = "Laguz [l] (water)", unicodeCodePoint = "U+16DA", category = "Týr's Ætt",
            unicodeName = "RUNIC LETTER LAUKAZ LAGU LOGR L", transliteration = "l", pronunciationIpa = "[l]", meaning = "Water / lake / leek", historicalNotes = "Reconstructed Proto-Germanic *laguz."),
        ScriptKey("ᛜ", label = "ᛜ", phoneticHint = "Ingwaz [ŋ] (god Yngvi)", unicodeCodePoint = "U+16DC", category = "Týr's Ætt",
            unicodeName = "RUNIC LETTER INGWAZ", transliteration = "ŋ", pronunciationIpa = "[ŋ]", meaning = "The god Yngvi / earth", historicalNotes = "Reconstructed Proto-Germanic *ingwaz."),
        ScriptKey("ᛞ", label = "ᛞ", phoneticHint = "Dagaz [d] (day)", unicodeCodePoint = "U+16DE", category = "Týr's Ætt",
            unicodeName = "RUNIC LETTER DAGAZ DAEG D", transliteration = "d", pronunciationIpa = "[d/ð]", meaning = "Day / daylight / dawn", historicalNotes = "Reconstructed Proto-Germanic *dagaz."),
        ScriptKey("ᛟ", label = "ᛟ", phoneticHint = "Othalan [o] (estate)", unicodeCodePoint = "U+16DF", category = "Týr's Ætt",
            unicodeName = "RUNIC LETTER OTHALAN ETHEL O", transliteration = "o", pronunciationIpa = "[oː]", meaning = "Heritage / ancestral estate", historicalNotes = "Reconstructed Proto-Germanic *ōþalan."),

        // Punctuation & Separators
        ScriptKey("᛫", label = "᛫", phoneticHint = "Single punctuation mark", unicodeCodePoint = "U+16EB", category = "Punctuation",
            unicodeName = "RUNIC SINGLE PUNCTUATION", transliteration = "·", pronunciationIpa = "Unavailable", meaning = "Word separation dot"),
        ScriptKey("᛬", label = "᛬", phoneticHint = "Double punctuation mark", unicodeCodePoint = "U+16EC", category = "Punctuation",
            unicodeName = "RUNIC MULTIPLE PUNCTUATION", transliteration = ":", pronunciationIpa = "Unavailable", meaning = "Word / clause separation colon"),
        ScriptKey("᛭", label = "᛭", phoneticHint = "Cross punctuation mark", unicodeCodePoint = "U+16ED", category = "Punctuation",
            unicodeName = "RUNIC CROSS PUNCTUATION", transliteration = "+", pronunciationIpa = "Unavailable", meaning = "Sentence / inscription closing cross")
    )
)
