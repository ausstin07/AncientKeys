package com.ancientkeys.keyboard.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Extended color tokens for AncientKeys keyboard, cards, and specialized historical UI.
 */
@Immutable
data class AncientKeysCustomColors(
    val keyboardBackground: Color,
    val keyBackground: Color,
    val keyActionBackground: Color,
    val keyPrimaryActionBackground: Color,
    val keyText: Color,
    val keyActionText: Color,
    val keyPrimaryActionText: Color,
    val keyBorder: Color,
    val selectedKeyBackground: Color,
    val glyphAccent: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val chipBackground: Color,
    val chipSelectedBackground: Color,
    val chipText: Color,
    val chipSelectedText: Color
)

// ==========================================
// 1. Minimal Sage Palette (Default / Highlight)
// Design identity: Scholarly Calm
// ==========================================
val SagePrimary = Color(0xFF4A7C59)
val SageOnPrimary = Color(0xFFFFFFFF)
val SagePrimaryContainer = Color(0xFFEAF2EC)
val SageOnPrimaryContainer = Color(0xFF1C2B20)

val SageSecondary = Color(0xFF5B7563)
val SageOnSecondary = Color(0xFFFFFFFF)
val SageSecondaryContainer = Color(0xFFDEE7E0)
val SageOnSecondaryContainer = Color(0xFF1C2B20)

val SageTertiary = Color(0xFF3E6B48)
val SageOnTertiary = Color(0xFFFFFFFF)
val SageTertiaryContainer = Color(0xFFD5E8DA)
val SageOnTertiaryContainer = Color(0xFF112316)

val SageBackground = Color(0xFFF8FAF8)
val SageOnBackground = Color(0xFF1C2B20)

val SageSurface = Color(0xFFFFFFFF)
val SageOnSurface = Color(0xFF1C2B20)
val SageSurfaceVariant = Color(0xFFEDF3EE)
val SageOnSurfaceVariant = Color(0xFF526657)

val SageOutline = Color(0xFFD1DDD4)
val SageOutlineVariant = Color(0xFFE2ECE4)

val SageCustomColors = AncientKeysCustomColors(
    keyboardBackground = SageSurfaceVariant,
    keyBackground = SageSurface,
    keyActionBackground = SageSecondaryContainer,
    keyPrimaryActionBackground = SagePrimary,
    keyText = SageOnSurface,
    keyActionText = SageOnSecondaryContainer,
    keyPrimaryActionText = SageOnPrimary,
    keyBorder = SageOutline,
    selectedKeyBackground = SageTertiaryContainer,
    glyphAccent = SagePrimary,
    cardBackground = SageSurface,
    cardBorder = SageOutline,
    chipBackground = SageSurface,
    chipSelectedBackground = SagePrimary,
    chipText = SageOnSurfaceVariant,
    chipSelectedText = SageOnPrimary
)

// ==========================================
// 2. Light Palette
// Clean neutral whites and forest accents
// ==========================================
val LightPrimary = Color(0xFF2E6A44)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFE5F0E8)
val LightOnPrimaryContainer = Color(0xFF0E2316)

val LightSecondary = Color(0xFF536356)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFD7E8D9)
val LightOnSecondaryContainer = Color(0xFF111F15)

val LightTertiary = Color(0xFF386568)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFBCEBEF)
val LightOnTertiaryContainer = Color(0xFF002022)

val LightBackground = Color(0xFFFAFAFA)
val LightOnBackground = Color(0xFF1A1C1E)

val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF1A1C1E)
val LightSurfaceVariant = Color(0xFFEFF0F1)
val LightOnSurfaceVariant = Color(0xFF44474E)

val LightOutline = Color(0xFFD8D9DC)
val LightOutlineVariant = Color(0xFFE3E4E7)

val LightCustomColors = AncientKeysCustomColors(
    keyboardBackground = LightSurfaceVariant,
    keyBackground = LightSurface,
    keyActionBackground = LightSecondaryContainer,
    keyPrimaryActionBackground = LightPrimary,
    keyText = LightOnSurface,
    keyActionText = LightOnSecondaryContainer,
    keyPrimaryActionText = LightOnPrimary,
    keyBorder = LightOutline,
    selectedKeyBackground = LightPrimaryContainer,
    glyphAccent = LightPrimary,
    cardBackground = LightSurface,
    cardBorder = LightOutline,
    chipBackground = LightSurface,
    chipSelectedBackground = LightPrimary,
    chipText = LightOnSurfaceVariant,
    chipSelectedText = LightOnPrimary
)

// ==========================================
// 3. Dark Palette
// Deep charcoal with organic night-sage contrast
// ==========================================
val DarkPrimary = Color(0xFF8FD8A0)
val DarkOnPrimary = Color(0xFF00391A)
val DarkPrimaryContainer = Color(0xFF234E32)
val DarkOnPrimaryContainer = Color(0xFFAAFBBA)

val DarkSecondary = Color(0xFFB7CCBA)
val DarkOnSecondary = Color(0xFF233528)
val DarkSecondaryContainer = Color(0xFF394C3E)
val DarkOnSecondaryContainer = Color(0xFFD3E8D6)

val DarkTertiary = Color(0xFFA0D0D3)
val DarkOnTertiary = Color(0xFF00373A)
val DarkTertiaryContainer = Color(0xFF1F4E51)
val DarkOnTertiaryContainer = Color(0xFFBCEBEF)

