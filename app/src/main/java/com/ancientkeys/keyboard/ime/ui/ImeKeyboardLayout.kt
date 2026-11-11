package com.ancientkeys.keyboard.ime.ui

import android.view.HapticFeedbackConstants
import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import com.ancientkeys.keyboard.ime.data.LongPressAlternativesData
import com.ancientkeys.keyboard.ime.phonetic.EasyPhoneticProvider
import com.ancientkeys.keyboard.ime.phonetic.PhoneticCandidate
import com.ancientkeys.keyboard.ime.romanization.BrahmiRomanizer
import com.ancientkeys.keyboard.ime.romanization.ScriptRomanizerProvider
import com.ancientkeys.keyboard.util.HistoricalFontProvider
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.ime.ImeInputHelper
import com.ancientkeys.keyboard.ime.data.EnglishKeyboardData
import com.ancientkeys.keyboard.ime.data.KeyboardMode
import com.ancientkeys.keyboard.ime.data.ShiftState
import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptKey
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.util.UnicodeMetadataHelper

@Composable
fun ImeKeyboardLayout(
    editorInfo: EditorInfo?,
    initialScriptId: String = "egyptian",
    onKeyClick: (ScriptKey) -> Unit,
    onSpaceClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    onEnterClick: () -> Unit,
    onSwitchKeyboardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val userPrefs = remember { UserPreferencesRepository.getInstance(context) }
    val userPrefsHeight = remember { userPrefs.getKeyboardHeightDp() }

    // Determine initial preferred script
    val preferredScriptId = remember {
        val recents = userPrefs.getRecentScriptIds()
        recents.firstOrNull { ScriptRegistry.getScriptById(it) != null } ?: initialScriptId
    }

    var currentScriptId by rememberSaveable { mutableStateOf(preferredScriptId) }
    var keyboardMode by rememberSaveable {
        val initialMode = if (EasyPhoneticProvider.isPhoneticSupported(preferredScriptId))
            KeyboardMode.EASY_PHONETIC else KeyboardMode.HISTORICAL
        mutableStateOf(initialMode)
    }

    val isSensitive = remember(editorInfo?.inputType) {
        ImeInputHelper.isSensitiveField(editorInfo)
    }

    val baseAdaptiveHeight = remember(configuration.screenHeightDp, isLandscape, keyboardMode, userPrefsHeight) {
        ImeDimensionsHelper.computeAdaptiveKeyboardHeight(
            screenHeightDp = configuration.screenHeightDp,
            isLandscape = isLandscape,
            keyboardMode = keyboardMode,
            userPreferenceHeightDp = userPrefsHeight
        )
    }

    val bottomNavInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val totalSurfaceHeight = baseAdaptiveHeight.dp + bottomNavInset

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(totalSurfaceHeight),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomNavInset)
        ) {
            if (isSensitive) {
                // Protected View for Password & Sensitive Fields
                SensitiveFieldFallbackView(
                    onSwitchKeyboardClick = onSwitchKeyboardClick
                )
            } else {
                // Multi-Mode Professional Keyboard (Historical, English QWERTY, Numbers & Symbols)
                MultiModeKeyboardContent(
                    userPrefs = userPrefs,
                    currentScriptId = currentScriptId,
                    onScriptChange = { currentScriptId = it },
                    keyboardMode = keyboardMode,
                    onKeyboardModeChange = { keyboardMode = it },
                    isLandscape = isLandscape,
                    onKeyClick = onKeyClick,
                    onSpaceClick = onSpaceClick,
                    onBackspaceClick = onBackspaceClick,
                    onEnterClick = onEnterClick,
                    onSwitchKeyboardClick = onSwitchKeyboardClick
                )
            }
        }
    }
}

