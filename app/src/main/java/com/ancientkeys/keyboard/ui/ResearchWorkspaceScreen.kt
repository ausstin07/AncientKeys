package com.ancientkeys.keyboard.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.model.ResearchRecord
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.storage.ResearchRecordRepository
import com.ancientkeys.keyboard.ui.components.AncientKeysCard
import com.ancientkeys.keyboard.ui.components.AncientKeysIconButton
import com.ancientkeys.keyboard.ui.theme.AncientKeysDesignTokens
import com.ancientkeys.keyboard.ui.theme.ancientKeysColors
import com.ancientkeys.keyboard.util.HistoricalFontProvider

/**
 * AncientKeys v1.1 Research Workspace Screen matching the Scholarly Calm design identity.
 *
 * Implements:
 * - Professional header with back navigation (`‹`)
 * - Inscription corpus manager with real-time text and citation search
 * - Full-featured editor for original historical scripts, academic transliterations,
 *   researcher translations, corpus citations, and epigraphic commentary
 * - 1-tap clipboard copying for original characters, transliterations, and translations
 * - Scholarly Markdown export (single record and full corpus batch)
 * - 100% app-local storage without network transmission
 */
@Composable
fun ResearchWorkspaceScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ResearchRecordRepository(context) }
    val clipboardManager = LocalClipboardManager.current

    var recordsList by remember { mutableStateOf(repository.getAllRecords()) }
    var activeRecord by remember { mutableStateOf<ResearchRecord?>(null) }
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var recordToDelete by remember { mutableStateOf<ResearchRecord?>(null) }
    var exportDialogContent by remember { mutableStateOf<String?>(null) }

    fun refreshList() {
        recordsList = repository.getAllRecords()
    }

    // Intercept hardware/gesture Back
    BackHandler {
        if (isEditing) {
            isEditing = false
            activeRecord = null
        } else {
            onBackClick()
        }
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
                            onClick = {
                                if (isEditing) {
                                    isEditing = false
                                    activeRecord = null
                                } else {
                                    onBackClick()
                                }
                            },
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
                                text = if (isEditing) "Edit Inscription" else "Research Workspace",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.ancientKeysColors.keyText
                            )
                            Text(
                                text = if (isEditing) "Corpus documentation & epigraphy" else "Offline epigraphic notebook (${recordsList.size})",
                                fontSize = 11.sp,
                                color = MaterialTheme.ancientKeysColors.chipText
                            )
                        }
                    }

                    if (!isEditing && recordsList.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                exportDialogContent = repository.exportAllToMarkdown()
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder)
                        ) {
                            Text("Export All", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.ancientKeysColors.keyText)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isEditing) {
                // Record Edit / Create View
                RecordEditorView(
                    record = activeRecord,
                    onSave = { updatedRecord ->
                        val success = repository.saveRecord(updatedRecord)
                        if (success) {
                            Toast.makeText(context, "Record saved successfully", Toast.LENGTH_SHORT).show()
                            refreshList()
                            isEditing = false
                            activeRecord = null
                        } else {
                            Toast.makeText(context, "Failed to save record", Toast.LENGTH_LONG).show()
                        }
                    },
                    onDelete = { record ->
                        recordToDelete = record
                    },
                    onExportMarkdown = { record ->
                        exportDialogContent = repository.exportToMarkdown(record)
                    },
                    onCancel = {
                        isEditing = false
                        activeRecord = null
                    }
                )
            } else {
                // Records List View
                RecordListView(
                    records = recordsList,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onNewRecordClick = {
                        activeRecord = ResearchRecord(
                            title = "",
                            scriptId = "greek",
                            originalText = "",
                            academicTransliteration = "",
                            researcherTranslation = "",
                            sourceReference = "",
                            notes = ""
                        )
                        isEditing = true
                    },
                    onRecordClick = { record ->
                        activeRecord = record
                        isEditing = true
                    },
                    onDeleteRecord = { record ->
                        recordToDelete = record
                    }
                )
            }

            // Delete Confirmation Dialog
            if (recordToDelete != null) {
                AlertDialog(
                    onDismissRequest = { recordToDelete = null },
                    title = { Text("Delete Inscription Record?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                    text = { Text("Are you sure you want to permanently delete \"${recordToDelete?.title}\"? This action cannot be undone.", fontSize = 13.sp) },
                    confirmButton = {
                        Button(
                            onClick = {
                                recordToDelete?.let { repository.deleteRecord(it.id) }
                                recordToDelete = null
                                refreshList()
                                isEditing = false
                                activeRecord = null
                                Toast.makeText(context, "Record deleted", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Delete", fontSize = 12.sp)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { recordToDelete = null }) {
                            Text("Cancel", fontSize = 12.sp)
                        }
                    }
                )
            }

            // Export Dialog (Markdown Viewer & Copy)
            if (exportDialogContent != null) {
                AlertDialog(
                    onDismissRequest = { exportDialogContent = null },
                    title = { Text("Exported Scholarly Markdown", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .verticalScroll(rememberScrollState())
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.ancientKeysColors.chipBackground)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = exportDialogContent ?: "",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.ancientKeysColors.keyText,
                                lineHeight = 16.sp
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(exportDialogContent ?: ""))
                                Toast.makeText(context, "Export copied to clipboard", Toast.LENGTH_SHORT).show()
                                exportDialogContent = null
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                                contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                            )
                        ) {
                            Text("📋 Copy to Clipboard", fontSize = 12.sp)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { exportDialogContent = null }) {
                            Text("Close", fontSize = 12.sp)
                        }
                    }
                )
            }
        }
    }
}