val DarkBackground = Color(0xFF111412)
val DarkOnBackground = Color(0xFFE1E3DF)

val DarkSurface = Color(0xFF181D1A)
val DarkOnSurface = Color(0xFFE1E3DF)
val DarkSurfaceVariant = Color(0xFF1E2520)
val DarkOnSurfaceVariant = Color(0xFFA0A5A0)

val DarkOutline = Color(0xFF354037)
val DarkOutlineVariant = Color(0xFF28322A)

val DarkCustomColors = AncientKeysCustomColors(
    keyboardBackground = DarkSurfaceVariant,
    keyBackground = Color(0xFF252D27),
    keyActionBackground = DarkSecondaryContainer,
    keyPrimaryActionBackground = DarkPrimary,
    keyText = DarkOnSurface,
    keyActionText = DarkOnSecondaryContainer,
    keyPrimaryActionText = DarkOnPrimary,
    keyBorder = DarkOutline,
    selectedKeyBackground = DarkPrimaryContainer,
    glyphAccent = DarkPrimary,
    cardBackground = DarkSurface,
    cardBorder = DarkOutline,
    chipBackground = DarkSurfaceVariant,
    chipSelectedBackground = DarkPrimary,
    chipText = DarkOnSurfaceVariant,
    chipSelectedText = DarkOnPrimary
)

// ==========================================
// 4. Scholar / Sepia Palette
// Archival papyrus and antiquarian vellum
// ==========================================
val SepiaPrimary = Color(0xFF7A5C38)
val SepOnPrimary = Color(0xFFFFFFFF)
val SepiaPrimaryContainer = Color(0xFFF4EBD9)
val SepiaOnPrimaryContainer = Color(0xFF2C2012)

val SepiaSecondary = Color(0xFF6C5C4E)
val SepiaOnSecondary = Color(0xFFFFFFFF)
val SepiaSecondaryContainer = Color(0xFFEADECC)
val SepiaOnSecondaryContainer = Color(0xFF251A10)

val SepiaTertiary = Color(0xFF5D623C)
val SepiaOnTertiary = Color(0xFFFFFFFF)
val SepiaTertiaryContainer = Color(0xFFE2E7B7)
val SepiaOnTertiaryContainer = Color(0xFF1A1E02)

val SepiaBackground = Color(0xFFFAF6EE)
val SepiaOnBackground = Color(0xFF2C2218)

val SepiaSurface = Color(0xFFFFFDF8)
val SepiaOnSurface = Color(0xFF2C2218)
val SepiaSurfaceVariant = Color(0xFFF2EADA)
val SepiaOnSurfaceVariant = Color(0xFF6B5B4D)

val SepiaOutline = Color(0xFFDECDB8)
val SepiaOutlineVariant = Color(0xFFEBE0D0)

val SepiaCustomColors = AncientKeysCustomColors(
    keyboardBackground = SepiaSurfaceVariant,
    keyBackground = SepiaSurface,
    keyActionBackground = SepiaSecondaryContainer,
    keyPrimaryActionBackground = SepiaPrimary,
    keyText = SepiaOnSurface,
    keyActionText = SepiaOnSecondaryContainer,
    keyPrimaryActionText = SepOnPrimary,
    keyBorder = SepiaOutline,
    selectedKeyBackground = SepiaPrimaryContainer,
    glyphAccent = SepiaPrimary,
    cardBackground = SepiaSurface,
    cardBorder = SepiaOutline,
    chipBackground = SepiaSurface,
    chipSelectedBackground = SepiaPrimary,
    chipText = SepiaOnSurfaceVariant,
    chipSelectedText = SepOnPrimary
)

// ==========================================
// 5. Legacy / Deprecated Aliases
// Preserved for absolute backward compatibility
// ==========================================
@Deprecated("Use AncientKeys design system tokens", ReplaceWith("SageKeyAction"))
val Purple80 = Color(0xFFD0BCFF)
@Deprecated("Use AncientKeys design system tokens", ReplaceWith("SageOutline"))
val PurpleGrey80 = Color(0xFFCCC2DC)
@Deprecated("Use AncientKeys design system tokens", ReplaceWith("SagePrimaryContainer"))
val Pink80 = Color(0xFFEFB8C8)

@Deprecated("Use AncientKeys design system tokens", ReplaceWith("SagePrimary"))
val Purple40 = Color(0xFF6650a4)
@Deprecated("Use AncientKeys design system tokens", ReplaceWith("SageTextSecondary"))
val PurpleGrey40 = Color(0xFF625b71)
@Deprecated("Use AncientKeys design system tokens", ReplaceWith("SageSecondary"))
val Pink40 = Color(0xFF7D5260)

@Deprecated("Use AncientKeys design system tokens", ReplaceWith("SagePrimary"))
val AncientGold = Color(0xFFC5A059)
@Deprecated("Use AncientKeys design system tokens", ReplaceWith("SepiaSurfaceVariant"))
val AncientSand = Color(0xFFE8DCC4)
@Deprecated("Use AncientKeys design system tokens", ReplaceWith("DarkBackground"))
val AncientDark = Color(0xFF1E1B18)
@Deprecated("Use AncientKeys design system tokens", ReplaceWith("DarkSurface"))
val AncientCardBg = Color(0xFF2A2724)
