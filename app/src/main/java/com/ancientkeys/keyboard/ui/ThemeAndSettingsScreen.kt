package com.ancientkeys.keyboard.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.ui.components.AncientKeysCard
import com.ancientkeys.keyboard.ui.components.AncientKeysIconButton
import com.ancientkeys.keyboard.ui.components.AncientKeysSectionHeader
import com.ancientkeys.keyboard.ui.theme.AncientKeysDesignTokens
import com.ancientkeys.keyboard.ui.theme.DarkOutline
import com.ancientkeys.keyboard.ui.theme.DarkPrimary
import com.ancientkeys.keyboard.ui.theme.DarkSurfaceVariant
import com.ancientkeys.keyboard.ui.theme.LightOutline
import com.ancientkeys.keyboard.ui.theme.LightPrimary
import com.ancientkeys.keyboard.ui.theme.LightSurface
import com.ancientkeys.keyboard.ui.theme.LightSurfaceVariant
import com.ancientkeys.keyboard.ui.theme.SageOutline
import com.ancientkeys.keyboard.ui.theme.SagePrimary
import com.ancientkeys.keyboard.ui.theme.SageSurface
import com.ancientkeys.keyboard.ui.theme.SageSurfaceVariant
import com.ancientkeys.keyboard.ui.theme.SepiaOutline
import com.ancientkeys.keyboard.ui.theme.SepiaPrimary
import com.ancientkeys.keyboard.ui.theme.SepiaSurface
import com.ancientkeys.keyboard.ui.theme.SepiaSurfaceVariant
import com.ancientkeys.keyboard.ui.theme.ancientKeysColors
import kotlin.math.roundToInt

/**
 * AncientKeys v1.1 Theme & Settings Screen matching Screen 4 of the visual design specification.
 *
 * Implements:
 * - Top back navigation & screen title
 * - Appearance theme selector with interactive miniature keyboard previews for:
 *   Minimal Sage (default), Light, Dark, Scholar / Sepia, and System Default
 * - Real-time dynamic theme switching affecting both app and IME without restart
 * - Keyboard Height slider (240–360 dp) with preset pills and live readout
 * - Key Size segmented control (Small, Medium, Large)
 * - Haptic feedback toggle connected to preferences
 * - Show Key Labels / Transliterations toggle
 * - Ask before switching scripts toggle
 * - Default Script selector (Greek, Egyptian, Brahmi, Kharosthi, Phoenician, Runic, English)
 * - Additional utilities: Keyboard Setup Guide, Android Keyboard Settings, About & OFL Fonts
 */
