package com.ancientkeys.keyboard.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptKey
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.search.ScriptSearchEngine
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.ui.components.AncientKeysCard
import com.ancientkeys.keyboard.ui.components.AncientKeysIconButton
import com.ancientkeys.keyboard.ui.components.AncientKeysSectionHeader
import com.ancientkeys.keyboard.ui.theme.AncientKeysDesignTokens
import com.ancientkeys.keyboard.ui.theme.ancientKeysColors
import kotlin.math.roundToInt

/**
 * AncientKeys v1.1 Home Dashboard matching Screen 1 of the visual identity specification.
 *
 * Implements:
 * - Brand header with Greek emblem, scholarly title, and subtitle
 * - Rounded search bar with real-time script & character filtering
 * - 4-item quick action grid (Keyboard, Learn, Explore, Favorites)
 * - Recent scripts horizontal pill row with glyph badges
 * - Favorites section with active favorites and helpful empty states
 * - Featured scripts cards for all 6 historical scripts
 * - Bottom Navigation Bar with intuitive Material 3 destinations
 * - Comprehensive settings dialog for theme, height, haptics, and scholar mode
 */
@Composable
fun HomeScreen(
    onOpenPlayground: (HistoricalScript) -> Unit,
    onOpenExplorer: (HistoricalScript) -> Unit,
    onOpenTestLab: () -> Unit = {},
    onOpenResearchWorkspace: () -> Unit = {},
    onOpenLearn: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesRepository.getInstance(context) }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showFavoritesDialog by rememberSaveable { mutableStateOf(false) }
    var showOnboardingDialog by rememberSaveable { mutableStateOf(!userPrefs.isOnboardingCompleted()) }

    val recentScriptIds = remember { userPrefs.getRecentScriptIds() }
    val recentScripts = remember(recentScriptIds) {
        val loaded = recentScriptIds.mapNotNull { ScriptRegistry.getScriptById(it) }
        if (loaded.isNotEmpty()) loaded else listOfNotNull(
            ScriptRegistry.getScriptById("greek"),
            ScriptRegistry.getScriptById("brahmi"),
            ScriptRegistry.getScriptById("runic")
        )
    }

    val favoriteScriptIds = remember { userPrefs.getFavoriteScriptIds() }
    val allFavoriteScripts = remember(favoriteScriptIds) {
        val fromFavScripts = favoriteScriptIds.mapNotNull { ScriptRegistry.getScriptById(it) }
        val fromFavChars = userPrefs.getAllFavoriteScriptIds().mapNotNull { ScriptRegistry.getScriptById(it) }
        (fromFavScripts + fromFavChars).distinctBy { it.id }
    }

    val filteredScripts = remember(searchQuery) {
        ScriptSearchEngine.searchScripts(ScriptRegistry.allScripts, searchQuery)
    }

    val matchingCharacters = remember(searchQuery) {
        if (searchQuery.length >= 2) {
            val allKeys = ScriptRegistry.allScripts.flatMap { script ->
                script.keys.map { key -> Pair(script, key) }
            }
            allKeys.filter { (_, key) ->
                ScriptSearchEngine.searchCharacters(listOf(key), searchQuery).isNotEmpty()
            }.take(12)
        } else {
            emptyList()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- 1. BRAND HEADER & TOP UTILITY ICONS ---
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top utility row: Bookmark & Settings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AncientKeysIconButton(
                            onClick = onOpenResearchWorkspace,
                            contentDescription = "Saved Transcripts & Corpus",
                            size = 40.dp
                        ) {
                            Text("🔖", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        AncientKeysIconButton(
                            onClick = onOpenSettings,
                            contentDescription = "Theme & Keyboard Settings",
                            size = 40.dp
                        ) {
                            Text("⚙️", fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Classical Pillar Emblem Badge
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.ancientKeysColors.chipBackground,
                        border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.7f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🏛️", fontSize = 28.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title
                    Text(
                        text = "AncientKeys",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.ancientKeysColors.keyText,
                        textAlign = TextAlign.Center
                    )

                    // Subtitle
                    Text(
                        text = "Ancient scripts made easy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.ancientKeysColors.chipText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // --- 2. SEARCH BAR ---
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Search scripts, symbols, or guides" },
                    placeholder = {
                        Text(
                            text = "Search scripts, symbols, or guides",
                            fontSize = 13.sp,
                            color = MaterialTheme.ancientKeysColors.chipText.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Text(
                            text = "🔍",
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Text(
                                    text = "✕",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.ancientKeysColors.chipText
                                )
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

            // --- 3. QUICK ACTIONS GRID (Keyboard, Learn, Explore, Favorites) ---
            if (searchQuery.isEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HomeQuickActionCard(
                            icon = "✍️",
                            label = "Playground",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val target = recentScripts.firstOrNull() ?: ScriptRegistry.allScripts.first()
                                onOpenPlayground(target)
                            }
                        )
                        HomeQuickActionCard(
                            icon = "🧭",
                            label = "Explore",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val target = recentScripts.firstOrNull() ?: ScriptRegistry.allScripts.first()
                                onOpenExplorer(target)
                            }
                        )
                        HomeQuickActionCard(
                            icon = "📖",
                            label = "Learn",
                            modifier = Modifier.weight(1f),
                            onClick = onOpenLearn
                        )
                        HomeQuickActionCard(
                            icon = "🔖",
                            label = "Corpus",
                            modifier = Modifier.weight(1f),
                            onClick = onOpenResearchWorkspace
                        )
                    }
                }
            }

            // --- 4. RECENT SCRIPTS HORIZONTAL CHIPS ---
            if (searchQuery.isEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        AncientKeysSectionHeader(
                            title = "Recent Scripts",
                            actionLabel = "See all >",
                            onActionClick = {
                                val first = recentScripts.firstOrNull() ?: ScriptRegistry.allScripts.first()
                                onOpenExplorer(first)
                            }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            recentScripts.forEach { script ->
                                val sampleGlyph = getScriptRepresentativeBadge(script.id)
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable { onOpenExplorer(script) }
                                        .semantics { role = Role.Button; contentDescription = "Open ${script.name}" },
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.ancientKeysColors.keyBackground,
                                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.8f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = sampleGlyph,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.ancientKeysColors.glyphAccent
                                        )
                                        Text(
                                            text = script.name.split(" ").firstOrNull() ?: script.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.ancientKeysColors.keyText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- 5. FAVORITES SECTION ---
            if (searchQuery.isEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        AncientKeysSectionHeader(
                            title = "Favorites",
                            actionLabel = "See all >",
                            onActionClick = { showFavoritesDialog = true }
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (allFavoriteScripts.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                allFavoriteScripts.forEach { script ->
                                    val icon = getScriptIcon(script.id)
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .clickable { onOpenExplorer(script) }
                                            .semantics { role = Role.Button; contentDescription = "Favorite ${script.name}" },
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.ancientKeysColors.keyBackground,
                                        border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.8f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(text = icon, fontSize = 13.sp)
                                            Text(
                                                text = script.name.split(" ").firstOrNull() ?: script.name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.ancientKeysColors.keyText
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Helpful and engaging empty state
                            AncientKeysCard(
                                modifier = Modifier.fillMaxWidth(),
                                containerColor = MaterialTheme.ancientKeysColors.chipBackground.copy(alpha = 0.4f),
                                onClick = { showFavoritesDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.ancientKeysColors.chipBackground,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("⭐", fontSize = 16.sp)
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "No favorites saved yet",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.ancientKeysColors.keyText
                                        )
                                        Text(
                                            text = "Star characters in Character Explorer to pin them here for quick access.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.ancientKeysColors.chipText
                                        )
                                    }
                                    Text(
                                        text = "Add +",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 6. MATCHING CHARACTERS (Visible when searching) ---
            if (matchingCharacters.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Matching Glyphs (${matchingCharacters.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.ancientKeysColors.glyphAccent
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            matchingCharacters.forEach { (script, key) ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onOpenExplorer(script) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.ancientKeysColors.keyBackground,
                                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = key.character,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.ancientKeysColors.keyText
                                        )
                                        Text(
                                            text = key.label,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.ancientKeysColors.chipText
                                        )
                                        Text(
                                            text = script.name.split(" ").firstOrNull() ?: script.name,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.ancientKeysColors.glyphAccent
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- 7. FEATURED SCRIPTS HEADER ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (searchQuery.isEmpty()) "Featured Scripts" else "Matching Scripts (${filteredScripts.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    if (searchQuery.isNotEmpty()) {
                        Text(
                            text = "Clear",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.ancientKeysColors.glyphAccent,
                            modifier = Modifier.clickable { searchQuery = "" }
                        )
                    }
                }
            }

            // --- 8. FEATURED SCRIPT CARDS ---
            items(filteredScripts, key = { it.id }) { script ->
                FeaturedScriptCard(
                    script = script,
                    onOpenExplorer = { onOpenExplorer(script) },
                    onOpenPlayground = { onOpenPlayground(script) }
                )
            }

            // --- 9. OFFLINE & PRIVACY INFO BANNER ---
            item {
                AncientKeysCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.ancientKeysColors.chipBackground.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔒", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "100% Offline & Private",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.ancientKeysColors.keyText
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "AncientKeys requires zero network permissions, does not collect analytics or keystrokes, and keeps your texts completely on your device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.ancientKeysColors.chipText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    try {
                                        context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                                    } catch (_: Exception) {
                                    }
                                }
                            ) {
                                Text("Enable System Keyboard →", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = onOpenAbout) {
                                Text("About & Licenses", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS ---

    if (showFavoritesDialog) {
        HomeFavoritesDialog(
            userPrefs = userPrefs,
            onDismiss = { showFavoritesDialog = false },
            onOpenExplorer = { script ->
                showFavoritesDialog = false
                onOpenExplorer(script)
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

// --- HELPER COMPOSABLES ---

@Composable
private fun HomeQuickActionCard(
    icon: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AncientKeysCard(
        modifier = modifier
            .height(76.dp)
            .semantics { role = Role.Button; contentDescription = label },
        shape = RoundedCornerShape(14.dp),
        containerColor = MaterialTheme.ancientKeysColors.keyBackground,
        borderColor = MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.7f),
        elevation = AncientKeysDesignTokens.Elevation.card,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.ancientKeysColors.keyText,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun FeaturedScriptCard(
    script: HistoricalScript,
    onOpenExplorer: () -> Unit,
    onOpenPlayground: () -> Unit
) {
    val icon = getScriptIcon(script.id)
    val periodSummary = "${script.historicalPeriod} • ${script.region}"

    AncientKeysCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { role = Role.Button; contentDescription = "View ${script.name}" },
        shape = AncientKeysDesignTokens.Shapes.cardShape,
        containerColor = MaterialTheme.ancientKeysColors.keyBackground,
        borderColor = MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.7f),
        elevation = AncientKeysDesignTokens.Elevation.card,
        onClick = onOpenExplorer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Emblem Badge
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.ancientKeysColors.chipBackground,
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.5f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = icon, fontSize = 22.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name and Description
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = script.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.ancientKeysColors.keyText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false).padding(end = 6.dp)
                        )
                        if (script.isRtl) {
                            SuggestionChip(
                                onClick = onOpenExplorer,
                                label = { Text("RTL", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }

                    Text(
                        text = periodSummary,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.ancientKeysColors.glyphAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Right Chevron
                Text(
                    text = "›",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.ancientKeysColors.chipText.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = script.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.ancientKeysColors.chipText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sample Inscription Text
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.ancientKeysColors.keyboardBackground.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = script.sampleText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenExplorer,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.8f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("📖 Explore", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onOpenPlayground,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                        contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("Type Playground →", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun HomeScreenScriptCard(
    script: HistoricalScript,
    onOpenPlayground: () -> Unit,
    onOpenExplorer: () -> Unit
) {
    FeaturedScriptCard(
        script = script,
        onOpenExplorer = onOpenExplorer,
        onOpenPlayground = onOpenPlayground
    )
}

@Composable
private fun HomeFavoritesDialog(
    userPrefs: UserPreferencesRepository,
    onDismiss: () -> Unit,
    onOpenExplorer: (HistoricalScript) -> Unit
) {
    val favoriteScriptIds = remember { userPrefs.getFavoriteScriptIds() }
    val allFavoriteScripts = remember(favoriteScriptIds) {
        val fromFavScripts = favoriteScriptIds.mapNotNull { ScriptRegistry.getScriptById(it) }
        val fromFavChars = userPrefs.getAllFavoriteScriptIds().mapNotNull { ScriptRegistry.getScriptById(it) }
        (fromFavScripts + fromFavChars).distinctBy { it.id }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "⭐ Starred Favorites",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScrollHelper()
            ) {
                Text(
                    text = "Quickly navigate to your pinned historical scripts and glyph collections:",
                    fontSize = 12.sp,
                    color = MaterialTheme.ancientKeysColors.chipText
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (allFavoriteScripts.isNotEmpty()) {
                    allFavoriteScripts.forEach { script ->
                        val icon = getScriptIcon(script.id)
                        val favKeys = userPrefs.getFavoriteKeysForScript(script.id)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onOpenExplorer(script) },
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.ancientKeysColors.chipBackground.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = icon, fontSize = 18.sp)
                                    Column {
                                        Text(
                                            text = script.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.ancientKeysColors.keyText
                                        )
                                        if (favKeys.isNotEmpty()) {
                                            Text(
                                                text = "${favKeys.size} starred glyphs",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.ancientKeysColors.glyphAccent
                                            )
                                        }
                                    }
                                }
                                Text("Open ›", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.glyphAccent)
                            }
                        }
                    }
                } else {
                    Text(
                        text = "You don't have any favorites yet. Choose any script below to star it:",
                        fontSize = 12.sp,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ScriptRegistry.allScripts.take(4).forEach { script ->
                        OutlinedButton(
                            onClick = {
                                userPrefs.toggleScriptFavorite(script.id)
                                onOpenExplorer(script)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+ Star ${script.name}", fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                    contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                )
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun Modifier.verticalScrollHelper(): Modifier = this.then(
    Modifier.verticalScroll(rememberScrollState())
)

private fun getScriptIcon(scriptId: String): String {
    return when (scriptId.lowercase().trim()) {
        "greek" -> "🏛️"
        "egyptian" -> "☥"
        "brahmi" -> "𑀅"
        "kharosthi" -> "𐨀"
        "phoenician" -> "𐤀"
        "runic" -> "ᚱ"
        else -> "📜"
    }
}

private fun getScriptRepresentativeBadge(scriptId: String): String {
    return when (scriptId.lowercase().trim()) {
        "greek" -> "α"
        "egyptian" -> "𓀀"
        "brahmi" -> "𑀅"
        "kharosthi" -> "𐨀"
        "phoenician" -> "𐤀"
        "runic" -> "ᚱ"
        else -> "📜"
    }
}
