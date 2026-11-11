package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ConversationEntity
import com.example.ui.ConversationsUiState
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperIndigoPrimary
import com.example.ui.theme.SuperRose
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: ConversationsUiState,
    onSelectConversation: (String) -> Unit,
    onRenameConversation: (String, String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onClearAllConversations: () -> Unit,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is ConversationsUiState.Loading -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .testTag("history_loading_view")
            ) {
                TopAppBar(
                    title = {
                        Text("Conversation History", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = SuperCyanAccent, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading conversation history...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        is ConversationsUiState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .testTag("history_error_view")
            ) {
                TopAppBar(
                    title = {
                        Text("Conversation History", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = SuperRose, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Unable to load conversations",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = uiState.errorMessage,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onRetry,
                                colors = ButtonDefaults.buttonColors(containerColor = SuperCyanAccent)
                            ) {
                                Text("Retry", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        is ConversationsUiState.Success -> {
            HistorySuccessContent(
                conversations = uiState.conversations,
                onSelectConversation = onSelectConversation,
                onRenameConversation = onRenameConversation,
                onDeleteConversation = onDeleteConversation,
                onClearAllConversations = onClearAllConversations,
                modifier = modifier
            )
        }
    }
}

/**
 * Overload for backward compatibility and list input.
 */
@Composable
fun HistoryScreen(
    conversations: List<ConversationEntity>,
    onSelectConversation: (String) -> Unit,
    onRenameConversation: (String, String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onClearAllConversations: () -> Unit,
    modifier: Modifier = Modifier
) {
    HistoryScreen(
        uiState = ConversationsUiState.Success(conversations),
        onSelectConversation = onSelectConversation,
        onRenameConversation = onRenameConversation,
        onDeleteConversation = onDeleteConversation,
        onClearAllConversations = onClearAllConversations,
        onRetry = {},
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistorySuccessContent(
    conversations: List<ConversationEntity>,
    onSelectConversation: (String) -> Unit,
    onRenameConversation: (String, String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onClearAllConversations: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showClearAllDialog by remember { mutableStateOf(false) }
    var conversationToRename by remember { mutableStateOf<ConversationEntity?>(null) }
    var renameText by remember { mutableStateOf("") }

    val filteredList = conversations.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("history_screen")
    ) {
        TopAppBar(
            title = {
                Column {
                    Text("Conversation History", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("${conversations.size} saved conversations", fontSize = 11.sp, color = SuperCyanAccent)
                }
            },
            actions = {
                if (conversations.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearAllDialog = true },
                        modifier = Modifier.testTag("clear_all_history_button")
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All", tint = SuperRose)
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search chats by keyword or category...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input")
            )

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "No conversation history yet." else "No chats matching \"$searchQuery\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList) { conv ->
                        val dateFormatted = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(conv.updatedAt))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            onClick = { onSelectConversation(conv.conversationId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("history_item_${conv.conversationId}")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubble,
                                    contentDescription = null,
                                    tint = SuperCyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(conv.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("$dateFormatted • ${conv.category.replaceFirstChar { it.uppercase() }}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Row {
                                    IconButton(
                                        onClick = {
                                            conversationToRename = conv
                                            renameText = conv.title
                                        },
                                        modifier = Modifier.size(32.dp).testTag("rename_history_button")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Rename", modifier = Modifier.size(16.dp))
                                    }

                                    IconButton(
                                        onClick = { onDeleteConversation(conv.conversationId) },
                                        modifier = Modifier.size(32.dp).testTag("delete_history_button")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SuperRose, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Rename Dialog
    if (conversationToRename != null) {
        AlertDialog(
            onDismissRequest = { conversationToRename = null },
            title = { Text("Rename Conversation") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("rename_input")
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (renameText.isNotBlank()) {
                            onRenameConversation(conversationToRename!!.conversationId, renameText.trim())
                            conversationToRename = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { conversationToRename = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear All Dialog
    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("Delete All Conversations?") },
            text = { Text("This will permanently delete all conversations and message histories.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllConversations()
                        showClearAllDialog = false
                    }
                ) {
                    Text("Delete All", color = SuperRose)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
