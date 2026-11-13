package com.ancientkeys.keyboard.ime.ui

/**
 * Handles script-switching evaluation and confirmation logic for AncientKeys IME.
 *
 * Ensures:
 * - Switching to the already-active script is ignored without confirmation.
 * - When confirmation is enabled in UserPreferences, a confirmation step is required.
 * - When confirmation is disabled, script switching is immediate.
 * - Fast English QWERTY and Numbers/Symbols mode toggles remain uninterrupted.
 */
object ScriptSwitchHandler {

    enum class Action {
        /** The selected script is already active; no action or prompt needed. */
        IGNORE,

        /** Switch to the target script immediately without confirmation. */
        SWITCH_IMMEDIATELY,

        /** Prompt the user with a compact, keyboard-contained confirmation interface. */
        PROMPT_CONFIRMATION
    }

    /**
     * Evaluates the appropriate action when the user attempts to switch scripts.
     *
     * @param currentScriptId The currently active script identifier.
     * @param targetScriptId The chosen script identifier.
     * @param isConfirmationEnabled Whether the "Ask before switching scripts" preference is active.
     */
    fun evaluateScriptSwitch(
        currentScriptId: String,
        targetScriptId: String,
        isConfirmationEnabled: Boolean
    ): Action {
        if (currentScriptId.equals(targetScriptId.trim(), ignoreCase = true)) {
            return Action.IGNORE
        }
        return if (isConfirmationEnabled) {
            Action.PROMPT_CONFIRMATION
        } else {
            Action.SWITCH_IMMEDIATELY
        }
    }
}

