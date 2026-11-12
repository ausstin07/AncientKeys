package com.ancientkeys.keyboard.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptKey
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.search.ScriptSearchEngine
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.ui.components.AncientKeysCard
import com.ancientkeys.keyboard.ui.components.AncientKeysIconButton
import com.ancientkeys.keyboard.ui.theme.AncientKeysDesignTokens
import com.ancientkeys.keyboard.ui.theme.ancientKeysColors
import com.ancientkeys.keyboard.util.HistoricalFontProvider
import com.ancientkeys.keyboard.util.UnicodeMetadataHelper
import kotlinx.coroutines.launch

/**
 * AncientKeys v1.1 Character Explorer matching Screen 3 of the visual identity specification.
 *
 * Implements:
 * - Clean screen header with back navigation and favorites counter
 * - Clearly labeled Script Selector supporting all 6 historical scripts
 * - Horizontally scrollable Category Filter Chips backed by actual script categories
 * - Responsive Unicode Glyph Grid with bundled Noto font rendering, transliterations, and selected states
 * - In-screen Character Detail Panel showing large preview, pronunciation guide, transliteration, and about notes
 * - Detailed Modal Bottom Sheet with code points, decimal values, and epigraphic notes
 * - Character search supporting code points (U+XXXX), names, transliterations, and glyphs
 * - Character-level favorites toggling and clipboard copying
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterExplorerScreen(
    script: HistoricalScript,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val userPrefs = remember { UserPreferencesRepository.getInstance(context) }

    // Active script ID can be switched within the explorer and must save a stable ID to avoid SaveableStateRegistry crash
    var currentScriptId by rememberSaveable(script.id) { mutableStateOf(script.id) }
    val currentScript = remember(currentScriptId) {
        ScriptRegistry.getScriptById(currentScriptId) ?: script
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchVisible by rememberSaveable { mutableStateOf(false) }
    var selectedCategoryIndex by rememberSaveable(currentScriptId) { mutableIntStateOf(0) }
    var showOnlyFavorites by rememberSaveable(currentScriptId) { mutableStateOf(false) }
    var isScriptMenuExpanded by remember { mutableStateOf(false) }
    var showFullDetailSheet by remember { mutableStateOf(false) }

    // Font family for the active script
    val scriptFont = remember(currentScript.id) {
        HistoricalFontProvider.getFontFamily(context, currentScript.id)
    }

    // Record access for recent script tracking
    LaunchedEffect(currentScript.id) {
        userPrefs.recordScriptAccess(currentScript.id)
    }

    var favoritesVersion by remember { mutableIntStateOf(0) }

    // Filter keys by category, favorites, and search query
    val activeCategory = if (currentScript.categories.isNotEmpty() && selectedCategoryIndex < currentScript.categories.size) {
        currentScript.categories[selectedCategoryIndex]
    } else {
        null
    }

    val filteredKeys = remember(currentScript, activeCategory, searchQuery, showOnlyFavorites, favoritesVersion) {
        var baseList = if (activeCategory == null) {
            currentScript.keys
        } else {
            currentScript.keys.filter { it.category == activeCategory }
        }

        if (showOnlyFavorites) {
            baseList = baseList.filter { userPrefs.isFavorite(currentScript.id, it.unicodeCodePoint) }
        }

        ScriptSearchEngine.searchCharacters(baseList, searchQuery)
    }

    // Selected key for the in-screen details panel (defaults to first filtered key)
    var selectedKey by remember(currentScript.id, activeCategory, showOnlyFavorites) {
        mutableStateOf(filteredKeys.firstOrNull())
    }

    // Keep selected key synchronized if current selection is filtered out
    LaunchedEffect(filteredKeys) {
        if (selectedKey == null || !filteredKeys.contains(selectedKey)) {
            selectedKey = filteredKeys.firstOrNull()
        }
    }

    val favCountForCurrentScript = remember(currentScript.id, favoritesVersion) {
        userPrefs.getFavoriteKeysForScript(currentScript.id).size
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AncientKeysIconButton(
                            onClick = onBackClick,
                            contentDescription = "Navigate back to Home",
                            size = 40.dp
                        ) {
                            Text(
                                text = "‹",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.ancientKeysColors.keyText
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Character Explorer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.ancientKeysColors.keyText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AncientKeysIconButton(
                            onClick = {
                                isSearchVisible = !isSearchVisible
                                if (!isSearchVisible) searchQuery = ""
                            },
                            contentDescription = "Search characters",
                            size = 40.dp
                        ) {
                            Text("🔍", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        AncientKeysIconButton(
                            onClick = {
                                showOnlyFavorites = !showOnlyFavorites
                            },
                            contentDescription = "Filter favorites ($favCountForCurrentScript saved)",
                            size = 40.dp
                        ) {
                            Text(
                                text = if (showOnlyFavorites) "★" else "☆",
                                fontSize = 18.sp,
                                color = if (showOnlyFavorites) MaterialTheme.ancientKeysColors.glyphAccent else MaterialTheme.ancientKeysColors.keyText
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- SCRIPT SELECTOR BAR ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { isScriptMenuExpanded = true }
                        .semantics { role = Role.DropdownList; contentDescription = "Select script: current is ${currentScript.name}" },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.ancientKeysColors.chipBackground,
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.8f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.ancientKeysColors.cardBackground,
                                border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.5f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val badge = getScriptRepresentativeBadge(currentScript.id)
                                    Text(
                                        text = badge,
                                        fontFamily = scriptFont,
                                        fontSize = if (currentScript.id.equals("egyptian", ignoreCase = true)) 18.sp else 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                                        textAlign = TextAlign.Center,
                                        style = TextStyle(
                                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                                            lineHeightStyle = LineHeightStyle(
                                                alignment = LineHeightStyle.Alignment.Center,
                                                trim = LineHeightStyle.Trim.Both
                                            )
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentScript.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.ancientKeysColors.keyText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = currentScript.historicalPeriod,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.ancientKeysColors.chipText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (currentScript.isRtl) {
                                SuggestionChip(
                                    onClick = { isScriptMenuExpanded = true },
                                    label = { Text("RTL", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.heightIn(min = 24.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = "▾",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = isScriptMenuExpanded,
                    onDismissRequest = { isScriptMenuExpanded = false }
                ) {
                    ScriptRegistry.allScripts.forEach { candidateScript ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.size(28.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.ancientKeysColors.cardBackground,
                                        border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.4f))
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            val candFont = HistoricalFontProvider.getFontFamily(LocalContext.current, candidateScript.id)
                                            val candBadge = getScriptRepresentativeBadge(candidateScript.id)
                                            Text(
                                                text = candBadge,
                                                fontFamily = candFont,
                                                fontSize = if (candidateScript.id.equals("egyptian", ignoreCase = true)) 14.sp else 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (candidateScript.id == currentScript.id) MaterialTheme.ancientKeysColors.keyPrimaryActionBackground else MaterialTheme.ancientKeysColors.keyText,
                                                textAlign = TextAlign.Center,
                                                style = TextStyle(
                                                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                                                    lineHeightStyle = LineHeightStyle(
                                                        alignment = LineHeightStyle.Alignment.Center,
                                                        trim = LineHeightStyle.Trim.Both
                                                    )
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = candidateScript.name,
                                        fontWeight = if (candidateScript.id == currentScript.id) FontWeight.Bold else FontWeight.Normal,
                                        color = if (candidateScript.id == currentScript.id) MaterialTheme.ancientKeysColors.keyPrimaryActionBackground else MaterialTheme.ancientKeysColors.keyText
                                    )
                                    if (candidateScript.isRtl) {
                                        Text("(RTL)", fontSize = 10.sp, color = MaterialTheme.ancientKeysColors.glyphAccent)
                                    }
                                }
                            },
                            onClick = {
                                currentScriptId = candidateScript.id
                                selectedCategoryIndex = 0
                                showOnlyFavorites = false
                                searchQuery = ""
                                isScriptMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // --- SEARCH FIELD (Collapsible) ---
            AnimatedVisibility(visible = isSearchVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    placeholder = {
                        Text("Search code point (e.g. U+03A6), name, transliteration, or meaning...", fontSize = 12.sp)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Text("✕", fontSize = 13.sp)
                            }
                        }
                    },
                    singleLine = true,
                    shape = AncientKeysDesignTokens.Shapes.pillShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.ancientKeysColors.keyBackground,
                        unfocusedContainerColor = MaterialTheme.ancientKeysColors.keyBackground,
                        focusedBorderColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                        unfocusedBorderColor = MaterialTheme.ancientKeysColors.keyBorder,
                        focusedTextColor = MaterialTheme.ancientKeysColors.keyText,
                        unfocusedTextColor = MaterialTheme.ancientKeysColors.keyText
                    )
                )
            }

            // --- CATEGORY & FAVORITES FILTER CHIPS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorites Filter Chip
                FilterChip(
                    selected = showOnlyFavorites,
                    onClick = {
                        showOnlyFavorites = !showOnlyFavorites
                    },
                    label = {
                        Text(
                            text = "★ Favs ($favCountForCurrentScript)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.7f)),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                        selectedLabelColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText,
                        containerColor = MaterialTheme.ancientKeysColors.chipBackground,
                        labelColor = MaterialTheme.ancientKeysColors.chipText
                    )
                )

                // Category Chips
                currentScript.categories.forEachIndexed { index, category ->
                    val isSelected = !showOnlyFavorites && selectedCategoryIndex == index
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            showOnlyFavorites = false
                            selectedCategoryIndex = index
                        },
                        label = {
                            Text(
                                text = category,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.7f)),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                            selectedLabelColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText,
                            containerColor = MaterialTheme.ancientKeysColors.chipBackground,
                            labelColor = MaterialTheme.ancientKeysColors.chipText
                        )
                    )
                }
            }

            // --- RESULTS COUNTER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredKeys.size} characters",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.ancientKeysColors.chipText
                )
                if (showOnlyFavorites && filteredKeys.isEmpty()) {
                    Text(
                        text = "Star glyphs below to save them",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.ancientKeysColors.glyphAccent
                    )
                }
            }

            // --- UNICODE GLYPH GRID ---
            val layoutDir = if (currentScript.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
            CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 64.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredKeys, key = { it.unicodeCodePoint + it.character + it.category }) { scriptKey ->
                        val isSelected = selectedKey?.unicodeCodePoint == scriptKey.unicodeCodePoint &&
                                selectedKey?.character == scriptKey.character
                        val isFav = userPrefs.isFavorite(currentScript.id, scriptKey.unicodeCodePoint)

                        ExplorerCharacterTile(
                            key = scriptKey,
                            scriptId = currentScript.id,
                            scriptFont = scriptFont,
                            isSelected = isSelected,
                            isFavorite = isFav,
                            onClick = { selectedKey = scriptKey }
                        )
                    }
                }
            }

            // --- IN-SCREEN CHARACTER DETAILS PANEL (Screen 3 Target) ---
            selectedKey?.let { detailKey ->
                CharacterDetailPanel(
                    script = currentScript,
                    key = detailKey,
                    scriptFont = scriptFont,
                    isFavorite = userPrefs.isFavorite(currentScript.id, detailKey.unicodeCodePoint),
                    onToggleFavorite = {
                        userPrefs.toggleFavorite(currentScript.id, detailKey.unicodeCodePoint)
                        favoritesVersion++
                    },
                    onCopyGlyph = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("${currentScript.name} Character", detailKey.character)
                        clipboard.setPrimaryClip(clip)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Copied ${detailKey.unicodeCodePoint} (${detailKey.character}) to clipboard")
                        }
                    },
                    onOpenFullDetails = { showFullDetailSheet = true }
                )
            }
        }
    }

    // Modal Sheet for Expanded Scholarly Metadata
    if (showFullDetailSheet && selectedKey != null) {
        FullCharacterDetailSheet(
            script = currentScript,
            key = selectedKey!!,
            scriptFont = scriptFont,
            isFavorite = userPrefs.isFavorite(currentScript.id, selectedKey!!.unicodeCodePoint),
            onToggleFavorite = {
                userPrefs.toggleFavorite(currentScript.id, selectedKey!!.unicodeCodePoint)
                favoritesVersion++
            },
            onCopyGlyph = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("${currentScript.name} Character", selectedKey!!.character)
                clipboard.setPrimaryClip(clip)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Copied ${selectedKey!!.unicodeCodePoint} (${selectedKey!!.character}) to clipboard")
                }
            },
            onDismiss = { showFullDetailSheet = false }
        )
    }
}

// --- SUB-COMPONENTS ---

@Composable
private fun ExplorerCharacterTile(
    key: ScriptKey,
    scriptId: String,
    scriptFont: FontFamily,
    isSelected: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isRenderable = remember(key.character, scriptId) {
        HistoricalFontProvider.canRenderGlyph(context, scriptId, key.character)
    }
    val displayGlyph = UnicodeMetadataHelper.getDisplayGlyph(key)
    val officialName = UnicodeMetadataHelper.getOfficialUnicodeName(key)
    val accessibleDesc = "$displayGlyph, $officialName, Code point ${key.unicodeCodePoint}"

    val tileBorder = if (isSelected) {
        BorderStroke(2.dp, MaterialTheme.ancientKeysColors.keyPrimaryActionBackground)
    } else {
        BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.7f))
    }

    val containerColor = if (isSelected) {
        MaterialTheme.ancientKeysColors.chipBackground
    } else {
        MaterialTheme.ancientKeysColors.keyBackground
    }

    AncientKeysCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp)
            .semantics {
                role = Role.Button
                contentDescription = accessibleDesc
            },
        shape = RoundedCornerShape(8.dp),
        containerColor = containerColor,
        borderColor = if (isSelected) MaterialTheme.ancientKeysColors.keyPrimaryActionBackground else MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.7f),
        elevation = if (isSelected) 2.dp else 1.dp,
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp)) {
            // Badges in corners
            if (!isRenderable) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 4.dp, top = 4.dp),
                    shape = RoundedCornerShape(3.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
                ) {
                    Text(
                        text = "Font N/A",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
                    )
                }
            }

            if (isFavorite) {
                Text(
                    text = "★",
                    color = MaterialTheme.ancientKeysColors.glyphAccent,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 4.dp, top = 4.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Centered glyph container with script-aware font sizing & balanced line metrics
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .padding(horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val tileFontSize = when (scriptId.lowercase().trim()) {
                        "egyptian", "egyptian_hieroglyphs" -> 20.sp
                        else -> 22.sp
                    }
                    Text(
                        text = displayGlyph,
                        fontFamily = scriptFont,
                        fontSize = tileFontSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.ancientKeysColors.keyText,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both
                            )
                        )
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                val hintText = when {
                    key.transliteration.isNotBlank() && key.transliteration != "Unavailable" -> key.transliteration
                    key.phoneticHint.isNotBlank() -> key.phoneticHint.split(" ").firstOrNull() ?: key.phoneticHint
                    else -> key.unicodeCodePoint
                }
                Text(
                    text = hintText,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.ancientKeysColors.glyphAccent else MaterialTheme.ancientKeysColors.chipText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Character Detail Panel directly matching Screen 3 bottom section of the visual identity specification.
 */
