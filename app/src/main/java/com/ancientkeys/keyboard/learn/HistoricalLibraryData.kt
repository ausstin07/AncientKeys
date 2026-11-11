package com.ancientkeys.keyboard.learn

/**
 * Curated offline historical and linguistic library for AncientKeys.
 *
 * All historical dates, epigraphic inscriptions, linguistic classifications, and
 * scholarly references are sourced from primary published academic authorities:
 * - Sir Alan Gardiner, Middle Egyptian Grammar (1957)
 * - Richard Salomon, Indian Epigraphy (1998) & Ancient Buddhist Scrolls from Gandhāra (1999)
 * - Andrew Glass, A Preliminary Study of Kharosthi Manuscript Paleography (2000)
 * - L. H. Jeffery, The Local Scripts of Archaic Greece (1990)
 * - R. I. Page, Runes (Reading the Past) (1987)
 * - Christopher A. Rollston, Writing and Literacy in the World of Ancient Israel (2010)
 * - Peter T. Daniels & William Bright, The World's Writing Systems (1996)
 */
object HistoricalLibraryData {

    val profiles: List<ScriptHistoryProfile> = listOf(
        ScriptHistoryProfile(
            scriptId = "egyptian",
            name = "Egyptian Hieroglyphs",
            alternativeNames = listOf("Medu Netjer", "Hieroglyphic Egyptian", "Monumental Script"),
            period = "c. 3200 BCE – 394 CE (Over 3,600 years of documented use)",
            geographicDistribution = "Nile River Valley (Ancient Egypt & Nubia), Eastern Mediterranean trading outposts",
            writingDirection = "Right-to-Left (canonical), Left-to-Right, or Vertical Columns. Living animal and human signs always face towards the start of the line.",
            systemType = "Mixed Logographic, Determinative, and Consonantal (Phonetic Uniliterals, Biliterals, Triliterals)",
            languagesWritten = listOf("Old Egyptian", "Middle Egyptian (Classical)", "Late Egyptian", "Ptolemaic / Liturgical Egyptian"),
            importantInscriptions = listOf(
                InscriptionArtifact(
                    name = "Palermo Stone (Royal Annals)",
                    date = "c. 2400 BCE (Fifth Dynasty)",
                    location = "Palermo Archaeological Museum, Sicily",
                    description = "Basalt stela fragment documenting the earliest recorded dynastic annals from the Predynastic period through the Fifth Dynasty.",
                    historicalSignificance = "One of the earliest comprehensive monumental historical records in the ancient Mediterranean world."
                ),
                InscriptionArtifact(
                    name = "Rosetta Stone (Decree of Memphis)",
                    date = "196 BCE (Ptolemy V Epiphanes)",
                    location = "British Museum, London",
                    description = "Granodiorite stela fragment inscribed with three parallel versions of a royal decree: Egyptian Hieroglyphic, Demotic script, and Ancient Greek.",
                    historicalSignificance = "Provided the essential multilingual epigraphic key used by Jean-François Champollion in 1822 to decipher Egyptian Hieroglyphs."
                ),
                InscriptionArtifact(
                    name = "Graffito of Esmet-Akhom",
                    date = "August 24, 394 CE",
                    location = "Temple of Isis at Philae, Upper Egypt",
                    description = "The latest known dated authentic hieroglyphic inscription in human history, carved by a priest of Isis on a temple wall.",
                    historicalSignificance = "Marks the epigraphic end of active indigenous knowledge of hieroglyphic writing in antiquity."
                )
            ),
            consonantVowelMechanics = "Hieroglyphic writing is purely consonantal: vowels were spoken in living speech but never written down. The system employs 24 core uniliteral signs (each representing a single consonant), biliteral signs (two consonants), triliteral signs (three consonants), and semantic determinatives (unpronounced classification glyphs placed at the end of words to indicate category: man, motion, divinity, tree, water).",
            numeralConventions = "A decimal non-positional tally system: unit stroke (𓏤 = 1), cattle hobble (𓎆 = 10), coil of rope (𓍢 = 100), water lily (𓆼 = 1,000), pointing finger (𓂭 = 10,000), tadpole (𓆐 = 100,000), and the god Heh raising arms (𓁨 = 1,000,000).",
            keyHistoricalDevelopments = listOf(
                "c. 3200 BCE: Earliest inscribed bone and ivory tags found in Tomb U-j at Abydos.",
                "c. 2686–2181 BCE: Standardization of the classical Old Kingdom monumental style (Pyramid Texts).",
                "c. 2000–1300 BCE: Middle Egyptian becomes the classical literary language, copied for over a millennium.",
                "1822 CE: Modern decipherment announced by Jean-François Champollion using cartouches of Ptolemy and Cleopatra."
            ),
            interestingFacts = listOf(
                "The Ancient Egyptian name for their writing was 'Medu Netjer', meaning 'Divine Words' or 'Words of the Gods'.",
                "To determine which way to read a hieroglyphic line, look at the birds and people: read toward their faces!",
                "Royal names were enclosed in an oval loop with a tied rope at the bottom called a 'cartouche' (shenu), symbolizing universal protection."
            ),
            scholarlySources = listOf(
                "Gardiner, Sir Alan. (1957). Egyptian Grammar: Being an Introduction to the Study of Hieroglyphs. Griffith Institute, Oxford.",
                "Allen, James P. (2014). Middle Egyptian: An Introduction to the Language and Culture of Hieroglyphs. Cambridge University Press.",
                "Faulkner, Raymond O. (1962). A Concise Dictionary of Middle Egyptian. Griffith Institute, Oxford."
            ),
            interactiveExamples = listOf(
                TypingPracticeTask("eg_1", "Type the word for 'Sun / Sun God' (Ra)", "ra", "𓇳", "Hieroglyphic ideogram N5 representing the sun disk with center dot."),
                TypingPracticeTask("eg_2", "Type the hieroglyph for 'Life' (Ankh)", "ankh", "𓋹", "Hieroglyphic sign S34 representing a sandal strap or mirror handle, meaning 'life' or 'to live'."),
                TypingPracticeTask("eg_3", "Type the sign for 'Endurance / Stability' (Djed)", "djed", "𓊽", "Hieroglyphic sign R11 representing the spine of Osiris or a bundle of reeds.")
            )
        ),
        ScriptHistoryProfile(
            scriptId = "brahmi",
            name = "Brahmi",
            alternativeNames = listOf("Ashokan Brahmi", "Early Brahmi", "Mauryan Brahmi"),
            period = "c. 6th–3rd Century BCE to 6th Century CE",
            geographicDistribution = "Ancient South Asia (Indian Subcontinent), Sri Lanka, Central Asian Silk Road outposts, and early Southeast Asia",
            writingDirection = "Left-to-Right (canonical Ashokan style). Rare archaic coin variants (e.g. Eran coins) exhibit Right-to-Left.",
            systemType = "Abugida (Alphasyllabary)",
            languagesWritten = listOf("Prakrit (Ashokan Edicts, Gandhari, Magadhi)", "Sanskrit", "Old Tamil (Tamil-Brahmi)"),
            importantInscriptions = listOf(
                InscriptionArtifact(
                    name = "Major Rock Edicts of Ashoka",
                    date = "c. 268–232 BCE",
                    location = "Girnar (Gujarat), Dhauli (Odisha), Kalsi (Uttarakhand)",
                    description = "Fourteen monumental edicts inscribed on massive rock faces recording Emperor Ashoka's moral teachings, diplomatic missions, and state welfare policies.",
                    historicalSignificance = "The earliest extensive, securely dated deciphered decipherment anchor in Indian history."
                ),
                InscriptionArtifact(
                    name = "Heliodorus Pillar Inscription",
                    date = "c. 113 BCE",
                    location = "Vidisha (Madhya Pradesh), India",
                    description = "Pillar erected by Heliodorus, Greek ambassador from the Indo-Greek King Antialcidas to the Shunga court, dedicating a Garuda standard to Vasudeva.",
                    historicalSignificance = "Crucial epigraphic evidence of Indo-Greek cultural and religious integration in central India."
                ),
                InscriptionArtifact(
                    name = "Piprahwa Relic Casket Inscription",
                    date = "c. 3rd–2nd Century BCE",
                    location = "Piprahwa (Uttar Pradesh), India",
                    description = "Inscribed soapstone stupa casket recording the enshrinement of the corporal relics of the Buddha by the Shakya clan.",
                    historicalSignificance = "One of the most intensely studied early Brahmi epigraphs in Buddhist history."
                )
            ),
            consonantVowelMechanics = "Every consonant letter possesses an inherent /a/ vowel sound (e.g. 𑀓 = ka). To express a different vowel, modifying diacritical marks (matras) are attached above, below, or beside the consonant (e.g. 𑀓\uD804\uDC39 = ki, 𑀓\uD804\uDC3B = ku, 𑀓\uD804\uDC3E = ke). Initial vowels that occur at the start of a word use independent full vowel letters (𑀅 = a, 𑀇 = i, 𑀉 = u). A virama sign silences the inherent vowel for consonant clusters.",
            numeralConventions = "Brahmi created the direct ancestors of modern numerals! The system featured additive number signs for 1–9, 10–90, 100, and 1000, alongside early decimal zero and digits (0–9) documented in late Brahmi epigraphy.",
            keyHistoricalDevelopments = listOf(
                "c. 3rd Century BCE: Emperor Ashoka standardizes Brahmi across the Maurya Empire.",
                "c. 2nd Century BCE – 3rd Century CE: Divergence into Northern (Kushan/Gupta) and Southern (Satavahana/Kadamba) paleographic branches.",
                "c. 4th–8th Century CE: Evolution into Nagari, Sharada, Siddham, Grantha, and the writing systems of Southeast Asia (Kawi, Khmer, Mon)."
            ),
            interestingFacts = listOf(
                "Brahmi is the mother script of virtually all modern writing systems of South and Southeast Asia, including Devanagari, Bengali, Tamil, Telugu, Thai, Burmese, and Tibetan!",
                "Deciphered between 1836 and 1838 by British polymath and mint assay master James Prinsep.",
                "The name 'Brahmi' is cited in ancient Buddhist (Lalitavistara) and Jain (Samavayanga Sutra) canonical lists of scripts."
            ),
            scholarlySources = listOf(
                "Salomon, Richard. (1998). Indian Epigraphy: A Guide to the Study of Inscriptions in Sanskrit, Prakrit, and the Other Indo-Aryan Languages. Oxford University Press.",
                "Bühler, Georg. (1896). Indische Palaeographie von circa 350 a. Chr. – circa 1300 p. Chr. Verlag von Karl J. Trübner, Strassburg.",
                "Falk, Harry. (1993). Schrift im alten Indien: Ein Forschungsbericht mit Anmerkungen. Gunter Narr Verlag, Tübingen."
            ),
            interactiveExamples = listOf(
                TypingPracticeTask("br_1", "Type the imperial name 'Ashoka'", "asoka", "𑀅𑀰𑁄𑀓", "Formed with independent vowel A (𑀅), palatal sha + dependent o (𑀰𑁄), and consonant ka (𑀓)."),
                TypingPracticeTask("br_2", "Type the word for 'Virtue / Law' (Dhamma)", "dhamma", "𑀥𑀫𑁆𑀫", "Prakrit form of Sanskrit Dharma: dha (𑀥) + ma with virama (𑀫𑁆) + ma (𑀫)."),
                TypingPracticeTask("br_3", "Type the syllable 'Namaste'", "namaste", "𑀦𑀫𑀲𑁆𑀢𑁂", "Dental na (𑀦) + ma (𑀫) + sa with virama (𑀲𑁆) + ta with vowel e (𑀢𑁂).")
            )
        ),
        ScriptHistoryProfile(
            scriptId = "greek",
            name = "Ancient Greek",
            alternativeNames = listOf("Classical Greek Alphabet", "Hellenic Alphabet", "Ionic Greek Script"),
            period = "c. 800 BCE – Present (Classical epigraphic period c. 6th–3rd Century BCE)",
            geographicDistribution = "Greek mainland, Aegean Islands, Asia Minor (Ionia), Magna Graecia (Southern Italy & Sicily), Mediterranean basin",
            writingDirection = "Left-to-Right in the Classical period. Archaic inscriptions (c. 8th–6th c. BCE) frequently used Boustrophedon ('as the ox turns', alternating line direction) or Right-to-Left.",
            systemType = "True Alphabet (separate explicit letters for both consonants and vowels)",
            languagesWritten = listOf("Ancient Greek (Attic, Ionic, Doric, Aeolic)", "Koine Greek (Hellenistic / New Testament)", "Bactrian Greek"),
            importantInscriptions = listOf(
                InscriptionArtifact(
                    name = "Dipylon Oinochoe Inscription",
                    date = "c. 740 BCE",
                    location = "National Archaeological Museum, Athens",
                    description = "Geometric pottery jug bearing a scratched poetic hexameter inscription: 'Whoever of all dancers now dances most playfully...'",
                    historicalSignificance = "One of the earliest surviving substantial Greek alphabetic inscriptions ever discovered."
                ),
                InscriptionArtifact(
                    name = "Nestor's Cup (Pithekoussai)",
                    date = "c. 730–720 BCE",
                    location = "Villa Arbusto Museum, Ischia, Italy",
                    description = "Clay drinking cup inscribed with three lines of Euboean Greek in retrograde (Right-to-Left) script quoting the Iliad.",
                    historicalSignificance = "The earliest known epigraphic reference to the Homeric epics."
                ),
                InscriptionArtifact(
                    name = "Great Law Code of Gortyn",
                    date = "c. 450 BCE",
                    location = "Gortyn, Crete",
                    description = "Monumental legal inscription carved in 12 columns of boustrophedon text into a stone wall, recording civil and family laws.",
                    historicalSignificance = "The most complete surviving epigraphic code of ancient Greek civic law."
                )
            ),
            consonantVowelMechanics = "The Greeks revolutionized writing by adapting the Phoenician consonantal abjad and reassigning Phoenician letters for guttural sounds not present in Greek (aleph, he, ayin, waw) to represent explicit vowels (alpha, epsilon, omicron, upsilon, and later eta and omega). Every word explicitly spells out all consonants and vowels. The letter sigma appears as σ inside words and as ς at word endings.",
            numeralConventions = "The Milesian/Ionian alphabetic system used 24 standard letters plus 3 archaic letters (Digamma/Stigma ϛ = 6, Koppa ϟ = 90, Sampi ϡ = 900) to represent numbers with a keraia mark (αʹ = 1, βʹ = 2, ιʹ = 10, ρʹ = 100). The earlier Attic acrophonic system used initial letters (Ι=1, Π=5, Δ=10, Η=100, Χ=1000, Μ=10000).",
            keyHistoricalDevelopments = listOf(
                "c. 800 BCE: Adaptation of the Phoenician alphabet by Greek merchants in Euboea or the Levant.",
                "403 BCE: Archon Euclides formally decrees the 24-letter Ionic alphabet as the official civic standard of Athens.",
                "c. 300 BCE – 300 CE: Koine Greek alphabet becomes the lingua franca of administration, philosophy, and science across Alexander's empire and Rome."
            ),
            interestingFacts = listOf(
                "The word 'alphabet' itself comes from the first two Greek letters: Alpha (α) + Beta (β).",
                "Archaic Greek was often written 'boustrophedon'—the first line went left-to-right, the second right-to-left (with mirrored letters), resembling an ox plowing a field!",
                "The Latin alphabet (used for English today) and the Cyrillic alphabet (used for Russian and Slavic languages) are both direct descendants of Greek."
            ),
            scholarlySources = listOf(
                "Jeffery, L. H. (1990). The Local Scripts of Archaic Greece: A Study of the Origin of the Greek Alphabet and its Development from the Eighth to the Fifth Centuries B.C. Oxford University Press.",
                "Woodard, Roger D. (2008). The Ancient Languages of Europe. Cambridge University Press.",
                "Threatte, Leslie. (1980). The Grammar of Attic Inscriptions. Walter de Gruyter, Berlin."
            ),
            interactiveExamples = listOf(
                TypingPracticeTask("gr_1", "Type 'Philosophia' (Love of wisdom)", "philosophia", "φιλοσοφια", "Spelled with phi (φ), iota (ι), lambda (λ), omicron (ο), sigma (σ), omicron (ο), phi (φ), iota (ι), alpha (α)."),
                TypingPracticeTask("gr_2", "Type 'Logos' (Word / Reason)", "logos", "λογος", "Notice how the final 's' automatically converts to final sigma (ς)."),
                TypingPracticeTask("gr_3", "Type 'Athena' with rough breathing", "athena", "αθηνα", "Spelled with alpha (α), theta (θ), eta (η), nu (ν), alpha (α).")
            )
        ),
        ScriptHistoryProfile(
            scriptId = "runic",
            name = "Elder Futhark Runic",
            alternativeNames = listOf("Older Futhark", "Common Germanic Futhark", "Proto-Norse Runes"),
            period = "c. 150 CE – 800 CE",
            geographicDistribution = "Scandinavia (Denmark, Norway, Sweden), Northern Germany, Eastern Europe (Gothic migrations), British Isles",
            writingDirection = "Left-to-Right, Right-to-Left (retrograde), or Boustrophedon. Rune staves were carved to fit the available surface of weapons, brooches, or stones.",
            systemType = "Alphabet (Acrophonic rune inventory)",
            languagesWritten = listOf("Proto-Germanic", "Proto-Norse", "Early Gothic"),
            importantInscriptions = listOf(
                InscriptionArtifact(
                    name = "Vimose Comb Inscription",
                    date = "c. 160 CE",
                    location = "National Museum of Denmark, Copenhagen",
                    description = "Bone comb recovered from a bog sacrifice bearing the single word inscription 'harja' carved in Elder Futhark runes.",
                    historicalSignificance = "Widely regarded by runologists as the oldest securely datable runic inscription in existence."
                ),
                InscriptionArtifact(
                    name = "Golden Horns of Gallehus",
                    date = "c. 400 CE",
                    location = "Gallehus, Southern Jutland, Denmark",
                    description = "A pair of 5th-century gold drinking horns; the smaller bore the famous maker's dedication: 'ek hlewagastiz holtijaz horna tawido'.",
                    historicalSignificance = "The quintessential monument of early Proto-Norse language and runic epigraphy."
                ),
                InscriptionArtifact(
                    name = "Kylver Stone (Gotland)",
                    date = "c. 400 CE",
                    location = "Gotland, Sweden (now in Swedish History Museum, Stockholm)",
                    description = "Limestone slab used inside a grave cist, inscribed with the complete canonical 24-character Elder Futhark row in order.",
                    historicalSignificance = "Provides definitive archaeological confirmation of the traditional 24-rune sequence and its division into three ættir."
                )
            ),
            consonantVowelMechanics = "The Elder Futhark comprises 24 runes, partitioned into three families (ættir) of eight runes each: Freyr's ætt, Hagal's ætt, and Týr's ætt. Every rune possesses an acrophonic name whose initial phoneme corresponds to the sound of the rune (e.g. ᚠ *fehu = /f/, ᚢ *ūruz = /u/, ᚦ *þurisaz = /θ/). Runic staves avoid horizontal strokes, featuring straight vertical and diagonal lines designed specifically for carving into grain wood, bone, and stone.",
            numeralConventions = "There were no separate numerical runes in the Elder Futhark; quantities were either written out phonetically as words, represented by repeated tally strokes, or indicated by word separators.",
            keyHistoricalDevelopments = listOf(
                "c. 1st–2nd Century CE: Germanic peoples create the Futhark, likely influenced by North Italic / Etruscan alphabets or Roman cursive.",
                "c. 200–500 CE: Widespread use across Northern Europe on prestige objects, bracteates, weaponry, and memorial runestones.",
                "c. 750–800 CE: Rapid linguistic sound changes prompt the simplification of Elder Futhark into the 16-rune Younger Futhark of the Viking Age."
            ),
            interestingFacts = listOf(
                "The name 'Futhark' is formed from the sounds of the first six runes: Fehu (F), Uruz (U), Thurisaz (Th), Ansuz (A), Raidho (R), Kaunan (K).",
                "The word 'rune' (Proto-Germanic *rūnō) originally meant 'secret', 'mystery', or 'whisper'.",
                "Runes were never written with curved horizontal lines because horizontal cuts on wooden staves would split the grain of the wood!"
            ),
            scholarlySources = listOf(
                "Page, R. I. (1987). Runes (Reading the Past). British Museum Publications, London.",
                "Barnes, Michael P. (2012). Runes: A Handbook. Boydell Press, Woodbridge.",
                "Looijenga, Tineke. (2003). Texts and Contexts of the Oldest Runic Inscriptions. Brill, Leiden."
            ),
            interactiveExamples = listOf(
                TypingPracticeTask("ru_1", "Type the maker's dedication 'tawido' (made / prepared)", "tawido", "ᛏᚨᚹᛁᛞᛟ", "The famous verb from the Gallehus Horn: Tiwaz (ᛏ) + Ansuz (ᚨ) + Wunjo (ᛚ/ᚹ) + Isa (ᛁ) + Dagaz (ᛞ) + Othala (ᛟ)."),
                TypingPracticeTask("ru_2", "Type the magical runic word 'alu'", "alu", "ᚨᛚᚢ", "Ancient Germanic protective formula: Ansuz (ᚨ) + Laguz (ᛚ) + Uruz (ᚢ)."),
                TypingPracticeTask("ru_3", "Type the rune row opening 'futhark'", "futhark", "ᚠᚢᚦᚨᚱᚲ", "The canonical six opening staves: Fehu, Uruz, Thurisaz, Ansuz, Raidho, Kaunan.")
            )
        ),
        ScriptHistoryProfile(
            scriptId = "phoenician",
            name = "Phoenician",
            alternativeNames = listOf("Canaanite-Phoenician Script", "Early Punic", "Byblian Alphabet"),
            period = "c. 1050 BCE – 150 BCE (Punic descendants until c. 300 CE)",
            geographicDistribution = "Coastal Levant (Byblos, Tyre, Sidon), Mediterranean trade outposts, Cyprus, North Africa (Carthage), Iberia",
            writingDirection = "Strictly Right-to-Left (RTL) horizontally. Lines are read from right to left, pages from top to bottom.",
            systemType = "Consonantal Abjad (Pure consonant alphabet)",
            languagesWritten = listOf("Phoenician", "Punic (Carthaginian)", "Early Old Byblian"),
            importantInscriptions = listOf(
                InscriptionArtifact(
                    name = "Sarcophagus of Ahiram (Byblos)",
                    date = "c. 1000 BCE",
                    location = "National Museum of Beirut, Lebanon",
                    description = "Basalt royal sarcophagus bearing a 38-word inscription warning against the desecration of King Ahiram's tomb.",
                    historicalSignificance = "The quintessential paleographic benchmark for the earliest standardized 22-letter Phoenician linear alphabet."
                ),
                InscriptionArtifact(
                    name = "Kilamuwa Stela",
                    date = "c. 825 BCE",
                    location = "Vorderasiatisches Museum, Berlin",
                    description = "Sixteen-line royal inscription of King Kilamuwa of Sam'al carved in relief on an orthostat stone.",
                    historicalSignificance = "Provides vital insight into Levant diplomacy and late Iron Age Phoenician orthography."
                ),
                InscriptionArtifact(
                    name = "Nora Stone (Sardinia)",
                    date = "c. 800 BCE",
                    location = "Museo Archeologico Nazionale, Cagliari, Sardinia",
                    description = "Sandstone stele found in southern Sardinia recording a military victory or temple dedication to Pumay.",
                    historicalSignificance = "The oldest surviving Phoenician monumental text discovered in the Western Mediterranean."
                )
            ),
            consonantVowelMechanics = "A pure consonantal abjad comprising exactly 22 consonant signs. Vowels are entirely unwritten and inferred by native speakers from context and grammar. Words were separated by small vertical divider points or written as continuous script. Every letter represents an authentic consonant sound, beginning with the glottal stop ʾālep and concluding with tāw.",
            numeralConventions = "Additive tally and stroke numbering: vertical strokes for units (𐤖 = 1, 𐤚 = 2, 𐤛 = 3), horizontal bars with hooked ends for tens (𐤗 = 10, 𐤘 = 20), and a stylized composite sign for one hundred (𐤙 = 100).",
            keyHistoricalDevelopments = listOf(
                "c. 1200–1050 BCE: Transition from Proto-Canaanite pictographic signs to the 22 standardized linear Phoenician letters.",
                "c. 900–700 BCE: Maritime trade spreads the script across the Mediterranean; adopted and adapted by Greeks, Etruscans, and Arameans.",
                "c. 500 BCE – 146 BCE: Western Phoenician evolves into the cursive Punic script of Carthage."
            ),
            interestingFacts = listOf(
                "Phoenician is the direct ancestor of nearly all modern alphabets in the world today: Latin, Greek, Hebrew, Arabic, Cyrillic, and Brahmi!",
                "Phoenician merchants were the greatest maritime navigators of the ancient world; they circumnavigated Africa over 2,000 years before Vasco da Gama.",
                "The letter ʾālep originally represented an ox head (𐤀); turned upside down over centuries, it became our modern capital 'A'!"
            ),
            scholarlySources = listOf(
                "Rollston, Christopher A. (2010). Writing and Literacy in the World of Ancient Israel: Epigraphic Evidence from the Iron Age. Society of Biblical Literature.",
                "Healey, John F. (1990). The Early Alphabet (Reading the Past). University of California Press / British Museum.",
                "Krahmalkov, Charles R. (2001). A Phoenician-Punic Grammar. Brill, Leiden."
            ),
            interactiveExamples = listOf(
                TypingPracticeTask("ph_1", "Type 'Melek' (King / Ruler)", "mlk", "𐤌𐤋𐤊", "Phoenician consonants Mem (𐤌) + Lamed (𐤋) + Kaf (𐤊). Vowels are unwritten."),
                TypingPracticeTask("ph_2", "Type 'Bayt' (House / Temple)", "byt", "𐤁𐤉𐤕", "Bet (𐤁) + Yod (𐤉) + Taw (𐤕)."),
                TypingPracticeTask("ph_3", "Type the first three letters of the abjad ('Alef, Bet, Gimel)", "abg", "𐤀𐤁𐤂", "The ancestral root of the alphabet: 'Alef (𐤀), Bet (𐤁), Gimel (𐤂).")
            )
        ),
        ScriptHistoryProfile(
            scriptId = "kharosthi",
            name = "Kharosthi",
            alternativeNames = listOf("Kharoshthi", "Gandhari Script", "Indo-Bactrian Script"),
            period = "c. 4th Century BCE – 3rd Century CE (Surviving in Khotan until c. 7th c. CE)",
            geographicDistribution = "Ancient Northwestern South Asia (Gandhāra, Taxila, modern Pakistan & Afghanistan), Silk Road outposts of the Tarim Basin (Niya, Khotan)",
            writingDirection = "Strictly Right-to-Left (RTL) horizontally. Unlike Brahmi, Kharosthi runs right-to-left, reflecting its Aramaic administrative heritage.",
            systemType = "Abugida (Alphasyllabary)",
            languagesWritten = listOf("Gāndhārī Prakrit", "Sanskrit (Buddhist Hybrid Sanskrit)"),
            importantInscriptions = listOf(
                InscriptionArtifact(
                    name = "Ashoka Rock Edicts (Shahbazgarhi & Mansehra)",
                    date = "c. 260 BCE",
                    location = "Khyber Pakhtunkhwa, Pakistan",
                    description = "Major Rock Edicts of Emperor Ashoka inscribed in Kharosthi script rather than Brahmi, specifically adapted for northwestern populations.",
                    historicalSignificance = "The earliest extensive securely dated Kharosthi inscriptions in existence."
                ),
                InscriptionArtifact(
                    name = "British Library Kharosthi Birchbark Scrolls",
                    date = "c. 1st Century CE",
                    location = "British Library, London (originating from Gandhara)",
                    description = "Fragile birchbark Buddhist scrolls containing canonical Dharmapada, Avadana, and Mahayana sūtra texts written in Gāndhārī ink calligraphy.",
                    historicalSignificance = "The earliest surviving Buddhist manuscripts in the world, revolutionizing the history of early Buddhism."
                ),
                InscriptionArtifact(
                    name = "Wardak Bronze Vase Inscription",
                    date = "c. 178 CE (Kushan Year 51, reign of Huvishka)",
                    location = "British Museum, London (found in Wardak, Afghanistan)",
                    description = "Bronze vase inscribed with a Kharosthi dedication recording the enshrinement of relics of the Buddha in the Vagramarega monastery.",
                    historicalSignificance = "Vital epigraphic evidence for Kushan imperial chronology and Buddhist patronage in ancient Bactria."
                )
            ),
            consonantVowelMechanics = "A right-to-left abugida where each consonant carries an inherent /a/ sound (𐨐 = ka). Vowel signs are attached as small strokes: -i is a loop above, -u is a hook below, -e is a stroke slanting to the left, and -o is an upward loop. The independent vowel base is 'A' (𐨀); all other initial vowels are formed by applying dependent vowel diacritics onto this base letter (𐨀\uD802\uDE01 = i, 𐨀\uD802\uDE02 = u, 𐨀\uD802\uDE05 = e, 𐨀\uD802\uDE06 = o).",
            numeralConventions = "A distinct right-to-left additive numbering system: unit strokes 1–4 (𐩀 = 1, 𐩁 = 2, 𐩂 = 3, 𐩃 = 4, using a cross for 4), hooked curves for tens (𐩄 = 10, 𐩅 = 20), and compound signs for 100 (𐩆) and 1000 (𐩇).",
            keyHistoricalDevelopments = listOf(
                "c. 500–350 BCE: Persian Achaemenid Empire introduces Imperial Aramaic scribal traditions to the Gandhara satrapy, inspiring the invention of Kharosthi.",
                "c. 250 BCE – 200 CE: Flourishes under the Indo-Greeks, Indo-Scythians, Indo-Parthians, and Kushan Empire as the primary civic and Buddhist script.",
                "c. 300–700 CE: Displaced by Brahmi in northwestern India, but thrives along the northern Silk Road oasis of Niya."
            ),
            interestingFacts = listOf(
                "Kharosthi was the script of the ancient Gandhara kingdom, famous for its magnificent Greco-Buddhist statues of the Buddha depicting Greek drapery and Apollo-like facial features!",
                "Unlike almost all other Indian scripts which descend from Brahmi, Kharosthi was derived from the Imperial Aramaic alphabet of the Persian Empire.",
                "Deciphered in the 1830s by James Prinsep, Charles Masson, and Christian Lassen using bilingual Indo-Greek coins bearing Greek on the obverse and Kharosthi on the reverse."
            ),
            scholarlySources = listOf(
                "Salomon, Richard. (1999). Ancient Buddhist Scrolls from Gandhāra: The British Library Kharoṣṭhī Fragments. University of Washington Press, Seattle.",
                "Glass, Andrew. (2000). A Preliminary Study of Kharoṣṭhī Manuscript Paleography. University of Washington, Seattle.",
                "Salomon, Richard. (1998). Indian Epigraphy. Oxford University Press."
            ),
            interactiveExamples = listOf(
                TypingPracticeTask("kh_1", "Type 'Dharma' in Gandhari Prakrit", "dharma", "𐨢𐨪𐨨", "Dha (𐨢) + ra (𐨪) + ma (𐨨). In Gāndhārī, consonant clusters are often written phonetically."),
                TypingPracticeTask("kh_2", "Type 'Budha' (The Enlightened One)", "budha", "𐨦\uD802\uDE02𐨢", "Ba with vowel sign u (𐨦\uD802\uDE02) followed by consonant dha (𐨢)."),
                TypingPracticeTask("kh_3", "Type the Iranian loan consonant 'Za'", "za", "𐨰", "The special Kharosthi letter 𐨰 (U+10A30) created specifically to transcribe Iranian and Greek z-sounds.")
            )
        )
    )

