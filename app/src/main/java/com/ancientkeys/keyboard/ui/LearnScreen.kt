package com.ancientkeys.keyboard.ui

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ancientkeys.keyboard.learn.FoundationalArticle
import com.ancientkeys.keyboard.learn.HistoricalLibraryData
import com.ancientkeys.keyboard.learn.ScriptHistoryProfile
import com.ancientkeys.keyboard.learn.TypingPracticeTask
import com.ancientkeys.keyboard.ui.components.AncientKeysCard
import com.ancientkeys.keyboard.ui.components.AncientKeysIconButton
import com.ancientkeys.keyboard.ui.theme.AncientKeysDesignTokens
import com.ancientkeys.keyboard.ui.theme.ancientKeysColors
import com.ancientkeys.keyboard.util.HistoricalFontProvider

/**
 * AncientKeys v1.1 Learn & History Screen matching the Scholarly Calm design identity.
 *
 * Implements:
 * - Professional header with back navigation (`‹`)
 * - Three organized tabs: Writing Systems, Foundations, and Interactive Practice Tasks
 * - Minimal Sage card aesthetics with authentic glyph previews
 * - In-depth script profiles (mechanics, numerals, epigraphic inscriptions, milestones, sources)
 * - Linguistic foundational articles with typography tuned for scholarly reading
 * - Interactive typing practice tasks with live validation and explanations
 */
@Composable
fun LearnScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var viewingProfile by remember { mutableStateOf<ScriptHistoryProfile?>(null) }
    var viewingArticle by remember { mutableStateOf<FoundationalArticle?>(null) }

    // Intercept hardware/gesture Back
    BackHandler {
        if (viewingProfile != null) {
            viewingProfile = null
        } else if (viewingArticle != null) {
            viewingArticle = null
        } else {
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LearnTopBar(
                title = when {
                    viewingProfile != null -> viewingProfile!!.name
                    viewingArticle != null -> "Article"
                    else -> "Learn & Explore"
                },
                subtitle = when {
                    viewingProfile != null -> viewingProfile!!.period
                    viewingArticle != null -> viewingArticle!!.subtitle
                    else -> "Epigraphic origins, mechanics & linguistics"
                },
                onBackClick = {
                    if (viewingProfile != null) {
                        viewingProfile = null
                    } else if (viewingArticle != null) {
                        viewingArticle = null
                    } else {
                        onNavigateBack()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Tab Row (only shown at root list view)
            if (viewingProfile == null && viewingArticle == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.ancientKeysColors.chipBackground)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Writing Systems", "Foundations", "Practice Tasks").forEachIndexed { idx, title ->
                        val isSelected = selectedTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                )
                                .clickable { selectedTab = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.ancientKeysColors.keyText
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    viewingProfile != null -> {
                        ScriptProfileDetailView(
                            profile = viewingProfile!!,
                            onBack = { viewingProfile = null }
                        )
                    }
                    viewingArticle != null -> {
                        ArticleDetailView(
                            article = viewingArticle!!,
                            onBack = { viewingArticle = null }
                        )
                    }
                    selectedTab == 0 -> {
                        WritingSystemsTab(
                            onSelectProfile = { viewingProfile = it }
                        )
                    }
                    selectedTab == 1 -> {
                        FoundationsTab(
                            onSelectArticle = { viewingArticle = it }
                        )
                    }
                    selectedTab == 2 -> {
                        PracticeTasksTab()
                    }
                }
            }
        }
    }
}

// ==========================================
// TOP BAR
// ==========================================

@Composable
private fun LearnTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.ancientKeysColors.keyText
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.ancientKeysColors.chipText
                )
            }
        }
    }
}

// ==========================================
// TAB 1: WRITING SYSTEMS
// ==========================================