@Composable
private fun CharacterDetailPanel(
    script: HistoricalScript,
    key: ScriptKey,
    scriptFont: FontFamily,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onCopyGlyph: () -> Unit,
    onOpenFullDetails: () -> Unit
) {
    val context = LocalContext.current
    val isRenderable = remember(key.character, script.id) {
        HistoricalFontProvider.canRenderGlyph(context, script.id, key.character)
    }
    val displayGlyph = UnicodeMetadataHelper.getDisplayGlyph(key)
    val officialName = UnicodeMetadataHelper.getOfficialUnicodeName(key)

    AncientKeysCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        containerColor = MaterialTheme.ancientKeysColors.keyBackground,
        borderColor = MaterialTheme.ancientKeysColors.keyBorder,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large Preview Glyph Tile with script-aware font size & balanced metrics
                val previewFontSize = when (script.id.lowercase().trim()) {
                    "egyptian", "egyptian_hieroglyphs" -> 28.sp
                    "greek" -> 30.sp
                    else -> 32.sp
                }
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.ancientKeysColors.chipBackground,
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.6f))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = displayGlyph,
                            fontFamily = scriptFont,
                            fontSize = previewFontSize,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.ancientKeysColors.keyText,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both
                                )
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Character Identity Details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val headerTitle = if (key.label.isNotBlank() && key.label != key.character) {
                            "${key.label} (${key.character})"
                        } else {
                            officialName.split(" ").takeLast(2).joinToString(" ")
                        }
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.ancientKeysColors.keyText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(end = 6.dp)
                        )
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text(
                                text = if (isFavorite) "★" else "☆",
                                fontSize = 18.sp,
                                color = if (isFavorite) MaterialTheme.ancientKeysColors.glyphAccent else MaterialTheme.ancientKeysColors.chipText
                            )
                        }
                    }

                    if (key.transliteration.isNotBlank() && key.transliteration != "Unavailable") {
                        Text(
                            text = "Transliteration: ${key.transliteration}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.ancientKeysColors.glyphAccent
                        )
                    }

                    if (key.pronunciationIpa.isNotBlank() && key.pronunciationIpa != "Unavailable") {
                        Text(
                            text = "Pronunciation: ${key.pronunciationIpa}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.ancientKeysColors.chipText
                        )
                    } else if (key.phoneticHint.isNotBlank()) {
                        Text(
                            text = "Phonetic: ${key.phoneticHint}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.ancientKeysColors.chipText
                        )
                    }

                    Text(
                        text = "Code: ${key.unicodeCodePoint} • ${key.category}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.ancientKeysColors.chipText.copy(alpha = 0.8f)
                    )

                    if (!isRenderable) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⚠️ Requires specialized font support on this device",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // About snippet if available
            val aboutSnippet = when {
                key.historicalNotes.isNotBlank() -> key.historicalNotes
                key.meaning.isNotBlank() && key.meaning != "Unavailable" -> "Sign / Meaning: ${key.meaning}"
                else -> null
            }

            if (aboutSnippet != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.ancientKeysColors.chipBackground.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = aboutSnippet,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.keyText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row: Copy Glyph & More Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCopyGlyph,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                        contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("📋 Copy Glyph", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onOpenFullDetails,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("ℹ️ Full Specs", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/**
 * Complete scholarly modal sheet for character epigraphy and Unicode details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullCharacterDetailSheet(
    script: HistoricalScript,
    key: ScriptKey,
    scriptFont: FontFamily,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onCopyGlyph: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isRenderable = remember(key.character, script.id) {
        HistoricalFontProvider.canRenderGlyph(context, script.id, key.character)
    }
    val displayGlyph = UnicodeMetadataHelper.getDisplayGlyph(key)
    val officialName = UnicodeMetadataHelper.getOfficialUnicodeName(key)
    val codePointInt = UnicodeMetadataHelper.parseCodePointHex(key.unicodeCodePoint)
        ?: UnicodeMetadataHelper.getCodePoint(key.character)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.ancientKeysColors.cardBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val sheetFontSize = when (script.id.lowercase().trim()) {
                "egyptian", "egyptian_hieroglyphs" -> 34.sp
                "greek" -> 38.sp
                else -> 40.sp
            }
            Surface(
                modifier = Modifier.size(84.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.ancientKeysColors.chipBackground,
                border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(6.dp)
                ) {
                    Text(
                        text = displayGlyph,
                        fontFamily = scriptFont,
                        fontSize = sheetFontSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.ancientKeysColors.keyText,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both
                            )
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = officialName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.ancientKeysColors.keyText,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${key.unicodeCodePoint} (Decimal: $codePointInt)",
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.ancientKeysColors.glyphAccent
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (key.isCombining || UnicodeMetadataHelper.isCombiningMark(codePointInt)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.ancientKeysColors.chipBackground.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "ℹ️ Non-Spacing Combining Mark",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.ancientKeysColors.glyphAccent
                        )
                        Text(
                            text = "This character combines logically with preceding base glyphs. Dotted circle (◌) is shown purely for visualization.",
                            fontSize = 11.sp,
                            color = MaterialTheme.ancientKeysColors.chipText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            HorizontalDivider(color = MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            DetailSpecRow(label = "Script", value = script.name)
            DetailSpecRow(label = "Category", value = key.category)
            DetailSpecRow(
                label = "Font Rendering",
                value = if (isRenderable) "Supported" else "Requires device/custom font support"
            )
            DetailSpecRow(label = "Transliteration", value = key.transliteration.ifBlank { "Unavailable" })
            DetailSpecRow(label = "Pronunciation / IPA", value = key.pronunciationIpa.ifBlank { key.phoneticHint.ifBlank { "Unavailable" } })
            DetailSpecRow(label = "Meaning / Sign", value = key.meaning.ifBlank { "Unavailable" })
            if (key.historicalNotes.isNotBlank()) {
                DetailSpecRow(label = "Historical Notes", value = key.historicalNotes)
            }
            DetailSpecRow(label = "Writing Direction", value = if (script.isRtl) "Right-to-Left (RTL)" else "Left-to-Right (LTR)")
            DetailSpecRow(label = "Period", value = script.historicalPeriod)
            DetailSpecRow(label = "Reference", value = script.sourcesAndReferences.ifBlank { "Unicode Standard ISO/IEC 10646" })

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isFavorite) "★ Starred" else "☆ Add Star", fontSize = 12.sp)
                }

                Button(
                    onClick = onCopyGlyph,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                        contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                    )
                ) {
                    Text("Copy Glyph", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailSpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.ancientKeysColors.chipText,
            modifier = Modifier.weight(0.38f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.ancientKeysColors.keyText,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.62f)
        )
    }
}

private fun getScriptRepresentativeBadge(scriptId: String): String {
    return when (scriptId.lowercase().trim()) {
        "greek" -> "Ω"
        "egyptian", "egyptian_hieroglyphs" -> "𓀀"
        "brahmi" -> "𑀅"
        "kharosthi" -> "𐨀"
        "phoenician" -> "𐤀"
        "runic", "elder_futhark" -> "ᚱ"
        else -> "📜"
    }
}

private fun getScriptIcon(scriptId: String): String = getScriptRepresentativeBadge(scriptId)
