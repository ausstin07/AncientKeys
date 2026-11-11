package com.ancientkeys.keyboard.model

/**
 * Metadata and keyboard layout specification for a historical writing system.
 *
 * @property id Unique string identifier (e.g. "egyptian", "brahmi").
 * @property name Canonical scholarly script name.
 * @property alternativeNames Common alternative names, transliterations, or regional designations.
 * @property historicalPeriod Era of historical usage (e.g. "c. 3200 BCE – 400 CE").
 * @property region Primary geographic region of historical use.
 * @property description Concise scholarly overview distinguishing the script from specific languages.
 * @property historicalContext Detailed historical and epigraphic context backed by reliable sources.
 * @property sourcesAndReferences Scholarly source references (e.g. Unicode Standard, academic grammars).
 * @property isRtl Whether the script is written Right-to-Left (e.g. Phoenician, Kharosthi).
 * @property sampleText Representative sample Unicode text.
 * @property categories Ordered list of category tabs for organizing keys.
 * @property keys All keys available in this script's keyboard layout.
 */
data class HistoricalScript(
    val id: String,
    val name: String,
    val alternativeNames: List<String> = emptyList(),
    val historicalPeriod: String,
    val region: String,
    val description: String,
    val historicalContext: String = "",
    val sourcesAndReferences: String = "",
    val isRtl: Boolean = false,
    val sampleText: String,
    val categories: List<String> = emptyList(),
    val keys: List<ScriptKey>
)
