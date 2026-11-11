package com.ancientkeys.keyboard.storage

import android.content.Context
import com.ancientkeys.keyboard.model.ResearchRecord
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Offline-first repository for epigraphic research records.
 *
 * Stores records securely in the app's internal files directory (`research_records.json`).
 * Isolated from the Input Method Service: typing keystrokes are never intercepted,
 * logged, or recorded into this repository. Records are persisted solely when the user
 * explicitly clicks the Save button in the Inscription Research Workspace.
 */
class ResearchRecordRepository(
    private val context: Context? = null,
    private val overrideDirectory: File? = null
) {

    private val memoryRecords = mutableListOf<ResearchRecord>()

    private val storageFile: File? by lazy {
        when {
            overrideDirectory != null -> File(overrideDirectory, "research_records.json")
            context != null -> File(context.filesDir, "research_records.json")
            else -> null
        }
    }

    private val recordsLock = Any()

    /**
     * Loads all saved research records, sorted by last updated descending.
     */
    fun getAllRecords(): List<ResearchRecord> = synchronized(recordsLock) {
        val file = storageFile
        if (file == null) {
            return memoryRecords.sortedByDescending { it.updatedAtEpochMs }
        }

        if (!file.exists()) return emptyList()

        return try {
            val content = file.readText(Charsets.UTF_8)
            ResearchRecordJson.parse(content).sortedByDescending { it.updatedAtEpochMs }
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Retrieves a single record by its unique ID.
     */
    fun getRecordById(id: String): ResearchRecord? = synchronized(recordsLock) {
        return getAllRecords().firstOrNull { it.id == id }
    }

    /**
     * Explicitly saves or updates an existing research record.
     */
    fun saveRecord(record: ResearchRecord): Boolean = synchronized(recordsLock) {
        val currentRecords = getAllRecords().toMutableList()
        val index = currentRecords.indexOfFirst { it.id == record.id }

        val updatedRecord = record.copy(updatedAtEpochMs = System.currentTimeMillis())
        if (index >= 0) {
            currentRecords[index] = updatedRecord
        } else {
            currentRecords.add(0, updatedRecord)
        }

        return writeRecordsToFile(currentRecords)
    }

    /**
     * Deletes a record by its unique ID.
     */
    fun deleteRecord(id: String): Boolean = synchronized(recordsLock) {
        val currentRecords = getAllRecords().toMutableList()
        val removed = currentRecords.removeAll { it.id == id }
        if (removed) {
            return writeRecordsToFile(currentRecords)
        }
        return false
    }

    /**
     * Clears all research records permanently.
     */
    fun clearAllRecords(): Boolean = synchronized(recordsLock) {
        memoryRecords.clear()
        val file = storageFile
        if (file != null && file.exists()) {
            return file.delete()
        }
        return true
    }

    /**
     * Serializes all records into formatted JSON.
     */
    fun exportToJson(records: List<ResearchRecord> = getAllRecords()): String {
        return ResearchRecordJson.serialize(records)
    }

    /**
     * Exports a single record into academic Markdown format suitable for scholarly citation.
     */
    fun exportToMarkdown(record: ResearchRecord): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val updatedDate = sdf.format(Date(record.updatedAtEpochMs))

        val sb = StringBuilder()
        sb.appendLine("# ${record.title}")
        if (record.sourceReference.isNotBlank()) {
            sb.appendLine("**Source / Corpus Reference**: ${record.sourceReference}")
        }
        sb.appendLine("**Historical Script**: ${record.scriptId.replaceFirstChar { it.uppercase() }}")
        sb.appendLine("**Last Updated**: $updatedDate")
        sb.appendLine()
        sb.appendLine("## Original Inscription Text")
        sb.appendLine("```")
        sb.appendLine(record.originalText)
        sb.appendLine("```")
        sb.appendLine()
        sb.appendLine("## Academic Transliteration")
        sb.appendLine(record.academicTransliteration.ifBlank { "*(None recorded)*" })
        sb.appendLine()
        sb.appendLine("## Researcher Translation (Manual)")
        sb.appendLine("> [!NOTE]")
        sb.appendLine("> This translation was provided manually by the researcher. AncientKeys does not perform automated machine translation.")
        sb.appendLine()
        sb.appendLine(record.researcherTranslation.ifBlank { "*(No translation recorded)*" })
        sb.appendLine()
        if (record.notes.isNotBlank()) {
            sb.appendLine("## Epigraphic & Linguistic Commentary")
            sb.appendLine(record.notes)
        }
        return sb.toString()
    }

    /**
     * Exports all saved records into a combined academic Markdown file.
     */
    fun exportAllToMarkdown(): String {
        val records = getAllRecords()
        if (records.isEmpty()) return "# AncientKeys Inscription Research Records\n\n*(No records saved)*"

        val sb = StringBuilder()
        sb.appendLine("# AncientKeys Inscription Research Workspace Archive")
        sb.appendLine("Total Records: ${records.size}")
        sb.appendLine("Export Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())}")
        sb.appendLine()
        sb.appendLine("---")
        sb.appendLine()
        for (r in records) {
            sb.appendLine(exportToMarkdown(r))
            sb.appendLine()
            sb.appendLine("---")
            sb.appendLine()
        }
        return sb.toString()
    }

    private fun writeRecordsToFile(records: List<ResearchRecord>): Boolean {
        val file = storageFile
        if (file == null) {
            memoryRecords.clear()
            memoryRecords.addAll(records)
            return true
        }

        return try {
            val json = ResearchRecordJson.serialize(records)
            file.writeText(json, Charsets.UTF_8)
            true
        } catch (_: Exception) {
            false
        }
    }
}
