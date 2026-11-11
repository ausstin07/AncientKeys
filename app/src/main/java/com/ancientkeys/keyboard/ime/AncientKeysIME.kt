package com.ancientkeys.keyboard.ime

import android.content.Context
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowCompat
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.ancientkeys.keyboard.ime.ui.ImeKeyboardLayout
import com.ancientkeys.keyboard.ui.theme.AncientKeysTheme

/**
 * Native Android InputMethodService for AncientKeys.
 *
 * Provides a system-wide alternative keyboard for six historical writing systems:
 * Egyptian Hieroglyphs, Brahmi, Ancient Greek, Elder Futhark Runic, Phoenician, and Kharosthi.
 *
 * Privacy & Security Guarantees:
 * - 100% Offline: zero network permissions requested or used.
 * - No Keystroke Logging: keystrokes are never logged, stored, or analyzed.
 * - No Clipboard Sniffing: does not read user clipboard contents.
 * - Non-Default: AncientKeys is never set as default automatically.
 * - Sensitive Field Protection: automatically recognizes password/PIN fields and disables
 *   character input, recommending the user switch to their standard keyboard.
 *
 * Lifecycle & Stability:
 * - Explicitly binds ViewTree owners (LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner)
 *   to both the IME window decor view and the local ComposeView to prevent WindowRecomposer crashes.
 * - Employs DisposeOnViewTreeLifecycleDestroyed strategy to survive repeated show/hide cycles.
 */
class AncientKeysIME : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val appViewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    // Observable state for the current field's EditorInfo to reactively handle focus changes
    private val currentEditorInfoState = mutableStateOf<EditorInfo?>(null)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        currentEditorInfoState.value = attribute
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        currentEditorInfoState.value = info
    }

    override fun onConfigureWindow(win: Window, isFullscreen: Boolean, isCandidatesOnly: Boolean) {
        super.onConfigureWindow(win, isFullscreen, isCandidatesOnly)
        ensureDecorViewOwners()
        try {
            WindowCompat.setDecorFitsSystemWindows(win, false)
        } catch (_: Exception) {
        }
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    override fun onCreateInputView(): View {
        ensureDecorViewOwners()

        val composeView = ComposeView(this).apply {
            // Preserve composition context across show/hide cycles until service is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            // Bind view tree owners locally to the ComposeView
            setViewTreeLifecycleOwner(this@AncientKeysIME)
            setViewTreeViewModelStoreOwner(this@AncientKeysIME)
            setViewTreeSavedStateRegistryOwner(this@AncientKeysIME)

            setContent {
                val userPrefs = androidx.compose.runtime.remember {
                    com.ancientkeys.keyboard.storage.UserPreferencesRepository.getInstance(this@AncientKeysIME)
                }
                val themePref = userPrefs.getThemePreference()
                val isDark = when (themePref) {
                    com.ancientkeys.keyboard.storage.UserPreferencesRepository.THEME_LIGHT -> false
                    com.ancientkeys.keyboard.storage.UserPreferencesRepository.THEME_DARK -> true
                    else -> androidx.compose.foundation.isSystemInDarkTheme()
                }

                AncientKeysTheme(themePreference = themePref, darkTheme = isDark) {
                    ImeKeyboardLayout(
                        editorInfo = currentEditorInfoState.value,
                        onKeyClick = { key ->
                            ImeInputHelper.commitCharacter(currentInputConnection, key.character)
                        },
                        onSpaceClick = {
                            ImeInputHelper.handleSpace(currentInputConnection)
                        },
                        onBackspaceClick = {
                            ImeInputHelper.handleBackspace(currentInputConnection)
                        },
                        onEnterClick = {
                            ImeInputHelper.handleEnter(currentInputConnection, currentInputEditorInfo)
                        },
                        onSwitchKeyboardClick = {
                            switchKeyboard()
                        }
                    )
                }
            }
        }
        return composeView
    }

    override fun onWindowShown() {
        super.onWindowShown()
        ensureDecorViewOwners()

        // Ensure standard Android lifecycle transitions: CREATED -> STARTED -> RESUMED
        if (lifecycleRegistry.currentState == Lifecycle.State.CREATED ||
            lifecycleRegistry.currentState == Lifecycle.State.INITIALIZED
        ) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        } else if (lifecycleRegistry.currentState == Lifecycle.State.STARTED) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        // Ensure standard Android lifecycle transitions: RESUMED -> PAUSED -> STOPPED
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ensureDecorViewOwners()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        appViewModelStore.clear()
    }

    /**
     * Binds this service as the ViewTreeLifecycleOwner, ViewTreeViewModelStoreOwner,
     * and ViewTreeSavedStateRegistryOwner to the IME window's decor view.
     *
     * This is critical: Jetpack Compose's WindowRecomposer inspects the window's root
     * (DecorView/parentPanel), and will throw an IllegalStateException if these owners
     * are not present on the window decor view.
     */
    private fun ensureDecorViewOwners() {
        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }
    }

    /**
     * Allows the user to switch seamlessly to another installed keyboard (e.g. Gboard, Samsung Keyboard).
     */
    private fun switchKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        // Attempt direct switch to next method on API 28+; if not available, show system picker
        val switched = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                switchToNextInputMethod(false)
            } catch (_: Exception) {
                false
            }
        } else {
            false
        }

        if (!switched) {
            try {
                imm?.showInputMethodPicker()
            } catch (_: Exception) {
            }
        }
    }
}
