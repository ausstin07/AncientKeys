package com.ancientkeys.keyboard.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.ancientkeys.keyboard.storage.UserPreferencesRepository

// Composition local for AncientKeys custom design tokens (keyboard, cards, glyphs)
val LocalAncientKeysCustomColors = staticCompositionLocalOf { SageCustomColors }

/**
 * Accessor for AncientKeys custom colors on MaterialTheme.
 */
val MaterialTheme.ancientKeysColors: AncientKeysCustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAncientKeysCustomColors.current

// ==========================================
// 1. Minimal Sage Color Scheme (Default)
// ==========================================
private val SageColorScheme = lightColorScheme(
    primary = SagePrimary,
    onPrimary = SageOnPrimary,
    primaryContainer = SagePrimaryContainer,
    onPrimaryContainer = SageOnPrimaryContainer,
    secondary = SageSecondary,
    onSecondary = SageOnSecondary,
    secondaryContainer = SageSecondaryContainer,
    onSecondaryContainer = SageOnSecondaryContainer,
    tertiary = SageTertiary,
    onTertiary = SageOnTertiary,
    tertiaryContainer = SageTertiaryContainer,
    onTertiaryContainer = SageOnTertiaryContainer,
    background = SageBackground,
    onBackground = SageOnBackground,
    surface = SageSurface,
    onSurface = SageOnSurface,
    surfaceVariant = SageSurfaceVariant,
    onSurfaceVariant = SageOnSurfaceVariant,
    outline = SageOutline,
    outlineVariant = SageOutlineVariant
)

// ==========================================
// 2. Light Color Scheme
// ==========================================
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant
)

// ==========================================
// 3. Dark Color Scheme
// ==========================================
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

// ==========================================
// 4. Scholar / Sepia Color Scheme
// ==========================================
private val SepiaColorScheme = lightColorScheme(
    primary = SepiaPrimary,
    onPrimary = SepOnPrimary,
    primaryContainer = SepiaPrimaryContainer,
    onPrimaryContainer = SepiaOnPrimaryContainer,
    secondary = SepiaSecondary,
    onSecondary = SepiaOnSecondary,
    secondaryContainer = SepiaSecondaryContainer,
    onSecondaryContainer = SepiaOnSecondaryContainer,
    tertiary = SepiaTertiary,
    onTertiary = SepiaOnTertiary,
    tertiaryContainer = SepiaTertiaryContainer,
    onTertiaryContainer = SepiaOnTertiaryContainer,
    background = SepiaBackground,
    onBackground = SepiaOnBackground,
    surface = SepiaSurface,
    onSurface = SepiaOnSurface,
    surfaceVariant = SepiaSurfaceVariant,
    onSurfaceVariant = SepiaOnSurfaceVariant,
    outline = SepiaOutline,
    outlineVariant = SepiaOutlineVariant
)

/**
 * Resolves a stored string preference to an AncientKeysThemeVariant.
 */
fun resolveThemeVariant(themePref: String?, isSystemDark: Boolean): AncientKeysThemeVariant {
    return when (themePref?.lowercase()?.trim()) {
        UserPreferencesRepository.THEME_SAGE -> AncientKeysThemeVariant.SAGE
        UserPreferencesRepository.THEME_LIGHT -> AncientKeysThemeVariant.LIGHT
        UserPreferencesRepository.THEME_DARK -> AncientKeysThemeVariant.DARK
        UserPreferencesRepository.THEME_SEPIA -> AncientKeysThemeVariant.SEPIA
        UserPreferencesRepository.THEME_SYSTEM -> if (isSystemDark) AncientKeysThemeVariant.DARK else AncientKeysThemeVariant.SAGE
        else -> AncientKeysThemeVariant.SAGE
    }
}

/**
 * AncientKeys Theme Composable.
 *
 * Implements "Scholarly Calm" across the main app and InputMethodService.
 * Dynamic color is false by default so our curated historical palettes are preserved.
 */
@Composable
fun AncientKeysTheme(
    themePreference: String? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val activeThemePref = themePreference ?: remember(context) {
        try {
            UserPreferencesRepository.getInstance(context).getThemePreference()
        } catch (_: Exception) {
            UserPreferencesRepository.THEME_SAGE
        }
    }

    val variant = resolveThemeVariant(activeThemePref, darkTheme)

    val (colorScheme, customColors) = when (variant) {
        AncientKeysThemeVariant.SAGE -> SageColorScheme to SageCustomColors
        AncientKeysThemeVariant.LIGHT -> LightColorScheme to LightCustomColors
        AncientKeysThemeVariant.DARK -> DarkColorScheme to DarkCustomColors
        AncientKeysThemeVariant.SEPIA -> SepiaColorScheme to SepiaCustomColors
        AncientKeysThemeVariant.SYSTEM -> {
            if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val dynScheme = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                val dynCustom = if (darkTheme) DarkCustomColors else SageCustomColors
                dynScheme to dynCustom
            } else if (darkTheme) {
                DarkColorScheme to DarkCustomColors
            } else {
                SageColorScheme to SageCustomColors
            }
        }
    }

    CompositionLocalProvider(LocalAncientKeysCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
