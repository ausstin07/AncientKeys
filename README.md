# AncientKeys — Historical Scripts & Epigraphic Keyboard

[![Version](https://img.shields.io/badge/version-1.0--RC1-blue.svg)](https://github.com/)
[![Platform](https://img.shields.io/badge/platform-Android%208.0%2B%20(API%2026--37)-brightgreen.svg)](https://developer.android.com/)
[![License](https://img.shields.io/badge/license-Apache%202.0-orange.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Privacy](https://img.shields.io/badge/privacy-100%25%20Offline%20%7C%20Zero%20Network-success.svg)](#privacy--security-guarantees)
[![Tests](https://img.shields.io/badge/tests-173%20passed-green.svg)](#verification--test-suite)

**AncientKeys** is a native Android alternative keyboard, scholarly transliteration tool, and offline epigraphic research platform designed for six major ancient and classical writing systems. Built from the ground up with academic rigor and privacy-first architecture, AncientKeys enables researchers, students, and linguistics enthusiasts to type, study, and catalog historical inscriptions directly on Android devices.

---

## Supported Historical Writing Systems

AncientKeys bundles standalone SIL Open Font Licensed (OFL 1.1) historical fallback fonts in `app/src/main/assets/fonts/` to ensure flawless rendering across all Android versions, even if device system fonts lack historical Unicode block coverage.

| Writing System | Historical Era | Direction | Unicode Block | Bundled Fallback Font |
|---|---|---|---|---|
| **Egyptian Hieroglyphs** | ~3200 BCE – 394 CE | Left-to-Right | `U+13000`–`U+1342F` | `NotoSansEgyptianHieroglyphs-Regular.ttf` |
| **Ashokan Brahmi** | c. 3rd c. BCE – 6th c. CE | Left-to-Right | `U+11000`–`U+1107F` | `NotoSansBrahmi-Regular.ttf` |
| **Kharosthi** | c. 3rd c. BCE – 3rd c. CE | Right-to-Left (RTL) | `U+10A00`–`U+10A5F` | `NotoSansKharosthi-Regular.ttf` |
| **Ancient Greek** | c. 8th c. BCE – Present | Left-to-Right | `U+0370`–`U+03FF` | System Polytonic Greek |
| **Phoenician** | c. 1050 BCE – 150 CE | Right-to-Left (RTL) | `U+10900`–`U+1091F` | `NotoSansPhoenician-Regular.ttf` |
| **Elder Futhark (Runic)** | c. 150 – 800 CE | Left-to-Right | `U+16A0`–`U+16EA` | `NotoSansRunic-Regular.ttf` |

---

## Core Features

### 1. Flexible Input Modes
* **Direct Script Mode**: Type authentic Unicode characters directly from dedicated script keyboards with shift and secondary character states.
* **Conversational "Easy Phonetic" Mode**: Type familiar Latin phonetics (e.g., `dhammavijaya`, `kharosthi`, `agape`) and receive real-time historical candidate suggestions for Ashokan Brahmi, Kharosthi, and Ancient Greek.
* **Scholarly Academic Transliteration**: Dedicated Latin transliteration mode adhering to international epigraphic standards (IAST for Indic scripts, Salomon & Glass conventions for Kharosthi, classical Romanization for Egyptian/Greek). Includes long-press popups for historical diacritics (`ā`, `ī`, `ū`, `ṛ`, `ṣ`, `ś`, `ñ`, `ṇ`, `ṃ`, `ḥ`).
* **English QWERTY & Modern Numbers**: Instant toggling between historical scripts, full English QWERTY, modern Arabic digits (`0`–`9`), mathematical operators, and historical numeral sub-palettes (Brahmi, Kharosthi, and Phoenician numerals).

### 2. Epigraphic Research Platform
* **Inscription Research Workspace**: Create, organize, and inspect inscription study records locally. Capture provenance, historical era, physical medium, raw transcription, scholarly translation, and epigraphic notes.
* **Local Markdown Export**: Export inscription records to standardized Markdown with a single tap for integration into scholarly papers, Obsidian, or research databases.
* **Character Explorer & Taxonomy**: Browse complete Unicode glyph sets with phonetic values, Gardiner categories, historical notes, and personal favorites.
* **History Foundations Library**: In-depth offline epigraphic guides covering linguistic evolution, historical discovery, decipherment, and paleographic significance for all six scripts.

### 3. Sensitive Field & Password Shield
* Automatically detects password, PIN, and sensitive credential fields (`EditorInfo.inputType`).
* Immediately deactivates historical input grids and displays a prominent warning shield to prevent accidental entry of complex Unicode glyphs into credentials fields.
* Provides a single-tap button to seamlessly switch to your device's default system keyboard (Gboard, Samsung Keyboard, etc.).

---

## Privacy & Security Guarantees

* **100% Offline (No Network Permissions)**: AncientKeys does **not** request the `android.permission.INTERNET` permission in its manifest. The operating system strictly prevents network access.
* **Zero Keystroke Logging**: Keystrokes are delivered directly to the active application via `InputConnection` and immediately discarded from memory. No local keystroke buffers or cloud sync exist.
* **Zero Third-Party Trackers or Analytics**: No advertising SDKs, no behavioral trackers, and no external crash reporting.
* **Local-Only Private Storage**: Research records and user preferences are saved strictly in application-private internal storage (`context.filesDir`). Cloud and ADB backups are explicitly disabled (`android:allowBackup="false"`).

For full details, read our [Privacy Policy](PRIVACY_POLICY.md).

---

## Android Compatibility

* **Minimum SDK**: Android 8.0 (API Level 26, Oreo)
* **Target SDK**: Android 17 Preview (API Level 37)
* **Compile SDK**: Android 36
* **Form Factors**: Optimized for Android smartphones and tablets in both portrait and landscape orientations.
* **UI Features**: Material 3 styling, dynamic color theming, adaptive launcher icons, and Android 13+ themed monochrome app icon.

---

## Installation

### Sideloading the Release APK
1. Download `AncientKeys-v1.0-signed.apk` from the latest [GitHub Releases](https://github.com/) release.
2. Open the APK on your Android device (ensure "Install unknown apps" permission is granted for your browser or file manager).
3. Once installed, open **AncientKeys** from your app drawer.
4. Follow the interactive setup guide to enable AncientKeys:
   * Tap **Enable in Settings** $\rightarrow$ Toggle **AncientKeys Keyboard** ON.
   * Tap **Switch Input Method** $\rightarrow$ Select **AncientKeys Keyboard**.
5. You can switch back to your standard keyboard at any time using the **🌐 Switch** button on the AncientKeys utility bar.

For a comprehensive walkthrough, see the [User Guide & Epigraphic Manual](USER_GUIDE.md).

---

## Building from Source

### Prerequisites
* **JDK**: OpenJDK 17 or higher (or Android Studio bundled JBR 17+)
* **Android SDK**: Build-Tools `36.0.0` or higher, Platform SDK `36`/`37`
* **Gradle**: Handled via included Gradle Wrapper (`gradlew`)

### Build Commands

Clone the repository and build the debug APK:
```bash
# Debug build
./gradlew assembleDebug

# Output APK location:
# app/build/outputs/apk/debug/app-debug.apk
```

On Windows PowerShell:
```powershell
.\gradlew.bat assembleDebug
```

### Verification & Test Suite

AncientKeys includes a comprehensive unit test suite covering phonetic transcoders, Unicode character tables, inscription record serialization, and input method business logic:

```bash
# Run JVM unit tests
./gradlew testDebugUnitTest

# Run release unit tests
./gradlew testReleaseUnitTest

# Run code style & Android lint checks
./gradlew lintDebug
```

---

## Documentation

* **[User Guide & Epigraphic Manual](USER_GUIDE.md)**: Detailed operating instructions, keyboard mode transitions, transliteration conventions, and research tools.
* **[Privacy Policy](PRIVACY_POLICY.md)**: Detailed security architecture, OS permission boundaries, and data storage policies.
* **Font Notices & Licenses**: `app/src/main/assets/fonts/OFL_NOTICE.txt` (SIL Open Font License 1.1).

---

## License

Copyright 2026 Austin Siby

AncientKeys is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details.

### Third-Party Font Notices
The bundled Google Noto fonts remain licensed separately under the **SIL Open Font License 1.1 (OFL 1.1)**. Their full license terms and copyright notices are preserved in [`app/src/main/assets/fonts/OFL_NOTICE.txt`](app/src/main/assets/fonts/OFL_NOTICE.txt).