// ==========================================
// RECORDS LIST VIEW
// ==========================================

@Composable
private fun RecordListView(
    records: List<ResearchRecord>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNewRecordClick: () -> Unit,
    onRecordClick: (ResearchRecord) -> Unit,
    onDeleteRecord: (ResearchRecord) -> Unit
) {
    val filteredRecords = remember(records, searchQuery) {
        if (searchQuery.isBlank()) {
            records
        } else {
            val q = searchQuery.lowercase().trim()
            records.filter {
                it.title.lowercase().contains(q) ||
                it.sourceReference.lowercase().contains(q) ||
                it.originalText.lowercase().contains(q) ||
                it.academicTransliteration.lowercase().contains(q) ||
                it.researcherTranslation.lowercase().contains(q) ||
                it.notes.lowercase().contains(q)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Action Row: Count & New Record Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Inscription Corpus (${records.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.ancientKeysColors.keyText
            )

            Button(
                onClick = onNewRecordClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                    contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("+ New Record", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search records, inscriptions, or citations...", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.chipText) },
            leadingIcon = { Text("🔍", fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.ancientKeysColors.cardBackground,
                unfocusedContainerColor = MaterialTheme.ancientKeysColors.cardBackground,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.ancientKeysColors.cardBorder
            )
        )

        if (filteredRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = if (records.isEmpty()) "No inscription research records yet" else "No matching records found",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Document original scripts, epigraphic notes, and transliterations securely offline.",
                        fontSize = 12.sp,
                        color = MaterialTheme.ancientKeysColors.chipText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = onNewRecordClick,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder)
                    ) {
                        Text("Create First Inscription Record", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.keyText)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredRecords, key = { it.id }) { record ->
                    val context = LocalContext.current
                    val scriptFont = remember(record.scriptId) {
                        HistoricalFontProvider.getFontFamily(context, record.scriptId)
                    }

                    AncientKeysCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRecordClick(record) },
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
                                    text = record.title.ifBlank { "Untitled Inscription" },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.ancientKeysColors.keyText,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.ancientKeysColors.chipSelectedBackground
                                ) {
                                    Text(
                                        text = record.scriptId.uppercase(),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.ancientKeysColors.chipSelectedText
                                    )
                                }
                            }

                            if (record.sourceReference.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = record.sourceReference,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (record.originalText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = record.originalText,
                                    fontFamily = scriptFont,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.ancientKeysColors.keyText,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (record.academicTransliteration.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = record.academicTransliteration,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.ancientKeysColors.chipText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { onDeleteRecord(record) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Delete", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Button(
                                    onClick = { onRecordClick(record) },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                                        contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                                    )
                                ) {
                                    Text("Open / Edit", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// RECORD EDITOR VIEW
// ==========================================

@Composable
private fun RecordEditorView(
    record: ResearchRecord?,
    onSave: (ResearchRecord) -> Unit,
    onDelete: (ResearchRecord) -> Unit,
    onExportMarkdown: (ResearchRecord) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var title by rememberSaveable { mutableStateOf(record?.title ?: "") }
    var scriptId by rememberSaveable { mutableStateOf(record?.scriptId ?: "greek") }
    var sourceReference by rememberSaveable { mutableStateOf(record?.sourceReference ?: "") }
    var originalText by rememberSaveable { mutableStateOf(record?.originalText ?: "") }
    var academicTransliteration by rememberSaveable { mutableStateOf(record?.academicTransliteration ?: "") }
    var researcherTranslation by rememberSaveable { mutableStateOf(record?.researcherTranslation ?: "") }
    var notes by rememberSaveable { mutableStateOf(record?.notes ?: "") }
    var isScriptDropdownExpanded by remember { mutableStateOf(false) }

    val currentScriptFont = remember(scriptId) {
        HistoricalFontProvider.getFontFamily(context, scriptId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Inscription Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Inscription Title / Label *", fontSize = 12.sp) },
            placeholder = { Text("e.g. Rosetta Stone Decree or Ashokan Pillar", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        // Script Selector Dropdown & Source Reference Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { isScriptDropdownExpanded = true },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val scriptName = ScriptRegistry.getScriptById(scriptId)?.name ?: scriptId.uppercase()
                    Text("Script: $scriptName ▾", maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                }

                DropdownMenu(
                    expanded = isScriptDropdownExpanded,
                    onDismissRequest = { isScriptDropdownExpanded = false }
                ) {
                    ScriptRegistry.allScripts.forEach { script ->
                        DropdownMenuItem(
                            text = { Text(script.name, fontSize = 12.sp) },
                            onClick = {
                                scriptId = script.id
                                isScriptDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = sourceReference,
                onValueChange = { sourceReference = it },
                label = { Text("Corpus Reference", fontSize = 12.sp) },
                placeholder = { Text("CIG 1234, CII Vol. II", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Original Script Text Field
        AncientKeysCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.ancientKeysColors.cardBackground,
            borderColor = MaterialTheme.ancientKeysColors.cardBorder
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Original Inscription Characters",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(originalText))
                            Toast.makeText(context, "Original characters copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.height(26.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder)
                    ) {
                        Text("📋 Copy", fontSize = 10.sp, color = MaterialTheme.ancientKeysColors.keyText)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = originalText,
                    onValueChange = { originalText = it },
                    placeholder = { Text("Enter or paste historical characters...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = TextStyle(
                        fontFamily = currentScriptFont,
                        fontSize = 20.sp,
                        lineHeight = 28.sp
                    )
                )
            }
        }

        // Academic Transliteration Field
        AncientKeysCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.ancientKeysColors.cardBackground,
            borderColor = MaterialTheme.ancientKeysColors.cardBorder
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Academic Transliteration",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(academicTransliteration))
                            Toast.makeText(context, "Transliteration copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.height(26.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder)
                    ) {
                        Text("📋 Copy", fontSize = 10.sp, color = MaterialTheme.ancientKeysColors.keyText)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = academicTransliteration,
                    onValueChange = { academicTransliteration = it },
                    placeholder = { Text("e.g. devānaṃpriya priyadarśī rājā...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Researcher Translation (Manual)
        AncientKeysCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.ancientKeysColors.cardBackground,
            borderColor = MaterialTheme.ancientKeysColors.cardBorder
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Researcher Translation (Manual)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.ancientKeysColors.keyText
                        )
                        Text(
                            text = "Human translation; 0 network/machine translation.",
                            fontSize = 10.sp,
                            color = MaterialTheme.ancientKeysColors.chipText
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(researcherTranslation))
                            Toast.makeText(context, "Translation copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.height(26.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder)
                    ) {
                        Text("📋 Copy", fontSize = 10.sp, color = MaterialTheme.ancientKeysColors.keyText)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = researcherTranslation,
                    onValueChange = { researcherTranslation = it },
                    placeholder = { Text("Enter your translation...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Epigraphic Commentary & Notes
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Epigraphic Commentary, Dialect & Notes", fontSize = 12.sp) },
            placeholder = { Text("Palaeographic features, stone medium, dating, alternate readings...", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(10.dp)
        )

        // Action Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (record != null) {
                OutlinedButton(
                    onClick = { onDelete(record) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = {
                        val draftRecord = record.copy(
                            title = title,
                            scriptId = scriptId,
                            sourceReference = sourceReference,
                            originalText = originalText,
                            academicTransliteration = academicTransliteration,
                            researcherTranslation = researcherTranslation,
                            notes = notes
                        )
                        onExportMarkdown(draftRecord)
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Export", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.keyText)
                }
            } else {
                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.keyText)
                }
            }

            Button(
                onClick = {
                    val finalTitle = title.ifBlank { "Untitled Inscription" }
                    val newRecord = (record ?: ResearchRecord(
                        title = finalTitle,
                        scriptId = scriptId,
                        originalText = originalText,
                        academicTransliteration = academicTransliteration,
                        researcherTranslation = researcherTranslation
                    )).copy(
                        title = finalTitle,
                        scriptId = scriptId,
                        sourceReference = sourceReference,
                        originalText = originalText,
                        academicTransliteration = academicTransliteration,
                        researcherTranslation = researcherTranslation,
                        notes = notes
                    )
                    onSave(newRecord)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                    contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1.5f)
            ) {
                Text("💾 Save Record", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
