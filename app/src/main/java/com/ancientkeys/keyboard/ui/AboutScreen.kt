package com.ancientkeys.keyboard.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.ui.components.AncientKeysCard
import com.ancientkeys.keyboard.ui.components.AncientKeysIconButton
import com.ancientkeys.keyboard.ui.theme.ancientKeysColors

/**
 * AncientKeys v1.1 In-app About, Privacy Policy, and Open Source Licenses screen.
 *
 * Provides comprehensive transparency regarding application versioning, offline architecture,
 * privacy guarantees, font licensing (SIL OFL 1.1), and epigraphic citations.
 * Styled with the Scholarly Calm / Minimal Sage design system tokens.
 */
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Intercept hardware and gesture back
    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AboutTopBar(onBackClick = onNavigateBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Segmented Minimal Sage Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.ancientKeysColors.chipBackground)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Overview", "Privacy Policy", "Open Source").forEachIndexed { idx, title ->
                    val isSelected = selectedTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .clickable { selectedTab = idx },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.ancientKeysColors.keyText
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> AboutOverviewContent()
                1 -> AboutPrivacyContent()
                2 -> AboutLicensesContent()
            }
        }
    }
}

@Composable
private fun AboutTopBar(onBackClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AncientKeysIconButton(
                onClick = onBackClick,
                contentDescription = "Navigate Back",
                size = 40.dp
            ) {
                Text(
                    text = "‹",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.ancientKeysColors.keyText
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column {
                Text(
                    text = "About AncientKeys",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.ancientKeysColors.keyText
                )
                Text(
                    text = "Offline epigraphy & open source licenses",
                    fontSize = 11.sp,
                    color = MaterialTheme.ancientKeysColors.chipText
                )
            }
        }
    }
}

@Composable
private fun AboutOverviewContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Identity Card
        item {
            AncientKeysCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                borderColor = MaterialTheme.ancientKeysColors.cardBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏛️ AncientKeys",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ancient scripts made easy",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.ancientKeysColors.chipBackground)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "v1.0.1",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Universal Historical Writing Systems & Epigraphic Research Platform",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.ancientKeysColors.keyText.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Technical Specifications
        item {
            AncientKeysCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                borderColor = MaterialTheme.ancientKeysColors.cardBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "System Metadata & Compatibility",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.4f)
                    )
                    MetadataRow("Package ID", "com.ancientkeys.keyboard")
                    MetadataRow("Architecture", "Kotlin, Jetpack Compose, Material 3")
                    MetadataRow("Android Min SDK", "API 26 (Android 8.0 Oreo)")
                    MetadataRow("Android Target SDK", "API 37 (Android 15+)")
                    MetadataRow("Input Method Service", "com.ancientkeys.keyboard.ime.AncientKeysIME")
                    MetadataRow("Offline Status", "100% Offline (0 Network Permissions)")
                }
            }
        }

        // Supported Writing Systems Header
        item {
            Text(
                text = "Supported Historical Writing Systems",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // 6 Historical Writing Systems
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ScriptSummaryItem(
                    name = "Egyptian Hieroglyphs",
                    code = "U+13000..U+1342E",
                    period = "c. 3200 BCE – 394 CE",
                    direction = "RTL canonical (faces read toward sign origin)",
                    engine = "Monumental Gardiner Category Layout"
                )
                ScriptSummaryItem(
                    name = "Ashokan Brahmi",
                    code = "U+11000..U+1107F",
                    period = "c. 3rd c. BCE – 6th c. CE",
                    direction = "LTR (Left-to-Right)",
                    engine = "Easy Phonetic + IAST Academic Transliteration"
                )
                ScriptSummaryItem(
                    name = "Kharosthi",
                    code = "U+10A00..U+10A5F",
                    period = "c. 3rd c. BCE – 3rd c. CE",
                    direction = "RTL (Right-to-Left)",
                    engine = "Easy Phonetic + Gāndhārī Prakrit Transliteration"
                )
                ScriptSummaryItem(
                    name = "Ancient Greek (Ionic/Attic)",
                    code = "U+0370..U+03FF & U+1F00..U+1FFF",
                    period = "c. 8th c. BCE – Present",
                    direction = "LTR (Left-to-Right)",
                    engine = "Easy Phonetic + Polytonic Diacritics + Final Sigma"
                )
                ScriptSummaryItem(
                    name = "Phoenician",
                    code = "U+10900..U+1091F",
                    period = "c. 1050 BCE – 150 CE",
                    direction = "RTL (Right-to-Left)",
                    engine = "22-Letter Abjad Direct Layout"
                )
                ScriptSummaryItem(
                    name = "Elder Futhark Runic",
                    code = "U+16A0..U+16F0",
                    period = "c. 150 – 800 CE",
                    direction = "LTR / Boustrophedon",
                    engine = "24-Rune Ættir Layout"
                )
            }
        }
    }
}

