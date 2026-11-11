package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ConversationEntity
import com.example.data.local.MessageEntity
import com.example.ui.ChatUiState
import com.example.ui.components.ChatInputBar
import com.example.ui.components.MarkdownText
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperIndigoPrimary
import com.example.ui.theme.SuperRose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onSendMessage: (String) -> Unit,
    onNewConversation: () -> Unit,
    onRenameConversation: (String, String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onClearConversation: (String) -> Unit,
    onRegenerateResponse: () -> Unit,
    onStopGeneration: () -> Unit,
    onRetryMessage: (String) -> Unit,
    onExplainCodeInChat: (String) -> Unit,
    onDismissError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is ChatUiState.Loading -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .testTag("chat_loading_view")
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "SUPER AI Chat",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Syncing with Room persistence...",
                                fontSize = 11.sp,
                                color = SuperCyanAccent
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        CircularProgressIndicator(
                            color = SuperCyanAccent,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Loading Conversation...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Retrieving chat history from local Room memory",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        is ChatUiState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .testTag("chat_error_view")
            ) {
                TopAppBar(
                    title = {
                        Text("SUPER AI Chat", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = SuperRose,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Operation Notice",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.errorMessage,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (uiState.canRetry) {
                                    Button(
                                        onClick = {
                                            onDismissError()
                                            if (uiState.failedPrompt != null) {
                                                onRetryMessage(uiState.failedPrompt)
                                            } else {
                                                onRegenerateResponse()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SuperCyanAccent),
                                        modifier = Modifier.testTag("chat_retry_button")
                                    ) {
                                        Text("Retry", color = Color.Black, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Button(
                                    onClick = {
                                        onDismissError()
                                        onNewConversation()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuperIndigoPrimary),
                                    modifier = Modifier.testTag("chat_new_button")
                                ) {
                                    Text("New Chat", color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = { onDismissError() },
                                modifier = Modifier.testTag("chat_dismiss_error_button")
                            ) {
                                Text("Dismiss", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        is ChatUiState.Success -> {
            ChatSuccessContent(
                currentConversation = uiState.conversation,
                messages = uiState.messages,
                isGenerating = uiState.isGenerating,
                onSendMessage = onSendMessage,
                onNewConversation = onNewConversation,
                onRenameConversation = onRenameConversation,
                onDeleteConversation = onDeleteConversation,
                onClearConversation = onClearConversation,
                onRegenerateResponse = onRegenerateResponse,
                onStopGeneration = onStopGeneration,
                onRetryMessage = onRetryMessage,
                onExplainCodeInChat = onExplainCodeInChat,
                modifier = modifier
            )
        }
    }
}

/**
 * Overload for backward compatibility and simple previews.
 */
@Composable
fun ChatScreen(
    currentConversation: ConversationEntity?,
    messages: List<MessageEntity>,
    isGenerating: Boolean,
    onSendMessage: (String) -> Unit,
    onNewConversation: () -> Unit,
    onRenameConversation: (String, String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onClearConversation: (String) -> Unit,
    onRegenerateResponse: () -> Unit,
    onStopGeneration: () -> Unit,
    onRetryMessage: (String) -> Unit,
    onExplainCodeInChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ChatScreen(
        uiState = ChatUiState.Success(
            conversation = currentConversation,
            messages = messages,
            isGenerating = isGenerating
        ),
        onSendMessage = onSendMessage,
        onNewConversation = onNewConversation,
        onRenameConversation = onRenameConversation,
        onDeleteConversation = onDeleteConversation,
        onClearConversation = onClearConversation,
        onRegenerateResponse = onRegenerateResponse,
        onStopGeneration = onStopGeneration,
        onRetryMessage = onRetryMessage,
        onExplainCodeInChat = onExplainCodeInChat,
        onDismissError = {},
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatSuccessContent(
    currentConversation: ConversationEntity?,
    messages: List<MessageEntity>,
    isGenerating: Boolean,
    onSendMessage: (String) -> Unit,
    onNewConversation: () -> Unit,
    onRenameConversation: (String, String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onClearConversation: (String) -> Unit,
    onRegenerateResponse: () -> Unit,
    onStopGeneration: () -> Unit,
    onRetryMessage: (String) -> Unit,
    onExplainCodeInChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var attachedFile by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    var showMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameText by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }

    // Scroll to bottom on new message
    LaunchedEffect(messages.size, messages.lastOrNull()?.content) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("chat_screen")
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = currentConversation?.title ?: "SUPER AI Chat",
                        maxLines = 1,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isGenerating) "SUPER AI is generating..." else "Online • Free Edition",
                        fontSize = 11.sp,
                        color = if (isGenerating) SuperCyanAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            actions = {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.testTag("chat_more_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Chat Options"
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Rename Chat") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            renameText = currentConversation?.title ?: ""
                            showRenameDialog = true
                        },
                        modifier = Modifier.testTag("menu_rename_chat")
                    )
                    DropdownMenuItem(
                        text = { Text("Clear Conversation") },
                        leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            showClearDialog = true
                        },
                        modifier = Modifier.testTag("menu_clear_conversation")
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Chat", color = SuperRose) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = SuperRose) },
                        onClick = {
                            showMenu = false
                            currentConversation?.let { onDeleteConversation(it.conversationId) }
                        },
                        modifier = Modifier.testTag("menu_delete_chat")
                    )
                }
            }
        )

        // Messages List or Welcome State
        Box(modifier = Modifier.weight(1f)) {
            if (messages.isEmpty()) {
                // Empty state with quick starters
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(SuperIndigoPrimary, SuperCyanAccent))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "SUPER AI",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "How can SUPER AI help you today?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ask questions, debug code, generate study notes, or brainstorm projects.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val sampleQuestions = listOf(
                        "Explain neural network attention simply",
                        "Write a Python function to parse JSON with error handling",
                        "Create a 7-day study plan for exams",
                        "Generate 5 high-converting YouTube title ideas"
                    )

                    sampleQuestions.forEach { question ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            onClick = { onSendMessage(question) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("starter_prompt_${question.take(15)}")
                        ) {
                            Text(
                                text = "💬 \"$question\"",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(messages) { message ->
                        if (message.role == "user") {
                            // User Message Bubble
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(18.dp, 4.dp, 18.dp, 18.dp),
                                    color = SuperIndigoPrimary,
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .testTag("user_message_${message.messageId}")
                                ) {
                                    Text(
                                        text = message.content,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        modifier = Modifier.padding(14.dp)
                                    )
                                }
                            }
                        } else {
                            // Assistant Response Card
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("assistant_message_${message.messageId}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
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
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "SUPER AI",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = SuperCyanAccent
                                            )
                                        }

                                        Row {
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("AI Response", message.content))
                                                    Toast.makeText(context, "Response copied", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(28.dp).testTag("copy_response_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "Copy",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }

                                            IconButton(
                                                onClick = onRegenerateResponse,
                                                modifier = Modifier.size(28.dp).testTag("regenerate_response_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "Regenerate",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Body
                                    if (message.status == "STREAMING") {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = SuperIndigoPrimary
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "SUPER AI is thinking & reasoning...",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    } else if (message.status == "ERROR") {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = "Error",
                                                tint = SuperRose,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = message.content,
                                                fontSize = 13.sp,
                                                color = SuperRose
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { onRetryMessage(messages.findLast { it.role == "user" }?.content ?: "") },
                                            colors = ButtonDefaults.buttonColors(containerColor = SuperRose),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("retry_failed_request_button")
                                        ) {
                                            Text("Retry Request", fontSize = 12.sp)
                                        }
                                    } else {
                                        MarkdownText(
                                            markdown = message.content,
                                            onExplainCode = onExplainCodeInChat
                                        )

                                        // Follow-Up Questions
                                        val followUps = message.followUpsJson?.split("|||")?.filter { it.isNotBlank() } ?: emptyList()
                                        if (followUps.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = "Suggested Follow-ups:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SuperCyanAccent
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                followUps.forEach { suggestion ->
                                                    FilterChip(
                                                        selected = false,
                                                        onClick = { onSendMessage(suggestion) },
                                                        label = { Text(suggestion, fontSize = 11.sp) },
                                                        colors = FilterChipDefaults.filterChipColors(
                                                            containerColor = MaterialTheme.colorScheme.surface
                                                        ),
                                                        modifier = Modifier.testTag("follow_up_chip")
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Input Bar
        ChatInputBar(
            text = inputText,
            onTextChange = { inputText = it },
            onSend = { prompt ->
                onSendMessage(prompt)
                inputText = ""
                attachedFile = null
            },
            isGenerating = isGenerating,
            onStopGeneration = onStopGeneration,
            onNewConversation = onNewConversation,
            attachedFileName = attachedFile,
            onFileSelected = { name, _ -> attachedFile = name },
            onClearAttachment = { attachedFile = null }
        )
    }

    // Rename Dialog
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Chat") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Conversation Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rename_chat_input")
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (renameText.isNotBlank()) {
                            currentConversation?.let { onRenameConversation(it.conversationId, renameText.trim()) }
                            showRenameDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_rename_button")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Conversation?") },
            text = { Text("All messages in this chat will be permanently removed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        currentConversation?.let { onClearConversation(it.conversationId) }
                        showClearDialog = false
                    },
                    modifier = Modifier.testTag("confirm_clear_button")
                ) {
                    Text("Clear", color = SuperRose)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
