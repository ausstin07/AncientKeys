package com.ancientkeys.keyboard.model

import java.util.UUID

/**
 * Represents an epigraphic or linguistic research record for an ancient inscription.
 *
 * Designed for offline documentation by epigraphers, linguists, and researchers.
 * Explicitly distinguishes original script, academic transliteration, and human translation.
 *
 * NOTE: AncientKeys never performs automated translation; all translations are entered
 * and verified manually by researchers.
 */
data class ResearchRecord(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val scriptId: String,
    val originalText: String,
    val academicTransliteration: String,
    val researcherTranslation: String,
    val sourceReference: String = "",
    val notes: String = "",
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)

