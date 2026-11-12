package com.ancientkeys.keyboard.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.ime.data.EnglishKeyboardData
import com.ancientkeys.keyboard.ime.romanization.ScriptRomanizerProvider
import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ResearchRecord
import com.ancientkeys.keyboard.model.ScriptKey
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.storage.ResearchRecordRepository
import com.ancientkeys.keyboard.ui.components.AncientKeysCard
import com.ancientkeys.keyboard.ui.components.AncientKeysIconButton
import com.ancientkeys.keyboard.ui.theme.AncientKeysDesignTokens
import com.ancientkeys.keyboard.ui.theme.ancientKeysColors
import com.ancientkeys.keyboard.util.HistoricalFontProvider
import com.ancientkeys.keyboard.util.UnicodeMetadataHelper
import com.ancientkeys.keyboard.util.UnicodeTextEditor
import kotlinx.coroutines.launch

/**
 * AncientKeys v1.1 Interactive Playground matching Screen 2 of the visual identity specification.
 *
 * Implements:
 * - Top header with Back (`‹`), Title ("AncientKeys Keyboard"), and Info (`ℹ`)
 * - Script selector pill row supporting English and all historical scripts
 * - Mode toggle: [⌨️ Keyboard] vs [📖 Learn Mode]
 * - Scholarly canvas card with authentic typography and Copy/Share quick actions
 * - Academic utility actions: Translate gloss, Transliterate, and Save to Research Workspace
 * - Vocabulary quick-suggestion candidate chips
 * - Full in-app interactive keyboard matching Screen 2 visual aesthetics
 * - Learn Mode panel with script overview, pronunciation, and linguistic mechanics
 */
