package com.ancientkeys.keyboard.data

import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptKey

/**
 * Phoenician script (Unicode block: Phoenician, U+10900..U+1091F).
 *
 * Right-to-Left 22-consonant abjad developed around 1050 BCE in coastal Phoenicia
 * (modern Lebanon and coastal Syria) and spread across the Mediterranean by Phoenician
 * and Punic seafarers. The ancestor of the Greek, Old Italic, Latin, Aramaic, Hebrew, and Arabic scripts.
 *
 * Source References:
 * - Naveh, Joseph (1982). Early History of the Alphabet: An Introduction to West Semitic Epigraphy and Palaeography. Magnes Press.
 * - Peckham, J. Brian (1968). The Development of the Late Phoenician Scripts. Harvard University Press.
 * - The Unicode Standard, Chapter 10.3: Phoenician.
 */
val PhoenicianScript = HistoricalScript(
    id = "phoenician",
    name = "Phoenician",
    alternativeNames = listOf("Punic", "Canaanite", "Phoenician Abjad", "Northwest Semitic"),
    historicalPeriod = "c. 1050 BCE – 150 BCE",
    region = "Levant, North Africa, Western Mediterranean",
    description = "Right-to-left 22-consonant abjad; direct ancestor to Greek, Old Italic, Aramaic, and Latin.",
    historicalContext = "Developed from Proto-Canaanite alphabetic experiments in coastal Levant around 1050 BCE, the 22-letter Phoenician script recorded Northwest Semitic languages purely consonantly (an abjad). Through maritime commerce across the Mediterranean, the script was carried to Cyprus, Carthage, Sardinia, and Iberia, and was adapted by Greek traders to create the Greek alphabet.",
    sourcesAndReferences = "Naveh, Early History of the Alphabet (1982); Peckham, Development of the Late Phoenician Scripts (1968); The Unicode Standard Version 16.0.",
    isRtl = true,
    sampleText = "𐤀 𐤁 𐤂 𐤃 𐤄 𐤅 𐤆 𐤇 𐤈 𐤉 𐤟",
    categories = listOf("Consonants (Abjad)", "Numerals & Punctuation"),
    keys = listOf(
        // The 22 Phoenician Consonants
        ScriptKey("𐤀", label = "𐤀", phoneticHint = "ʾālep [ʔ] (ox)", unicodeCodePoint = "U+10900", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER ALF", transliteration = "ʾ", pronunciationIpa = "[ʔ]", meaning = "Ox head", historicalNotes = "Glottal stop consonant; ancestor of Greek Alpha and Latin A."),
        ScriptKey("𐤁", label = "𐤁", phoneticHint = "bēt [b] (house)", unicodeCodePoint = "U+10901", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER BET", transliteration = "b", pronunciationIpa = "[b]", meaning = "House / tent", historicalNotes = "Voiced bilabial stop; ancestor of Greek Beta and Latin B."),
        ScriptKey("𐤂", label = "𐤂", phoneticHint = "gīml [g] (camel)", unicodeCodePoint = "U+10902", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER GAML", transliteration = "g", pronunciationIpa = "[ɡ]", meaning = "Camel / throwstick", historicalNotes = "Voiced velar stop; ancestor of Greek Gamma and Latin C/G."),
        ScriptKey("𐤃", label = "𐤃", phoneticHint = "dālet [d] (door)", unicodeCodePoint = "U+10903", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER DELT", transliteration = "d", pronunciationIpa = "[d]", meaning = "Door", historicalNotes = "Voiced dental stop; ancestor of Greek Delta and Latin D."),
        ScriptKey("𐤄", label = "𐤄", phoneticHint = "hē [h] (window)", unicodeCodePoint = "U+10904", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER HE", transliteration = "h", pronunciationIpa = "[h]", meaning = "Window / jubilant figure", historicalNotes = "Glottal fricative; ancestor of Greek Epsilon and Latin E."),
        ScriptKey("𐤅", label = "𐤅", phoneticHint = "wāw [w] (hook)", unicodeCodePoint = "U+10905", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER WAU", transliteration = "w", pronunciationIpa = "[w]", meaning = "Hook / peg / tent pin", historicalNotes = "Labiovelar glide; ancestor of Greek Digamma/Upsilon and Latin F/U/V/W/Y."),
        ScriptKey("𐤆", label = "𐤆", phoneticHint = "zayin [z] (weapon)", unicodeCodePoint = "U+10906", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER ZAI", transliteration = "z", pronunciationIpa = "[z]", meaning = "Weapon / sword", historicalNotes = "Alveolar fricative; ancestor of Greek Zeta and Latin Z."),
        ScriptKey("𐤇", label = "𐤇", phoneticHint = "ḥēt [ħ] (wall)", unicodeCodePoint = "U+10907", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER HET", transliteration = "ḥ", pronunciationIpa = "[ħ]", meaning = "Courtyard / fence / wall", historicalNotes = "Voiceless pharyngeal fricative; ancestor of Greek Eta and Latin H."),
        ScriptKey("𐤈", label = "𐤈", phoneticHint = "ṭēt [tˤ] (wheel)", unicodeCodePoint = "U+10908", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER TET", transliteration = "ṭ", pronunciationIpa = "[tˤ]", meaning = "Wheel", historicalNotes = "Emphatic pharyngealized dental stop; ancestor of Greek Theta."),
        ScriptKey("𐤉", label = "𐤉", phoneticHint = "yōd [j] (arm)", unicodeCodePoint = "U+10909", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER YOD", transliteration = "y", pronunciationIpa = "[j]", meaning = "Arm / hand", historicalNotes = "Palatal glide; ancestor of Greek Iota and Latin I/J."),
        ScriptKey("𐤊", label = "𐤊", phoneticHint = "kap [k] (palm)", unicodeCodePoint = "U+1090A", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER KAF", transliteration = "k", pronunciationIpa = "[k]", meaning = "Palm of hand", historicalNotes = "Voiceless velar stop; ancestor of Greek Kappa and Latin K."),
        ScriptKey("𐤋", label = "𐤋", phoneticHint = "lāmed [l] (goad)", unicodeCodePoint = "U+1090B", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER LAMD", transliteration = "l", pronunciationIpa = "[l]", meaning = "Ox goad", historicalNotes = "Alveolar lateral liquid; ancestor of Greek Lambda and Latin L."),
        ScriptKey("𐤌", label = "𐤌", phoneticHint = "mēm [m] (water)", unicodeCodePoint = "U+1090C", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER MEM", transliteration = "m", pronunciationIpa = "[m]", meaning = "Water ripple", historicalNotes = "Bilabial nasal; ancestor of Greek Mu and Latin M."),
        ScriptKey("𐤍", label = "𐤍", phoneticHint = "nūn [n] (serpent)", unicodeCodePoint = "U+1090D", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER NUN", transliteration = "n", pronunciationIpa = "[n]", meaning = "Serpent / fish", historicalNotes = "Alveolar nasal; ancestor of Greek Nu and Latin N."),
        ScriptKey("𐤎", label = "𐤎", phoneticHint = "sāmek [s] (pillar)", unicodeCodePoint = "U+1090E", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER SEMK", transliteration = "s", pronunciationIpa = "[s]", meaning = "Support / pillar / fish", historicalNotes = "Alveolar sibilant; ancestor of Greek Xi."),
        ScriptKey("𐤏", label = "𐤏", phoneticHint = "ʿayin [ʕ] (eye)", unicodeCodePoint = "U+1090F", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER AIN", transliteration = "ʿ", pronunciationIpa = "[ʕ]", meaning = "Eye", historicalNotes = "Voiced pharyngeal fricative; ancestor of Greek Omicron and Latin O."),
        ScriptKey("𐤐", label = "𐤐", phoneticHint = "pē [p] (mouth)", unicodeCodePoint = "U+10910", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER PE", transliteration = "p", pronunciationIpa = "[p]", meaning = "Mouth", historicalNotes = "Voiceless bilabial stop; ancestor of Greek Pi and Latin P."),
        ScriptKey("𐤑", label = "𐤑", phoneticHint = "ṣādē [sˤ] (papyrus)", unicodeCodePoint = "U+10911", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER SADE", transliteration = "ṣ", pronunciationIpa = "[sˤ]", meaning = "Papyrus plant / hunt", historicalNotes = "Emphatic pharyngealized sibilant; ancestor of Greek San."),
        ScriptKey("𐤒", label = "𐤒", phoneticHint = "qōp [q] (needle eye)", unicodeCodePoint = "U+10912", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER QOF", transliteration = "q", pronunciationIpa = "[q]", meaning = "Eye of needle / monkey", historicalNotes = "Voiceless uvular stop; ancestor of Greek Koppa and Latin Q."),
        ScriptKey("𐤓", label = "𐤓", phoneticHint = "rēš [r] (head)", unicodeCodePoint = "U+10913", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER ROSH", transliteration = "r", pronunciationIpa = "[r]", meaning = "Head", historicalNotes = "Alveolar liquid; ancestor of Greek Rho and Latin R."),
        ScriptKey("𐤔", label = "𐤔", phoneticHint = "šīn [ʃ] (tooth)", unicodeCodePoint = "U+10914", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER SHIN", transliteration = "š", pronunciationIpa = "[ʃ]", meaning = "Tooth", historicalNotes = "Postalveolar fricative; ancestor of Greek Sigma and Latin S."),
        ScriptKey("𐤕", label = "𐤕", phoneticHint = "tāw [t] (mark)", unicodeCodePoint = "U+10915", category = "Consonants (Abjad)",
            unicodeName = "PHOENICIAN LETTER TAU", transliteration = "t", pronunciationIpa = "[t]", meaning = "Mark / cross sign", historicalNotes = "Voiceless dental stop; ancestor of Greek Tau and Latin T."),

        // Numerals & Punctuation
        ScriptKey("𐤟", label = "𐤟", phoneticHint = "Word separator dot", unicodeCodePoint = "U+1091F", category = "Numerals & Punctuation",
            unicodeName = "PHOENICIAN WORD SEPARATOR", transliteration = "·", pronunciationIpa = "Unavailable", meaning = "Word separation dot used in monumental inscriptions"),
        ScriptKey("𐤖", label = "𐤖", phoneticHint = "Numeral 1", unicodeCodePoint = "U+10916", category = "Numerals & Punctuation",
            unicodeName = "PHOENICIAN NUMBER ONE", transliteration = "1", pronunciationIpa = "Unavailable", meaning = "Numeral 1 unit stroke"),
        ScriptKey("𐤚", label = "𐤚", phoneticHint = "Numeral 2", unicodeCodePoint = "U+1091A", category = "Numerals & Punctuation",
            unicodeName = "PHOENICIAN NUMBER TWO", transliteration = "2", pronunciationIpa = "Unavailable", meaning = "Numeral 2 stroke pair"),
        ScriptKey("𐤛", label = "𐤛", phoneticHint = "Numeral 3", unicodeCodePoint = "U+1091B", category = "Numerals & Punctuation",
            unicodeName = "PHOENICIAN NUMBER THREE", transliteration = "3", pronunciationIpa = "Unavailable", meaning = "Numeral 3 stroke triad"),
        ScriptKey("𐤗", label = "𐤗", phoneticHint = "Numeral 10", unicodeCodePoint = "U+10917", category = "Numerals & Punctuation",
            unicodeName = "PHOENICIAN NUMBER TEN", transliteration = "10", pronunciationIpa = "Unavailable", meaning = "Numeral 10 horizontal bar"),
        ScriptKey("𐤘", label = "𐤘", phoneticHint = "Numeral 20", unicodeCodePoint = "U+10918", category = "Numerals & Punctuation",
            unicodeName = "PHOENICIAN NUMBER TWENTY", transliteration = "20", pronunciationIpa = "Unavailable", meaning = "Numeral 20 ligated double arch"),
        ScriptKey("𐤙", label = "𐤙", phoneticHint = "Numeral 100", unicodeCodePoint = "U+10919", category = "Numerals & Punctuation",
            unicodeName = "PHOENICIAN NUMBER ONE HUNDRED", transliteration = "100", pronunciationIpa = "Unavailable", meaning = "Numeral 100 vertical fishhook sign")
    )
)
