package com.example.data.repository

import com.example.data.local.ConversationDao
import com.example.data.local.ConversationEntity
import com.example.data.local.MessageDao
import com.example.data.local.MessageEntity
import com.example.data.local.UsageDao
import com.example.data.local.UsageEntity
import com.example.data.remote.AiEngine
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Repository interface defining data access and persistence operations for chat,
 * conversations, messages, and associated usage telemetry.
 *
 * Fully isolates Room DAOs and remote AI generation logic from ViewModels and the UI layer.
 */
interface ChatRepository {
    /** Reactive stream of all conversations ordered by recent activity. */
    val allConversations: Flow<List<ConversationEntity>>

    /** Reactive stream of usage records. */
    val usageHistory: Flow<List<UsageEntity>>

    /** Search conversations by title query. */
    fun searchConversations(query: String): Flow<List<ConversationEntity>>

    /** Reactive stream of messages for a given conversation ordered chronologically. */
    fun getMessages(conversationId: String): Flow<List<MessageEntity>>

    /** Retrieve one-shot list of messages for a given conversation. */
    suspend fun getMessagesList(conversationId: String): List<MessageEntity>

    /** Retrieve conversation metadata by ID. */
    suspend fun getConversation(conversationId: String): ConversationEntity?

    /** Total count of saved conversations. */
    suspend fun getConversationCount(): Int

    /** Create and persist a new conversation entity. Returns the generated conversation ID. */
    suspend fun createConversation(title: String, category: String = "general"): String

    /** Rename an existing conversation. */
    suspend fun renameConversation(conversationId: String, newTitle: String)

    /** Delete a conversation and its messages. */
    suspend fun deleteConversation(conversationId: String)

    /** Clear messages for a specific conversation while keeping conversation record. */
    suspend fun clearConversationMessages(conversationId: String)

    /** Clear all conversations and messages from local database. */
    suspend fun clearAllConversations()

    /** Insert or update an individual message entity. */
    suspend fun saveMessage(message: MessageEntity)

    /** Delete a specific message by its ID. */
    suspend fun deleteMessage(messageId: String)

    /**
     * Send a prompt in the conversation:
     * 1. Persists user prompt to Room.
     * 2. Persists temporary streaming/thinking assistant placeholder to Room.
     * 3. Calls AI generation engine with contextual history.
     * 4. Updates assistant message with response and code snippets.
     * 5. Updates conversation title & timestamp in Room.
     * 6. Records usage telemetry to Room.
     */
    suspend fun sendMessage(
        conversationId: String,
        userPrompt: String,
        mode: String = "chat"
    ): Result<MessageEntity>

    /** Retries the last prompt in the conversation. */
    suspend fun retryMessage(conversationId: String, lastUserPrompt: String, mode: String = "chat")

    /** Wipes all chat conversations, messages, and usage telemetry records. */
    suspend fun wipeAllChatData()
}

/**
 * Default implementation of [ChatRepository] backed by Room database DAOs and [AiEngine].
 */