@Composable
private fun WritingSystemsTab(
    onSelectProfile: (ScriptHistoryProfile) -> Unit
) {
    val profiles = HistoricalLibraryData.profiles
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(profiles, key = { it.scriptId }) { profile ->
            val scriptFont = remember(profile.scriptId) { HistoricalFontProvider.getFontFamily(context, profile.scriptId) }

            AncientKeysCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectProfile(profile) },
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
                            text = profile.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.ancientKeysColors.keyText
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.ancientKeysColors.chipSelectedBackground
                        ) {
                            Text(
                                text = profile.systemType,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.ancientKeysColors.chipSelectedText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "📅 ${profile.period}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Text(
                        text = "🌍 ${profile.geographicDistribution}",
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )
                    Text(
                        text = "✍️ Direction: ${profile.writingDirection}",
                        fontSize = 11.sp,
                        color = MaterialTheme.ancientKeysColors.chipText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Read Full History & Inscriptions ➔",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 2: FOUNDATIONS ARTICLES
// ==========================================

@Composable
private fun FoundationsTab(
    onSelectArticle: (FoundationalArticle) -> Unit
) {
    val articles = HistoricalLibraryData.foundationalArticles

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(articles, key = { it.id }) { article ->
            AncientKeysCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectArticle(article) },
                shape = RoundedCornerShape(14.dp),
                containerColor = MaterialTheme.ancientKeysColors.cardBackground,
                borderColor = MaterialTheme.ancientKeysColors.cardBorder
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = article.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.ancientKeysColors.keyText
                    )
                    Text(
                        text = article.subtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = article.summary,
                        fontSize = 12.sp,
                        color = MaterialTheme.ancientKeysColors.chipText,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Read Article ➔",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 3: PRACTICE TASKS
// ==========================================

@Composable
private fun PracticeTasksTab() {
    val allTasks = remember { HistoricalLibraryData.profiles.flatMap { it.interactiveExamples } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(allTasks, key = { it.id }) { task ->
            PracticeTaskItem(task = task)
        }
    }
}

@Composable
private fun PracticeTaskItem(task: TypingPracticeTask) {
    var userText by remember { mutableStateOf("") }
    var hasAnswered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    AncientKeysCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        containerColor = MaterialTheme.ancientKeysColors.cardBackground,
        borderColor = MaterialTheme.ancientKeysColors.cardBorder
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = task.prompt,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.ancientKeysColors.keyText
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Target Roman Input: '${task.romanInput}'",
                fontSize = 11.sp,
                color = MaterialTheme.ancientKeysColors.chipText
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = userText,
                onValueChange = {
                    userText = it
                    hasAnswered = false
                },
                placeholder = { Text("Type input or target script...", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.ancientKeysColors.cardBorder
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        hasAnswered = true
                        val normalizedUser = userText.trim()
                        isCorrect = (normalizedUser.equals(task.targetScriptText, ignoreCase = true) ||
                                normalizedUser.equals(task.romanInput, ignoreCase = true))
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                        contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
                    )
                ) {
                    Text("Check Answer", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                if (hasAnswered) {
                    Text(
                        text = if (isCorrect) "✓ Correct!" else "Try again or view below",
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }

            if (hasAnswered) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.ancientKeysColors.chipBackground,
                    border = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Target Script: ${task.targetScriptText}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.explanation,
                            fontSize = 11.sp,
                            color = MaterialTheme.ancientKeysColors.chipText,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SCRIPT PROFILE DETAIL VIEW
// ==========================================

@Composable
private fun ScriptProfileDetailView(
    profile: ScriptHistoryProfile,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Alternative names
        if (profile.alternativeNames.isNotEmpty()) {
            Text(
                text = "Also known as: ${profile.alternativeNames.joinToString(", ")}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Historical Metadata Card
        AncientKeysCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            containerColor = MaterialTheme.ancientKeysColors.cardBackground,
            borderColor = MaterialTheme.ancientKeysColors.cardBorder
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("📋 Historical Metadata", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.ancientKeysColors.keyText)
                Text("• Period: ${profile.period}", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.chipText)
                Text("• Geographic Distribution: ${profile.geographicDistribution}", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.chipText)
                Text("• Writing Direction: ${profile.writingDirection}", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.chipText)
                Text("• System Type: ${profile.systemType}", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.chipText)
                Text("• Languages: ${profile.languagesWritten.joinToString(", ")}", fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.chipText)
            }
        }

        // Consonant & Vowel Mechanics
        Column {
            Text("✍️ Consonant & Vowel Mechanics", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.ancientKeysColors.keyText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(profile.consonantVowelMechanics, fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.chipText, lineHeight = 18.sp)
        }

        // Numeral System
        Column {
            Text("🔢 Numeral System", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.ancientKeysColors.keyText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(profile.numeralConventions, fontSize = 12.sp, color = MaterialTheme.ancientKeysColors.chipText, lineHeight = 18.sp)
        }

        // Notable Epigraphic Inscriptions
        Column {
            Text("🏛️ Notable Epigraphic Inscriptions", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.ancientKeysColors.keyText)
            Spacer(modifier = Modifier.height(6.dp))

            profile.importantInscriptions.forEach { insc ->
                AncientKeysCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    containerColor = MaterialTheme.ancientKeysColors.chipBackground,
                    borderColor = MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(insc.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        Text("Date: ${insc.date} | Location: ${insc.location}", fontSize = 10.sp, color = MaterialTheme.ancientKeysColors.chipText)
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(insc.description, fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.keyText)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Significance: ${insc.historicalSignificance}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.ancientKeysColors.chipText)
                    }
                }
            }
        }

        // Key Historical Milestones
        Column {
            Text("📜 Key Historical Milestones", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.ancientKeysColors.keyText)
            Spacer(modifier = Modifier.height(4.dp))
            profile.keyHistoricalDevelopments.forEach { dev ->
                Text("• $dev", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.chipText)
                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        // Scholarly Sources & Citations
        Column {
            Text("📚 Scholarly Sources & Citations", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.ancientKeysColors.keyText)
            Spacer(modifier = Modifier.height(4.dp))
            profile.scholarlySources.forEach { src ->
                Text("• $src", fontSize = 11.sp, color = MaterialTheme.ancientKeysColors.chipText)
                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
            )
        ) {
            Text("← Back to Writing Systems", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ==========================================
// ARTICLE DETAIL VIEW
// ==========================================

@Composable
private fun ArticleDetailView(
    article: FoundationalArticle,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = article.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.ancientKeysColors.keyText
        )
        Text(
            text = article.subtitle,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(color = MaterialTheme.ancientKeysColors.cardBorder.copy(alpha = 0.5f))

        Text(
            text = article.fullMarkdown,
            fontSize = 13.sp,
            color = MaterialTheme.ancientKeysColors.keyText,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.ancientKeysColors.keyPrimaryActionBackground,
                contentColor = MaterialTheme.ancientKeysColors.keyPrimaryActionText
            )
        ) {
            Text("← Back to Foundations", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