@Composable
fun PlaygroundScreen(
    script: HistoricalScript,
    textValue: TextFieldValue,
    onTextValueChange: (TextFieldValue) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val researchRepo = remember { ResearchRecordRepository(context) }

    // Intercept Back gesture/button
    BackHandler {
        onBackClick()
    }

    // Active script can be changed directly from top script pills
    var activeScriptId by rememberSaveable(script.id) { mutableStateOf(script.id) }
    val currentScript = remember(activeScriptId) {
        ScriptRegistry.getScriptById(activeScriptId) ?: script
    }
    val isEnglish = activeScriptId == "english"

    // Per-script text buffer isolation so switching scripts doesn't render mismatched fonts
    val perScriptTextValues = rememberSaveable(
        saver = Saver(
            save = { map ->
                HashMap(map.mapValues { it.value.text })
            },
            restore = { restored ->
                val map = mutableStateMapOf<String, TextFieldValue>()
                @Suppress("UNCHECKED_CAST")
                (restored as? Map<String, String>)?.forEach { (k, v) -> map[k] = TextFieldValue(v) }
                map
            }
        )
    ) {
        mutableStateMapOf<String, TextFieldValue>().apply {
            if (textValue.text.isNotEmpty()) {
                put(script.id, textValue)
            }
        }
    }

    LaunchedEffect(textValue) {
        if (activeScriptId == script.id && textValue != perScriptTextValues[script.id]) {
            perScriptTextValues[script.id] = textValue
        }
    }

    val currentTextValue = perScriptTextValues[activeScriptId] ?: TextFieldValue("")
    val handleTextChange: (TextFieldValue) -> Unit = { newValue ->
        perScriptTextValues[activeScriptId] = newValue
        if (activeScriptId == script.id) {
            onTextValueChange(newValue)
        }
    }

    // View mode: 0 = Keyboard, 1 = Learn Mode
    var playgroundMode by rememberSaveable { mutableIntStateOf(0) }
    var selectedCategoryIndex by rememberSaveable(activeScriptId) { mutableIntStateOf(0) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showTransliterateDialog by remember { mutableStateOf(false) }
    var showTranslateDialog by remember { mutableStateOf(false) }

    val scriptFont = remember(currentScript.id, isEnglish) {
        if (isEnglish) null else HistoricalFontProvider.getFontFamily(context, currentScript.id)
    }

    val activeCategory = if (!isEnglish && currentScript.categories.isNotEmpty() && selectedCategoryIndex < currentScript.categories.size) {
        currentScript.categories[selectedCategoryIndex]
    } else {
        null
    }

    val visibleKeys = remember(currentScript, activeCategory, isEnglish) {
        if (isEnglish) {
            emptyList()
        } else if (activeCategory == null) {
            currentScript.keys
        } else {
            currentScript.keys.filter { it.category == activeCategory }
        }
    }

    // Curated vocabulary suggestions per script
    val suggestions = remember(activeScriptId) {
        when (activeScriptId) {
            "greek" -> listOf("γράμματα", "λόγος", "ἱστορία", "σοφία", "ἀρχή")
            "egyptian" -> listOf("𓊹", "𓋹", "𓇳", "𓂋", "𓏠", "𓈖")
            "brahmi" -> listOf("𑀥𑀫", "𑀲𑀘", "𑀤𑀸𑀦", "𑀅𑀰𑁄𑀓", "𑀰𑀸𑀦𑁆𑀢𑀺")
            "kharosthi" -> listOf("𐨢𐨨", "𐨯𐨕", "𐨡𐨞", "𐨒𐨣𐨢𐨪", "𐨀𐨁𐨭𐨿𐨬𐨪")
            "phoenician" -> listOf("𐤌𐤋𐤊", "𐤒𐤓𐤕", "𐤔𐤋𐤌", "𐤁𐤉𐤕", "𐤀𐤓𐤑")
            "runic" -> listOf("ᚱᚢᚾᛟ", "ᛏᛁᚹᚨᛉ", "ᚠᛖᚺᚢ", "ᚨᚾᛊᚢᛉ", "ᚹᚢᚾᛃᛟ")
            else -> listOf("Ancient", "Script", "History", "Wisdom", "Keys")
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            PlaygroundTopBar(
                title = "AncientKeys Keyboard",
                onBackClick = onBackClick,
                onInfoClick = { showInfoDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            // ==========================================
            // 1. SCRIPT SELECTION PILL ROW
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // English pill
                ScriptSelectionPill(
                    label = "English",
                    isSelected = activeScriptId == "english",
                    onClick = { activeScriptId = "english" }
                )

                // 6 Historical Script Pills
                ScriptRegistry.allScripts.forEach { s ->
                    val shortName = when (s.id) {
                        "greek" -> "Greek"
                        "brahmi" -> "Brahmi"
                        "runic" -> "Runic"
                        "phoenician" -> "Phoenician"
                        "kharosthi" -> "Kharosthi"
                        "egyptian" -> "Egyptian"
                        else -> s.name
                    }
                    ScriptSelectionPill(
                        label = shortName,
                        isSelected = activeScriptId == s.id,
                        onClick = {
                            activeScriptId = s.id
                            selectedCategoryIndex = 0
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // 2. VIEW MODE TOGGLE PILLS [Keyboard] [Learn Mode]
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.ancientKeysColors.chipBackground)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Keyboard Mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (playgroundMode == 0) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                        .clickable { playgroundMode = 0 },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⌨️ Keyboard",
                        fontSize = 12.sp,
                        fontWeight = if (playgroundMode == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (playgroundMode == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.ancientKeysColors.keyText
                    )
                }

                // Learn Mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (playgroundMode == 1) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                        .clickable { playgroundMode = 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📖 Learn Mode",
                        fontSize = 12.sp,
                        fontWeight = if (playgroundMode == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (playgroundMode == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.ancientKeysColors.keyText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // 3. SCHOLARLY TEXT EDITING CANVAS CARD
            // ==========================================
            AncientKeysCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                borderColor = MaterialTheme.ancientKeysColors.cardBorder
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val textLayoutDirection = if (!isEnglish && currentScript.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
                    CompositionLocalProvider(LocalLayoutDirection provides textLayoutDirection) {
                        OutlinedTextField(
                            value = currentTextValue,
                            onValueChange = handleTextChange,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(end = 40.dp),
                            textStyle = TextStyle(
                                fontSize = 22.sp,
                                lineHeight = 30.sp,
                                fontFamily = scriptFont,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.ancientKeysColors.keyText,
                                textDirection = if (!isEnglish && currentScript.isRtl) TextDirection.Rtl else TextDirection.Ltr
                            ),
                            placeholder = {
                                Text(
                                    text = if (isEnglish) "Type English text here..." else "Write in ${currentScript.name}...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.ancientKeysColors.chipText
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent
                            )
                        )
                    }

                    // Copy & Share buttons inside canvas bottom-right
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        AncientKeysIconButton(
                            onClick = {
                                if (currentTextValue.text.isNotEmpty()) {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("AncientKeys text", currentTextValue.text))
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Copied text to clipboard")
                                    }
                                }
                            },
                            contentDescription = "Copy text",
                            size = 32.dp
                        ) {
                            Text("📋", fontSize = 14.sp)
                        }

                        AncientKeysIconButton(
                            onClick = {
                                if (currentTextValue.text.isNotEmpty()) {
                                    try {
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, currentTextValue.text)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Share ancient text"))
                                    } catch (_: Exception) {
                                    }
                                }
                            },
                            contentDescription = "Share text",
                            size = 32.dp
                        ) {
                            Text("📤", fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // 4. QUICK ACTION PILLS (Translate, Transliterate, Save)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Translate Action Pill
                CanvasActionButton(
                    icon = "✨",
                    label = "Translate",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        showTranslateDialog = true
                    }
                )

                // Transliterate Action Pill
                CanvasActionButton(
                    icon = "Aa",
                    label = "Transliterate",
                    modifier = Modifier.weight(1.1f),
                    onClick = {
                        if (currentTextValue.text.isNotBlank()) {
                            showTransliterateDialog = true
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Type ancient glyphs first to view transliteration details")
                            }
                        }
                    }
                )

                // Save to Research Workspace Pill
                CanvasActionButton(
                    icon = "🔖",
                    label = "Save",
                    modifier = Modifier.weight(0.9f),
                    onClick = {
                        if (currentTextValue.text.isNotBlank()) {
                            val record = ResearchRecord(
                                title = "${currentScript.name} Inscription",
                                scriptId = currentScript.id,
                                originalText = currentTextValue.text,
                                academicTransliteration = "",
                                researcherTranslation = "",
                                sourceReference = "AncientKeys In-App Playground",
                                notes = "Saved from AncientKeys Interactive Canvas."
                            )
                            researchRepo.saveRecord(record)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Saved to Research Workspace 🔖")
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Cannot save empty inscription")
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // 5. VOCABULARY QUICK CANDIDATES ROW
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                suggestions.forEach { word ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.ancientKeysColors.chipBackground,
                        border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.6f)),
                        modifier = Modifier.clickable {
                            val newText = if (currentTextValue.text.isEmpty()) word else "${currentTextValue.text} $word"
                            handleTextChange(TextFieldValue(newText))
                        }
                    ) {
                        Text(
                            text = word,
                            fontFamily = scriptFont,
                            fontSize = if (isEnglish) 12.sp else 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.ancientKeysColors.keyText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // 6. LOWER INTERACTIVE AREA (Keyboard vs Learn Mode)
            // ==========================================
            if (playgroundMode == 1) {
                // LEARN MODE PANEL
                PlaygroundLearnModeView(
                    script = currentScript,
                    isEnglish = isEnglish
                )
            } else {
                // KEYBOARD VIEW
                PlaygroundKeyboardView(
                    currentScript = currentScript,
                    isEnglish = isEnglish,
                    scriptFont = scriptFont,
                    selectedCategoryIndex = selectedCategoryIndex,
                    onCategorySelect = { selectedCategoryIndex = it },
                    visibleKeys = visibleKeys,
                    onKeyInsert = { char ->
                        handleTextChange(UnicodeTextEditor.insert(currentTextValue, char))
                    },
                    onSpace = {
                        handleTextChange(UnicodeTextEditor.insertSpace(currentTextValue))
                    },
                    onBackspace = {
                        handleTextChange(UnicodeTextEditor.deleteBackward(currentTextValue))
                    },
                    onEnter = {
                        handleTextChange(UnicodeTextEditor.insertNewline(currentTextValue))
                    },
                    onGlobeClick = {
                        val scriptCycleList = listOf("english", "greek", "brahmi", "runic", "phoenician", "kharosthi", "egyptian")
                        val currentIdx = scriptCycleList.indexOf(activeScriptId).coerceAtLeast(0)
                        val nextIdx = (currentIdx + 1) % scriptCycleList.size
                        activeScriptId = scriptCycleList[nextIdx]
                        selectedCategoryIndex = 0
                    },
                    onScriptSelect = { selectedId ->
                        activeScriptId = selectedId
                        selectedCategoryIndex = 0
                    }
                )
            }
        }
    }

    // Info Dialog
    if (showInfoDialog) {
        PlaygroundScriptInfoDialog(
            script = currentScript,
            isEnglish = isEnglish,
            onDismiss = { showInfoDialog = false }
        )
    }

    // Transliteration Dialog
    if (showTransliterateDialog) {
        val transliteratedText = remember(currentTextValue.text, currentScript.id) {
            transliterateAncientText(currentTextValue.text, currentScript.id)
        }
        val romanizer = remember(currentScript.id) {
            ScriptRomanizerProvider.getRomanizer(currentScript.id)
        }
        AlertDialog(
            onDismissRequest = { showTransliterateDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (transliteratedText.isNotBlank()) {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Transliteration", transliteratedText))
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Transliteration copied 📋")
                            }
                        }
                        showTransliterateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                        contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                    )
                ) {
                    Text("Copy & Close", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showTransliterateDialog = false }) {
                    Text("Close")
                }
            },
            title = {
                Text(
                    text = "Academic Transliteration",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.ancientKeysColors.keyText
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val scheme = romanizer?.schemeName ?: "Standard Epigraphic"
                    val convention = romanizer?.academicConvention ?: "International Academic"
                    Text(
                        text = "Script: ${if (isEnglish) "English" else currentScript.name}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Standard: $scheme ($convention)",
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                    HorizontalDivider()
                    Text(
                        text = "Original Text:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                    Text(
                        text = currentTextValue.text.ifBlank { "*(Canvas is empty)*" },
                        fontFamily = scriptFont,
                        fontSize = 18.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Latin Transliteration:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                    Text(
                        text = transliteratedText.ifBlank { "*(None)*" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            containerColor = MaterialTheme.ancientKeysColors.cardBackground,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Translation Educational Dialog
    if (showTranslateDialog) {
        AlertDialog(
            onDismissRequest = { showTranslateDialog = false },
            confirmButton = {
                Button(
                    onClick = { showTranslateDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                        contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                    )
                ) {
                    Text("Understood", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Historical Epigraphy & Translation",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.ancientKeysColors.keyText
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "AncientKeys provides authentic Unicode character input and deterministic transliteration. Unlike modern languages, ancient epigraphic texts require morphological and contextual translation by human scholars.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.ancientKeysColors.keyText.copy(alpha = 0.9f)
                    )
                    HorizontalDivider()
                    Text(
                        text = "• Transliteration converts characters (e.g. 𑀅𑀰𑁄𑀓 ➔ aśoka).\n• Translation converts meaning (e.g. 𓋹𓍑𓋴 ➔ 'Life, Prosperity, Health').\n• To record human scholarly translations, use the Save (🔖) action to store records in the Inscription Research Workspace.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                }
            },
            containerColor = MaterialTheme.ancientKeysColors.cardBackground,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ==========================================
// SUBCOMPONENTS
// ==========================================

@Composable
private fun PlaygroundTopBar(
    title: String,
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AncientKeysIconButton(
                    onClick = onBackClick,
                    contentDescription = "Navigate Back to Home",
                    size = 40.dp
                ) {
                    Text(
                        text = "‹",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.ancientKeysColors.keyText
                )
            }

            AncientKeysIconButton(
                onClick = onInfoClick,
                contentDescription = "Script Information",
                size = 36.dp
            ) {
                Text(
                    text = "ℹ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.ancientKeysColors.chipText
                )
            }
        }
    }
}

@Composable
private fun ScriptSelectionPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.ancientKeysColors.chipSelectedBackground else MaterialTheme.ancientKeysColors.chipBackground,
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.7f)
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.ancientKeysColors.chipSelectedText else MaterialTheme.ancientKeysColors.chipText,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun CanvasActionButton(
    icon: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.ancientKeysColors.chipBackground,
        border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.7f)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.ancientKeysColors.keyText
            )
        }
    }
}

@Composable
private fun PlaygroundKeyboardView(
    currentScript: HistoricalScript,
    isEnglish: Boolean,
    scriptFont: androidx.compose.ui.text.font.FontFamily?,
    selectedCategoryIndex: Int,
    onCategorySelect: (Int) -> Unit,
    visibleKeys: List<ScriptKey>,
    onKeyInsert: (String) -> Unit,
    onSpace: () -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onGlobeClick: () -> Unit,
    onScriptSelect: (String) -> Unit
) {
    var isNumericMode by rememberSaveable { mutableStateOf(false) }
    var showScriptDropdown by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Category Pills (if script has categories and not in numeric mode)
        if (!isNumericMode && !isEnglish && currentScript.categories.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                currentScript.categories.forEachIndexed { idx, catName ->
                    val isCatSelected = selectedCategoryIndex == idx
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCatSelected) MaterialTheme.ancientKeysColors.keyPrimaryActionBackground else MaterialTheme.ancientKeysColors.chipBackground,
                        border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.6f)),
                        modifier = Modifier.clickable { onCategorySelect(idx) }
                    ) {
                        Text(
                            text = catName,
                            fontSize = 10.sp,
                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCatSelected) MaterialTheme.ancientKeysColors.keyPrimaryActionText else MaterialTheme.ancientKeysColors.chipText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Keys Grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (isNumericMode) {
                // Functional Numeric & Symbols Keyboard
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        EnglishKeyboardData.NUMBERS_ROW,
                        EnglishKeyboardData.SYMBOLS_ROW_1,
                        EnglishKeyboardData.SYMBOLS_ROW_2
                    ).forEach { rowKeys ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            rowKeys.forEach { k ->
                                PlaygroundKeyCard(
                                    label = k,
                                    subLabel = "",
                                    font = null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    onClick = { onKeyInsert(k) }
                                )
                            }
                        }
                    }
                }
            } else if (isEnglish) {
                // English keyboard grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        EnglishKeyboardData.QWERTY_ROW_1_LOWER,
                        EnglishKeyboardData.QWERTY_ROW_2_LOWER,
                        EnglishKeyboardData.QWERTY_ROW_3_LOWER
                    ).forEach { rowKeys ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            rowKeys.forEach { k ->
                                PlaygroundKeyCard(
                                    label = k,
                                    subLabel = "",
                                    font = null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    onClick = { onKeyInsert(k) }
                                )
                            }
                        }
                    }
                }
            } else {
                val layoutDirection = if (currentScript.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 54.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(visibleKeys, key = { it.unicodeCodePoint + it.character + it.category }) { scriptKey ->
                            val glyph = UnicodeMetadataHelper.getDisplayGlyph(scriptKey)
                            PlaygroundKeyCard(
                                label = glyph,
                                subLabel = scriptKey.phoneticHint,
                                font = scriptFont,
                                modifier = Modifier.height(48.dp),
                                onClick = { onKeyInsert(scriptKey.character) }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Control Row matching Screen 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Numbers toggle
            Surface(
                shape = AncientKeysDesignTokens.Shapes.keyShape,
                color = if (isNumericMode) MaterialTheme.ancientKeysColors.keyPrimaryActionBackground else MaterialTheme.ancientKeysColors.keyActionBackground,
                border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(42.dp)
                    .clickable { isNumericMode = !isNumericMode }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isNumericMode) (if (isEnglish) "ABC" else "🏛️") else "123",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isNumericMode) MaterialTheme.ancientKeysColors.keyPrimaryActionText else MaterialTheme.ancientKeysColors.keyText
                    )
                }
            }

            // Globe / language toggle
            Surface(
                shape = AncientKeysDesignTokens.Shapes.keyShape,
                color = MaterialTheme.ancientKeysColors.keyActionBackground,
                border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(42.dp)
                    .clickable { onGlobeClick() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🌐", fontSize = 14.sp)
                }
            }

            // Script Name Badge with interactive DropdownMenu
            Box {
                Surface(
                    shape = AncientKeysDesignTokens.Shapes.keyShape,
                    color = MaterialTheme.ancientKeysColors.chipBackground,
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(min = 68.dp)
                        .clickable { showScriptDropdown = true }
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                        Text(
                            text = if (isEnglish) "English ▾" else "${currentScript.name.split(" ").first()} ▾",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.ancientKeysColors.keyText
                        )
                    }
                }

                DropdownMenu(
                    expanded = showScriptDropdown,
                    onDismissRequest = { showScriptDropdown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("English (Latin QWERTY)", fontSize = 13.sp) },
                        onClick = {
                            onScriptSelect("english")
                            showScriptDropdown = false
                        }
                    )
                    HorizontalDivider()
                    ScriptRegistry.allScripts.forEach { script ->
                        DropdownMenuItem(
                            text = { Text(script.name, fontSize = 13.sp) },
                            onClick = {
                                onScriptSelect(script.id)
                                showScriptDropdown = false
                            }
                        )
                    }
                }
            }

            // Spacebar
            Surface(
                shape = AncientKeysDesignTokens.Shapes.keyShape,
                color = MaterialTheme.ancientKeysColors.keyBackground,
                border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.6f)),
                shadowElevation = AncientKeysDesignTokens.Elevation.keyElevation,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(onClick = onSpace)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("space", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.chipText)
                }
            }

            // Backspace
            Surface(
                shape = AncientKeysDesignTokens.Shapes.keyShape,
                color = MaterialTheme.ancientKeysColors.keyActionBackground,
                border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(44.dp)
                    .clickable(onClick = onBackspace)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("⌫", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.ancientKeysColors.keyText)
                }
            }

            // Enter Key (Sage Green highlight matching Screen 2)
            Surface(
                shape = AncientKeysDesignTokens.Shapes.keyShape,
                color = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(min = 52.dp)
                    .clickable { onEnter() }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = "Enter ↵",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                    )
                }
            }
        }
    }
}

