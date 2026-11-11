package com.ancientkeys.keyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.ui.AboutScreen
import com.ancientkeys.keyboard.ui.CharacterExplorerScreen
import com.ancientkeys.keyboard.ui.HomeScreen
import com.ancientkeys.keyboard.ui.ImeTestLabScreen
import com.ancientkeys.keyboard.ui.LearnScreen
import com.ancientkeys.keyboard.ui.PlaygroundScreen
import com.ancientkeys.keyboard.ui.ResearchWorkspaceScreen
import com.ancientkeys.keyboard.ui.theme.AncientKeysTheme

enum class ScreenMode {
    HOME,
    PLAYGROUND,
    EXPLORER,
    TEST_LAB,
    LEARN,
    RESEARCH_WORKSPACE,
    ABOUT
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AncientKeysTheme {
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
fun AncientKeysApp() {
    var screenMode by rememberSaveable { mutableStateOf(ScreenMode.HOME) }
    var activeScriptId by rememberSaveable { mutableStateOf<String?>(null) }
    val saveableStateHolder = rememberSaveableStateHolder()

    val activeScript = activeScriptId?.let { ScriptRegistry.getScriptById(it) }

    when {
        screenMode == ScreenMode.PLAYGROUND && activeScript != null -> {
            BackHandler {
                screenMode = ScreenMode.HOME
            }

            saveableStateHolder.SaveableStateProvider(key = activeScript.id) {
                var textValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                    mutableStateOf(TextFieldValue(""))
                }

                PlaygroundScreen(
                    script = activeScript,
                    textValue = textValue,
                    onTextValueChange = { textValue = it },
                    onBackClick = { screenMode = ScreenMode.HOME }
                )
            }
        }
        screenMode == ScreenMode.EXPLORER && activeScript != null -> {
            BackHandler {
                screenMode = ScreenMode.HOME
            }

            CharacterExplorerScreen(
                script = activeScript,
                onBackClick = { screenMode = ScreenMode.HOME }
            )
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

            ResearchWorkspaceScreen(
                onBackClick = { screenMode = ScreenMode.HOME }
            )
        }
        screenMode == ScreenMode.LEARN -> {
            BackHandler {
                screenMode = ScreenMode.HOME
            }

            LearnScreen(
                onNavigateBack = { screenMode = ScreenMode.HOME }
            )
        }
        screenMode == ScreenMode.ABOUT -> {
            BackHandler {
                screenMode = ScreenMode.HOME
            }

            AboutScreen(
                onNavigateBack = { screenMode = ScreenMode.HOME }
            )
        }
        else -> {
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
                }
            )
        }
    }
}
