package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LessonProgressEntity
import com.example.data.repository.LearningLevel
import com.example.data.repository.Lesson
import com.example.ui.components.CodeBlockView
import com.example.ui.components.MarkdownText
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperEmerald
import com.example.ui.theme.SuperIndigoPrimary
import com.example.ui.theme.SuperRose
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    curriculum: List<LearningLevel>,
    progressList: List<LessonProgressEntity>,
    onMarkCompleted: (String, Int, Int) -> Unit,
    onTryInChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLesson by remember { mutableStateOf<Lesson?>(null) }

    val totalLessons = curriculum.sumOf { it.lessons.size }
    val completedCount = progressList.count { it.isCompleted }
    val progressFraction = if (totalLessons > 0) completedCount.toFloat() / totalLessons.toFloat() else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("learn_screen")
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = if (selectedLesson == null) "AI Learning Center" else selectedLesson!!.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        maxLines = 1
                    )
                    Text(
                        text = if (selectedLesson == null) "Progressive 6-level mastery curriculum" else "Level ${selectedLesson!!.levelNumber}",
                        fontSize = 11.sp,
                        color = SuperCyanAccent
                    )
                }
            },
            navigationIcon = {
                if (selectedLesson != null) {
                    IconButton(onClick = { selectedLesson = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            }
        )

        if (selectedLesson == null) {
            // Curriculum Levels Overview
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Progress Header Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("curriculum_progress_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Curriculum Progress", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${(progressFraction * 100).toInt()}% Completed", fontWeight = FontWeight.Bold, color = SuperCyanAccent, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = SuperIndigoPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$completedCount of $totalLessons lessons mastered",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Level Cards
                items(curriculum) { level ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("level_card_${level.levelNumber}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SuperIndigoPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${level.levelNumber}", fontWeight = FontWeight.Bold, color = SuperIndigoPrimary)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(level.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(level.subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Lessons in this level
                            level.lessons.forEach { lesson ->
                                val isDone = progressList.any { it.lessonId == lesson.id && it.isCompleted }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    onClick = { selectedLesson = lesson },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .testTag("lesson_item_${lesson.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (isDone) SuperEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(lesson.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            Text(lesson.shortSummary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                        }
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Start",
                                            tint = SuperCyanAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Interactive Lesson View
            LessonDetailView(
                lesson = selectedLesson!!,
                isCompleted = progressList.any { it.lessonId == selectedLesson!!.id && it.isCompleted },
                onComplete = { score ->
                    onMarkCompleted(selectedLesson!!.id, selectedLesson!!.levelNumber, score)
                },
                onTryInChat = onTryInChat
            )
        }
    }
}

@Composable
fun LessonDetailView(
    lesson: Lesson,
    isCompleted: Boolean,
    onComplete: (Int) -> Unit,
    onTryInChat: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedOption by remember { mutableIntStateOf(-1) }
    var quizSubmitted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("lesson_detail_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explanation
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📖 Lesson Explanation", fontWeight = FontWeight.Bold, color = SuperCyanAccent, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    MarkdownText(markdown = lesson.explanation)
                }
            }
        }

        // Code / Practical Example
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💻 Code & Architecture Example", fontWeight = FontWeight.Bold, color = SuperIndigoPrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    CodeBlockView(code = lesson.codeExample, language = "python")
                }
            }
        }

        // Interactive Practice Prompt
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚡ Practice with SUPER AI", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(lesson.practicePrompt, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onTryInChat(lesson.practicePrompt) },
                        colors = ButtonDefaults.buttonColors(containerColor = SuperIndigoPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("try_practice_prompt_button")
                    ) {
                        Text("Ask SUPER AI Now", fontSize = 12.sp)
                    }
                }
            }
        }

        // Interactive Quiz Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lesson_quiz_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Quiz, contentDescription = null, tint = SuperCyanAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Knowledge Check Quiz", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(lesson.quiz.question, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    lesson.quiz.options.forEachIndexed { index, option ->
                        val isCorrect = index == lesson.quiz.correctIndex
                        val isSelected = selectedOption == index

                        val optionBg = when {
                            !quizSubmitted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            isCorrect -> SuperEmerald.copy(alpha = 0.2f)
                            isSelected -> SuperRose.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = optionBg,
                            onClick = { if (!quizSubmitted) selectedOption = index },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("quiz_option_$index")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { if (!quizSubmitted) selectedOption = index }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(option, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!quizSubmitted) {
                        Button(
                            onClick = {
                                if (selectedOption != -1) {
                                    quizSubmitted = true
                                    val score = if (selectedOption == lesson.quiz.correctIndex) 100 else 50
                                    onComplete(score)
                                    Toast.makeText(context, if (score == 100) "Correct! Lesson marked complete." else "Reviewed! Keep practicing.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = selectedOption != -1,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_quiz_button")
                        ) {
                            Text("Submit Answer")
                        }
                    } else {
                        // Explanation
                        Text(
                            text = if (selectedOption == lesson.quiz.correctIndex) "✅ Correct! ${lesson.quiz.explanation}" else "❌ Incorrect. ${lesson.quiz.explanation}",
                            fontSize = 12.sp,
                            color = if (selectedOption == lesson.quiz.correctIndex) SuperEmerald else SuperRose,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Mini Project Prompt
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🚀 Hands-on Mini Project", fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(lesson.miniProject, fontSize = 13.sp)
                }
            }
        }
    }
}
