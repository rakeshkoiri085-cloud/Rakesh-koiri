package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperIndigoDark
import com.example.ui.theme.SuperIndigoPrimary
import com.example.ui.theme.SuperRose

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: (String) -> Unit,
    isGenerating: Boolean = false,
    onStopGeneration: () -> Unit = {},
    onNewConversation: () -> Unit = {},
    attachedFileName: String? = null,
    onFileSelected: (String, String) -> Unit = { _, _ -> },
    onClearAttachment: () -> Unit = {},
    quickSuggestions: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAttachDialog by remember { mutableStateOf(false) }

    // Speech Recognition Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                val newText = if (text.isBlank()) spokenText else "$text $spokenText"
                onTextChange(newText)
                Toast.makeText(context, "Voice input captured", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Quick Suggestions Bar
        if (quickSuggestions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickSuggestions.forEach { suggestion ->
                    FilterChip(
                        selected = false,
                        onClick = { onSend(suggestion) },
                        label = { Text(suggestion, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("suggestion_chip")
                    )
                }
            }
        }

        // Active Attachment Indicator
        if (attachedFileName != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Attached File",
                    tint = SuperIndigoPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Attached: $attachedFileName",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = onClearAttachment,
                    modifier = Modifier.testTag("clear_attachment_button")
                ) {
                    Text("Remove", fontSize = 11.sp, color = SuperRose)
                }
            }
        }

        // Main Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // New Chat button
            IconButton(
                onClick = onNewConversation,
                modifier = Modifier
                    .size(42.dp)
                    .testTag("new_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Conversation",
                    tint = SuperIndigoPrimary
                )
            }

            // Attachment button
            IconButton(
                onClick = { showAttachDialog = true },
                modifier = Modifier
                    .size(42.dp)
                    .testTag("attachment_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach File",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Voice input button
            IconButton(
                onClick = {
                    try {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to SUPER AI...")
                        }
                        speechLauncher.launch(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Speech recognition not available on this device", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .testTag("voice_input_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = SuperCyanAccent
                )
            }

            // Input Text Field
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Ask SUPER AI anything...", fontSize = 14.sp) },
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SuperIndigoPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .testTag("chat_input_textfield")
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Action: Send or Stop Button
            if (isGenerating) {
                IconButton(
                    onClick = onStopGeneration,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = SuperRose),
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .testTag("stop_generation_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop Generation",
                        tint = Color.White
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        if (text.isNotBlank() || attachedFileName != null) {
                            val promptToSend = if (attachedFileName != null) {
                                "[Attached File: $attachedFileName]\n$text"
                            } else text
                            onSend(promptToSend)
                            onTextChange("")
                        }
                    },
                    enabled = text.isNotBlank() || attachedFileName != null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = if (text.isNotBlank() || attachedFileName != null) {
                                    listOf(SuperIndigoPrimary, SuperCyanAccent)
                                } else {
                                    listOf(Color(0xFF334155), Color(0xFF475569))
                                }
                            )
                        )
                        .testTag("send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send message",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // File Attachment Dialog
    if (showAttachDialog) {
        AlertDialog(
            onDismissRequest = { showAttachDialog = false },
            title = { Text("Attach File for AI Analysis") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Select or attach supported files (TXT, Code, PDF, CSV, Images) for SUPER AI context analysis (Max 10MB):",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val sampleFiles = listOf(
                        "main.py (Python algorithm code)",
                        "study_notes_biology.txt (Exam notes)",
                        "dataset_sales.csv (Tabular data)",
                        "system_architecture.png (Diagram)"
                    )

                    sampleFiles.forEach { file ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            onClick = {
                                val fileName = file.substringBefore(" (")
                                val content = "Sample file contents for $fileName"
                                onFileSelected(fileName, content)
                                showAttachDialog = false
                                Toast.makeText(context, "Attached $fileName", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = SuperIndigoPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(file, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAttachDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
