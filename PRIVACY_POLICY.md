# AncientKeys — Privacy Policy

**Effective Date:** November 10, 2026  
**Application Version:** 1.0  
**Application ID:** `com.ancientkeys.keyboard`  
**Publisher:** AncientKeys Project  

---

## 1. Introduction

AncientKeys is a native Android alternative keyboard and epigraphic research platform designed specifically for historical writing systems (Egyptian Hieroglyphs, Ashokan Brahmi, Kharosthi, Ancient Greek, Phoenician, and Elder Futhark).

We firmly believe that personal typing data, academic research, and notes should belong exclusively to you. AncientKeys is architected from the ground up as a **100% offline, privacy-first tool**.

---

## 2. Core Privacy Guarantees

### Zero Network Permissions
- AncientKeys **does not request or declare the `android.permission.INTERNET` permission** in `AndroidManifest.xml`.
- The application cannot connect to the internet, make HTTP/HTTPS calls, download remote code, or transmit data to any remote server or third party under any circumstances.
- This is an OS-enforced sandbox guarantee verified by Android runtime permissions.

### Zero Keystroke Logging
- AncientKeys **never logs, collects, stores, or transmits keystrokes**, words, or text entered by the user.
- Characters typed in any mode (English QWERTY, Direct Historical, Easy Phonetic, or Scholarly Transliteration) are committed directly to the focused application's active `InputConnection` and immediately discarded from memory.
- There are no local keystroke logs, no telemetry buffers, and no predictive learning caches stored on disk.

### Zero Third-Party Trackers & SDKs
- AncientKeys contains **zero third-party analytics SDKs** (such as Google Analytics, Firebase, Mixpanel, or Facebook SDK).
- AncientKeys contains **zero advertising SDKs** (such as AdMob, Unity Ads, etc.).
- AncientKeys contains **zero crash reporting telemetry** to external servers.

### Password & Sensitive Input Shield
- AncientKeys actively monitors the `EditorInfo.inputType` of focused text fields.
- When focus enters any sensitive field (including `TYPE_TEXT_VARIATION_PASSWORD`, `TYPE_TEXT_VARIATION_VISIBLE_PASSWORD`, `TYPE_TEXT_VARIATION_WEB_PASSWORD`, or `TYPE_NUMBER_VARIATION_PASSWORD`):
  1. All historical script keys and composition buffers are immediately obscured and disabled.
  2. A prominent security shield banner informs the user that historical character entry is disabled for safety.
  3. A single-tap button is provided allowing the user to seamlessly switch to their system default keyboard.

### Local-Only Research Storage
- Inscription records cataloged within the **Inscription Research Workspace** are stored strictly within the application's private internal storage directory (`context.filesDir`).
- The application manifest specifies `android:allowBackup="false"`, preventing unintentional exposure through cloud backups or adb backup extractions.
- Data export (to Markdown) is initiated purely through explicit user tap on the copy action. AncientKeys does not monitor, read, or track your system clipboard.

---

## 3. Font Assets & Intellectual Property

AncientKeys bundles offline font binaries to ensure reliable rendering of historical scripts without needing system downloads:
- **Google Noto Fonts**: `Noto Sans Brahmi`, `Noto Sans Egyptian Hieroglyphs`, `Noto Sans Kharosthi`, `Noto Sans Phoenician`, `Noto Sans Runic`.
- These fonts are bundled locally in the APK assets and licensed under the **SIL Open Font License, Version 1.1 (OFL-1.1)**.
- No network requests are made to fetch web fonts or remote typefaces.

---

## 4. Permissions Audit

Below is the complete list of system permissions declared in `AndroidManifest.xml`:

| Permission | Purpose | Required / Optional |
|---|---|---|
| `android.permission.BIND_INPUT_METHOD` | Required by the Android OS for any service implementing `InputMethodService`. | Required for system IME service. |
| `android.permission.INTERNET` | **NONE (Not Declared)** | N/A |
| `android.permission.ACCESS_NETWORK_STATE` | **NONE (Not Declared)** | N/A |
| `android.permission.READ_EXTERNAL_STORAGE` | **NONE (Not Declared)** | N/A |
| `android.permission.WRITE_EXTERNAL_STORAGE` | **NONE (Not Declared)** | N/A |
| `android.permission.RECORD_AUDIO` | **NONE (Not Declared)** | N/A |
| `android.permission.CAMERA` | **NONE (Not Declared)** | N/A |

---

## 5. Children's Privacy

AncientKeys does not collect any personal information from any user, including children under the age of 13. The application is completely offline and safe for educational, classroom, and academic use.

---

## 6. Open Source Transparency & Verification

AncientKeys' source code, Gradle configurations, and unit tests are open for inspection and independent security verification. Any user or researcher can verify the network sandbox by inspecting the compiled `AndroidManifest.xml` or using standard Android debugging tools (`adb shell dumpsys package com.ancientkeys.keyboard`).

---

## 7. Contact & Epigraphic Inquiries

For epigraphic questions, bug reports, or suggestions regarding writing system accuracy, please open an issue or reach out via the official AncientKeys project repository.