@Composable
fun ThemeAndSettingsScreen(
    onNavigateBack: () -> Unit,
    onOpenOnboarding: () -> Unit = {},
    onOpenAbout: () -> Unit = {}
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesRepository.getInstance(context) }

    // Intercept hardware/gesture Back
    BackHandler {
        onNavigateBack()
    }

    // Settings state
    var selectedTheme by remember { mutableStateOf(userPrefs.getThemePreference()) }
    var keyboardHeightDp by remember { mutableFloatStateOf(userPrefs.getKeyboardHeightDp().toFloat()) }
    var keySize by remember { mutableStateOf(userPrefs.getKeySizePreference()) }
    var hapticsEnabled by remember { mutableStateOf(userPrefs.isHapticsEnabled()) }
    var showKeyLabels by remember { mutableStateOf(userPrefs.isShowKeyLabelsEnabled()) }
    var confirmScriptSwitch by remember { mutableStateOf(userPrefs.isConfirmScriptSwitchEnabled()) }
    var defaultScriptId by remember { mutableStateOf(userPrefs.getDefaultScriptId()) }
    var isDefaultScriptMenuExpanded by remember { mutableStateOf(false) }

    val defaultScriptDisplay = remember(defaultScriptId) {
        when (defaultScriptId) {
            "english" -> "English QWERTY"
            else -> ScriptRegistry.getScriptById(defaultScriptId)?.name ?: "Ancient Greek"
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SettingsTopBar(
                title = "Theme & Settings",
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ==========================================
            // 1. APPEARANCE SECTION
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingsSectionHeader(
                    icon = "🎨",
                    title = "Appearance",
                    subtitle = "Choose a look for the app and keyboard."
                )

                // Horizontal row of miniature keyboard theme preview cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Minimal Sage (Default Highlight)
                    ThemePreviewCard(
                        title = "Minimal Sage",
                        themeId = UserPreferencesRepository.THEME_SAGE,
                        isSelected = selectedTheme == UserPreferencesRepository.THEME_SAGE,
                        cardBgColor = SageSurfaceVariant,
                        keyBgColor = SageSurface,
                        keyBorderColor = SageOutline,
                        accentKeyColor = SagePrimary,
                        onClick = {
                            selectedTheme = UserPreferencesRepository.THEME_SAGE
                            userPrefs.setThemePreference(UserPreferencesRepository.THEME_SAGE)
                        }
                    )

                    // Light
                    ThemePreviewCard(
                        title = "Light",
                        themeId = UserPreferencesRepository.THEME_LIGHT,
                        isSelected = selectedTheme == UserPreferencesRepository.THEME_LIGHT,
                        cardBgColor = LightSurfaceVariant,
                        keyBgColor = LightSurface,
                        keyBorderColor = LightOutline,
                        accentKeyColor = LightPrimary,
                        onClick = {
                            selectedTheme = UserPreferencesRepository.THEME_LIGHT
                            userPrefs.setThemePreference(UserPreferencesRepository.THEME_LIGHT)
                        }
                    )

                    // Dark
                    ThemePreviewCard(
                        title = "Dark",
                        themeId = UserPreferencesRepository.THEME_DARK,
                        isSelected = selectedTheme == UserPreferencesRepository.THEME_DARK,
                        cardBgColor = DarkSurfaceVariant,
                        keyBgColor = Color(0xFF252D27),
                        keyBorderColor = DarkOutline,
                        accentKeyColor = DarkPrimary,
                        onClick = {
                            selectedTheme = UserPreferencesRepository.THEME_DARK
                            userPrefs.setThemePreference(UserPreferencesRepository.THEME_DARK)
                        }
                    )

                    // Scholar / Sepia
                    ThemePreviewCard(
                        title = "Scholar",
                        themeId = UserPreferencesRepository.THEME_SEPIA,
                        isSelected = selectedTheme == UserPreferencesRepository.THEME_SEPIA,
                        cardBgColor = SepiaSurfaceVariant,
                        keyBgColor = SepiaSurface,
                        keyBorderColor = SepiaOutline,
                        accentKeyColor = SepiaPrimary,
                        onClick = {
                            selectedTheme = UserPreferencesRepository.THEME_SEPIA
                            userPrefs.setThemePreference(UserPreferencesRepository.THEME_SEPIA)
                        }
                    )

                    // System Default
                    ThemePreviewCard(
                        title = "System",
                        themeId = UserPreferencesRepository.THEME_SYSTEM,
                        isSelected = selectedTheme == UserPreferencesRepository.THEME_SYSTEM,
                        cardBgColor = Color(0xFFE4E9E5),
                        keyBgColor = Color(0xFFF9FAF9),
                        keyBorderColor = Color(0xFFCCD6CE),
                        accentKeyColor = SagePrimary,
                        onClick = {
                            selectedTheme = UserPreferencesRepository.THEME_SYSTEM
                            userPrefs.setThemePreference(UserPreferencesRepository.THEME_SYSTEM)
                        }
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            // ==========================================
            // 2. KEYBOARD SETTINGS SECTION
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SettingsSectionHeader(
                    icon = "⌨️",
                    title = "Keyboard Settings",
                    subtitle = "Customize keyboard sizing, layout density, and feedback."
                )

                // Keyboard Height Slider & Presets
                AncientKeysCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                    borderColor = MaterialTheme.ancientKeysColors.cardBorder
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("↔️", fontSize = 16.sp)
                                Column {
                                    Text(
                                        text = "Keyboard Height",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.ancientKeysColors.keyText
                                    )
                                    Text(
                                        text = "Adjust the keyboard's vertical size.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.ancientKeysColors.chipText
                                    )
                                }
                            }

                            // Live DP Readout Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.ancientKeysColors.chipSelectedBackground,
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    text = "${keyboardHeightDp.roundToInt()} dp",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.ancientKeysColors.chipSelectedText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Slider with icons on ends
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⌨️", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.chipText)
                            Slider(
                                value = keyboardHeightDp,
                                onValueChange = { newValue ->
                                    keyboardHeightDp = newValue
                                    userPrefs.setKeyboardHeightDp(newValue.roundToInt())
                                },
                                valueRange = UserPreferencesRepository.MIN_KEYBOARD_HEIGHT_DP.toFloat()..UserPreferencesRepository.MAX_KEYBOARD_HEIGHT_DP.toFloat(),
                                steps = 11,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.ancientKeysColors.chipBackground
                                )
                            )
                            Text("⌨️", fontSize = 18.sp, color = MaterialTheme.ancientKeysColors.chipText)
                        }

                        // Height Quick Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                250 to "Compact (250 dp)",
                                290 to "Standard (290 dp)",
                                330 to "Tall (330 dp)"
                            ).forEach { (presetDp, label) ->
                                val isSelected = keyboardHeightDp.roundToInt() == presetDp
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        keyboardHeightDp = presetDp.toFloat()
                                        userPrefs.setKeyboardHeightDp(presetDp)
                                    },
                                    label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                                        selectedLabelColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText,
                                        containerColor = MaterialTheme.ancientKeysColors.chipBackground,
                                        labelColor = MaterialTheme.ancientKeysColors.chipText
                                    )
                                )
                            }
                        }
                    }
                }

                // Key Size Segmented Control
                AncientKeysCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                    borderColor = MaterialTheme.ancientKeysColors.cardBorder
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🔲", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = "Key Size",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.ancientKeysColors.keyText
                                )
                                Text(
                                    text = "Adjust the size of each key.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.ancientKeysColors.chipText
                                )
                            }
                        }

                        // Segmented control pill row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.ancientKeysColors.chipBackground)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                UserPreferencesRepository.KEY_SIZE_SMALL to "Small",
                                UserPreferencesRepository.KEY_SIZE_MEDIUM to "Medium",
                                UserPreferencesRepository.KEY_SIZE_LARGE to "Large"
                            ).forEach { (sizeKey, label) ->
                                val isSelected = keySize == sizeKey
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                        )
                                        .clickable {
                                            keySize = sizeKey
                                            userPrefs.setKeySizePreference(sizeKey)
                                        }
                                        .semantics {
                                            role = Role.RadioButton
                                            contentDescription = "$label key size${if (isSelected) ", selected" else ""}"
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.ancientKeysColors.keyText
                                    )
                                }
                            }
                        }
                    }
                }

                // Haptic Feedback Setting Row
                SettingToggleRow(
                    icon = "📳",
                    title = "Haptic Feedback",
                    subtitle = "Gentle vibration on key press.",
                    checked = hapticsEnabled,
                    onCheckedChange = { checked ->
                        hapticsEnabled = checked
                        userPrefs.setHapticsEnabled(checked)
                    }
                )

                // Show Key Labels / Transliterations Setting Row
                SettingToggleRow(
                    icon = "Aa",
                    title = "Show Key Labels",
                    subtitle = "Show transliterations (e.g., A, B, G).",
                    checked = showKeyLabels,
                    onCheckedChange = { checked ->
                        showKeyLabels = checked
                        userPrefs.setShowKeyLabelsEnabled(checked)
                    }
                )
            }

            HorizontalDivider(
                color = MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            // ==========================================
            // 3. LAYOUT PREFERENCES SECTION
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SettingsSectionHeader(
                    icon = "⊞",
                    title = "Layout Preferences",
                    subtitle = "Configure script behavior and startup writing system."
                )

                // Ask before switching scripts toggle
                SettingToggleRow(
                    icon = "⚙️",
                    title = "Ask before switching scripts",
                    subtitle = "Confirm when changing keyboard scripts.",
                    checked = confirmScriptSwitch,
                    onCheckedChange = { checked ->
                        confirmScriptSwitch = checked
                        userPrefs.setConfirmScriptSwitchEnabled(checked)
                    }
                )

                // Default Script Selector Row
                AncientKeysCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                    borderColor = MaterialTheme.ancientKeysColors.cardBorder
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🏛️", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = "Default Script",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.ancientKeysColors.keyText
                                )
                                Text(
                                    text = "Initial script loaded on keyboard open.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.ancientKeysColors.chipText
                                )
                            }
                        }

                        // Dropdown Selector Button
                        Box {
                            OutlinedButton(
                                onClick = { isDefaultScriptMenuExpanded = true },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.ancientKeysColors.chipBackground,
                                    contentColor = MaterialTheme.ancientKeysColors.keyText
                                )
                            ) {
                                Text(
                                    text = "$defaultScriptDisplay ▾",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            DropdownMenu(
                                expanded = isDefaultScriptMenuExpanded,
                                onDismissRequest = { isDefaultScriptMenuExpanded = false }
                            ) {
                                // 7 script choices: Greek, Egyptian, Brahmi, Kharosthi, Phoenician, Runic, English
                                listOf(
                                    "greek" to "🏛️ Ancient Greek",
                                    "egyptian" to "𓀀 Egyptian Hieroglyphs",
                                    "brahmi" to "𑀅 Ashokan Brahmi",
                                    "kharosthi" to "𐨀 Kharosthi",
                                    "phoenician" to "𐤀 Phoenician",
                                    "runic" to "ᚠ Elder Futhark",
                                    "english" to "⌨️ English QWERTY"
                                ).forEach { (id, label) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = label,
                                                fontWeight = if (defaultScriptId == id) FontWeight.Bold else FontWeight.Normal,
                                                color = if (defaultScriptId == id) MaterialTheme.colorScheme.primary else MaterialTheme.ancientKeysColors.keyText
                                            )
                                        },
                                        onClick = {
                                            defaultScriptId = id
                                            userPrefs.setDefaultScriptId(id)
                                            isDefaultScriptMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            // ==========================================
            // 4. HELP & SYSTEM UTILITIES SECTION
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingsSectionHeader(
                    icon = "🔧",
                    title = "System & Information",
                    subtitle = "Keyboard activation, diagnostics, and licenses."
                )

                // Setup Guide Action
                UtilityActionRow(
                    icon = "🚀",
                    title = "Reopen Keyboard Setup Guide",
                    subtitle = "Step-by-step instructions to enable AncientKeys IME",
                    onClick = onOpenOnboarding
                )

                // Android Keyboard Settings Action
                UtilityActionRow(
                    icon = "⚙️",
                    title = "Open Android Keyboard Settings",
                    subtitle = "Manage input methods and system keyboard list",
                    onClick = {
                        try {
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } catch (_: Exception) {
                        }
                    }
                )

                // About, Privacy & OFL Fonts Action
                UtilityActionRow(
                    icon = "ℹ️",
                    title = "About AncientKeys, Privacy & OFL Fonts",
                    subtitle = "Open source licenses, offline privacy policy, and OFL notices",
                    onClick = onOpenAbout
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==========================================
// COMPONENT HELPERS
// ==========================================

/**
 * Top bar matching Screen 4 of the approved visual identity.
 */
@Composable
private fun SettingsTopBar(
    title: String,
    onBackClick: () -> Unit
) {
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

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.ancientKeysColors.keyText
            )
        }
    }
}

/**
 * Section Header with leading icon and subtitle.
 */
@Composable
private fun SettingsSectionHeader(
    icon: String,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = icon, fontSize = 18.sp)
        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.ancientKeysColors.keyText
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.ancientKeysColors.chipText
            )
        }
    }
}

