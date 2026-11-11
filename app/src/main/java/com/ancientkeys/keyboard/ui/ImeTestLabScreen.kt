package com.ancientkeys.keyboard.ui

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.ime.romanization.BrahmiRomanizer
import com.ancientkeys.keyboard.ime.romanization.ScriptRomanizerProvider
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.util.HistoricalFontProvider

/**
 * Dedicated In-App IME Testing Screen ("Test Lab").
 *
 * Provides synthetic editable fields of various Android input types (Normal single-line,
 * Multiline, Numeric, and Sensitive Password) with live Unicode analysis, character count,
 * script detection, and a system keyboard switcher.
 *
 * All inputs are strictly local and synthetic to verify IME behavior without touching
 * real passwords, personal chats, or external apps.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ImeTestLabScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var normalText by rememberSaveable { mutableStateOf("") }
    var multilineText by rememberSaveable { mutableStateOf("") }
    var numericText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    // Active field index: 0 = Normal, 1 = Multiline, 2 = Numeric, 3 = Password
    var activeFieldIndex by rememberSaveable { mutableIntStateOf(0) }
    var auditResults by remember { mutableStateOf<List<TestLabCheckResult>?>(null) }
    var compositionScriptId by rememberSaveable { mutableStateOf("brahmi") }
    var selectedConsonant by rememberSaveable { mutableStateOf("k") }

    val activeText = when (activeFieldIndex) {
        0 -> normalText
        1 -> multilineText
        2 -> numericText
        3 -> passwordText
        else -> normalText
    }

    val activeFieldName = when (activeFieldIndex) {
        0 -> "Normal Text Field"
        1 -> "Multiline Inscription Field"
        2 -> "Numeric Field"
        3 -> "Synthetic Password Field"
        else -> "Field"
    }

    val appendSample: (String) -> Unit = { sample ->
        when (activeFieldIndex) {
            0 -> normalText += sample
            1 -> multilineText += sample
            2 -> numericText += sample
            3 -> passwordText += sample
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🧪 IME Test Lab",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            normalText = ""
                            multilineText = ""
                            numericText = ""
                            passwordText = ""
                        }
                    ) {
                        Text("Reset All", color = MaterialTheme.colorScheme.error)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Environment Banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🔒 Synthetic Local Test Environment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Test AncientKeys system keyboard behavior across different Android input field types. " +
                                "All tests run locally in memory. Never enter real personal passwords.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.showInputMethodPicker()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Switch Active Keyboard (IME Picker)", fontSize = 13.sp)
                    }
                }
            }

            // Quick Samples Bar
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Insert Test Glyph Samples into Active Field:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SuggestionChip(
                            onClick = { appendSample("𓇳𓏤 Ra ") },
                            label = { Text("Egyptian: 𓇳𓏤", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = { appendSample("𐤀𐤁𐤂 ") },
                            label = { Text("Phoenician (RTL): 𐤀𐤁𐤂", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = { appendSample("𐨐𐨁𐨟 ") },
                            label = { Text("Kharosthi (RTL): 𐨐𐨁𐨟", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = { appendSample("ᚠᚢᚦ ") },
                            label = { Text("Runic: ᚠᚢᚦ", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = { appendSample("𑀅𑀰𑁄𑀓 ") },
                            label = { Text("Brahmi: 𑀅𑀰𑁄𑀓", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = { appendSample("King 𓇳 = 42 ") },
                            label = { Text("Mixed Script", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Character Composition Preview (Consonant + Matras)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Historical Script Composition & Transliteration Preview",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Script selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        listOf("brahmi" to "Brahmi", "kharosthi" to "Kharosthi", "greek" to "Greek").forEach { (id, name) ->
                            FilterChip(
                                selected = compositionScriptId == id,
                                onClick = {
                                    compositionScriptId = id
                                    selectedConsonant = if (id == "greek") "a" else "k"
                                },
                                label = { Text(name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }

                    // Key selector
                    val keysForScript = when (compositionScriptId) {
                        "brahmi" -> listOf("k", "kh", "g", "c", "t", "d", "n", "p", "m", "y", "r", "l", "v", "s", "h")
                        "kharosthi" -> listOf("k", "kh", "g", "c", "j", "ṭ", "ḍ", "t", "d", "n", "p", "m", "y", "r", "l", "v", "ś", "s", "h")
                        "greek" -> listOf("a", "e", "i", "o", "u", "s", "t", "p", "k")
                        else -> listOf("k")
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        keysForScript.forEach { c ->
                            FilterChip(
                                selected = selectedConsonant == c,
                                onClick = { selectedConsonant = c },
                                label = { Text(c, fontSize = 11.sp) },
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap any composed glyph/syllable to insert into active field:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val activeRomanizer = remember(compositionScriptId) {
                        ScriptRomanizerProvider.getRomanizer(compositionScriptId) ?: BrahmiRomanizer
                    }
                    val previews = remember(selectedConsonant, activeRomanizer) {
                        activeRomanizer.getCompositionPreviews(selectedConsonant)
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        previews.forEach { preview ->
                            val glyph = preview.scriptResult.split(" ")[0]
                            SuggestionChip(
                                onClick = {
                                    appendSample(glyph)
                                },
                                label = {
                                    Text(
                                        text = "${preview.latinInput} → ${preview.scriptResult}",
                                        fontFamily = HistoricalFontProvider.getFontFamily(context, compositionScriptId),
                                        fontSize = 11.sp
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Automated Self-Diagnostic Audit Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🧪 Automated Test Lab Audit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Button(
                            onClick = {
                                auditResults = TestLabValidator.runFullAudit()
                            },
                            contentPadding = ButtonDefaults.TextButtonContentPadding,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Run Audit", fontSize = 11.sp)
                        }
                    }

                    if (auditResults != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        auditResults!!.forEach { r ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(r.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = if (r.passed) "✓ PASS" else "✗ FAIL",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (r.passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                            Text(
                                text = r.details,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (!r.passed && r.actionableResolution.isNotBlank()) {
                                Text(
                                    text = "→ Fix: ${r.actionableResolution}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            // 1. Normal Single-line Field
            OutlinedTextField(
                value = normalText,
                onValueChange = {
                    normalText = it
                    activeFieldIndex = 0
                },
                label = { Text("1. Normal Text Field (Single Line)") },
                placeholder = { Text("Type English, Greek, Runic, Hieroglyphs...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Multiline Inscription Field
            OutlinedTextField(
                value = multilineText,
                onValueChange = {
                    multilineText = it
                    activeFieldIndex = 1
                },
                label = { Text("2. Multiline Inscription Field") },
                placeholder = { Text("Test line breaks (↵), spaces, and cursor positioning...") },
                singleLine = false,
                minLines = 3,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Default
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Numeric Field
            OutlinedTextField(
                value = numericText,
                onValueChange = {
                    numericText = it
                    activeFieldIndex = 2
                },
                label = { Text("3. Numeric Field (Digits & Numerals)") },
                placeholder = { Text("Test modern 0–9 and historical numerals...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 4. Sensitive Password Field (Shield Test)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🛡️ 4. Synthetic Password Field (Shield Verification)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Expected IME behavior: When AncientKeys focuses this field, it MUST hide historical keys " +
                                "and display the Password Shield, recommending switching back to the default keyboard.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = {
                            passwordText = it
                            activeFieldIndex = 3
                        },
                        label = { Text("Synthetic Password Field") },
                        placeholder = { Text("Focus this field to test IME shield") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Live Unicode & Script Diagnostics Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📊 Live Inspector: $activeFieldName",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Button(
                            onClick = {
                                when (activeFieldIndex) {
                                    0 -> normalText = ""
                                    1 -> multilineText = ""
                                    2 -> numericText = ""
                                    3 -> passwordText = ""
                                }
                            },
                            contentPadding = ButtonDefaults.TextButtonContentPadding,
                            colors = ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Clear", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val codePointCount = if (activeText.isNotEmpty()) activeText.codePointCount(0, activeText.length) else 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("UTF-16 Code Units: ${activeText.length}", fontSize = 12.sp)
                        Text("Unicode Code Points: $codePointCount", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val detectedScripts = detectScriptsInText(activeText)
                    Text(
                        text = "Detected Scripts (${detectedScripts.size}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (detectedScripts.isEmpty()) {
                        Text("None (Field is empty)", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            detectedScripts.forEach { scriptName ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(scriptName, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Code point breakdown (up to first 12 code points)
                    Text(
                        text = "Hex Code Points (Sample):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val codePointsList = getCodePointsList(activeText, maxCount = 12)
                    if (codePointsList.isEmpty()) {
                        Text("—", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    } else {
                        Text(
                            text = codePointsList.joinToString("  "),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}


/**
 * Detects which scripts (historical or modern) are present in the given text.
 */
