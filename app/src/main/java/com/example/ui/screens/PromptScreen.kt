package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.GeneratedPromptResult
import com.example.data.repository.PromptCategory
import com.example.data.repository.PromptRepository
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperIndigoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptScreen(
    promptRepository: PromptRepository,
    onTestInChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categories = remember { promptRepository.getCategories() }
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    var userGoal by remember { mutableStateOf(selectedCategory.sampleGoal) }
    var customStyle by remember { mutableStateOf("Photorealistic 8K, cinematic lighting") }
    var generatedPromptResult by remember { mutableStateOf<GeneratedPromptResult?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("prompt_screen")
    ) {
        TopAppBar(
            title = {
                Column {
                    Text("Prompt Generator", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Architect gold-standard prompts across 8 specialized domains", fontSize = 11.sp, color = SuperCyanAccent)
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
            // Category Chips (8 categories)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory.id == cat.id,
                        onClick = {
                            selectedCategory = cat
                            userGoal = cat.sampleGoal
                            generatedPromptResult = null
                        },
                        label = { Text(cat.name, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SuperIndigoPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("prompt_cat_chip_${cat.id}")
                    )
                }
            }

            // Input Form Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Customize Prompt Directives",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = userGoal,
                        onValueChange = { userGoal = it },
                        label = { Text("Core Objective or Subject") },
                        placeholder = { Text(selectedCategory.placeholder) },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("prompt_goal_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customStyle,
                        onValueChange = { customStyle = it },
                        label = { Text("Style / Tone / Architectural Constraints") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("prompt_style_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            generatedPromptResult = promptRepository.buildEngineeredPrompt(
                                category = selectedCategory.id,
                                userGoal = userGoal,
                                styleOrTone = customStyle
                            )
                        },
                        enabled = userGoal.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SuperIndigoPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_master_prompt_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Build Engineered Prompt")
                    }
                }
            }

            // Generated Prompt Result Box
            if (generatedPromptResult != null) {
                val res = generatedPromptResult!!
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generated_prompt_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Engineered Master Prompt",
                                fontWeight = FontWeight.Bold,
                                color = SuperCyanAccent,
                                fontSize = 13.sp
                            )
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Master Prompt", res.masterPrompt))
                                    Toast.makeText(context, "Prompt copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp).testTag("copy_prompt_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = res.masterPrompt,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onTestInChat(res.masterPrompt) },
                            colors = ButtonDefaults.buttonColors(containerColor = SuperIndigoPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_prompt_in_chat_button")
                        ) {
                            Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Immediately in SUPER AI Chat", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
