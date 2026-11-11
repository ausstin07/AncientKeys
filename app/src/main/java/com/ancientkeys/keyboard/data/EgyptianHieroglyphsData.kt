package com.ancientkeys.keyboard.data

import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptKey

/**
 * Egyptian Hieroglyphs (Unicode block: Egyptian Hieroglyphs, U+13000..U+1342F).
 *
 * Historical writing system used in Ancient Egypt for monumental and religious inscriptions.
 * Hieroglyphs is a script, not a language; it recorded Old Egyptian, Middle Egyptian,
 * Late Egyptian, and Ptolemaic Egyptian.
 *
 * Source References:
 * - Gardiner, Sir Alan H. (1957). Egyptian Grammar: Being an Introduction to the Study of Hieroglyphs. Griffith Institute.
 * - Allen, James P. (2014). Middle Egyptian: An Introduction to the Language and Culture of Hieroglyphs. Cambridge University Press.
 * - Unicode Consortium. The Unicode Standard, Chapter 11.4: Egyptian Hieroglyphs.
 */
val EgyptianHieroglyphsScript = HistoricalScript(
    id = "egyptian",
    name = "Egyptian Hieroglyphs",
    alternativeNames = listOf("Hieroglyphics", "Medu Netjer", "Ancient Egyptian", "Kemetic"),
    historicalPeriod = "c. 3200 BCE – 400 CE",
    region = "Nile Valley, Egypt & Nubia",
    description = "Monumental writing system combining phonetic uniliterals, multiliterals, and ideographic determinatives.",
    historicalContext = "Developed in the late 4th millennium BCE in the Nile Valley, Egyptian Hieroglyphs served primarily for sacred monumental carvings ('medu netjer', divine words) and royal decrees. While cursive hieratic and demotic scripts were used for everyday administration on papyrus, monumental hieroglyphs retained their formal, pictorial precision for over three millennia until the Philae inscription of 394 CE.",
    sourcesAndReferences = "Gardiner, Egyptian Grammar (1957); Allen, Middle Egyptian (2014); The Unicode Standard Version 16.0 (ISO/IEC 10646).",
    isRtl = false,
    sampleText = "𓀀 𓁐 𓃠 𓆃 𓋹 𓊽 𓌀",
    categories = listOf("Uniliterals (Phonograms)", "Determinatives", "Ideograms & Sacred Signs", "Strokes & Counts"),
    keys = listOf(
        // Uniliteral signs (core phonetic inventory)
        ScriptKey("𓄿", label = "𓄿", phoneticHint = "ꜣ (G1)", unicodeCodePoint = "U+1313F", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH G001", transliteration = "ꜣ", pronunciationIpa = "[ʔ]", meaning = "Egyptian vulture", historicalNotes = "Gardiner G1. Glottal stop."),
        ScriptKey("𓇋", label = "𓇋", phoneticHint = "j (M17)", unicodeCodePoint = "U+131CB", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH M017", transliteration = "j", pronunciationIpa = "[j]", meaning = "Flowering reed", historicalNotes = "Gardiner M17. Palatal glide."),
        ScriptKey("𓇌", label = "𓇌", phoneticHint = "y (M17A)", unicodeCodePoint = "U+131CC", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH M017A", transliteration = "y", pronunciationIpa = "[j]", meaning = "Two reeds", historicalNotes = "Gardiner M17A. Dual flowering reeds."),
        ScriptKey("𓂝", label = "𓂝", phoneticHint = "ꜥ (D36)", unicodeCodePoint = "U+1309D", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH D036", transliteration = "ꜥ", pronunciationIpa = "[ʕ]", meaning = "Forearm", historicalNotes = "Gardiner D36. Voiced pharyngeal fricative."),
        ScriptKey("𓅱", label = "𓅱", phoneticHint = "w (G43)", unicodeCodePoint = "U+13171", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH G043", transliteration = "w", pronunciationIpa = "[w]", meaning = "Quail chick", historicalNotes = "Gardiner G43. Labiovelar glide."),
        ScriptKey("𓃀", label = "𓃀", phoneticHint = "b (D58)", unicodeCodePoint = "U+130C0", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH D058", transliteration = "b", pronunciationIpa = "[b]", meaning = "Foot / lower leg", historicalNotes = "Gardiner D58. Bilabial stop."),
        ScriptKey("𓊪", label = "𓊪", phoneticHint = "p (Q3)", unicodeCodePoint = "U+132AA", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH Q003", transliteration = "p", pronunciationIpa = "[p]", meaning = "Stool / reed mat", historicalNotes = "Gardiner Q3. Voiceless bilabial stop."),
        ScriptKey("𓆑", label = "𓆑", phoneticHint = "f (I9)", unicodeCodePoint = "U+13191", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH I009", transliteration = "f", pronunciationIpa = "[f]", meaning = "Horned viper", historicalNotes = "Gardiner I9. Labiodental fricative."),
        ScriptKey("𓅓", label = "𓅓", phoneticHint = "m (G17)", unicodeCodePoint = "U+13153", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH G017", transliteration = "m", pronunciationIpa = "[m]", meaning = "Owl", historicalNotes = "Gardiner G17. Bilabial nasal."),
        ScriptKey("𓈖", label = "𓈖", phoneticHint = "n (N35)", unicodeCodePoint = "U+13216", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH N035", transliteration = "n", pronunciationIpa = "[n]", meaning = "Water ripple", historicalNotes = "Gardiner N35. Alveolar nasal."),
        ScriptKey("𓂋", label = "𓂋", phoneticHint = "r (D21)", unicodeCodePoint = "U+1308B", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH D021", transliteration = "r", pronunciationIpa = "[r]", meaning = "Mouth", historicalNotes = "Gardiner D21. Alveolar liquid."),
        ScriptKey("𓉔", label = "𓉔", phoneticHint = "h (O4)", unicodeCodePoint = "U+13254", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH O004", transliteration = "h", pronunciationIpa = "[h]", meaning = "Reed shelter / courtyard", historicalNotes = "Gardiner O4. Voiceless glottal fricative."),
        ScriptKey("𓎛", label = "𓎛", phoneticHint = "ḥ (V28)", unicodeCodePoint = "U+1339B", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH V028", transliteration = "ḥ", pronunciationIpa = "[ħ]", meaning = "Twisted flax wick", historicalNotes = "Gardiner V28. Voiceless pharyngeal fricative."),
        ScriptKey("𓐍", label = "𓐍", phoneticHint = "ḫ (Aa1)", unicodeCodePoint = "U+1340D", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH AA001", transliteration = "ḫ", pronunciationIpa = "[x]", meaning = "Animal placenta / sieve", historicalNotes = "Gardiner Aa1. Voiceless velar fricative."),
        ScriptKey("𓄡", label = "𓄡", phoneticHint = "ẖ (F32)", unicodeCodePoint = "U+13121", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH F032", transliteration = "ẖ", pronunciationIpa = "[ç]", meaning = "Animal belly and tail", historicalNotes = "Gardiner F32. Voiceless palatal fricative."),
        // NOTE: Gardiner codes O34 (door bolt) and S29 (folded cloth) verified against Unicode Standard
        ScriptKey("𓊃", label = "𓊃", phoneticHint = "z (O34)", unicodeCodePoint = "U+13283", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH O034", transliteration = "z", pronunciationIpa = "[z]", meaning = "Door bolt", historicalNotes = "Gardiner O34. Archaic voiced alveolar fricative, later merged into /s/."),
        ScriptKey("𓋴", label = "𓋴", phoneticHint = "s (S29)", unicodeCodePoint = "U+132F4", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH S029", transliteration = "s", pronunciationIpa = "[s]", meaning = "Folded cloth", historicalNotes = "Gardiner S29. Voiceless alveolar fricative."),
        ScriptKey("𓈙", label = "𓈙", phoneticHint = "š (N37)", unicodeCodePoint = "U+13219", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH N037", transliteration = "š", pronunciationIpa = "[ʃ]", meaning = "Pool of water", historicalNotes = "Gardiner N37. Voiceless postalveolar fricative."),
        ScriptKey("𓈎", label = "𓈎", phoneticHint = "q (N29)", unicodeCodePoint = "U+1320E", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH N029", transliteration = "q", pronunciationIpa = "[q]", meaning = "Hill slope", historicalNotes = "Gardiner N29. Voiceless uvular stop."),
        ScriptKey("𓎡", label = "𓎡", phoneticHint = "k (V31)", unicodeCodePoint = "U+133A1", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH V031", transliteration = "k", pronunciationIpa = "[k]", meaning = "Basket with handle", historicalNotes = "Gardiner V31. Voiceless velar stop."),
        ScriptKey("𓎼", label = "𓎼", phoneticHint = "g (W11)", unicodeCodePoint = "U+133BC", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH W011", transliteration = "g", pronunciationIpa = "[g]", meaning = "Ring stand for jar", historicalNotes = "Gardiner W11. Voiced velar stop."),
        ScriptKey("𓏏", label = "𓏏", phoneticHint = "t (X1)", unicodeCodePoint = "U+133CF", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH X001", transliteration = "t", pronunciationIpa = "[t]", meaning = "Bread loaf", historicalNotes = "Gardiner X1. Voiceless dental/alveolar stop, feminine marker."),
        ScriptKey("𓍿", label = "𓍿", phoneticHint = "ṯ (V13)", unicodeCodePoint = "U+1337F", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH V013", transliteration = "ṯ", pronunciationIpa = "[t͡ʃ]", meaning = "Tethering rope", historicalNotes = "Gardiner V13. Voiceless palatal stop/affricate."),
        ScriptKey("𓂧", label = "𓂧", phoneticHint = "d (D46)", unicodeCodePoint = "U+130A7", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH D046", transliteration = "d", pronunciationIpa = "[d]", meaning = "Hand", historicalNotes = "Gardiner D46. Voiced dental/alveolar stop."),
        ScriptKey("𓆓", label = "𓆓", phoneticHint = "ḏ (I10)", unicodeCodePoint = "U+13193", category = "Uniliterals (Phonograms)",
            unicodeName = "EGYPTIAN HIEROGLYPH I010", transliteration = "ḏ", pronunciationIpa = "[d͡ʒ]", meaning = "Cobra", historicalNotes = "Gardiner I10. Voiced palatal stop/affricate."),

        // Determinatives
        ScriptKey("𓀀", label = "𓀀", phoneticHint = "Man (A1)", unicodeCodePoint = "U+13000", category = "Determinatives",
            unicodeName = "EGYPTIAN HIEROGLYPH A001", transliteration = "Unavailable", pronunciationIpa = "Unavailable", meaning = "Seated man", historicalNotes = "Gardiner A1. Determinative for male humans, occupations, and 1st person singular."),
        ScriptKey("𓁐", label = "𓁐", phoneticHint = "Woman (B1)", unicodeCodePoint = "U+13050", category = "Determinatives",
            unicodeName = "EGYPTIAN HIEROGLYPH B001", transliteration = "Unavailable", pronunciationIpa = "Unavailable", meaning = "Seated woman", historicalNotes = "Gardiner B1. Determinative for female humans, female relations, and feminine roles."),
        ScriptKey("𓃠", label = "𓃠", phoneticHint = "Cat (E13)", unicodeCodePoint = "U+130E0", category = "Determinatives",
            unicodeName = "EGYPTIAN HIEROGLYPH E013", transliteration = "mjw", pronunciationIpa = "[miw]", meaning = "Cat", historicalNotes = "Gardiner E13. Determinative for cats and feline entities."),
        ScriptKey("𓆃", label = "𓆃", phoneticHint = "Bird (G54)", unicodeCodePoint = "U+13183", category = "Determinatives",
            unicodeName = "EGYPTIAN HIEROGLYPH G054", transliteration = "Unavailable", pronunciationIpa = "Unavailable", meaning = "Plucked bird / trussed fowl", historicalNotes = "Gardiner G54. Determinative for offerings, fowl, and slaughter."),
        ScriptKey("𓈗", label = "𓈗", phoneticHint = "Water (N35A)", unicodeCodePoint = "U+13217", category = "Determinatives",
            unicodeName = "EGYPTIAN HIEROGLYPH N035A", transliteration = "mw", pronunciationIpa = "[maːw]", meaning = "Three water ripples", historicalNotes = "Gardiner N35A. Determinative for water, fluids, and liquid actions."),

        // Ideograms & Sacred Signs
        ScriptKey("𓇳", label = "𓇳", phoneticHint = "Sun / Ra (N5)", unicodeCodePoint = "U+131F3", category = "Ideograms & Sacred Signs",
            unicodeName = "EGYPTIAN HIEROGLYPH N005", transliteration = "rꜥ", pronunciationIpa = "[raːʕ]", meaning = "Sun / sun god Ra", historicalNotes = "Gardiner N5. Ideogram for sun, day (hrw), and time."),
        ScriptKey("𓋹", label = "𓋹", phoneticHint = "Ankh / Life (S34)", unicodeCodePoint = "U+132F9", category = "Ideograms & Sacred Signs",
            unicodeName = "EGYPTIAN HIEROGLYPH S034", transliteration = "ꜥnḫ", pronunciationIpa = "[ʕaːnax]", meaning = "Sandal strap / Ankh (Life)", historicalNotes = "Gardiner S34. Triliteral sign for ꜥ-n-ḫ ('life')."),
        ScriptKey("𓊽", label = "𓊽", phoneticHint = "Djed / Stability (R11)", unicodeCodePoint = "U+132BD", category = "Ideograms & Sacred Signs",
            unicodeName = "EGYPTIAN HIEROGLYPH R011", transliteration = "ḏd", pronunciationIpa = "[d͡ʒad]", meaning = "Djed pillar / Stability", historicalNotes = "Gardiner R11. Biliteral sign associated with Osiris representing endurance."),
        ScriptKey("𓌀", label = "𓌀", phoneticHint = "Was / Power (S40)", unicodeCodePoint = "U+13300", category = "Ideograms & Sacred Signs",
            unicodeName = "EGYPTIAN HIEROGLYPH S040", transliteration = "wꜣs", pronunciationIpa = "[waːs]", meaning = "Was scepter / Dominion", historicalNotes = "Gardiner S40. Triliteral sign representing royal and divine power."),
        ScriptKey("𓊵", label = "𓊵", phoneticHint = "Hotep / Peace (R4)", unicodeCodePoint = "U+132B5", category = "Ideograms & Sacred Signs",
            unicodeName = "EGYPTIAN HIEROGLYPH R004", transliteration = "ḥtp", pronunciationIpa = "[ħoːtɛp]", meaning = "Offering mat / Hotep (Peace)", historicalNotes = "Gardiner R4. Triliteral sign for offering, peace, satisfaction."),
        ScriptKey("𓄤", label = "𓄤", phoneticHint = "Nefer / Good (F35)", unicodeCodePoint = "U+13124", category = "Ideograms & Sacred Signs",
            unicodeName = "EGYPTIAN HIEROGLYPH F035", transliteration = "nfr", pronunciationIpa = "[nɛfɛr]", meaning = "Heart and windpipe / Beauty, good", historicalNotes = "Gardiner F35. Triliteral sign for beauty, excellence, perfection."),
        ScriptKey("𓏠", label = "𓏠", phoneticHint = "Men / Endure (Y5)", unicodeCodePoint = "U+133E0", category = "Ideograms & Sacred Signs",
            unicodeName = "EGYPTIAN HIEROGLYPH Y005", transliteration = "mn", pronunciationIpa = "[mɛn]", meaning = "Senet board / Endure", historicalNotes = "Gardiner Y5. Biliteral sign for stability, endurance, foundation."),

        // Strokes & Counts
        ScriptKey("𓏤", label = "𓏤", phoneticHint = "Unit stroke (Z1)", unicodeCodePoint = "U+133E4", category = "Strokes & Counts",
            unicodeName = "EGYPTIAN HIEROGLYPH Z001", transliteration = "Unavailable", pronunciationIpa = "Unavailable", meaning = "Single vertical stroke", historicalNotes = "Gardiner Z1. Indicates that the preceding sign is used as an ideogram or numeral 1."),
        ScriptKey("𓏥", label = "𓏥", phoneticHint = "Plural strokes (Z2)", unicodeCodePoint = "U+133E5", category = "Strokes & Counts",
            unicodeName = "EGYPTIAN HIEROGLYPH Z002", transliteration = "Unavailable", pronunciationIpa = "Unavailable", meaning = "Three vertical plural strokes", historicalNotes = "Gardiner Z2. Grammatical plural marker.")
    )
)
