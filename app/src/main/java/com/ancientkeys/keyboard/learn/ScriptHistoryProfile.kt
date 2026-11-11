package com.ancientkeys.keyboard.learn

/**
 * Historical inscription or manuscript artifact representation.
 */
data class InscriptionArtifact(
    val name: String,
    val date: String,
    val location: String,
    val description: String,
    val historicalSignificance: String
)

/**
 * Interactive typing practice task for educational learning.
 */
data class TypingPracticeTask(
    val id: String,
    val prompt: String,
    val romanInput: String,
    val targetScriptText: String,
    val explanation: String
)

/**
 * In-depth academic history and epigraphic profile for a historical writing system.
 */
data class ScriptHistoryProfile(
    val scriptId: String,
    val name: String,
    val alternativeNames: List<String>,
    val period: String,
    val geographicDistribution: String,
    val writingDirection: String,
    val systemType: String,
    val languagesWritten: List<String>,
    val importantInscriptions: List<InscriptionArtifact>,
    val consonantVowelMechanics: String,
    val numeralConventions: String,
    val keyHistoricalDevelopments: List<String>,
    val interestingFacts: List<String>,
    val scholarlySources: List<String>,
    val interactiveExamples: List<TypingPracticeTask>
)

/**
 * Educational foundational article on writing systems and epigraphy.
 */
data class FoundationalArticle(
    val id: String,
    val title: String,
    val subtitle: String,
    val summary: String,
    val fullMarkdown: String
)

