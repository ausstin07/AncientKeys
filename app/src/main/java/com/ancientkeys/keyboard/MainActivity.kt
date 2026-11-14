package com.ancientkeys.keyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.storage.UserPreferencesRepository
import com.ancientkeys.keyboard.ui.AboutScreen
import com.ancientkeys.keyboard.ui.CharacterExplorerScreen
import com.ancientkeys.keyboard.ui.HomeScreen
import com.ancientkeys.keyboard.ui.ImeTestLabScreen
import com.ancientkeys.keyboard.ui.LearnScreen
import com.ancientkeys.keyboard.ui.PlaygroundScreen
import com.ancientkeys.keyboard.ui.ResearchWorkspaceScreen
import com.ancientkeys.keyboard.ui.ThemeAndSettingsScreen
import com.ancientkeys.keyboard.ui.theme.AncientKeysTheme
import com.ancientkeys.keyboard.ui.theme.ancientKeysColors

enum class ScreenMode {
    HOME,
    PLAYGROUND,
    EXPLORER,
    TEST_LAB,
    LEARN,
    RESEARCH_WORKSPACE,
    ABOUT,
    SETTINGS
}

data class NavigationDestination(
    val screen: ScreenMode,
    val title: String,
    val icon: String,
    val contentDescription: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val userPrefs = remember { UserPreferencesRepository.getInstance(context) }
            var currentTheme by remember { mutableStateOf(userPrefs.getThemePreference()) }

            DisposableEffect(userPrefs) {
                val unregister = userPrefs.registerThemeChangeListener { newTheme ->
                    currentTheme = newTheme
                }
                onDispose {
                    unregister()
                }
            }

            AncientKeysTheme(themePreference = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AncientKeysApp()
                }
            }
        }
    }
}

@Composable
fun AncientKeysPersistentBottomBar(
    currentScreen: ScreenMode,
    onNavigateTo: (ScreenMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavigationDestination(
            screen = ScreenMode.PLAYGROUND,
            title = "Playground",
            icon = "⌨️",
            contentDescription = "Interactive typing playground"
        ),
        NavigationDestination(
            screen = ScreenMode.EXPLORER,
            title = "Explore",
            icon = "🧭",
            contentDescription = "Character and script explorer"
        ),
        NavigationDestination(
            screen = ScreenMode.LEARN,
            title = "Learn",
            icon = "📖",
            contentDescription = "Historical library and learning guides"
        ),
        NavigationDestination(
            screen = ScreenMode.RESEARCH_WORKSPACE,
            title = "Corpus",
            icon = "📜",
            contentDescription = "Research workspace and inscription corpus"
        ),
        NavigationDestination(
            screen = ScreenMode.SETTINGS,
            title = "Settings",
            icon = "⚙️",
            contentDescription = "Theme, layout, and keyboard settings"
        )
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.ancientKeysColors.keyboardBackground,
        tonalElevation = 3.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.4f))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(64.dp)
        ) {
            items.forEach { dest ->
                val selected = currentScreen == dest.screen
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        onNavigateTo(dest.screen)
                    },
                    icon = {
                        Text(
                            text = dest.icon,
                            fontSize = 18.sp
                        )
                    },
                    label = {
                        Text(
                            text = dest.title,
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                        selectedIconColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText,
                        selectedTextColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                        unselectedIconColor = MaterialTheme.ancientKeysColors.keyText.copy(alpha = 0.7f),
                        unselectedTextColor = MaterialTheme.ancientKeysColors.keyText.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.semantics {
                        this.contentDescription = dest.contentDescription
                    }
                )
            }
        }
    }
}