fun detectScriptsInText(text: String): List<String> {
    if (text.isEmpty()) return emptyList()

    val detected = mutableSetOf<String>()
    var i = 0
    while (i < text.length) {
        val cp = text.codePointAt(i)
        when (cp) {
            in 0x0020..0x007E -> detected.add("Latin / ASCII")
            in 0x0370..0x03FF -> detected.add("Ancient Greek")
            in 0x16A0..0x16FF -> detected.add("Elder Futhark Runic")
            in 0x10900..0x1091F -> detected.add("Phoenician (RTL)")
            in 0x10A00..0x10A5F -> detected.add("Kharosthi (RTL)")
            in 0x11000..0x1107F -> detected.add("Brahmi")
            in 0x13000..0x1342F -> detected.add("Egyptian Hieroglyphs")
        }
        i += Character.charCount(cp)
    }
    return detected.toList()
}

/**
 * Returns a formatted list of Unicode code points (e.g. U+131F3) for the text.
 */
fun getCodePointsList(text: String, maxCount: Int = 12): List<String> {
    if (text.isEmpty()) return emptyList()

    val result = mutableListOf<String>()
    var i = 0
    var count = 0
    while (i < text.length && count < maxCount) {
        val cp = text.codePointAt(i)
        val charStr = String(Character.toChars(cp))
        val hex = if (cp > 0xFFFF) "U+%05X".format(cp) else "U+%04X".format(cp)
        result.add("$charStr ($hex)")
        i += Character.charCount(cp)
        count++
    }
    if (i < text.length) {
        result.add("…")
    }
    return result
}