/**
 * Deterministically maps ancient characters to their scholarly Latin transliteration
 * using the script's authentic Unicode key metadata registry.
 */
fun transliterateAncientText(text: String, scriptId: String): String {
    if (text.isEmpty()) return ""
    if (scriptId.equals("english", ignoreCase = true)) return text
    val script = ScriptRegistry.getScriptById(scriptId) ?: return text
    val keyMap = script.keys.associate { it.character to it.transliteration.ifBlank { it.phoneticHint } }

    // Normalize text (NFD decomposes composite accents e.g. 'ό' -> 'ο' + combining acute)
    val normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)

    val sb = StringBuilder()
    var i = 0
    while (i < normalized.length) {
        val cp = normalized.codePointAt(i)
        val charStr = String(Character.toChars(cp))
        val translit = keyMap[charStr] ?: if (Character.getType(cp) == Character.NON_SPACING_MARK.toInt()) "" else charStr
        sb.append(translit)
        i += Character.charCount(cp)
    }
    return sb.toString()
}

@Composable
private fun PlaygroundKeyCard(
    label: String,
    subLabel: String,
    font: androidx.compose.ui.text.font.FontFamily?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp, max = 54.dp)
            .clickable(onClick = onClick),
        shape = AncientKeysDesignTokens.Shapes.keyShape,
        border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.6f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.ancientKeysColors.keyBackground,
            contentColor = MaterialTheme.ancientKeysColors.keyText
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AncientKeysDesignTokens.Elevation.flat)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 2.dp, horizontal = 1.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val annotatedLabel = remember(label, font) {
                HistoricalFontProvider.buildDisplayGlyphAnnotatedString(label, font)
            }
            Text(
                text = annotatedLabel,
                fontFamily = if (label.contains('◌')) null else font,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.ancientKeysColors.keyText,
                textAlign = TextAlign.Center
            )
            if (subLabel.isNotEmpty()) {
                Text(
                    text = subLabel,
                    fontSize = 7.sp,
                    color = MaterialTheme.ancientKeysColors.glyphAccent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PlaygroundLearnModeView(
    script: HistoricalScript,
    isEnglish: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AncientKeysCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            containerColor = MaterialTheme.ancientKeysColors.cardBackground,
            borderColor = MaterialTheme.ancientKeysColors.cardBorder
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "📖 ${if (isEnglish) "English QWERTY" else script.name} Overview",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.ancientKeysColors.keyText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isEnglish) "Standard Latin orthography used for modern transliteration and system fallback." else script.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.ancientKeysColors.chipText,
                    lineHeight = 18.sp
                )

                if (!isEnglish) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Period: ${script.historicalPeriod}",
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Text(
                        text = "• Region: ${script.region}",
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Text(
                        text = "• Writing Direction: ${if (script.isRtl) "Right-to-Left (RTL)" else "Left-to-Right (LTR)"}",
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Text(
                        text = "• Total Characters: ${script.keys.size} codepoints",
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                }
            }
        }

        AncientKeysCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            containerColor = MaterialTheme.ancientKeysColors.cardBackground,
            borderColor = MaterialTheme.ancientKeysColors.cardBorder
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "✍️ Writing Sample",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.ancientKeysColors.keyText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isEnglish) "The quick brown fox jumps over the lazy dog." else script.sampleText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PlaygroundScriptInfoDialog(
    script: HistoricalScript,
    isEnglish: Boolean,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEnglish) "English QWERTY" else script.name,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isEnglish) "Standard Latin keyboard layout for English typing." else script.description,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
                if (!isEnglish) {
                    Text("• Historical Era: ${script.historicalPeriod}", fontSize = 11.sp)
                    Text("• Geographic Origin: ${script.region}", fontSize = 11.sp)
                    Text("• Direction: ${if (script.isRtl) "Right-to-Left" else "Left-to-Right"}", fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close")
            }
        }
    )
}