/**
 * Miniature keyboard preview card matching Screen 4 of reference image.
 */
@Composable
private fun ThemePreviewCard(
    title: String,
    themeId: String,
    isSelected: Boolean,
    cardBgColor: Color,
    keyBgColor: Color,
    keyBorderColor: Color,
    accentKeyColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(104.dp)
            .clickable(onClick = onClick)
            .semantics {
                role = Role.RadioButton
                contentDescription = "$title theme${if (isSelected) ", active selected theme" else ""}"
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(cardBgColor)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.ancientKeysColors.cardBorder,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(6.dp)
        ) {
            // Miniature Keyboard Keycaps graphic
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = if (isSelected) 6.dp else 3.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Row 1: 5 tiny keys
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(5) {
                        MiniKey(keyBgColor, keyBorderColor)
                    }
                }
                // Row 2: 5 tiny keys
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(5) {
                        MiniKey(keyBgColor, keyBorderColor)
                    }
                }
                // Row 3: 2 tiny keys + spacebar + accent Enter key
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MiniKey(keyBgColor, keyBorderColor)
                    // Mini Spacebar
                    Box(
                        modifier = Modifier
                            .width(26.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(keyBgColor)
                            .border(0.5.dp, keyBorderColor, RoundedCornerShape(2.dp))
                    )
                    // Mini Accent / Enter key
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(accentKeyColor)
                    )
                }
            }

            // Top-right checkmark badge if selected
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.ancientKeysColors.keyText,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MiniKey(keyBgColor: Color, keyBorderColor: Color) {
    Box(
        modifier = Modifier
            .size(width = 11.dp, height = 10.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(keyBgColor)
            .border(0.5.dp, keyBorderColor, RoundedCornerShape(2.dp))
    )
}

/**
 * Reusable Setting Row with an icon, title, description, and Switch toggle.
 */
@Composable
private fun SettingToggleRow(
    icon: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    AncientKeysCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.ancientKeysColors.cardBackground,
        borderColor = MaterialTheme.ancientKeysColors.cardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = icon, fontSize = 16.sp)
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.ancientKeysColors.chipText,
                    uncheckedTrackColor = MaterialTheme.ancientKeysColors.chipBackground
                )
            )
        }
    }
}

/**
 * Reusable utility action row for Help, System Settings, and About links.
 */
@Composable
private fun UtilityActionRow(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    AncientKeysCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        containerColor = MaterialTheme.ancientKeysColors.cardBackground,
        borderColor = MaterialTheme.ancientKeysColors.cardBorder,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = icon, fontSize = 16.sp)
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                }
            }

            Text(
                text = "›",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.ancientKeysColors.chipText
            )
        }
    }
}