@Composable
private fun SensitiveFieldFallbackView(
    onSwitchKeyboardClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔒 Password / Sensitive Field Detected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "AncientKeys only supports historical scripts and English text, and is disabled in sensitive fields for your security. Please switch to your standard device keyboard to enter credentials.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onSwitchKeyboardClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("🌐 Switch to Standard Keyboard", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MultiModeKeyboardContent(
    userPrefs: UserPreferencesRepository,
    currentScriptId: String,
    onScriptChange: (String) -> Unit,
    keyboardMode: KeyboardMode,
    onKeyboardModeChange: (KeyboardMode) -> Unit,
    isLandscape: Boolean,
    onKeyClick: (ScriptKey) -> Unit,
    onSpaceClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    onEnterClick: () -> Unit,
    onSwitchKeyboardClick: () -> Unit
) {
    val view = LocalView.current
    val hapticsEnabled = remember { userPrefs.isHapticsEnabled() }

    fun triggerHaptic() {
        if (hapticsEnabled) {
            try {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            } catch (_: Exception) {
            }
        }
    }

    var shiftState by rememberSaveable { mutableStateOf(ShiftState.OFF) }
    var selectedCategoryIndex by rememberSaveable(currentScriptId) { mutableIntStateOf(0) }
    var showFavoritesOnly by rememberSaveable(currentScriptId) { mutableStateOf(false) }
    var isScriptMenuExpanded by remember { mutableStateOf(false) }
    var romanizedBuffer by rememberSaveable { mutableStateOf("") }
    var phoneticBuffer by rememberSaveable { mutableStateOf("") }
    var isScholarMode by rememberSaveable { mutableStateOf(userPrefs.isScholarMode()) }
    var longPressPopupKey by remember { mutableStateOf<String?>(null) }

    val currentScript = remember(currentScriptId) {
        val script = ScriptRegistry.getScriptById(currentScriptId) ?: ScriptRegistry.allScripts.first()
        userPrefs.recordScriptAccess(script.id)
        script
    }

    val activeRomanizer = remember(currentScript.id) {
        ScriptRomanizerProvider.getRomanizer(currentScript.id)
    }

    val activePhoneticEngine = remember(currentScript.id) {
        EasyPhoneticProvider.getEngine(currentScript.id)
    }

    val favoriteKeysSet = remember(currentScript.id) {
        userPrefs.getFavoriteKeysForScript(currentScript.id)
    }

    // Historical script visible keys
    val visibleKeys = remember(currentScript, selectedCategoryIndex, showFavoritesOnly, favoriteKeysSet) {
        if (showFavoritesOnly) {
            val favs = currentScript.keys.filter { userPrefs.isFavorite(currentScript.id, it.unicodeCodePoint) }
            if (favs.isNotEmpty()) favs else currentScript.keys
        } else if (currentScript.categories.isNotEmpty() && selectedCategoryIndex < currentScript.categories.size) {
            val cat = currentScript.categories[selectedCategoryIndex]
            currentScript.keys.filter { it.category == cat }
        } else {
            currentScript.keys
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 4.dp)
    ) {
        // --- Top Utility Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Script Selector Dropdown trigger
            Box {
                Button(
                    onClick = {
                        triggerHaptic()
                        isScriptMenuExpanded = true
                    },
                    modifier = Modifier
                        .height(if (isLandscape) 28.dp else 32.dp)
                        .widthIn(max = 120.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (keyboardMode == KeyboardMode.HISTORICAL)
                            MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (keyboardMode == KeyboardMode.HISTORICAL)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = "${ImeDimensionsHelper.getCompactScriptName(currentScript)} ▾",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                DropdownMenu(
                    expanded = isScriptMenuExpanded,
                    onDismissRequest = { isScriptMenuExpanded = false }
                ) {
                    ScriptRegistry.allScripts.forEach { script ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        script.name,
                                        fontWeight = if (script.id == currentScript.id) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (script.isRtl) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("(RTL)", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            },
                            onClick = {
                                onScriptChange(script.id)
                                onKeyboardModeChange(KeyboardMode.HISTORICAL)
                                selectedCategoryIndex = 0
                                showFavoritesOnly = false
                                isScriptMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Mode Selector Pills: [Easy] [ABC] [Native] [?123] [Rom]
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Easy Phonetic Mode Pill (for supported scripts: Brahmi, Kharosthi, Greek)
                if (activePhoneticEngine != null) {
                    FilterChip(
                        selected = keyboardMode == KeyboardMode.EASY_PHONETIC,
                        onClick = {
                            triggerHaptic()
                            onKeyboardModeChange(KeyboardMode.EASY_PHONETIC)
                        },
                        label = { Text("Easy", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.height(28.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }

                // English QWERTY Mode Pill
                FilterChip(
                    selected = keyboardMode == KeyboardMode.ENGLISH_QWERTY,
                    onClick = {
                        triggerHaptic()
                        onKeyboardModeChange(KeyboardMode.ENGLISH_QWERTY)
                    },
                    label = { Text("ABC", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.height(28.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                // Native Historical Script Mode Pill
                FilterChip(
                    selected = keyboardMode == KeyboardMode.HISTORICAL,
                    onClick = {
                        triggerHaptic()
                        onKeyboardModeChange(KeyboardMode.HISTORICAL)
                    },
                    label = { Text("Native", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.height(28.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                // Numbers / Symbols Mode Pill
                FilterChip(
                    selected = keyboardMode == KeyboardMode.NUMBERS_SYMBOLS,
                    onClick = {
                        triggerHaptic()
                        onKeyboardModeChange(KeyboardMode.NUMBERS_SYMBOLS)
                    },
                    label = { Text("?123", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.height(28.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                // Scholarly Romanized Input Mode Pill (for supported scripts: Brahmi, Kharosthi, Greek)
                if (activeRomanizer != null) {
                    FilterChip(
                        selected = keyboardMode == KeyboardMode.ROMANIZED,
                        onClick = {
                            triggerHaptic()
                            onKeyboardModeChange(KeyboardMode.ROMANIZED)
                        },
                        label = { Text("Rom", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.height(28.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Switch to External System IME Button
            OutlinedButton(
                onClick = {
                    triggerHaptic()
                    onSwitchKeyboardClick()
                },
                modifier = Modifier
                    .height(if (isLandscape) 28.dp else 30.dp)
                    .widthIn(min = 36.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("🌐 Switch", fontSize = 10.sp, fontWeight = FontWeight.Medium, maxLines = 1)
            }
        }

        // --- Category Bar (Visible in Historical Mode) ---
        if (keyboardMode == KeyboardMode.HISTORICAL) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Scholar vs Beginner Mode Toggle
                FilterChip(
                    selected = isScholarMode,
                    onClick = {
                        triggerHaptic()
                        isScholarMode = !isScholarMode
                        userPrefs.setScholarMode(isScholarMode)
                    },
                    label = { Text(if (isScholarMode) "🎓 Scholar" else "🌱 Beginner", fontSize = 10.sp) },
                    modifier = Modifier.height(28.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )

                if (favoriteKeysSet.isNotEmpty()) {
                    FilterChip(
                        selected = showFavoritesOnly,
                        onClick = {
                            triggerHaptic()
                            showFavoritesOnly = !showFavoritesOnly
                        },
                        label = { Text("★ Favs (${favoriteKeysSet.size})", fontSize = 10.sp) },
                        modifier = Modifier.height(28.dp)
                    )
                }

                currentScript.categories.forEachIndexed { index, catName ->
                    FilterChip(
                        selected = !showFavoritesOnly && selectedCategoryIndex == index,
                        onClick = {
                            triggerHaptic()
                            showFavoritesOnly = false
                            selectedCategoryIndex = index
                        },
                        label = { Text(catName, fontSize = 10.sp) },
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
        }

        // --- Keyboard Body (Changes by KeyboardMode) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (keyboardMode) {
                KeyboardMode.HISTORICAL -> {
                    val layoutDirection = if (currentScript.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
                    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = if (isLandscape) 52.dp else 58.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 2.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(visibleKeys, key = { it.unicodeCodePoint + it.character + it.category }) { scriptKey ->
                                ImeKeyItem(
                                    key = scriptKey,
                                    scriptId = currentScript.id,
                                    isScholarMode = isScholarMode,
                                    isLandscape = isLandscape,
                                    onClick = {
                                        triggerHaptic()
                                        onKeyClick(scriptKey)
                                    }
                                )
                            }
                        }
                    }
                }

                KeyboardMode.ENGLISH_QWERTY -> {
                    EnglishQwertyKeyboard(
                        shiftState = shiftState,
                        isLandscape = isLandscape,
                        onShiftClick = {
                            triggerHaptic()
                            shiftState = EnglishKeyboardData.nextShiftState(shiftState)
                        },
                        onLetterClick = { letter ->
                            triggerHaptic()
                            onKeyClick(ScriptKey(character = letter, unicodeCodePoint = "U+%04X".format(letter[0].code)))
                            if (shiftState == ShiftState.ON_ONCE) {
                                shiftState = ShiftState.OFF
                            }
                        },
                        onLetterLongClick = { letter ->
                            triggerHaptic()
                            longPressPopupKey = letter
                        },
                        onBackspaceClick = {
                            triggerHaptic()
                            onBackspaceClick()
                        }
                    )
                }

                KeyboardMode.NUMBERS_SYMBOLS -> {
                    NumbersSymbolsKeyboard(
                        isLandscape = isLandscape,
                        onSymbolClick = { symbol ->
                            triggerHaptic()
                            onKeyClick(ScriptKey(character = symbol, unicodeCodePoint = "U+%04X".format(symbol[0].code)))
                        },
                        onBackspaceClick = {
                            triggerHaptic()
                            onBackspaceClick()
                        }
                    )
                }

                KeyboardMode.EASY_PHONETIC -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 2.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // --- Dedicated Context-Sensitive Suggestion Strip for Easy Phonetic ---
                        val primaryConverted = if (phoneticBuffer.isNotEmpty() && activePhoneticEngine != null) {
                            activePhoneticEngine.convert(phoneticBuffer)
                        } else {
                            ""
                        }
                        val candidates = if (phoneticBuffer.isNotEmpty() && activePhoneticEngine != null) {
                            remember(phoneticBuffer, activePhoneticEngine) {
                                activePhoneticEngine.getCandidates(phoneticBuffer)
                            }
                        } else {
                            emptyList()
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val context = LocalContext.current
                                    val scriptFont = remember(currentScript.id) { HistoricalFontProvider.getFontFamily(context, currentScript.id) }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = if (phoneticBuffer.isEmpty()) "Type English (e.g. 'ka', 'kaa', 'ki')..." else phoneticBuffer,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (phoneticBuffer.isEmpty()) FontWeight.Normal else FontWeight.Bold,
                                            color = if (phoneticBuffer.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (primaryConverted.isNotEmpty()) {
                                            Text(
                                                text = " ➔ ",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = primaryConverted,
                                                fontFamily = scriptFont,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    if (phoneticBuffer.isNotEmpty()) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "✕",
                                                modifier = Modifier
                                                    .clickable {
                                                        triggerHaptic()
                                                        phoneticBuffer = ""
                                                    }
                                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            Button(
                                                onClick = {
                                                    triggerHaptic()
                                                    if (primaryConverted.isNotEmpty()) {
                                                        onKeyClick(ScriptKey(character = primaryConverted, unicodeCodePoint = currentScript.id.uppercase()))
                                                    }
                                                    phoneticBuffer = ""
                                                },
                                                modifier = Modifier.height(26.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                            ) {
                                                Text("Commit ↵", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }

                                if (candidates.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState())
                                            .padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        val context = LocalContext.current
                                        val scriptFont = remember(currentScript.id) { HistoricalFontProvider.getFontFamily(context, currentScript.id) }
                                        candidates.forEach { cand ->
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (cand.isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                                                modifier = Modifier.clickable {
                                                    triggerHaptic()
                                                    onKeyClick(ScriptKey(character = cand.scriptResult, unicodeCodePoint = currentScript.id.uppercase()))
                                                    phoneticBuffer = ""
                                                }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = cand.scriptResult,
                                                        fontFamily = scriptFont,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (cand.isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = cand.displayLabel,
                                                        fontSize = 10.sp,
                                                        color = (if (cand.isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer).copy(alpha = 0.8f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // QWERTY keyboard for Easy Phonetic
                        Box(modifier = Modifier.weight(1f)) {
                            EnglishQwertyKeyboard(
                                shiftState = shiftState,
                                isLandscape = isLandscape,
                                onShiftClick = {
                                    triggerHaptic()
                                    shiftState = EnglishKeyboardData.nextShiftState(shiftState)
                                },
                                onLetterClick = { letter ->
                                    triggerHaptic()
                                    phoneticBuffer += if (shiftState != ShiftState.OFF) letter.uppercase() else letter.lowercase()
                                    if (shiftState == ShiftState.ON_ONCE) {
                                        shiftState = ShiftState.OFF
                                    }
                                },
                                onLetterLongClick = { letter ->
                                    triggerHaptic()
                                    longPressPopupKey = letter
                                },
                                onBackspaceClick = {
                                    triggerHaptic()
                                    if (phoneticBuffer.isNotEmpty()) {
                                        phoneticBuffer = phoneticBuffer.dropLast(1)
                                    } else {
                                        onBackspaceClick()
                                    }
                                }
                            )
                        }
                    }
                }

                KeyboardMode.ROMANIZED -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 2.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Composition & Transliteration Preview Header
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val transliterated = if (romanizedBuffer.isNotEmpty() && activeRomanizer != null) {
                                        activeRomanizer.transliterateToken(romanizedBuffer)
                                    } else {
                                        ""
                                    }
                                    val context = LocalContext.current
                                    val scriptFont = remember(currentScript.id) { HistoricalFontProvider.getFontFamily(context, currentScript.id) }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = if (romanizedBuffer.isEmpty()) "Type Latin (${activeRomanizer?.schemeName ?: "Romanized"})..." else romanizedBuffer,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (romanizedBuffer.isEmpty()) FontWeight.Normal else FontWeight.Bold,
                                            color = if (romanizedBuffer.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (transliterated.isNotEmpty()) {
                                            Text(
                                                text = " ➔ ",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = transliterated,
                                                fontFamily = scriptFont,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    if (transliterated.isNotEmpty()) {
                                        Button(
                                            onClick = {
                                                triggerHaptic()
                                                onKeyClick(ScriptKey(character = transliterated, unicodeCodePoint = currentScript.id.uppercase()))
                                                romanizedBuffer = ""
                                            },
                                            modifier = Modifier.height(28.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Text("Commit ↵", fontSize = 11.sp)
                                        }
                                    }
                                }

                                // Matra / Composition Options bar
                                if (romanizedBuffer.isNotEmpty() && activeRomanizer != null) {
                                    val previews = remember(romanizedBuffer, activeRomanizer) { activeRomanizer.getCompositionPreviews(romanizedBuffer) }
                                    if (previews.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState())
                                                .padding(top = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            val context = LocalContext.current
                                            val scriptFont = remember(currentScript.id) { HistoricalFontProvider.getFontFamily(context, currentScript.id) }
                                            previews.forEach { preview ->
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    modifier = Modifier.clickable {
                                                        triggerHaptic()
                                                        onKeyClick(ScriptKey(character = preview.scriptResult, unicodeCodePoint = currentScript.id.uppercase()))
                                                        romanizedBuffer = ""
                                                    }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = preview.scriptResult,
                                                            fontFamily = scriptFont,
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                                        )
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text(
                                                            text = preview.latinInput,
                                                            fontSize = 10.sp,
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // QWERTY keyboard for romanized typing
                        Box(modifier = Modifier.weight(1f)) {
                            EnglishQwertyKeyboard(
                                shiftState = shiftState,
                                isLandscape = isLandscape,
                                onShiftClick = {
                                    triggerHaptic()
                                    shiftState = EnglishKeyboardData.nextShiftState(shiftState)
                                },
                                onLetterClick = { letter ->
                                    triggerHaptic()
                                    romanizedBuffer += letter.lowercase()
                                    if (shiftState == ShiftState.ON_ONCE) {
                                        shiftState = ShiftState.OFF
                                    }
                                },
                                onLetterLongClick = { letter ->
                                    triggerHaptic()
                                    longPressPopupKey = letter
                                },
                                onBackspaceClick = {
                                    triggerHaptic()
                                    if (romanizedBuffer.isNotEmpty()) {
                                        romanizedBuffer = romanizedBuffer.dropLast(1)
                                    } else {
                                        onBackspaceClick()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Long Press Popup Overlay
            if (longPressPopupKey != null) {
                val alts = LongPressAlternativesData.getAlternatives(longPressPopupKey!!)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Alternatives for \"$longPressPopupKey\"",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "✕ Dismiss",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .clickable { longPressPopupKey = null }
                                    .padding(4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            alts.forEach { alt ->
                                Button(
                                    onClick = {
                                        triggerHaptic()
                                        if (keyboardMode == KeyboardMode.ROMANIZED) {
                                            romanizedBuffer += alt
                                        } else {
                                            onKeyClick(ScriptKey(character = alt, unicodeCodePoint = "U+%04X".format(alt[0].code)))
                                        }
                                        longPressPopupKey = null
                                    },
                                    modifier = Modifier.height(44.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = alt,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        val bottomRowHeight = ImeDimensionsHelper.computeBottomRowHeight(isLandscape).dp

        // --- Bottom Control Row ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomRowHeight)
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quick Mode Toggle / Mode Indicator
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(min = 38.dp)
                    .clickable {
                        triggerHaptic()
                        onKeyboardModeChange(
                            when (keyboardMode) {
                                KeyboardMode.HISTORICAL -> if (activePhoneticEngine != null) KeyboardMode.EASY_PHONETIC else KeyboardMode.ENGLISH_QWERTY
                                KeyboardMode.EASY_PHONETIC -> KeyboardMode.ENGLISH_QWERTY
                                KeyboardMode.ENGLISH_QWERTY -> KeyboardMode.NUMBERS_SYMBOLS
                                KeyboardMode.NUMBERS_SYMBOLS -> if (activeRomanizer != null) KeyboardMode.ROMANIZED else KeyboardMode.HISTORICAL
                                KeyboardMode.ROMANIZED -> KeyboardMode.HISTORICAL
                            }
                        )
                    }
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (keyboardMode) {
                            KeyboardMode.HISTORICAL -> if (currentScript.isRtl) "RTL" else "LTR"
                            KeyboardMode.EASY_PHONETIC -> "EASY"
                            KeyboardMode.ENGLISH_QWERTY -> "ABC"
                            KeyboardMode.NUMBERS_SYMBOLS -> "?123"
                            KeyboardMode.ROMANIZED -> "ROM"
                        },
                        fontSize = if (isLandscape) 10.sp else 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }

            // Comma (for English, Numbers, or Phonetic mode)
            if (keyboardMode != KeyboardMode.HISTORICAL) {
                Button(
                    onClick = {
                        triggerHaptic()
                        onKeyClick(ScriptKey(character = ",", unicodeCodePoint = "U+002C"))
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(min = 34.dp, max = 40.dp)
                        .semantics { contentDescription = "Comma" },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(",", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Space Bar
            Button(
                onClick = {
                    triggerHaptic()
                    if (keyboardMode == KeyboardMode.EASY_PHONETIC && phoneticBuffer.isNotEmpty() && activePhoneticEngine != null) {
                        val converted = activePhoneticEngine.convert(phoneticBuffer)
                        if (converted.isNotEmpty()) {
                            onKeyClick(ScriptKey(character = converted, unicodeCodePoint = currentScript.id.uppercase()))
                        }
                        phoneticBuffer = ""
                    } else if (keyboardMode == KeyboardMode.ROMANIZED && romanizedBuffer.isNotEmpty() && activeRomanizer != null) {
                        val transliterated = activeRomanizer.transliterateToken(romanizedBuffer)
                        if (transliterated.isNotEmpty()) {
                            onKeyClick(ScriptKey(character = transliterated, unicodeCodePoint = currentScript.id.uppercase()))
                        }
                        romanizedBuffer = ""
                    }
                    onSpaceClick()
                },
                modifier = Modifier
                    .weight(2.4f)
                    .fillMaxHeight()
                    .widthIn(min = ImeDimensionsHelper.MIN_SPACE_KEY_WIDTH_DP.dp)
                    .semantics { contentDescription = "Space" },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = when (keyboardMode) {
                        KeyboardMode.HISTORICAL -> "${ImeDimensionsHelper.getCompactScriptName(currentScript)} Space"
                        KeyboardMode.EASY_PHONETIC -> "${ImeDimensionsHelper.getCompactScriptName(currentScript)} Space"
                        KeyboardMode.ENGLISH_QWERTY -> "English"
                        KeyboardMode.NUMBERS_SYMBOLS -> "Space"
                        KeyboardMode.ROMANIZED -> "Space"
                    },
                    fontSize = if (isLandscape) 11.sp else 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Period (for English, Numbers, or Phonetic mode)
            if (keyboardMode != KeyboardMode.HISTORICAL) {
                Button(
                    onClick = {
                        triggerHaptic()
                        onKeyClick(ScriptKey(character = ".", unicodeCodePoint = "U+002E"))
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(min = 34.dp, max = 40.dp)
                        .semantics { contentDescription = "Period" },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(".", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Backspace (⌫)
            Button(
                onClick = {
                    triggerHaptic()
                    if (keyboardMode == KeyboardMode.EASY_PHONETIC && phoneticBuffer.isNotEmpty()) {
                        phoneticBuffer = phoneticBuffer.dropLast(1)
                    } else if (keyboardMode == KeyboardMode.ROMANIZED && romanizedBuffer.isNotEmpty()) {
                        romanizedBuffer = romanizedBuffer.dropLast(1)
                    } else {
                        onBackspaceClick()
                    }
                },
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight()
                    .widthIn(min = ImeDimensionsHelper.MIN_BACKSPACE_KEY_WIDTH_DP.dp)
                    .semantics { contentDescription = "Backspace" },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("⌫", fontSize = if (isLandscape) 15.sp else 18.sp, fontWeight = FontWeight.Bold)
            }

            // Enter (↵)
            Button(
                onClick = {
                    triggerHaptic()
                    if (keyboardMode == KeyboardMode.EASY_PHONETIC && phoneticBuffer.isNotEmpty() && activePhoneticEngine != null) {
                        val converted = activePhoneticEngine.convert(phoneticBuffer)
                        if (converted.isNotEmpty()) {
                            onKeyClick(ScriptKey(character = converted, unicodeCodePoint = currentScript.id.uppercase()))
                        }
                        phoneticBuffer = ""
                    } else if (keyboardMode == KeyboardMode.ROMANIZED && romanizedBuffer.isNotEmpty() && activeRomanizer != null) {
                        val transliterated = activeRomanizer.transliterateToken(romanizedBuffer)
                        if (transliterated.isNotEmpty()) {
                            onKeyClick(ScriptKey(character = transliterated, unicodeCodePoint = currentScript.id.uppercase()))
                        }
                        romanizedBuffer = ""
                    }
                    onEnterClick()
                },
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight()
                    .widthIn(min = ImeDimensionsHelper.MIN_ENTER_KEY_WIDTH_DP.dp)
                    .semantics { contentDescription = "Enter" },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Text("↵", fontSize = if (isLandscape) 15.sp else 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- English QWERTY View ---

@Composable
private fun EnglishQwertyKeyboard(
    shiftState: ShiftState,
    isLandscape: Boolean = false,
    onShiftClick: () -> Unit,
    onLetterClick: (String) -> Unit,
    onLetterLongClick: ((String) -> Unit)? = null,
    onBackspaceClick: () -> Unit
) {
    val isUpper = EnglishKeyboardData.isUppercase(shiftState)
    val row1 = if (isUpper) EnglishKeyboardData.QWERTY_ROW_1_UPPER else EnglishKeyboardData.QWERTY_ROW_1_LOWER
    val row2 = if (isUpper) EnglishKeyboardData.QWERTY_ROW_2_UPPER else EnglishKeyboardData.QWERTY_ROW_2_LOWER
    val row3 = if (isUpper) EnglishKeyboardData.QWERTY_ROW_3_UPPER else EnglishKeyboardData.QWERTY_ROW_3_LOWER

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 1.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Row 1
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            row1.forEach { char ->
                QwertyKey(
                    label = char,
                    onClick = { onLetterClick(char) },
                    onLongClick = if (onLetterLongClick != null) { { onLetterLongClick(char) } } else null,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isLandscape = isLandscape
                )
            }
        }

        // Row 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = if (isLandscape) 8.dp else 12.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            row2.forEach { char ->
                QwertyKey(
                    label = char,
                    onClick = { onLetterClick(char) },
                    onLongClick = if (onLetterLongClick != null) { { onLetterLongClick(char) } } else null,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isLandscape = isLandscape
                )
            }
        }

        // Row 3 (Shift, z-m, Backspace)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shift Key
            val shiftDesc = when (shiftState) {
                ShiftState.OFF -> "Shift off"
                ShiftState.ON_ONCE -> "Shift on, single letter"
                ShiftState.CAPS_LOCK -> "Caps lock enabled"
            }
            Button(
                onClick = onShiftClick,
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .padding(vertical = 1.dp)
                    .semantics { contentDescription = shiftDesc },
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (shiftState) {
                        ShiftState.OFF -> MaterialTheme.colorScheme.surfaceVariant
                        ShiftState.ON_ONCE -> MaterialTheme.colorScheme.primaryContainer
                        ShiftState.CAPS_LOCK -> MaterialTheme.colorScheme.primary
                    },
                    contentColor = when (shiftState) {
                        ShiftState.OFF -> MaterialTheme.colorScheme.onSurfaceVariant
                        ShiftState.ON_ONCE -> MaterialTheme.colorScheme.onPrimaryContainer
                        ShiftState.CAPS_LOCK -> MaterialTheme.colorScheme.onPrimary
                    }
                )
            ) {
                Text(
                    text = when (shiftState) {
                        ShiftState.OFF -> "⇧"
                        ShiftState.ON_ONCE -> "⬆"
                        ShiftState.CAPS_LOCK -> "⇪"
                    },
                    fontSize = if (isLandscape) 15.sp else 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            row3.forEach { char ->
                QwertyKey(
                    label = char,
                    onClick = { onLetterClick(char) },
                    onLongClick = if (onLetterLongClick != null) { { onLetterLongClick(char) } } else null,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isLandscape = isLandscape
                )
            }

            // Inline Backspace on Row 3
            Button(
                onClick = onBackspaceClick,
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .padding(vertical = 1.dp)
                    .semantics { contentDescription = "Backspace" },
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("⌫", fontSize = if (isLandscape) 14.sp else 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- Numbers & Modern Symbols View ---

@Composable
private fun NumbersSymbolsKeyboard(
    isLandscape: Boolean = false,
    onSymbolClick: (String) -> Unit,
    onBackspaceClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 1.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Numbers 1-0 Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            EnglishKeyboardData.NUMBERS_ROW.forEach { num ->
                QwertyKey(
                    label = num,
                    onClick = { onSymbolClick(num) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isAccent = true,
                    isLandscape = isLandscape
                )
            }
        }

        // Symbol Row 1
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            EnglishKeyboardData.SYMBOLS_ROW_1.forEach { sym ->
                QwertyKey(
                    label = sym,
                    onClick = { onSymbolClick(sym) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isLandscape = isLandscape
                )
            }
        }

        // Symbol Row 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            EnglishKeyboardData.SYMBOLS_ROW_2.forEach { sym ->
                QwertyKey(
                    label = sym,
                    onClick = { onSymbolClick(sym) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isLandscape = isLandscape
                )
            }
        }

        // Symbol Row 3 with Backspace
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EnglishKeyboardData.SYMBOLS_ROW_3.take(8).forEach { sym ->
                QwertyKey(
                    label = sym,
                    onClick = { onSymbolClick(sym) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isLandscape = isLandscape
                )
            }

            Button(
                onClick = onBackspaceClick,
                modifier = Modifier
                    .weight(1.8f)
                    .fillMaxHeight()
                    .padding(vertical = 1.dp),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("⌫", fontSize = if (isLandscape) 14.sp else 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- Key Button Helpers ---

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QwertyKey(
    label: String,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isAccent: Boolean = false,
    isLandscape: Boolean = false
) {
    val hasAlts = remember(label) { LongPressAlternativesData.hasAlternatives(label) }

    Card(
        modifier = modifier
            .padding(vertical = 1.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = if (hasAlts && onLongClick != null) onLongClick else null
            )
            .semantics { contentDescription = label },
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAccent)
                MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = if (isLandscape) 14.sp else 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (hasAlts) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 1.dp, end = 2.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text("·", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun ImeKeyItem(
    key: ScriptKey,
    scriptId: String,
    isScholarMode: Boolean = false,
    isLandscape: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayGlyph = UnicodeMetadataHelper.getDisplayGlyph(key)
    val accessibleDesc = "${key.label}, ${UnicodeMetadataHelper.getOfficialUnicodeName(key)}"
    val context = LocalContext.current
    val fontFamily = remember(scriptId) { HistoricalFontProvider.getFontFamily(context, scriptId) }
    val itemHeight = if (isLandscape) 42.dp else 52.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight)
            .clickable(onClick = onClick)
            .semantics { contentDescription = accessibleDesc },
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 2.dp, horizontal = 1.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = displayGlyph,
                fontFamily = fontFamily,
                fontSize = if (isLandscape) 17.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (isScholarMode) {
                Text(
                    text = key.unicodeCodePoint,
                    fontSize = if (isLandscape) 6.sp else 7.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            } else if (key.phoneticHint.isNotEmpty()) {
                Text(
                    text = key.phoneticHint,
                    fontSize = if (isLandscape) 7.sp else 8.sp,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
