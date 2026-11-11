package com.ancientkeys.keyboard.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.search.ScriptSearchEngine
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenPlayground: (HistoricalScript) -> Unit,
    onOpenExplorer: (HistoricalScript) -> Unit,
    onOpenTestLab: () -> Unit = {},
    onOpenResearchWorkspace: () -> Unit = {},
    onOpenLearn: () -> Unit = {},
    onOpenAbout: () -> Unit = {}
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesRepository.getInstance(context) }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showHeightDialog by rememberSaveable { mutableStateOf(false) }
    var showOnboardingDialog by rememberSaveable { mutableStateOf(!userPrefs.isOnboardingCompleted()) }

    val recentScriptIds = remember { userPrefs.getRecentScriptIds() }
    val recentScripts = remember(recentScriptIds) {
        recentScriptIds.mapNotNull { ScriptRegistry.getScriptById(it) }
    }

    val filteredScripts = remember(searchQuery) {
        ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AncientKeys",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    IconButton(onClick = onOpenLearn) {
                        Text("📖", fontSize = 18.sp)
                    }
                    IconButton(onClick = onOpenResearchWorkspace) {
                        Text("📜", fontSize = 18.sp)
                    }
                    IconButton(onClick = onOpenTestLab) {
                        Text("🧪", fontSize = 18.sp)
                    }
                    IconButton(onClick = onOpenAbout) {
                        Text("ℹ️", fontSize = 18.sp)
                    }
                    IconButton(onClick = { showHeightDialog = true }) {
                        Text("⚙️", fontSize = 18.sp)
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header & Search Field
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = "Historical Script Library",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Explore ancient writing systems, search character inventories, practice in playgrounds, or type system-wide.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Search Input Field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Search scripts by name, alias, or region...", fontSize = 13.sp)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Text("✕", fontSize = 14.sp)
                                    }
                                }
                            },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = onOpenTestLab,
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("🧪 Keyboard Lab", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = onOpenLearn,
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("📖 Learn", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = onOpenResearchWorkspace,
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("📜 Research", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Onboarding Banner if not completed
                        if (!userPrefs.isOnboardingCompleted()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showOnboardingDialog = true },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🚀", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("First time using AncientKeys?", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                        Text("Tap to view the 1-minute Keyboard Setup & Typing Guide.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                    }
                                    Text("Guide ➔", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }

                // Recent Scripts Section (if any scripts have been accessed)
                if (recentScripts.isNotEmpty() && searchQuery.isEmpty()) {
                    item {
                        Column {
                            Text(
                                text = "Recently Viewed",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                recentScripts.forEach { script ->
                                    FilterChip(
                                        selected = false,
                                        onClick = { onOpenExplorer(script) },
                                        label = {
                                            Text(
                                                "${script.name} ${if (script.isRtl) "(RTL)" else ""}",
                                                fontSize = 12.sp
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Optional System Keyboard Instructions Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "System Keyboard Setup (Optional)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "To use AncientKeys in any app:\n" +
                                        "1. Tap below to open Keyboard Settings.\n" +
                                        "2. Enable the AncientKeys Keyboard toggle.\n" +
                                        "3. Switch to it via the keyboard switcher (🌐) whenever you wish to write in historical scripts.\n\n" +
                                        "🔒 Privacy & Offline Guarantee: Runs 100% offline, requires 0 internet permissions, and never stores or logs typed text.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    try {
                                        context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                                    } catch (_: Exception) {
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Open Keyboard Settings →", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Search Results Counter
                if (searchQuery.isNotEmpty()) {
                    item {
                        Text(
                            text = "${filteredScripts.size} scripts matching \"$searchQuery\"",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // Historical Script Cards
                items(filteredScripts, key = { it.id }) { script ->
                    HomeScreenScriptCard(
                        script = script,
                        onOpenPlayground = { onOpenPlayground(script) },
                        onOpenExplorer = { onOpenExplorer(script) }
                    )
                }
            }
        }
    }

    // Keyboard Height Settings Dialog
    if (showHeightDialog) {
        KeyboardHeightSettingsDialog(
            userPrefs = userPrefs,
            onDismiss = { showHeightDialog = false },
            onReopenOnboarding = {
                showHeightDialog = false
                showOnboardingDialog = true
            },
            onOpenAbout = {
                showHeightDialog = false
                onOpenAbout()
            }
        )
    }

    if (showOnboardingDialog) {
        OnboardingFlowDialog(
            onDismiss = { showOnboardingDialog = false },
            userPrefs = userPrefs
        )
    }
}

@Composable
fun HomeScreenScriptCard(
    script: HistoricalScript,
    onOpenPlayground: () -> Unit,
    onOpenExplorer: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenExplorer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = script.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (script.isRtl) {
                    SuggestionChip(
                        onClick = onOpenExplorer,
                        label = { Text("RTL", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Text(
                text = "${script.historicalPeriod} • ${script.region}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = script.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = script.sampleText,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onOpenExplorer,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📖 Character Explorer", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onOpenPlayground,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text("Type Playground →", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun KeyboardHeightSettingsDialog(
    userPrefs: UserPreferencesRepository,
    onDismiss: () -> Unit,
    onReopenOnboarding: () -> Unit = {},
    onOpenAbout: () -> Unit = {}
) {
    var heightSliderValue by remember {
        mutableFloatStateOf(userPrefs.getKeyboardHeightDp().toFloat())
    }
    var hapticsValue by remember {
        mutableStateOf(userPrefs.isHapticsEnabled())
    }
    var themeValue by remember {
        mutableStateOf(userPrefs.getThemePreference())
    }
    var scholarModeValue by remember {
        mutableStateOf(userPrefs.isScholarMode())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Keyboard & Scholar Settings", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                // Scholar Mode Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Scholar Mode",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Show Unicode hex code points & epigraphic notes on character keys",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    FilterChip(
                        selected = scholarModeValue,
                        onClick = { scholarModeValue = !scholarModeValue },
                        label = { Text(if (scholarModeValue) "🎓 Scholar" else "🌱 Beginner", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Keyboard Height",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${heightSliderValue.roundToInt()} dp",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when {
                            heightSliderValue < 270 -> "Compact"
                            heightSliderValue <= 310 -> "Standard (Default)"
                            else -> "Tall"
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Slider(
                    value = heightSliderValue,
                    onValueChange = { heightSliderValue = it },
                    valueRange = UserPreferencesRepository.MIN_KEYBOARD_HEIGHT_DP.toFloat()..UserPreferencesRepository.MAX_KEYBOARD_HEIGHT_DP.toFloat(),
                    steps = 5
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { heightSliderValue = 250f }) {
                        Text("Compact (250)", fontSize = 10.sp)
                    }
                    TextButton(onClick = { heightSliderValue = UserPreferencesRepository.DEFAULT_KEYBOARD_HEIGHT_DP.toFloat() }) {
                        Text("Standard (290)", fontSize = 10.sp)
                    }
                    TextButton(onClick = { heightSliderValue = 330f }) {
                        Text("Tall (330)", fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Haptics Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Keypress Haptics",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Vibrate briefly on key taps",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    FilterChip(
                        selected = hapticsValue,
                        onClick = { hapticsValue = !hapticsValue },
                        label = { Text(if (hapticsValue) "Enabled" else "Disabled", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Theme Setting
                Text(
                    text = "Keyboard Theme",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = themeValue == UserPreferencesRepository.THEME_SYSTEM,
                        onClick = { themeValue = UserPreferencesRepository.THEME_SYSTEM },
                        label = { Text("System", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = themeValue == UserPreferencesRepository.THEME_LIGHT,
                        onClick = { themeValue = UserPreferencesRepository.THEME_LIGHT },
                        label = { Text("Light", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = themeValue == UserPreferencesRepository.THEME_DARK,
                        onClick = { themeValue = UserPreferencesRepository.THEME_DARK },
                        label = { Text("Dark", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onReopenOnboarding,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🚀 Reopen Keyboard Setup Guide", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onOpenAbout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ℹ️ About, Privacy & Open Source Licenses", fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    userPrefs.setKeyboardHeightDp(heightSliderValue.roundToInt())
                    userPrefs.setHapticsEnabled(hapticsValue)
                    userPrefs.setThemePreference(themeValue)
                    userPrefs.setScholarMode(scholarModeValue)
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