    val foundationalArticles: List<FoundationalArticle> = listOf(
        FoundationalArticle(
            id = "script_vs_language",
            title = "Script vs. Language",
            subtitle = "Why writing systems outlive and cross linguistic boundaries",
            summary = "A script is a visual technology; a language is a spoken system of grammar and vocabulary. One script can write dozens of unrelated tongues.",
            fullMarkdown = """
# Script vs. Language: Understanding the Distinction

One of the most common confusions in historical linguistics is conflating a **writing system** (script) with a **spoken language**.

### The Core Difference
- **Language** is a system of spoken sounds, mental grammar, and vocabulary developed by a community to communicate. Humans acquired spoken language over 100,000 years ago.
- **Script** is a visual recording technology invented relatively recently (c. 3200 BCE) to fix speech into physical matter (clay, papyrus, stone, parchment, digital screens).

### One Script, Multiple Languages
A single writing system frequently records completely unrelated languages:
- **Latin Script**: Used today for English (Germanic), French (Romance), Finnish (Finno-Ugric), Turkish (Turkic), and Vietnamese (Austroasiatic).
- **Brahmi Script**: Historically used to record Prakrit (Middle Indo-Aryan), Sanskrit (Old Indo-Aryan), and Old Tamil (Dravidian).
- **Cuneiform Script**: Historically recorded Sumerian (an isolate), Akkadian (Semitic), Hittite (Indo-European), and Elamite.

### One Language, Multiple Scripts
Conversely, a single language can be written in entirely different scripts over time:
- **Turkish** was written in Arabic script for 500 years until 1928, when it adopted the Latin alphabet.
- **Hindustani** is written in Devanagari script (Hindi) in India, and in Nastaliq Perso-Arabic script (Urdu) in Pakistan, despite being grammatically the same spoken language.
- **Egyptian** was written in Hieroglyphs, Hieratic, Demotic, and finally the Greek alphabet (Coptic).

> [!NOTE]
> AncientKeys provides digital keyboards for **historical writing systems**. When you type in AncientKeys, you are generating Unicode glyphs representing these writing systems, which were used to write multiple languages across millennia.
            """.trimIndent()
        ),
        FoundationalArticle(
            id = "phonetic_vs_transliteration",
            title = "Phonetic Input vs. Scholarly Transliteration",
            subtitle = "Bridging everyday intuitive typing and academic precision",
            summary = "Conversational phonetic typing prioritizes speed and natural English letters; scholarly transliteration prioritizes strict one-to-one character reconstruction.",
            fullMarkdown = """
# Phonetic Input vs. Scholarly Transliteration

To make historical scripts accessible to everyone—from high school students to working epigraphers—AncientKeys provides two distinct Roman-letter typing workflows.

### 1. Conversational "Easy Phonetic" Input
- **Purpose**: Everyday typing, casual learning, and rapid text generation without memorizing complex scholarly diacritics.
- **How it works**: Users type natural English approximations:
  - `ka` $\rightarrow$ 𑀓
  - `kaa` $\rightarrow$ 𑀓\uD804\uDC38
  - `ki` $\rightarrow$ 𑀓\uD804\uDC39
  - `th` $\rightarrow$ θ
  - `sh` $\rightarrow$ 𑀰
- **Ambiguity Resolution**: When a sound has multiple historical equivalents (e.g., dental `t` vs retroflex `ṭ`), the keyboard immediately displays alternative candidate chips in the suggestion strip.

### 2. Scholarly Transliteration (Academic Mode)
- **Purpose**: Precision academic work, published paper citations, epigraphic editions, and cataloging.
- **How it works**: Follows strict academic international conventions:
  - **IAST** (International Alphabet of Sanskrit Transliteration) for Brahmi.
  - **Salomon & Glass Conventions** for Kharosthi.
  - **Classical Epigraphic Conventions** for Ancient Greek.
- **Precision**: Uses standard accented scholarly letters (`ā`, `ī`, `ū`, `ṛ`, `ṣ`, `ś`, `ṃ`, `ḥ`) to guarantee unambiguous 1-to-1 character reconstruction.

> [!TIP]
> Toggle easily between **Easy** mode and **Scholar** mode on the keyboard utility bar according to your current task!
            """.trimIndent()
        ),
        FoundationalArticle(
            id = "transliteration_vs_translation",
            title = "Transliteration vs. Translation",
            subtitle = "How characters travel across writing systems without altering meaning",
            summary = "Transliteration converts characters from one script to another; translation converts meaning from one language to another.",
            fullMarkdown = """
# Transliteration vs. Translation

AncientKeys is a **script keyboard and epigraphic research tool**, not a machine translation engine. Understanding the distinction between transliteration and translation is essential.

### Transliteration (Character Conversion)
- **Definition**: Mapping the characters of one writing system into the corresponding characters of another writing system based on sound or conventional equivalences.
- **Language**: The language remains completely unchanged.
- **Example**: 
  - Brahmi: 𑀅𑀰𑁄𑀓
  - Transliteration: *aśoka*
  - Language: Still ancient Magadhi Prakrit!
- **AncientKeys Role**: AncientKeys performs **accurate, deterministic transliteration** between Latin letters and ancient Unicode glyphs.

### Translation (Semantic Conversion)
- **Definition**: Converting the semantic meaning of a text from one language into another language so that speakers of the target language understand what was said.
- **Language**: The language changes completely.
- **Example**:
  - Original Egyptian: 𓋹𓍑𓋴 (*ʿnḫ wḏꜢ snb*)
  - Transliteration: *ankh wedja seneb*
  - Translation: "May he live, be prosperous, and be healthy"
- **AncientKeys Role**: AncientKeys **does not perform automated translation**. In the Inscription Research Workspace, researcher translations are explicitly entered and curated by human scholars.

> [!IMPORTANT]
> Never assume that typing an English word in a historical keyboard automatically "translates" it into the ancient language. Typing "king" in Brahmi yields the phonemes /k-i-n-g/ (𑀓𑀺𑀗), not the ancient word for king (*rājan* 𑀭𑀸𑀚𑀦)!
            """.trimIndent()
        )
    )

    fun getProfileById(scriptId: String): ScriptHistoryProfile? {
        return profiles.firstOrNull { it.scriptId.equals(scriptId, ignoreCase = true) }
    }
}

