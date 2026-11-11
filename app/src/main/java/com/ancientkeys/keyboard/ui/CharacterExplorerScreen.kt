package com.ancientkeys.keyboard.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptKey
import com.ancientkeys.keyboard.search.ScriptSearchEngine
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.util.UnicodeMetadataHelper
import kotlinx.coroutines.launch

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

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategoryIndex by rememberSaveable(script.id) { mutableIntStateOf(0) }
    var showOnlyFavorites by rememberSaveable(script.id) { mutableStateOf(false) }
    var isHistoryExpanded by rememberSaveable(script.id) { mutableStateOf(false) }

    var selectedKeyForDetail by remember { mutableStateOf<ScriptKey?>(null) }
    var favoritesVersion by remember { mutableIntStateOf(0) }

    // Record access for recent script tracking
    LaunchedEffect(script.id) {
        userPrefs.recordScriptAccess(script.id)
    }

    // Filter keys by category, favorites, and search query
    val activeCategory = if (script.categories.isNotEmpty() && selectedCategoryIndex < script.categories.size) {
        script.categories[selectedCategoryIndex]
    } else {
        null
    }

    val filteredKeys = remember(script, activeCategory, searchQuery, showOnlyFavorites, favoritesVersion) {
        var baseList = if (activeCategory == null) {
            script.keys
        } else {
            script.keys.filter { it.category == activeCategory }
        }

        if (showOnlyFavorites) {
            baseList = baseList.filter { userPrefs.isFavorite(script.id, it.unicodeCodePoint) }
        }

        ScriptSearchEngine.searchCharacters(baseList, searchQuery)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${script.name} Explorer",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            if (script.isRtl) {
                                Spacer(modifier = Modifier.width(6.dp))
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text("RTL", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                        }
                        Text(
                            text = script.historicalPeriod,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
            // Character Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                placeholder = {
                    Text("Search by code point (e.g. U+16A0), Unicode name, or symbol...", fontSize = 13.sp)
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

            // Category & Favorites Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorites Filter Chip
                FilterChip(
                    selected = showOnlyFavorites,
                    onClick = { showOnlyFavorites = !showOnlyFavorites },
                    label = { Text("★ Favorites", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )

                // Category Chips
                script.categories.forEachIndexed { index, category ->
                    FilterChip(
                        selected = !showOnlyFavorites && selectedCategoryIndex == index,
                        onClick = {
                            showOnlyFavorites = false
                            selectedCategoryIndex = index
                        },
                        label = { Text(category, fontSize = 12.sp) }
                    )
                }
            }

            // Expandable Historical Context Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isHistoryExpanded = !isHistoryExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historical Context & Scholarly Sources",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isHistoryExpanded) "▲ Hide" else "▼ Read",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    AnimatedVisibility(visible = isHistoryExpanded) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = script.historicalContext.ifBlank { script.description },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Scholarly References:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = script.sourcesAndReferences.ifBlank { "The Unicode Standard (ISO/IEC 10646)" },
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            // Results Counter
            Text(
                text = "${filteredKeys.size} characters found",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
            )

            // Characters Grid
            val layoutDir = if (script.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
            CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 78.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredKeys, key = { it.unicodeCodePoint + it.character + it.category }) { scriptKey ->
                        val isFav = userPrefs.isFavorite(script.id, scriptKey.unicodeCodePoint)
                        CharacterGridCard(
                            key = scriptKey,
                            isFavorite = isFav,
                            onClick = { selectedKeyForDetail = scriptKey }
                        )
                    }
                }
            }
        }
    }

    // Detail Bottom Sheet when character is tapped
    selectedKeyForDetail?.let { detailKey ->
        CharacterDetailBottomSheet(
            script = script,
            key = detailKey,
            isFavorite = userPrefs.isFavorite(script.id, detailKey.unicodeCodePoint),
            onToggleFavorite = {
                userPrefs.toggleFavorite(script.id, detailKey.unicodeCodePoint)
                favoritesVersion++
            },
            onCopyGlyph = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("${script.name} Character", detailKey.character)
                clipboard.setPrimaryClip(clip)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Copied ${detailKey.unicodeCodePoint} (${detailKey.character}) to clipboard")
                }
            },
            onDismiss = { selectedKeyForDetail = null }
        )
    }
}

@Composable
private fun CharacterGridCard(
    key: ScriptKey,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    val displayGlyph = UnicodeMetadataHelper.getDisplayGlyph(key)
    val officialName = UnicodeMetadataHelper.getOfficialUnicodeName(key)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "$displayGlyph, $officialName, Code point ${key.unicodeCodePoint}"
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = displayGlyph,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = key.unicodeCodePoint,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
                if (key.transliteration.isNotBlank() && key.transliteration != "Unavailable") {
                    Text(
                        text = key.transliteration,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (isFavorite) {
                Text(
                    text = "★",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterDetailBottomSheet(
    script: HistoricalScript,
    key: ScriptKey,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onCopyGlyph: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val displayGlyph = UnicodeMetadataHelper.getDisplayGlyph(key)
    val officialName = UnicodeMetadataHelper.getOfficialUnicodeName(key)
    val codePointInt = UnicodeMetadataHelper.parseCodePointHex(key.unicodeCodePoint) ?: UnicodeMetadataHelper.getCodePoint(key.character)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large Display Glyph
            Surface(
                modifier = Modifier.size(90.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = displayGlyph,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Official Unicode Name
            Text(
                text = officialName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Code Point Details
            Text(
                text = "${key.unicodeCodePoint} (Decimal: $codePointInt)",
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Combining Mark Warning / Note
            if (key.isCombining || UnicodeMetadataHelper.isCombiningMark(codePointInt)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "ℹ️ Non-Spacing Combining Mark (Diacritic)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "This character is a dependent vowel sign or diacritic. In digital text, it combines logically with the preceding base consonant. The dotted circle (◌) is shown purely for visual reference in this viewer and is not inserted into typed text.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            HorizontalDivider()

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Attributes Table
            DetailRow(label = "Script", value = script.name)
            DetailRow(label = "Category", value = key.category)
            DetailRow(label = "Transliteration", value = key.transliteration.ifBlank { "Unavailable" })
            DetailRow(label = "Pronunciation (IPA)", value = key.pronunciationIpa.ifBlank { "Unavailable" })
            DetailRow(label = "Meaning / Sign", value = key.meaning.ifBlank { "Unavailable" })
            if (key.historicalNotes.isNotBlank()) {
                DetailRow(label = "Historical Notes", value = key.historicalNotes)
            }
            DetailRow(label = "Direction", value = if (script.isRtl) "Right-to-Left (RTL)" else "Left-to-Right (LTR)")
            DetailRow(label = "Source Reference", value = script.sourcesAndReferences)

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Favorite and Copy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isFavorite) "★ Favorited" else "☆ Add Favorite", fontSize = 13.sp)
                }

                Button(
                    onClick = onCopyGlyph,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Copy Glyph", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(130.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

