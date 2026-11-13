package com.ancientkeys.keyboard.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * AncientKeys v1.1 Visual Identity: Scholarly Calm.
 *
 * Centralizes layout tokens, spacing, shapes, and accessibility bounds across the application and IME.
 */
enum class AncientKeysThemeVariant(val id: String, val displayName: String) {
    SAGE("sage", "Minimal Sage"),
    LIGHT("light", "Light"),
    DARK("dark", "Dark"),
    SEPIA("sepia", "Scholar / Sepia"),
    SYSTEM("system", "System Default")
}

object AncientKeysDesignTokens {

    object Spacing {
        val none = 0.dp
        val xxs = 2.dp
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val xl = 24.dp
        val xxl = 32.dp
    }

    object Shapes {
        val keyShape = RoundedCornerShape(7.dp)
        val actionKeyShape = RoundedCornerShape(7.dp)
        val pillShape = RoundedCornerShape(24.dp)
        val cardShape = RoundedCornerShape(14.dp)
        val chipShape = RoundedCornerShape(8.dp)
        val dialogShape = RoundedCornerShape(18.dp)
        val previewCardShape = RoundedCornerShape(12.dp)
    }

    object TouchTargets {
        val minTouchTarget = 48.dp
        val minKeyWidth = 42.dp
        val minKeyHeight = 44.dp
        val minIconButton = 40.dp
    }

    object Elevation {
        val flat = 0.dp
        val card = 1.dp
        val key = 1.dp
        val keyElevation = 1.dp
        val activeElevation = 2.dp
        val dropdown = 3.dp
        val modal = 6.dp
    }
}

