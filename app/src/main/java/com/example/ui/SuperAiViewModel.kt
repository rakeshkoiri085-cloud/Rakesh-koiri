package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ConversationEntity
import com.example.data.local.MessageEntity
import com.example.data.local.ProjectEntity
import com.example.data.local.SuperAiDatabase
import com.example.data.local.UsageEntity
import com.example.data.repository.ChatRepository
import com.example.data.repository.ContentAndBusinessRepository
import com.example.data.repository.EntitlementRepository
import com.example.data.repository.LearningRepository
import com.example.data.repository.ProjectRepository
import com.example.data.repository.PromptRepository
import com.example.data.repository.StudyRepository
import com.example.data.repository.SuperAiPlan
import com.example.ui.components.AppScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SuperAiViewModel(application: Application) : AndroidViewModel(application) {
    private val database = SuperAiDatabase.getDatabase(application)

    val chatRepository = ChatRepository(
        conversationDao = database.conversationDao(),
        messageDao = database.messageDao(),
        usageDao = database.usageDao()
    )

    val projectRepository = ProjectRepository(
        projectDao = database.projectDao(),
        projectTaskDao = database.projectTaskDao()
    )

    val learningRepository = LearningRepository(
        lessonProgressDao = database.lessonProgressDao()
    )

    val studyRepository = StudyRepository()
    val promptRepository = PromptRepository()
    val toolsRepository = ContentAndBusinessRepository()
    val entitlementRepository = EntitlementRepository(database.userDao())

    // UI Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Active Conversation
    private val _activeConversationId = MutableStateFlow<String?>(null)
    val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

    val allConversations = chatRepository.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMessages: StateFlow<List<MessageEntity>> = _activeConversationId
        .flatMapLatest { id ->
            if (id != null) chatRepository.getMessages(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Generation State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()
    private var currentGenerationJob: Job? = null

    // Transient Data Operation States for UI
    private val _chatOperationError = MutableStateFlow<String?>(null)
    val chatOperationError: StateFlow<String?> = _chatOperationError.asStateFlow()

    private val _isOperationLoading = MutableStateFlow(false)
    val isOperationLoading: StateFlow<Boolean> = _isOperationLoading.asStateFlow()

    // Internal data class for combining chat state inputs
    private data class ChatStateInputs(
        val conversationId: String?,
        val conversations: List<ConversationEntity>,
        val isGenerating: Boolean,
        val error: String?,
        val isOpLoading: Boolean
    )

    /**
     * Primary Sealed Chat UI State observed by the UI layer from the Room repository.
     * Accurately reflects Loading, Success (with messages and streaming status), and Error states.
     */
    val chatUiState: StateFlow<ChatUiState> = combine(
        _activeConversationId,
        chatRepository.allConversations,
        _isGenerating,
        _chatOperationError,
        _isOperationLoading
    ) { convId, conversations, generating, error, opLoading ->
        ChatStateInputs(convId, conversations, generating, error, opLoading)
    }.flatMapLatest { inputs ->
        if (inputs.isOpLoading) {
            flowOf(ChatUiState.Loading)
        } else if (inputs.error != null) {
            flowOf(ChatUiState.Error(errorMessage = inputs.error, canRetry = true))
        } else {
            val targetConvId = inputs.conversationId ?: inputs.conversations.firstOrNull()?.conversationId
            if (targetConvId == null) {
                flowOf(
                    ChatUiState.Success(
                        conversation = null,
                        messages = emptyList(),
                        isGenerating = inputs.isGenerating
                    )
                )
            } else {
                chatRepository.getMessages(targetConvId)
                    .map<List<MessageEntity>, ChatUiState> { messages ->
                        val currentConv = inputs.conversations.find { it.conversationId == targetConvId }
                        ChatUiState.Success(
                            conversation = currentConv,
                            messages = messages,
                            isGenerating = inputs.isGenerating
                        )
                    }
                    .catch { e ->
                        emit(
                            ChatUiState.Error(
                                errorMessage = e.message ?: "Failed to read conversation from local database",
                                canRetry = true
                            )
                        )
                    }
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ChatUiState.Loading
    )

    /**
     * Sealed Conversation History State observed by the UI layer from the Room repository.
     */
    val conversationsUiState: StateFlow<ConversationsUiState> = chatRepository.allConversations
        .map<List<ConversationEntity>, ConversationsUiState> { conversations ->
            ConversationsUiState.Success(conversations)
        }
        .catch { e ->
            emit(ConversationsUiState.Error(e.message ?: "Failed to load conversations"))
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ConversationsUiState.Loading
        )

    // Projects & Tasks
    val allProjects = projectRepository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Lesson Progress
    val lessonProgress = learningRepository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Usage Telemetry
    val usageHistory: StateFlow<List<UsageEntity>> = chatRepository.usageHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Plan
    val currentPlan = entitlementRepository.currentPlan

    init {
        // Create initial starter project and conversation if first time
        viewModelScope.launch {
            if (chatRepository.getConversationCount() == 0) {
                val convId = chatRepository.createConversation("Welcome to SUPER AI", "general")
                chatRepository.sendMessage(
                    conversationId = convId,
                    userPrompt = "Hello SUPER AI! What can you do?",
                    mode = "chat"
                )
                _activeConversationId.value = convId
            }

            if (projectRepository.getProjectCount() == 0) {
                projectRepository.createProject(
                    name = "SUPER AI App Prototype",
                    description = "Design and build an AI platform with Jetpack Compose, Room persistence, and Gemini API",
                    category = "AI App",
                    status = "In Progress"
                )
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectConversation(conversationId: String) {
        _activeConversationId.value = conversationId
        _currentScreen.value = AppScreen.CHAT
    }

    fun createNewConversation(title: String = "New Chat", category: String = "general") {
        viewModelScope.launch {
            _isOperationLoading.value = true
            _chatOperationError.value = null
            try {
                val newId = chatRepository.createConversation(title, category)
                _activeConversationId.value = newId
                _currentScreen.value = AppScreen.CHAT
            } catch (e: Exception) {
                _chatOperationError.value = "Failed to create conversation: ${e.localizedMessage}"
            } finally {
                _isOperationLoading.value = false
            }
        }
    }

    fun sendMessage(userPrompt: String, mode: String = "chat") {
        val convId = _activeConversationId.value
        if (convId == null) {
            viewModelScope.launch {
                _isOperationLoading.value = true
                _chatOperationError.value = null
                try {
                    val newId = chatRepository.createConversation("SUPER AI Chat", mode)
                    _activeConversationId.value = newId
                    executeSend(newId, userPrompt, mode)
                } catch (e: Exception) {
                    _chatOperationError.value = "Failed to initiate conversation: ${e.localizedMessage}"
                } finally {
                    _isOperationLoading.value = false
                }
            }
        } else {
            executeSend(convId, userPrompt, mode)
        }
    }

    private fun executeSend(conversationId: String, userPrompt: String, mode: String) {
        currentGenerationJob?.cancel()
        _chatOperationError.value = null
        currentGenerationJob = viewModelScope.launch {
            _isGenerating.value = true
            val result = chatRepository.sendMessage(conversationId, userPrompt, mode)
            _isGenerating.value = false
            if (result.isFailure) {
                val errorMsg = result.exceptionOrNull()?.localizedMessage
                    ?: "Unable to process prompt. Please verify connection and retry."
                _chatOperationError.value = errorMsg
            }
        }
    }

    fun stopGeneration() {
        currentGenerationJob?.cancel()
        _isGenerating.value = false
    }

    fun dismissChatError() {
        _chatOperationError.value = null
    }

    fun regenerateLastResponse() {
        val convId = _activeConversationId.value ?: return
        val lastUserMsg = activeMessages.value.findLast { it.role == "user" }?.content ?: return
        sendMessage(lastUserMsg)
    }

    fun retryMessage(prompt: String) {
        _chatOperationError.value = null
        sendMessage(prompt)
    }

    fun startPromptInChat(prompt: String, mode: String = "chat") {
        _currentScreen.value = AppScreen.CHAT
        sendMessage(prompt, mode)
    }

    fun renameConversation(conversationId: String, newTitle: String) {
        viewModelScope.launch {
            try {
                chatRepository.renameConversation(conversationId, newTitle)
            } catch (e: Exception) {
                _chatOperationError.value = "Failed to rename conversation: ${e.localizedMessage}"
            }
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                chatRepository.deleteConversation(conversationId)
                if (_activeConversationId.value == conversationId) {
                    _activeConversationId.value = allConversations.value.firstOrNull { it.conversationId != conversationId }?.conversationId
                }
            } catch (e: Exception) {
                _chatOperationError.value = "Failed to delete conversation: ${e.localizedMessage}"
            }
        }
    }

    fun clearConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                chatRepository.clearConversationMessages(conversationId)
            } catch (e: Exception) {
                _chatOperationError.value = "Failed to clear conversation messages: ${e.localizedMessage}"
            }
        }
    }

    fun clearAllConversations() {
        viewModelScope.launch {
            try {
                chatRepository.clearAllConversations()
                _activeConversationId.value = null
            } catch (e: Exception) {
                _chatOperationError.value = "Failed to clear conversation history: ${e.localizedMessage}"
            }
        }
    }

    fun createProject(name: String, description: String, category: String) {
        viewModelScope.launch {
            projectRepository.createProject(name, description, category)
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            projectRepository.deleteProject(projectId)
        }
    }

    fun markLessonCompleted(lessonId: String, levelNumber: Int, quizScore: Int) {
        viewModelScope.launch {
            learningRepository.markLessonCompleted(lessonId, levelNumber, quizScore)
        }
    }

    fun wipeAllData() {
        viewModelScope.launch {
            database.clearAllTables()
            _activeConversationId.value = null
            _currentScreen.value = AppScreen.HOME
        }
    }
}
