package com.ancientkeys.keyboard.ui

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.ancientkeys.keyboard.ui.components.AncientKeysCard
import com.ancientkeys.keyboard.ui.components.AncientKeysIconButton
import com.ancientkeys.keyboard.ui.theme.AncientKeysDesignTokens
import com.ancientkeys.keyboard.ui.theme.ancientKeysColors
import com.ancientkeys.keyboard.util.HistoricalFontProvider

/**
 * AncientKeys v1.1 In-App IME Testing Screen ("Test Lab").
 *
 * Implements:
 * - Professional header with Back (`‹`) and Reset All actions
 * - 4 dedicated synthetic test fields: Normal Text, Multiline Inscription, Numeric, and Password Shield
 * - Quick Glyph Insertion chips for testing RTL, Greek, Runic, and Hieroglyphic rendering
 * - Syllable Composition & Transliteration preview
 * - Programmatic self-diagnostic audit runner
 * - Live Inspector reporting UTF-16 code units, Unicode code points, detected scripts, and hex codes
 */
@OptIn(ExperimentalLayoutApi::class)
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

    // Intercept hardware/gesture Back
    BackHandler {
        onBackClick()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AncientKeysIconButton(
                            onClick = onBackClick,
                            contentDescription = "Navigate Back",
                            size = 40.dp
                        ) {
                            Text(
                                text = "‹",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.ancientKeysColors.keyText
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column {
                            Text(
                                text = "IME Test Lab",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.ancientKeysColors.keyText
                            )
                            Text(
                                text = "Synthetic field diagnostics & IME verification",
                                fontSize = 11.sp,
                                color = MaterialTheme.ancientKeysColors.chipText
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            normalText = ""
                            multilineText = ""
                            numericText = ""
                            passwordText = ""
                        }
                    ) {
                        Text("Reset All", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Environment Banner
            AncientKeysCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                borderColor = MaterialTheme.ancientKeysColors.cardBorder
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🔒 Local Synthetic Testing Environment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Verify AncientKeys system keyboard behavior across single-line, multiline, numeric, and password inputs. All tests execute in local memory.",
                        fontSize = 12.sp,
                        color = MaterialTheme.ancientKeysColors.chipText,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.showInputMethodPicker()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                            contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                        )
                    ) {
                        Text("Switch Active Keyboard (IME Picker)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Quick Samples Bar
            AncientKeysCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                borderColor = MaterialTheme.ancientKeysColors.cardBorder
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Quick Sample Glyphs (Tap to insert into active field):",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Egyptian: 𓇳𓏤" to "𓇳𓏤 Ra ",
                            "Phoenician (RTL): 𐤀𐤁𐤂" to "𐤀𐤁𐤂 ",
                            "Kharosthi (RTL): 𐨐𐨁𐨟" to "𐨐𐨁𐨟 ",
                            "Runic: ᚠᚢᚦ" to "ᚠᚢᚦ ",
                            "Brahmi: 𑀅𑀰𑁄𑀓" to "𑀅𑀰𑁄𑀓 ",
                            "Greek: Ἀρχαῖα" to "Ἀρχαῖα "
                        ).forEach { (label, glyph) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.ancientKeysColors.chipBackground,
                                border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.6f)),
                                modifier = Modifier.clickable { appendSample(glyph) }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.ancientKeysColors.keyText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Syllable Composition & Transliteration Preview
            AncientKeysCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                borderColor = MaterialTheme.ancientKeysColors.cardBorder
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Historical Script Composition & Transliteration Preview",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Script selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        listOf("brahmi" to "Brahmi", "kharosthi" to "Kharosthi", "greek" to "Greek").forEach { (id, name) ->
                            val isSelected = compositionScriptId == id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    compositionScriptId = id
                                    selectedConsonant = if (id == "greek") "a" else "k"
                                },
                                label = { Text(name, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                                    selectedLabelColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                                ),
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
                            val isSelected = selectedConsonant == c
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedConsonant = c },
                                label = { Text(c, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                                    selectedLabelColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                                ),
                                modifier = Modifier.height(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap any composed glyph/syllable to insert into active field:",
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.chipText
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
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.ancientKeysColors.chipBackground,
                                border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.6f)),
                                modifier = Modifier.clickable { appendSample(glyph) }
                            ) {
                                Text(
                                    text = "${preview.latinInput} → ${preview.scriptResult}",
                                    fontFamily = HistoricalFontProvider.getFontFamily(context, compositionScriptId),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.ancientKeysColors.keyText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Automated Self-Diagnostic Audit Card
            AncientKeysCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                borderColor = MaterialTheme.ancientKeysColors.cardBorder
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Automated Diagnostic Engine",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.ancientKeysColors.keyText
                        )
                        Button(
                            onClick = {
                                auditResults = TestLabValidator.runFullAudit()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                                contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Run Audit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (auditResults != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        auditResults!!.forEach { r ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(r.name, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.ancientKeysColors.keyText)
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
                                color = MaterialTheme.ancientKeysColors.chipText
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
                label = { Text("1. Normal Text Field (Single Line)", fontSize = 12.sp) },
                placeholder = { Text("Type English, Greek, Runic, Hieroglyphs...", fontSize = 12.sp) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
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
                label = { Text("2. Multiline Inscription Field", fontSize = 12.sp) },
                placeholder = { Text("Test line breaks (↵), spaces, and cursor positioning...", fontSize = 12.sp) },
                singleLine = false,
                minLines = 3,
                shape = RoundedCornerShape(10.dp),
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
                label = { Text("3. Numeric Field (Digits & Numerals)", fontSize = 12.sp) },
                placeholder = { Text("Test modern 0–9 and historical numerals...", fontSize = 12.sp) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 4. Sensitive Password Field (Shield Test)
            AncientKeysCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                borderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🛡️ 4. Sensitive Password Field (Shield Verification)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Expected IME behavior: When AncientKeys focuses this field, it MUST hide historical keys " +
                                "and display the Password Shield, recommending switching back to the default keyboard.",
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = {
                            passwordText = it
                            activeFieldIndex = 3
                        },
                        label = { Text("Synthetic Password Field", fontSize = 12.sp) },
                        placeholder = { Text("Focus this field to test IME shield", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Live Unicode & Script Diagnostics Inspector Card
            AncientKeysCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                borderColor = MaterialTheme.ancientKeysColors.cardBorder
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
                            fontSize = 13.sp,
                            color = MaterialTheme.ancientKeysColors.keyText
                        )
                        OutlinedButton(
                            onClick = {
                                when (activeFieldIndex) {
                                    0 -> normalText = ""
                                    1 -> multilineText = ""
                                    2 -> numericText = ""
                                    3 -> passwordText = ""
                                }
                            },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp),
                            border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder)
                        ) {
                            Text("Clear", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.keyText)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val codePointCount = if (activeText.isNotEmpty()) activeText.codePointCount(0, activeText.length) else 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("UTF-16 Code Units: ${activeText.length}", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.chipText)
                        Text("Unicode Code Points: $codePointCount", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.ancientKeysColors.keyText)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val detectedScripts = detectScriptsInText(activeText)
                    Text(
                        text = "Detected Scripts (${detectedScripts.size}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (detectedScripts.isEmpty()) {
                        Text("None (Field is empty)", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.chipText)
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            detectedScripts.forEach { scriptName ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.ancientKeysColors.chipBackground,
                                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = scriptName,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.ancientKeysColors.keyText,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Hex Code Points (Sample):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val codePointsList = getCodePointsList(activeText, maxCount = 12)
                    if (codePointsList.isEmpty()) {
                        Text("—", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.chipText)
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
