# AncientKeys — User Guide & Epigraphic Manual
**Version**: 1.0 (Release Candidate 1)  
**Platform**: Android 8.0+ (API 26 to API 37+)  
**License**: 100% Offline, Privacy-First, Open Source Font Licensed (OFL 1.1)

---

## Table of Contents
1. [Overview & Philosophy](#1-overview--philosophy)
2. [Enabling AncientKeys as an Alternative System Keyboard](#2-enabling-ancientkeys-as-an-alternative-system-keyboard)
3. [Keyboard Modes & Mode Bar](#3-keyboard-modes--mode-bar)
4. [English QWERTY Typing & Symbols](#4-english-qwerty-typing--symbols)
5. [Direct Historical Script Typing](#5-direct-historical-script-typing)
6. [Conversational "Easy Phonetic" Typing](#6-conversational-easy-phonetic-typing)
7. [Scholarly Academic Transliteration](#7-scholarly-academic-transliteration)
8. [Sensitive Field & Password Shield](#8-sensitive-field--password-shield)
9. [Character Explorer, Search & Favorites](#9-character-explorer-search--favorites)
10. [Offline History Library & Foundations](#10-offline-history-library--foundations)
11. [Inscription Research Workspace](#11-inscription-research-workspace)
12. [Settings & Ergonomic Customization](#12-settings--ergonomic-customization)

---

## 1. Overview & Philosophy

**AncientKeys** is a native Android alternative keyboard and educational research platform for six primary ancient writing systems:
1. **Egyptian Hieroglyphs** (~3200 BCE – 394 CE)
2. **Ashokan Brahmi** (c. 3rd c. BCE – 6th c. CE)
3. **Kharosthi** (c. 3rd c. BCE – 3rd c. CE)
4. **Ancient Greek** (c. 8th c. BCE – Present)
5. **Phoenician** (c. 1050 BCE – 150 CE)
6. **Elder Futhark Runic** (c. 150 – 800 CE)

### Core Guarantees:
- **100% Offline**: Zero network permissions requested. AncientKeys cannot transmit data.
- **Zero Keystroke Logging**: What you type is delivered directly to the active application and immediately discarded.
- **Academic Rigor**: Based strictly on authoritative epigraphic and linguistic publications (Gardiner, Salomon, Glass, Jeffery, Page, Rollston).

---

## 2. Enabling AncientKeys as an Alternative System Keyboard

AncientKeys functions as an optional system-wide keyboard without replacing your primary keyboard:

1. Open **Settings** on your Android device.
2. Navigate to **System** $\rightarrow$ **Languages & input** $\rightarrow$ **On-screen keyboard** (or **Manage keyboards**).
3. Toggle on **AncientKeys Keyboard**.
4. When typing in any application (Notes, Browser, Messaging), tap the **Keyboard icon** at the bottom corner of your screen (or press and hold the spacebar on standard keyboards) and select **AncientKeys Keyboard**.
5. To switch back to Gboard or Samsung Keyboard at any moment, tap the **🌐 Switch** button on the AncientKeys utility bar.

---

## 3. Keyboard Modes & Mode Bar

The top bar of the keyboard allows instant single-tap transitions:

| Mode Button | Function |
|---|---|
| `[Current Script ▾]` | Dropdown menu to immediately select one of the 6 historical scripts. |
| `[Easy]` | Conversational English-to-historical phonetic typing with live candidate suggestions (Brahmi, Kharosthi, Greek). |
| `[ABC]` | Full English QWERTY keyboard with Shift, Caps Lock, and scholarly diacritic long-press options. |
| `[Native]` | Direct historical script keys arranged by categorical phonology or epigraphic order. |
| `[?123]` | Universal numbers and punctuation panel (including historical numerals). |
| `[Rom]` | Strict scholarly transliteration mode (IAST, Salomon & Glass, Classical). |
| `[🌐]` | Opens the Android system keyboard picker to switch back to your device keyboard. |

---

## 4. English QWERTY Typing & Symbols

The `[ABC]` mode provides a standard English typing experience:
- **Single Tap Shift (`⇧`)**: Capitalizes the next letter, then automatically returns to lowercase.
- **Double Tap Shift (`⇪`)**: Locks Caps Lock until tapped again.
- **Long-Press Diacritics**: Press and hold vowels or scholarly consonants to reveal accents:
  - `a` $\rightarrow$ `ā`, `á`, `à`, `â`, `ä`
  - `t` $\rightarrow$ `ṭ`, `th`, `t.`
  - `d` $\rightarrow$ `ḍ`, `dh`, `d.`
  - `s` $\rightarrow$ `ś`, `ṣ`, `s.`
  - `n` $\rightarrow$ `ṇ`, `ñ`, `ṅ`

---

## 5. Direct Historical Script Typing

The `[Native]` mode displays authentic historical glyphs rendered with bundled offline Google Noto fonts:

### Script Categories:
- **Brahmi & Kharosthi**: Organized into independent vowels, velars, palatals, retroflexes, dentals, labials, semivowels, sibilants, and combining vowel signs.
- **Ancient Greek**: Consonants, vowels, punctuation, and long-press polytonic diacritics (acute, grave, circumflex, rough and smooth breathing).
- **Egyptian Hieroglyphs**: Classified according to Sir Alan Gardiner's sign categories (Man & His Occupations, Animals, Birds, Sky & Earth, Buildings).
- **Phoenician & Elder Futhark**: Ordered by traditional alphabet and runic ættir sequences.

### Scholar vs. Beginner Mode:
Tap the **Scholar / Beginner** toggle chip in the category toolbar:
- **Beginner Mode**: Keys display intuitive Latin phonetic hints (e.g. `ka`, `kha`, `ga`).
- **Scholar Mode**: Keys display official Unicode code points (e.g. `U+11013`) and Gardiner sign numbers (e.g. `A1`, `O28`).

---

## 6. Conversational "Easy Phonetic" Typing

The `[Easy]` mode allows ordinary users to type conversational English letters (similar to "Manglish" / "Hinglish") without needing complex academic diacritics:

### Brahmi:
- Type `k` $\rightarrow$ `𑀓` (inherent vowel 'a' is automatically present).
- Type `kaa` or `ka` + `a` $\rightarrow$ `𑀓𑀸` (dependent long ā matra).
- Type `ki` $\rightarrow$ `𑀓𑀺`, `ku` $\rightarrow$ `𑀓𑀼`, `ke` $\rightarrow$ `𑀓𑁂`, `ko` $\rightarrow$ `𑀓𑁄`.
- Type `asoka` $\rightarrow$ `𑀅𑀰𑁄𑀓` (palatal Aśoka) or pick `𑀅𑀲𑁄𑀓` (dental Asoka) from the candidate strip.
- Type `namas_te` $\rightarrow$ `𑀦𑀫𑀲𑁆𑀢𑁂` (the `_` character inserts the authentic Brahmi virama `𑁆`).

### Kharosthi:
- Type `k` $\rightarrow$ `𐨐`, `ki` $\rightarrow$ `𐨐\uD802\uDE01`, `ku` $\rightarrow$ `𐨐\uD802\uDE02`.
- Type `dhama` $\rightarrow$ `𐨢𐨨` (Gāndhārī Prakrit).
- Type `1`, `2`, `10`, `20`, `100` $\rightarrow$ converts to authentic Kharosthi numerals (`𐩀`, `𐩁`, `𐩄`, `𐩅`, `𐩆`).

### Ancient Greek:
- Type `th` $\rightarrow$ `θ`, `ph` $\rightarrow$ `φ`, `ch` $\rightarrow$ `χ`, `ps` $\rightarrow$ `ψ`, `ks` $\rightarrow$ `ξ`.
- Type `ha` $\rightarrow$ `ἁ` (automatic rough breathing mark).
- Type `logos` $\rightarrow$ `λογος` (converts word-final 's' to final sigma `ς` and medial 's' to `σ`).
- Type `Logos` $\rightarrow$ `Λογος` (capitalization preserved).

### Committing & Suggestions:
- Press **Space** or **Enter** to commit the active phonetic word.
- Tap any alternative chip on the suggestion strip to choose dental vs retroflex variants (`ta` vs `ṭa`, `da` vs `ḍa`, `sa` vs `śa`).

---

## 7. Scholarly Academic Transliteration

For epigraphers, linguists, and catalogers requiring exact 1-to-1 character reconstruction:
- **IAST (International Alphabet of Sanskrit Transliteration)**: Standard conventions for Sanskrit and Prakrit epigraphy.
- **Salomon & Glass Conventions**: Rigorous transliteration for Gandharan birch-bark manuscripts.
- **Classical Greek**: Direct scholarly transcription.

---

## 8. Sensitive Field & Password Shield

To protect your security and privacy:
- When any password, PIN, visible password, or web credential field receives focus, AncientKeys actively detects the sensitive input type.
- Historical character keys are **completely hidden**.
- A prominent security banner is presented explaining that AncientKeys is disabled in sensitive fields.
- A one-tap button **"🌐 Switch to Standard Keyboard"** allows you to seamlessly enter credentials with your device's default keyboard.

---

## 9. Character Explorer, Search & Favorites

From the AncientKeys main app:
- **Instant Search**: Search scripts and characters by name, modern English spelling, historical region (e.g. "Gandhara"), or Unicode code point (e.g. `11013` or `U+11013`).
- **Character Explorer**: Inspect high-resolution glyphs, official Unicode names, phonetic descriptions, and historical classifications.
- **Favorites (`★`)**: Tap the star icon on any character to save it to your personal quick-access toolbar.

---

## 10. Offline History Library & Foundations

Tap the **📖 Learn** icon in the app:
- **Writing System Profiles**: In-depth academic summaries covering historical periods, geographic ranges, primary inscriptions (e.g. Rosetta Stone, Girnar Rock Edicts, Heliodorus Pillar), grammar mechanics, and numerals.
- **Foundational Articles**:
  - *Script vs. Language* (distinguishing writing systems from spoken languages).
  - *Phonetic Typing vs. Transliteration* (understanding conversational input vs academic precision).
  - *Transliteration vs. Translation* (why converting letters is not automated language translation).
- **Interactive Practice Tasks**: Hands-on typing tasks with real-time feedback to build muscle memory for historical scripts.

---

## 11. Inscription Research Workspace

Tap the **📜 Research** icon in the app:
- Document and catalog ancient inscriptions offline.
- Record original script text, scholarly transliteration, manual researcher translation, corpus references, and epigraphic commentary.
- **Export Options**: Export individual records or your complete workspace archive into publication-ready Markdown format with explicit translation disclaimers.

---

## 12. Settings & Ergonomic Customization

Tap the **⚙️ Settings** icon on the home screen:
- **Keyboard Height**: Adjust between 220 dp and 380 dp (presets: Compact, Standard, Tall, Expanded).
- **Haptic Feedback**: Toggle tactile vibration feedback on key presses.
- **Theme Selection**: Switch between System Default, Papyrus Light (`#F8F9FA`), and Sandstone Dark (`#0F172A`).
- **Scholar Mode**: Set the default display mode for character annotations.
- **About & Licenses**: View version metadata, privacy policy, and third-party font licensing.

