package com.ancientkeys.keyboard.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * In-app About, Privacy Policy, and Open Source Licenses screen.
 *
 * Provides comprehensive transparency regarding application versioning, offline architecture,
 * privacy guarantees, font licensing (OFL 1.1), and scholarly citations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "About AncientKeys",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Overview", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Privacy Policy", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Open Source", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
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
private fun AboutOverviewContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Identity Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏛️ AncientKeys",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 1.0 (Release Candidate)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Universal Historical Writing Systems & Epigraphic Research Platform",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Technical Specifications
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    MetadataRow("Package ID", "com.ancientkeys.keyboard")
                    MetadataRow("Architecture", "Kotlin, Jetpack Compose, Material 3")
                    MetadataRow("Android Min SDK", "API 26 (Android 8.0 Oreo)")
                    MetadataRow("Android Target SDK", "API 37 (Android 15+)")
                    MetadataRow("Input Method Service", "com.ancientkeys.keyboard.ime.AncientKeysIME")
                    MetadataRow("Offline Status", "100% Offline (0 Network Permissions)")
                }
            }
        }

        // Supported Writing Systems
        item {
            Text(
                text = "Supported Historical Writing Systems",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🔒 Zero Network Permission Guarantee",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AncientKeys has zero network permissions in its AndroidManifest.xml. The application cannot connect to the internet, make HTTP requests, or send telemetry under any circumstances.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Google Noto Fonts (SIL Open Font License 1.1)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Bundled fonts: Noto Sans Brahmi, Noto Sans Egyptian Hieroglyphs, Noto Sans Kharosthi, Noto Sans Phoenician, Noto Sans Runic.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Copyright (c) 2013-2022 Google Inc. All Rights Reserved. Licensed under the SIL Open Font License, Version 1.1. Full notice is bundled in app assets at fonts/OFL_NOTICE.txt.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "AndroidX & Jetpack Compose (Apache 2.0)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "androidx.core:core-ktx, androidx.lifecycle, androidx.activity:activity-compose, androidx.compose.material3, androidx.compose.ui.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "JUnit 4 (Eclipse Public License 1.0)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Used exclusively for host-side unit testing.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp)
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
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = code, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Monospace)
            }
            Text(text = "Period: $period", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "Direction: $direction", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "Engine: $engine", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
private fun PrivacyPrincipleItem(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun CitationLine(author: String, work: String) {
    Column {
        Text(text = author, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = work, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

