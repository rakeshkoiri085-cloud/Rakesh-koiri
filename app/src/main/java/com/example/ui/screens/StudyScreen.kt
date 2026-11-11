package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.AiEngine
import com.example.data.repository.Flashcard
import com.example.data.repository.StudyTopic
import com.example.ui.components.MarkdownText
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperIndigoPrimary
import kotlinx.coroutines.launch

enum class StudyMode(val title: String, val promptTemplate: String) {
    FLASHCARDS("Interactive Flashcards", ""),
    CONCEPT_EXPLAIN("Concept Explainer", "Explain this concept in simple terms with a real-world analogy: "),
    STUDY_NOTES("Study Notes Generator", "Generate structured high-yield revision notes with bullet points and key takeaways for: "),
    REVISION_PLAN("7-Day Revision Plan", "Create an actionable, day-by-day 7-day study revision plan for: ")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    studyTopics: List<StudyTopic>,
    onOpenInChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var selectedMode by remember { mutableStateOf(StudyMode.FLASHCARDS) }

    // Flashcard deck state
    val allCards = remember(studyTopics) { studyTopics.flatMap { it.flashcards } }
    var cardIndex by remember { mutableIntStateOf(0) }
    var isCardFlipped by remember { mutableStateOf(false) }

    // Custom query state
    var topicQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("study_screen")
    ) {
        TopAppBar(
            title = {
                Column {
                    Text("Study Assistant", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Flashcards, active recall & high-yield revision notes", fontSize = 11.sp, color = SuperCyanAccent)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mode Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StudyMode.values().forEach { mode ->
                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = { selectedMode = mode },
                        label = { Text(mode.title, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SuperIndigoPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("study_chip_${mode.name}")
                    )
                }
            }

            // Interactive Flashcards Deck View
            if (selectedMode == StudyMode.FLASHCARDS && allCards.isNotEmpty()) {
                val currentCard = allCards[cardIndex]

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clickable { isCardFlipped = !isCardFlipped }
                        .testTag("flashcard_interactive_card")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SuperIndigoPrimary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = currentCard.subject,
                                        color = SuperIndigoPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = "Card ${cardIndex + 1} of ${allCards.size}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 12.dp)
                            ) {
                                Text(
                                    text = if (!isCardFlipped) "QUESTION" else "ANSWER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = if (!isCardFlipped) SuperCyanAccent else Color(0xFF10B981)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (!isCardFlipped) currentCard.question else currentCard.answer,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp
                                )
                                if (isCardFlipped) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Key concept: ${currentCard.keyConcept}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flip,
                                    contentDescription = "Flip",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (!isCardFlipped) "Tap card to reveal answer" else "Tap card to view question",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Deck Navigation Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (cardIndex > 0) {
                                cardIndex--
                                isCardFlipped = false
                            }
                        },
                        enabled = cardIndex > 0,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("prev_card_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Previous")
                    }

                    Button(
                        onClick = {
                            if (cardIndex < allCards.size - 1) {
                                cardIndex++
                                isCardFlipped = false
                            }
                        },
                        enabled = cardIndex < allCards.size - 1,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("next_card_button")
                    ) {
                        Text("Next")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Custom Subject / Topic Input
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Generate Custom Study Material",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = topicQuery,
                        onValueChange = { topicQuery = it },
                        placeholder = { Text("e.g. Mitochondria and ATP synthesis, or MergeSort algorithm") },
                        label = { Text("Subject or Textbook Topic") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("study_topic_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                val prompt = "${selectedMode.promptTemplate} $topicQuery"
                                val result = AiEngine.generate(
                                    prompt = prompt,
                                    mode = "study",
                                    systemInstruction = "You are SUPER AI Master Academic Tutor. Provide structured, accurate, and deeply clear educational breakdowns."
                                )
                                generatedResult = result.text
                                isLoading = false
                            }
                        },
                        enabled = !isLoading && topicQuery.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuperIndigoPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_study_material_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SUPER AI is synthesizing notes...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate ${selectedMode.title}")
                        }
                    }
                }
            }

            // Generated Study Notes Result Card
            if (generatedResult != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("study_result_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Generated Study Notes",
                            fontWeight = FontWeight.Bold,
                            color = SuperCyanAccent,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MarkdownText(markdown = generatedResult ?: "")
                    }
                }
            }

            // Academic Integrity Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SUPER AI is designed to build understanding and mastery. Verify references and never use AI to bypass authentic learning.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
