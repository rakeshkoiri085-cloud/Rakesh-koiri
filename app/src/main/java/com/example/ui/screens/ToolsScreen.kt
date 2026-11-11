package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.AiEngine
import com.example.data.repository.BusinessTool
import com.example.data.repository.ContentAndBusinessRepository
import com.example.data.repository.ContentTemplate
import com.example.ui.components.MarkdownText
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperIndigoPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    repository: ContentAndBusinessRepository,
    onOpenInChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val contentTemplates = remember { repository.getContentTemplates() }
    val businessTools = remember { repository.getBusinessTools() }

    var selectedTemplateTitle by remember { mutableStateOf("YouTube Video Script") }
    var userPromptQuery by remember { mutableStateOf("") }
    var activePromptPrefix by remember { mutableStateOf(contentTemplates[0].defaultPrompt) }

    var isLoading by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("tools_screen")
    ) {
        TopAppBar(
            title = {
                Column {
                    Text("Creator & Business Suite", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Viral scripts, lean startup plans, ICP & hooks", fontSize = 11.sp, color = SuperCyanAccent)
                }
            }
        )

        PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Content Creator") },
                icon = { Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_content_creator")
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Business & Startup") },
                icon = { Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_business_startup")
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Preset Tool Selector Cards
            item {
                Text(
                    text = if (selectedTabIndex == 0) "Content Generation Engines" else "Startup Validation Engines",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            if (selectedTabIndex == 0) {
                items(contentTemplates) { template ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedTemplateTitle == template.title) SuperIndigoPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                        border = if (selectedTemplateTitle == template.title) androidx.compose.foundation.BorderStroke(1.5.dp, SuperIndigoPrimary) else null,
                        onClick = {
                            selectedTemplateTitle = template.title
                            activePromptPrefix = template.defaultPrompt
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tool_card_${template.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(template.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(template.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(businessTools) { tool ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedTemplateTitle == tool.title) SuperIndigoPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                        border = if (selectedTemplateTitle == tool.title) androidx.compose.foundation.BorderStroke(1.5.dp, SuperIndigoPrimary) else null,
                        onClick = {
                            selectedTemplateTitle = tool.title
                            activePromptPrefix = tool.promptTemplate
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tool_card_${tool.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(tool.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(tool.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Input Form
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Selected: $selectedTemplateTitle",
                            fontWeight = FontWeight.Bold,
                            color = SuperCyanAccent,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = userPromptQuery,
                            onValueChange = { userPromptQuery = it },
                            placeholder = { Text(if (selectedTabIndex == 0) "e.g. 5 productivity habits for computer science students" else "e.g. AI-powered micro-learning app for developers") },
                            label = { Text("Topic or Idea Focus") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tool_topic_input")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    val full = "$activePromptPrefix $userPromptQuery"
                                    val result = AiEngine.generate(
                                        prompt = full,
                                        mode = if (selectedTabIndex == 0) "content" else "business"
                                    )
                                    generatedResult = result.text
                                    isLoading = false
                                }
                            },
                            enabled = !isLoading && userPromptQuery.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = SuperIndigoPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("run_tool_action_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("SUPER AI is generating...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate $selectedTemplateTitle")
                            }
                        }
                    }
                }
            }

            // Result
            if (generatedResult != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tool_result_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Generated Strategy & Deliverable", fontWeight = FontWeight.Bold, color = SuperCyanAccent, fontSize = 13.sp)
                                Row {
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Tool Output", generatedResult ?: ""))
                                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp).testTag("copy_tool_result_button")
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { onOpenInChat(generatedResult ?: "") },
                                        modifier = Modifier.size(32.dp).testTag("discuss_tool_result_button")
                                    ) {
                                        Icon(Icons.Default.ChatBubble, contentDescription = "Chat", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            MarkdownText(markdown = generatedResult ?: "")
                        }
                    }
                }
            }
        }
    }
}
