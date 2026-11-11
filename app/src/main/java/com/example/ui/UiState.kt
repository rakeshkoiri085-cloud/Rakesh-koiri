package com.example.ui

import com.example.data.local.ConversationEntity
import com.example.data.local.MessageEntity

/**
 * Generic sealed class representing standard UI states for data operations.
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
}

/**
 * Sealed class hierarchy modeling the UI states of the Chat and conversation screens,
 * observed directly from the Room database repository layer.
 */
sealed class ChatUiState {
    /** Data operation is in progress (retrieving conversations or messages from Room). */
    data object Loading : ChatUiState()

    /** Active conversation and messages successfully loaded and observed from Room. */
    data class Success(
        val conversation: ConversationEntity?,
        val messages: List<MessageEntity>,
        val isGenerating: Boolean = false
    ) : ChatUiState()

    /** An error occurred during database access, message dispatch, or inference. */
    data class Error(
        val errorMessage: String,
        val canRetry: Boolean = true,
        val failedPrompt: String? = null
    ) : ChatUiState()
}

/**
 * Sealed class hierarchy representing conversation history state observed from the repository.
 */
sealed class ConversationsUiState {
    data object Loading : ConversationsUiState()
    data class Success(val conversations: List<ConversationEntity>) : ConversationsUiState()
    data class Error(val errorMessage: String) : ConversationsUiState()
}
