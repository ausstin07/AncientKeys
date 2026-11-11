package com.ancientkeys.keyboard.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.ui.components.KeyboardKeyButton
import com.ancientkeys.keyboard.ui.components.PlaygroundToolbar
import com.ancientkeys.keyboard.util.HistoricalFontProvider
import com.ancientkeys.keyboard.util.UnicodeTextEditor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundScreen(
    script: HistoricalScript,
    textValue: TextFieldValue,
    onTextValueChange: (TextFieldValue) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scriptFont = remember(script.id) { HistoricalFontProvider.getFontFamily(context, script.id) }

    var selectedCategoryIndex by rememberSaveable(script.id) { mutableIntStateOf(0) }

    val activeCategory = if (script.categories.isNotEmpty() && selectedCategoryIndex < script.categories.size) {
        script.categories[selectedCategoryIndex]
    } else {
        null
    }

    val visibleKeys = remember(script, activeCategory) {
        if (activeCategory == null) {
            script.keys
        } else {
            script.keys.filter { it.category == activeCategory }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHostStateModifier(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = script.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            if (script.isRtl) {
                                Spacer(modifier = Modifier.width(8.dp))
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text("RTL", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                                )
                            }
                        }
                        Text(
                            text = script.historicalPeriod,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Text Input / Output Display Area
            val textLayoutDirection = if (script.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
            CompositionLocalProvider(LocalLayoutDirection provides textLayoutDirection) {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = onTextValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        fontFamily = scriptFont,
                        textDirection = if (script.isRtl) TextDirection.Rtl else TextDirection.Ltr
                    ),
                    placeholder = {
                        Text(
                            text = "Tap keys below to write in ${script.name}...",
                            fontSize = 14.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            // Action Toolbar (Space, Backspace, Copy, Clear)
            PlaygroundToolbar(
                onSpaceClick = {
                    onTextValueChange(UnicodeTextEditor.insertSpace(textValue))
                },
                onBackspaceClick = {
                    onTextValueChange(UnicodeTextEditor.deleteBackward(textValue))
                },
                onClearClick = {
                    onTextValueChange(UnicodeTextEditor.clear())
                },
                onCopyClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("${script.name} text", textValue.text)
                    clipboard.setPrimaryClip(clip)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Copied ${script.name} text to clipboard")
                    }
                }
            )

            // Category Tabs (if script has multiple categories)
            if (script.categories.size > 1) {
                if (script.categories.size <= 3) {
                    TabRow(selectedTabIndex = selectedCategoryIndex) {
                        script.categories.forEachIndexed { index, category ->
                            Tab(
                                selected = selectedCategoryIndex == index,
                                onClick = { selectedCategoryIndex = index },
                                text = { Text(category, fontSize = 12.sp, maxLines = 1) }
                            )
                        }
                    }
                } else {
                    ScrollableTabRow(
                        selectedTabIndex = selectedCategoryIndex,
                        edgePadding = 8.dp
                    ) {
                        script.categories.forEachIndexed { index, category ->
                            Tab(
                                selected = selectedCategoryIndex == index,
                                onClick = { selectedCategoryIndex = index },
                                text = { Text(category, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            // Keyboard Keys Grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 64.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(visibleKeys, key = { it.unicodeCodePoint + it.character + it.category }) { scriptKey ->
                    KeyboardKeyButton(
                        key = scriptKey,
                        fontFamily = scriptFont,
                        onClick = {
                            onTextValueChange(UnicodeTextEditor.insert(textValue, scriptKey.character))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SnackbarHostStateModifier(state: SnackbarHostState) {
    SnackbarHost(hostState = state)
}

