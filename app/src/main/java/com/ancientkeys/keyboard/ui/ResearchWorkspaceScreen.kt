package com.ancientkeys.keyboard.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.model.ResearchRecord
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.storage.ResearchRecordRepository
import com.ancientkeys.keyboard.util.HistoricalFontProvider

/**
 * Dedicated offline Research Screen for epigraphers, linguists, and researchers.
 *
 * Supports independent fields for original historical scripts, academic transliteration,
 * human translation, source corpus reference, and epigraphic commentary.
 */
@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Inscription Record" else "📜 Research Workspace",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing) {
                            isEditing = false
                            activeRecord = null
                        } else {
                            onBackClick()
                        }
                    }) {
                        Text("←", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    if (!isEditing && recordsList.isNotEmpty()) {
                        TextButton(onClick = {
                            exportDialogContent = repository.exportAllToMarkdown()
                        }) {
                            Text("Export All", fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
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
                            scriptId = "brahmi",
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
                    title = { Text("Delete Inscription Record?") },
                    text = { Text("Are you sure you want to permanently delete \"${recordToDelete?.title}\"? This action cannot be undone.") },
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
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { recordToDelete = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Export Dialog (Markdown / JSON Viewer & Copy)
            if (exportDialogContent != null) {
                AlertDialog(
                    onDismissRequest = { exportDialogContent = null },
                    title = { Text("Exported Scholarly Markdown") },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = exportDialogContent ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            clipboardManager.setText(AnnotatedString(exportDialogContent ?: ""))
                            Toast.makeText(context, "Export copied to clipboard", Toast.LENGTH_SHORT).show()
                            exportDialogContent = null
                        }) {
                            Text("📋 Copy to Clipboard")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { exportDialogContent = null }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}

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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Info & New Inscription Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Inscription Corpus (${records.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "App-local private storage; 0 network access",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onNewRecordClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("+ New Record", fontWeight = FontWeight.Bold)
            }
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search records, inscriptions, or citations...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        if (filteredRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (records.isEmpty()) "No inscription research records yet" else "No matching records found",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Document original scripts, epigraphic notes, and translations securely offline.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(onClick = onNewRecordClick) {
                        Text("Create First Inscription Record")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredRecords, key = { it.id }) { record ->
                    val context = LocalContext.current
                    val scriptFont = remember(record.scriptId) {
                        HistoricalFontProvider.getFontFamily(context, record.scriptId)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRecordClick(record) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = record.title.ifBlank { "Untitled Inscription" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = record.scriptId.uppercase(),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            if (record.sourceReference.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = record.sourceReference,
                                    style = MaterialTheme.typography.bodySmall,
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
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (record.academicTransliteration.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = record.academicTransliteration,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { onDeleteRecord(record) }) {
                                    Text("Delete", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = { onRecordClick(record) },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Open / Edit", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

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
    var scriptId by rememberSaveable { mutableStateOf(record?.scriptId ?: "brahmi") }
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
            label = { Text("Inscription Title / Label *") },
            placeholder = { Text("e.g. Major Rock Edict XIII (Girnar)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val scriptName = ScriptRegistry.getScriptById(scriptId)?.name ?: scriptId.uppercase()
                    Text("Script: $scriptName ▾", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                DropdownMenu(
                    expanded = isScriptDropdownExpanded,
                    onDismissRequest = { isScriptDropdownExpanded = false }
                ) {
                    ScriptRegistry.allScripts.forEach { script ->
                        DropdownMenuItem(
                            text = { Text(script.name) },
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
                label = { Text("Corpus Reference") },
                placeholder = { Text("CIG 1234, CII Vol. II") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        // Original Script Text Field (with Copy Button)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Original Inscription Characters",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(originalText))
                            Toast.makeText(context, "Original characters copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("📋 Copy Original", fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = originalText,
                    onValueChange = { originalText = it },
                    placeholder = { Text("Enter or paste historical characters...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    textStyle = TextStyle(
                        fontFamily = currentScriptFont,
                        fontSize = 20.sp
                    )
                )
            }
        }

        // Academic Transliteration Field (with Copy Button)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Academic Transliteration",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(academicTransliteration))
                            Toast.makeText(context, "Transliteration copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("📋 Copy Transliteration", fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = academicTransliteration,
                    onValueChange = { academicTransliteration = it },
                    placeholder = { Text("e.g. devānaṃpriya priyadarśī rājā...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }

        // Researcher Translation (Manual)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
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
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Human translation; AncientKeys does not perform machine translation.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(researcherTranslation))
                            Toast.makeText(context, "Translation copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("📋 Copy Translation", fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = researcherTranslation,
                    onValueChange = { researcherTranslation = it },
                    placeholder = { Text("Enter your English / target language translation...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        }

        // Epigraphic Commentary & Notes
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Epigraphic Commentary, Dialect & Notes") },
            placeholder = { Text("Palaeographic features, rock medium, dating, alternate readings...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // Action Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (record != null) {
                OutlinedButton(
                    onClick = { onDelete(record) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
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
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Export")
                }
            } else {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
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
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.weight(1.5f)
            ) {
                Text("💾 Save Record", fontWeight = FontWeight.Bold)
            }
        }
    }
}