class ChatRepositoryImpl(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val usageDao: UsageDao
) : ChatRepository {

    override val allConversations: Flow<List<ConversationEntity>> =
        conversationDao.getAllConversations()

    override val usageHistory: Flow<List<UsageEntity>> =
        usageDao.getAllUsage()

    override fun searchConversations(query: String): Flow<List<ConversationEntity>> =
        conversationDao.searchConversations(query)

    override fun getMessages(conversationId: String): Flow<List<MessageEntity>> =
        messageDao.getMessagesForConversation(conversationId)

    override suspend fun getMessagesList(conversationId: String): List<MessageEntity> =
        messageDao.getMessagesList(conversationId)

    override suspend fun getConversation(conversationId: String): ConversationEntity? =
        conversationDao.getConversationById(conversationId)

    override suspend fun getConversationCount(): Int =
        conversationDao.getConversationCount()

    override suspend fun createConversation(title: String, category: String): String {
        val id = UUID.randomUUID().toString()
        val conversation = ConversationEntity(
            conversationId = id,
            title = title,
            category = category,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        conversationDao.insertConversation(conversation)
        return id
    }

    override suspend fun renameConversation(conversationId: String, newTitle: String) {
        conversationDao.renameConversation(conversationId, newTitle)
    }

    override suspend fun deleteConversation(conversationId: String) {
        messageDao.deleteMessagesForConversation(conversationId)
        conversationDao.deleteConversationById(conversationId)
    }

    override suspend fun clearConversationMessages(conversationId: String) {
        messageDao.deleteMessagesForConversation(conversationId)
    }

    override suspend fun clearAllConversations() {
        conversationDao.clearAllConversations()
    }

    override suspend fun saveMessage(message: MessageEntity) {
        messageDao.insertMessage(message)
    }

    override suspend fun deleteMessage(messageId: String) {
        messageDao.deleteMessageById(messageId)
    }

    override suspend fun sendMessage(
        conversationId: String,
        userPrompt: String,
        mode: String
    ): Result<MessageEntity> {
        // 1. Insert user message into Room
        val userMsgId = UUID.randomUUID().toString()
        val userMsg = MessageEntity(
            messageId = userMsgId,
            conversationId = conversationId,
            role = "user",
            content = userPrompt,
            createdAt = System.currentTimeMillis()
        )
        messageDao.insertMessage(userMsg)

        // 2. Fetch recent conversation messages for contextual chat
        val historyList = messageDao.getMessagesList(conversationId)
            .takeLast(6)
            .map { it.role to it.content }

        // 3. Insert temporary streaming/loading message
        val assistantMsgId = UUID.randomUUID().toString()
        val pendingMsg = MessageEntity(
            messageId = assistantMsgId,
            conversationId = conversationId,
            role = "assistant",
            content = "SUPER AI is thinking...",
            status = "STREAMING",
            createdAt = System.currentTimeMillis() + 1
        )
        messageDao.insertMessage(pendingMsg)

        try {
            // 4. Generate response via unified AI Engine
            val result = AiEngine.generate(
                prompt = userPrompt,
                mode = mode,
                conversationHistory = historyList
            )

            // Extract potential code snippet if present in markdown
            val codeSnippet = extractCodeSnippet(result.text)

            // 5. Update assistant message with completed content
            val completedMsg = MessageEntity(
                messageId = assistantMsgId,
                conversationId = conversationId,
                role = "assistant",
                content = result.text,
                codeSnippet = codeSnippet,
                language = if (codeSnippet != null) "python" else null,
                createdAt = System.currentTimeMillis() + 2,
                status = "SUCCESS",
                followUpsJson = result.followUps.joinToString(separator = "|||")
            )
            messageDao.insertMessage(completedMsg)

            // 6. Update conversation timestamp and smart auto-title
            val conversation = conversationDao.getConversationById(conversationId)
            if (conversation != null) {
                val newTitle = if (conversation.title.startsWith("New Chat", ignoreCase = true) ||
                    conversation.title == "SUPER AI Chat"
                ) {
                    userPrompt.take(30).trim() + if (userPrompt.length > 30) "..." else ""
                } else conversation.title

                conversationDao.updateConversation(
                    conversation.copy(
                        title = newTitle,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }

            // 7. Log usage metrics to Room
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            usageDao.logUsage(
                UsageEntity(
                    date = dateStr,
                    requests = 1,
                    tokens = result.tokensEstimate,
                    featureUsage = mode
                )
            )

            return Result.success(completedMsg)
        } catch (e: Exception) {
            val errorMsg = MessageEntity(
                messageId = assistantMsgId,
                conversationId = conversationId,
                role = "assistant",
                content = "SUPER AI is temporarily unable to complete this request. Please check your connection or tap retry.",
                createdAt = System.currentTimeMillis() + 2,
                status = "ERROR"
            )
            messageDao.insertMessage(errorMsg)
            return Result.failure(e)
        }
    }

    override suspend fun retryMessage(
        conversationId: String,
        lastUserPrompt: String,
        mode: String
    ) {
        sendMessage(conversationId, lastUserPrompt, mode)
    }

    override suspend fun wipeAllChatData() {
        conversationDao.clearAllConversations()
        usageDao.clearUsage()
    }

    private fun extractCodeSnippet(text: String): String? {
        val startTag = "```"
        val startIndex = text.indexOf(startTag)
        if (startIndex != -1) {
            val codeStart = text.indexOf('\n', startIndex)
            if (codeStart != -1) {
                val endIndex = text.indexOf("```", codeStart)
                if (endIndex != -1) {
                    return text.substring(codeStart + 1, endIndex).trim()
                }
            }
        }
        return null
    }
}

/**
 * Factory function allowing instantiation via `ChatRepository(conversationDao, messageDao, usageDao)`
 * for seamless dependency injection and backward compatibility.
 */
fun ChatRepository(
    conversationDao: ConversationDao,
    messageDao: MessageDao,
    usageDao: UsageDao
): ChatRepository = ChatRepositoryImpl(conversationDao, messageDao, usageDao)