@Composable
private fun AboutPrivacyContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AncientKeysCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.ancientKeysColors.chipBackground,
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🔒 Zero Network Permission Guarantee",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AncientKeys has zero network permissions in its AndroidManifest.xml. The application cannot connect to the internet, make HTTP requests, or send telemetry under any circumstances.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.ancientKeysColors.keyText.copy(alpha = 0.85f),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            PrivacyPrincipleItem(
                title = "1. No Keystroke Logging or Storing",
                description = "AncientKeys never logs, records, analyzes, or stores keystrokes or text entered by the user. Keystrokes are delivered directly to the Android InputConnection of the focused application and immediately cleared from memory."
            )
        }

        item {
            PrivacyPrincipleItem(
                title = "2. Sensitive & Password Field Shield",
                description = "The keyboard actively detects sensitive password and PIN input variations (TYPE_TEXT_VARIATION_PASSWORD, WEB_PASSWORD, NUMBER_VARIATION_PASSWORD). Character keys are completely hidden, and the user is provided a single-tap button to switch to their standard system keyboard."
            )
        }

        item {
            PrivacyPrincipleItem(
                title = "3. Isolated Local Research Records",
                description = "Epigraphic notes and transcription records in the Inscription Research Workspace are saved strictly in app-private sandbox storage (filesDir). No external files or content URIs are exposed, and android:allowBackup='false' prevents ADB extraction."
            )
        }

        item {
            PrivacyPrincipleItem(
                title = "4. Transparent Clipboard Sharing",
                description = "Exporting research records or character transcripts occurs exclusively when you tap an explicit copy button. AncientKeys never monitors or reads your clipboard."
            )
        }
    }
}

@Composable
private fun AboutLicensesContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Third-Party Open Source Font Notices",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            AncientKeysCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                borderColor = MaterialTheme.ancientKeysColors.cardBorder
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Google Noto Fonts (SIL Open Font License 1.1)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Text(
                        text = "Bundled fonts: Noto Sans Brahmi, Noto Sans Egyptian Hieroglyphs, Noto Sans Kharosthi, Noto Sans Phoenician, Noto Sans Runic.",
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                    Text(
                        text = "Copyright (c) 2013-2022 Google Inc. All Rights Reserved. Licensed under the SIL Open Font License, Version 1.1. Full notice is bundled in app assets at fonts/OFL_NOTICE.txt.",
                        fontSize = 10.sp,
                        color = MaterialTheme.ancientKeysColors.chipText.copy(alpha = 0.8f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Android Platform & Testing Libraries",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            AncientKeysCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                borderColor = MaterialTheme.ancientKeysColors.cardBorder
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "AndroidX & Jetpack Compose (Apache 2.0)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Text(
                        text = "androidx.core:core-ktx, androidx.lifecycle, androidx.activity:activity-compose, androidx.compose.material3, androidx.compose.ui.",
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "JUnit 4 (Eclipse Public License 1.0)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Text(
                        text = "Used exclusively for host-side unit testing.",
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                }
            }
        }

        item {
            Text(
                text = "Academic & Epigraphic Citations",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            AncientKeysCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                borderColor = MaterialTheme.ancientKeysColors.cardBorder
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CitationLine("Sir Alan Gardiner (1957)", "Egyptian Grammar: Being an Introduction to the Study of Hieroglyphs.")
                    CitationLine("Richard Salomon (1998)", "Indian Epigraphy: A Guide to the Study of Inscriptions in Sanskrit, Prakrit, and the Other Indo-Aryan Languages.")
                    CitationLine("Andrew Glass (2000)", "A Preliminary Study of Kharosthi Manuscript Paleography.")
                    CitationLine("L. H. Jeffery (1990)", "The Local Scripts of Archaic Greece.")
                    CitationLine("R. I. Page (1987)", "Runes (Reading the Past).")
                    CitationLine("Christopher A. Rollston (2010)", "Writing and Literacy in the World of Ancient Israel.")
                    CitationLine("Peter T. Daniels & William Bright (1996)", "The World's Writing Systems.")
                }
            }
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.chipText)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.ancientKeysColors.keyText)
    }
}

@Composable
private fun ScriptSummaryItem(
    name: String,
    code: String,
    period: String,
    direction: String,
    engine: String
) {
    AncientKeysCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.ancientKeysColors.cardBackground,
        borderColor = MaterialTheme.ancientKeysColors.cardBorder
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.ancientKeysColors.keyText
                )
                Text(
                    text = code,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = "Period: $period",
                fontSize = 11.sp,
                color = MaterialTheme.ancientKeysColors.chipText
            )
            Text(
                text = "Direction: $direction",
                fontSize = 11.sp,
                color = MaterialTheme.ancientKeysColors.chipText
            )
            Text(
                text = "Engine: $engine",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PrivacyPrincipleItem(title: String, description: String) {
    AncientKeysCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.ancientKeysColors.cardBackground,
        borderColor = MaterialTheme.ancientKeysColors.cardBorder
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = MaterialTheme.ancientKeysColors.keyText.copy(alpha = 0.85f),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun CitationLine(author: String, work: String) {
    Column {
        Text(
            text = author,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.ancientKeysColors.keyText
        )
        Text(
            text = work,
            fontSize = 10.sp,
            color = MaterialTheme.ancientKeysColors.chipText
        )
    }
}
