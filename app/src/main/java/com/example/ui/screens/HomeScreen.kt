package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ConversationEntity
import com.example.data.repository.SuperAiPlan
import com.example.ui.components.AppScreen
import com.example.ui.components.PlanBadge
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperIndigoDark
import com.example.ui.theme.SuperIndigoPrimary
import com.example.ui.theme.SuperViolet

data class QuickActionItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val targetScreen: AppScreen
)

@Composable
fun HomeScreen(
    currentPlan: SuperAiPlan,
    recentConversations: List<ConversationEntity>,
    onNavigate: (AppScreen) -> Unit,
    onStartPromptInChat: (String) -> Unit,
    onSelectConversation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var quickQuery by remember { mutableStateOf("") }

    val quickActions = listOf(
        QuickActionItem(
            title = "AI Chat",
            description = "Ask, brainstorm, explain & plan",
            icon = Icons.Default.ChatBubble,
            accentColor = SuperIndigoPrimary,
            targetScreen = AppScreen.CHAT
        ),
        QuickActionItem(
            title = "Code Assistant",
            description = "Python, JS, C++, debug & convert",
            icon = Icons.Default.Code,
            accentColor = SuperCyanAccent,
            targetScreen = AppScreen.CODE
        ),
        QuickActionItem(
            title = "Learn AI",
            description = "6 progressive levels & quizzes",
            icon = Icons.Default.School,
            accentColor = SuperViolet,
            targetScreen = AppScreen.LEARN
        ),
        QuickActionItem(
            title = "Study Mode",
            description = "Flashcards, revision & notes",
            icon = Icons.Default.AutoAwesome,
            accentColor = Color(0xFFF59E0B),
            targetScreen = AppScreen.STUDY
        ),
        QuickActionItem(
            title = "Projects",
            description = "AI Apps, tasks & code workspace",
            icon = Icons.Default.Folder,
            accentColor = Color(0xFF10B981),
            targetScreen = AppScreen.PROJECTS
        ),
        QuickActionItem(
            title = "Tools & Prompts",
            description = "Content & Business generators",
            icon = Icons.Default.Widgets,
            accentColor = Color(0xFFEC4899),
            targetScreen = AppScreen.TOOLS
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Brand Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    SuperIndigoPrimary.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(SuperIndigoPrimary, SuperCyanAccent)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = "SUPER AI Logo",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "SUPER AI",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Learn. Create. Code. With SUPER AI.",
                                        fontSize = 11.sp,
                                        color = SuperCyanAccent,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            PlanBadge(
                                plan = currentPlan,
                                onClick = { onNavigate(AppScreen.SETTINGS) }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Your Next-Generation AI Assistant for Learning, Coding and Creating.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Search / Ask Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = quickQuery,
                                onValueChange = { quickQuery = it },
                                placeholder = { Text("Ask SUPER AI anything...", fontSize = 14.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SuperIndigoPrimary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("home_quick_query_input")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (quickQuery.isNotBlank()) {
                                        val q = quickQuery
                                        quickQuery = ""
                                        onStartPromptInChat(q)
                                    } else {
                                        onNavigate(AppScreen.CHAT)
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(SuperIndigoPrimary, SuperCyanAccent)
                                        )
                                    )
                                    .testTag("home_send_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Ask in chat",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Actions Section Header
        item {
            Text(
                text = "Assistant Workspaces",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 2-Column Grid of Quick Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (i in quickActions.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (j in 0 until 2) {
                            val actionIndex = i + j
                            if (actionIndex < quickActions.size) {
                                val action = quickActions[actionIndex]
                                ElevatedCard(
                                    onClick = { onNavigate(action.targetScreen) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("action_tile_${action.title.replace(" ", "_")}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(action.accentColor.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = action.icon,
                                                contentDescription = action.title,
                                                tint = action.accentColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = action.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = action.description,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Daily AI Insight Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_insight_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Insight",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily AI Mastery Tip",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFF59E0B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Add few-shot examples to your coding prompts to achieve 40% more consistent variable names and architectural structure.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Recent Chats Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Conversations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = { onNavigate(AppScreen.HISTORY) },
                    modifier = Modifier.testTag("view_all_history_button")
                ) {
                    Text("View All", fontSize = 12.sp, color = SuperIndigoPrimary)
                }
            }
        }

        if (recentConversations.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No previous conversations yet.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = { onNavigate(AppScreen.CHAT) },
                            modifier = Modifier.testTag("start_first_chat_button")
                        ) {
                            Text("Start your first chat", fontSize = 12.sp, color = SuperIndigoPrimary)
                        }
                    }
                }
            }
        } else {
            items(recentConversations.take(4)) { conversation ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    onClick = { onSelectConversation(conversation.conversationId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recent_chat_item_${conversation.conversationId}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = null,
                            tint = SuperCyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = conversation.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "Category: ${conversation.category.replaceFirstChar { it.uppercase() }}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
