package com.ancientkeys.keyboard.ime.ui

import com.ancientkeys.keyboard.ime.data.KeyboardMode
import com.ancientkeys.keyboard.model.HistoricalScript

/**
 * Computes responsive sizing, heights, and compact labeling for the AncientKeys IME.
 *
 * Ensures that the keyboard:
 * - Fits comfortably in both portrait (240–360 dp) and landscape (160–205 dp) orientations.
 * - Prevents the Bottom Control Row (Enter/Send, Backspace, Space) from clipping or being pushed off-screen.
 * - Dynamically adapts to composition strips in Easy Phonetic and Romanized modes.
 * - Provides compact labels for narrow utility bars on 320–360 dp screens.
 */
object ImeDimensionsHelper {

    const val DEFAULT_PORTRAIT_HEIGHT_DP = 270
    const val MIN_PORTRAIT_HEIGHT_DP = 230
    const val MAX_PORTRAIT_HEIGHT_DP = 360

    const val MIN_LANDSCAPE_HEIGHT_DP = 160
    const val MAX_LANDSCAPE_HEIGHT_DP = 205

    const val MIN_ENTER_KEY_WIDTH_DP = 44
    const val MIN_BACKSPACE_KEY_WIDTH_DP = 42
    const val MIN_SPACE_KEY_WIDTH_DP = 70

    /**
     * Height of the compact phonetic and transliteration suggestion strip in portrait (dp).
     */
    const val PHONETIC_STRIP_PORTRAIT_HEIGHT_DP = 40

    /**
     * Height of the compact phonetic and transliteration suggestion strip in landscape (dp).
     */
    const val PHONETIC_STRIP_LANDSCAPE_HEIGHT_DP = 32

    /**
     * Computes the adaptive total keyboard height in density-independent pixels (dp).
     *
     * In landscape, keyboard height is capped proportionally to screen height to ensure the
     * host application's focused text field remains visible and interactable.
     * In portrait, the user's preferred height is respected while giving slight expansion
     * to phonetic and romanized composition modes if screen height permits.
     */
    fun computeAdaptiveKeyboardHeight(
        screenHeightDp: Int,
        isLandscape: Boolean,
        keyboardMode: KeyboardMode,
        userPreferenceHeightDp: Int = DEFAULT_PORTRAIT_HEIGHT_DP
    ): Int {
        if (isLandscape) {
            val proportional = (screenHeightDp * 0.52f).toInt()
            return proportional.coerceIn(MIN_LANDSCAPE_HEIGHT_DP, MAX_LANDSCAPE_HEIGHT_DP)
        }

        val baseHeight = userPreferenceHeightDp.coerceIn(MIN_PORTRAIT_HEIGHT_DP, MAX_PORTRAIT_HEIGHT_DP)
        val maxAllowedHeight = (screenHeightDp * 0.48f).toInt()

        val modeAdjustedHeight = when (keyboardMode) {
            KeyboardMode.EASY_PHONETIC, KeyboardMode.ROMANIZED -> baseHeight + 20
            else -> baseHeight
        }

        return modeAdjustedHeight.coerceIn(
            MIN_PORTRAIT_HEIGHT_DP,
            maxOf(MIN_PORTRAIT_HEIGHT_DP, maxAllowedHeight)
        )
    }

    /**
     * Computes the ergonomic bottom inset padding for the keyboard surface.
     *
     * In gesture navigation, Android navigation bar insets are typically 12–24 dp.
     * We constrain the bottom chin to 8–14 dp so the spacebar has safe clearance from
     * the system home/app-switching gesture area without creating an unsightly dead gap.
     *
     * In 3-button navigation (insets > 24 dp), the system hardware/navigation bar sits at
     * the bottom of the screen. We apply the required clearance without extra artificial padding.
     *
     * In landscape, bottom chin is minimized (0–4 dp) while horizontal cutouts are handled.
     */
    fun computeBottomChinPaddingDp(
        systemBottomInsetDp: Float,
        isLandscape: Boolean
    ): Float {
        if (isLandscape) {
            return 0f
        }
        return when {
            systemBottomInsetDp <= 0f -> 4f // Minimum aesthetic clearance
            systemBottomInsetDp in 1f..24f -> systemBottomInsetDp.coerceIn(8f, 14f) // Ergonomic gesture chin
            else -> systemBottomInsetDp.coerceAtMost(48f) // 3-button navigation bar clearance
        }
    }

    /**
     * Returns an abbreviated label for top utility bar buttons to prevent horizontal overflow on narrow displays.
     */
    fun getCompactScriptName(script: HistoricalScript): String {
        return when (script.id.lowercase().trim()) {
            "egyptian" -> "Egyptian"
            "brahmi" -> "Brahmi"
            "runic" -> "Runic"
            "greek" -> "Greek"
            "phoenician" -> "Phoenician"
            "kharosthi" -> "Kharosthi"
            else -> script.name.split(" ").firstOrNull() ?: script.name
        }
    }

    /**
     * Computes the target height for individual key rows in English and Numbers modes.
     */
    fun computeKeyRowHeight(isLandscape: Boolean): Int {
        return if (isLandscape) 34 else 44
    }

    /**
     * Computes the target height for the bottom control row (Space, Enter, Backspace).
     */
    fun computeBottomRowHeight(isLandscape: Boolean): Int {
        return if (isLandscape) 36 else 44
    }
}

