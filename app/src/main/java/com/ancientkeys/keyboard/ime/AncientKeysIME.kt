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
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
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

    // Observable state for active theme to reactively handle theme changes while IME is open
    private val currentThemeState = mutableStateOf(UserPreferencesRepository.DEFAULT_THEME)
    private var themeUnregisterCallback: (() -> Unit)? = null

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
        ensureDecorViewOwners()

        val userPrefs = UserPreferencesRepository.getInstance(this)
        currentThemeState.value = userPrefs.getThemePreference()
        themeUnregisterCallback = userPrefs.registerThemeChangeListener { newTheme ->
            currentThemeState.value = newTheme
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        currentEditorInfoState.value = attribute
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        ensureDecorViewOwners()
        currentEditorInfoState.value = info
        currentThemeState.value = UserPreferencesRepository.getInstance(this).getThemePreference()
    }

    override fun onConfigureWindow(win: Window, isFullscreen: Boolean, isCandidatesOnly: Boolean) {
        super.onConfigureWindow(win, isFullscreen, isCandidatesOnly)
        ensureDecorViewOwners(win)
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
            // Bind view tree owners locally to the ComposeView before composition or window attachment
            setViewTreeLifecycleOwner(this@AncientKeysIME)
            setViewTreeViewModelStoreOwner(this@AncientKeysIME)
            setViewTreeSavedStateRegistryOwner(this@AncientKeysIME)

            // Preserve composition context across show/hide cycles until the service itself is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(lifecycle))

            // Re-assert decor view and root view owners upon window attachment
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    ensureDecorViewOwners()
                    v.rootView?.let { root ->
                        root.setViewTreeLifecycleOwner(this@AncientKeysIME)
                        root.setViewTreeViewModelStoreOwner(this@AncientKeysIME)
                        root.setViewTreeSavedStateRegistryOwner(this@AncientKeysIME)
                    }
                }

                override fun onViewDetachedFromWindow(v: View) {}
            })

            setContent {
                val themePref = currentThemeState.value
                val isDark = when (themePref) {
                    UserPreferencesRepository.THEME_LIGHT -> false
                    UserPreferencesRepository.THEME_DARK -> true
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
                            ImeInputHelper.handleBackspace(currentInputConnection, isRepeating = false)
                        },
                        onBackspaceRepeat = {
                            ImeInputHelper.handleBackspace(currentInputConnection, isRepeating = true)
                        },
                        onEnterClick = {
                            ImeInputHelper.handleEnter(
                                currentInputConnection,
                                currentInputEditorInfo,
                                sendDefaultAction = { sendDefaultEditorAction(it); true },
                                sendDownUpKey = { sendDownUpKeyEvents(it) }
                            )
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
        dispatchWindowShownLifecycle()
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        dispatchWindowHiddenLifecycle()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ensureDecorViewOwners()
    }

    override fun onDestroy() {
        themeUnregisterCallback?.invoke()
        themeUnregisterCallback = null
        super.onDestroy()
        dispatchDestroyLifecycle()
        appViewModelStore.clear()
    }

    internal fun dispatchWindowShownLifecycle() {
        for (event in computeWindowShownEvents(lifecycleRegistry.currentState)) {
            lifecycleRegistry.handleLifecycleEvent(event)
        }
    }

    internal fun dispatchWindowHiddenLifecycle() {
        for (event in computeWindowHiddenEvents(lifecycleRegistry.currentState)) {
            lifecycleRegistry.handleLifecycleEvent(event)
        }
    }

    internal fun dispatchDestroyLifecycle() {
        for (event in computeWindowHiddenEvents(lifecycleRegistry.currentState)) {
            lifecycleRegistry.handleLifecycleEvent(event)
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    companion object {
        fun computeWindowShownEvents(currentState: Lifecycle.State): List<Lifecycle.Event> {
            return when (currentState) {
                Lifecycle.State.INITIALIZED,
                Lifecycle.State.CREATED -> listOf(Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME)
                Lifecycle.State.STARTED -> listOf(Lifecycle.Event.ON_RESUME)
                else -> emptyList()
            }
        }

        fun computeWindowHiddenEvents(currentState: Lifecycle.State): List<Lifecycle.Event> {
            val events = mutableListOf<Lifecycle.Event>()
            if (currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                events.add(Lifecycle.Event.ON_PAUSE)
            }
            if (currentState.isAtLeast(Lifecycle.State.STARTED)) {
                events.add(Lifecycle.Event.ON_STOP)
            }
            return events
        }
    }

    /**
     * Binds this service as the ViewTreeLifecycleOwner, ViewTreeViewModelStoreOwner,
     * and ViewTreeSavedStateRegistryOwner to the IME window's decor view and root view.
     *
     * This is critical: Jetpack Compose's WindowRecomposer inspects the window's root
     * (DecorView/parentPanel), and will throw an IllegalStateException if these owners
     * are not present on the window decor view.
     */
    private fun ensureDecorViewOwners(targetWindow: Window? = null) {
        val win = targetWindow ?: window?.window
        win?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
            decorView.rootView?.let { root ->
                if (root !== decorView) {
                    root.setViewTreeLifecycleOwner(this)
                    root.setViewTreeViewModelStoreOwner(this)
                    root.setViewTreeSavedStateRegistryOwner(this)
                }
            }
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