@Composable
fun AncientKeysApp() {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesRepository.getInstance(context) }
    var screenMode by rememberSaveable { mutableStateOf(ScreenMode.HOME) }
    var activeScriptId by rememberSaveable { mutableStateOf<String?>(null) }
    val saveableStateHolder = rememberSaveableStateHolder()

    val activeScript = activeScriptId?.let { ScriptRegistry.getScriptById(it) }

    // Check IME visibility to hide bottom nav bar when user is typing
    val imeBottomInset = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val isImeOpen = imeBottomInset > 0.dp

    // Bottom navigation is persistent on main sections and Home, hidden during typing or modal sub-screens
    val showBottomBar = !isImeOpen && (screenMode != ScreenMode.ABOUT && screenMode != ScreenMode.TEST_LAB)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                AncientKeysPersistentBottomBar(
                    currentScreen = screenMode,
                    onNavigateTo = { targetScreen ->
                        screenMode = targetScreen
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            when {
                screenMode == ScreenMode.PLAYGROUND -> {
                    BackHandler {
                        screenMode = ScreenMode.HOME
                    }

                    val targetScript = activeScript
                        ?: ScriptRegistry.getScriptById(userPrefs.getDefaultScriptId())
                        ?: ScriptRegistry.allScripts.first()

                    saveableStateHolder.SaveableStateProvider(key = "playground") {
                        var textValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                            mutableStateOf(TextFieldValue(""))
                        }

                        PlaygroundScreen(
                            script = targetScript,
                            textValue = textValue,
                            onTextValueChange = { textValue = it },
                            onBackClick = { screenMode = ScreenMode.HOME }
                        )
                    }
                }
                screenMode == ScreenMode.EXPLORER -> {
                    BackHandler {
                        screenMode = ScreenMode.HOME
                    }

                    val targetScript = activeScript
                        ?: ScriptRegistry.getScriptById(userPrefs.getDefaultScriptId())
                        ?: ScriptRegistry.allScripts.first()

                    saveableStateHolder.SaveableStateProvider(key = "explorer") {
                        CharacterExplorerScreen(
                            script = targetScript,
                            onBackClick = { screenMode = ScreenMode.HOME }
                        )
                    }
                }
                screenMode == ScreenMode.TEST_LAB -> {
                    BackHandler {
                        screenMode = ScreenMode.HOME
                    }

                    ImeTestLabScreen(
                        onBackClick = { screenMode = ScreenMode.HOME }
                    )
                }
                screenMode == ScreenMode.RESEARCH_WORKSPACE -> {
                    BackHandler {
                        screenMode = ScreenMode.HOME
                    }

                    saveableStateHolder.SaveableStateProvider(key = "corpus") {
                        ResearchWorkspaceScreen(
                            onBackClick = { screenMode = ScreenMode.HOME }
                        )
                    }
                }
                screenMode == ScreenMode.LEARN -> {
                    BackHandler {
                        screenMode = ScreenMode.HOME
                    }

                    saveableStateHolder.SaveableStateProvider(key = "learn") {
                        LearnScreen(
                            onNavigateBack = { screenMode = ScreenMode.HOME }
                        )
                    }
                }
                screenMode == ScreenMode.ABOUT -> {
                    BackHandler {
                        screenMode = ScreenMode.SETTINGS
                    }

                    AboutScreen(
                        onNavigateBack = { screenMode = ScreenMode.SETTINGS }
                    )
                }
                screenMode == ScreenMode.SETTINGS -> {
                    BackHandler {
                        screenMode = ScreenMode.HOME
                    }

                    saveableStateHolder.SaveableStateProvider(key = "settings") {
                        ThemeAndSettingsScreen(
                            onNavigateBack = { screenMode = ScreenMode.HOME },
                            onOpenAbout = { screenMode = ScreenMode.ABOUT }
                        )
                    }
                }
                else -> {
                    saveableStateHolder.SaveableStateProvider(key = "home") {
                        HomeScreen(
                            onOpenPlayground = { script ->
                                activeScriptId = script.id
                                screenMode = ScreenMode.PLAYGROUND
                            },
                            onOpenExplorer = { script ->
                                activeScriptId = script.id
                                screenMode = ScreenMode.EXPLORER
                            },
                            onOpenTestLab = {
                                screenMode = ScreenMode.TEST_LAB
                            },
                            onOpenResearchWorkspace = {
                                screenMode = ScreenMode.RESEARCH_WORKSPACE
                            },
                            onOpenLearn = {
                                screenMode = ScreenMode.LEARN
                            },
                            onOpenAbout = {
                                screenMode = ScreenMode.ABOUT
                            },
                            onOpenSettings = {
                                screenMode = ScreenMode.SETTINGS
                            }
                        )
                    }
                }
            }
        }
    }
}
