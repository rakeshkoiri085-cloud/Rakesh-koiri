package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.remote.AiEngine
import androidx.room.Room
import com.example.data.local.SuperAiDatabase
import com.example.data.repository.ChatRepository
import com.example.data.repository.EntitlementRepository
import com.example.data.repository.LearningRepository
import com.example.data.repository.PromptRepository
import com.example.data.repository.SuperAiPlan
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name string resource`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SUPER AI", appName)
    }

    @Test
    fun `chat repository manages persistence with room`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, SuperAiDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        val repository: ChatRepository = ChatRepository(
            conversationDao = db.conversationDao(),
            messageDao = db.messageDao(),
            usageDao = db.usageDao()
        )

        // 1. Initial state check
        assertEquals(0, repository.getConversationCount())

        // 2. Create conversation via repository
        val convId = repository.createConversation("Kotlin Jetpack Compose Guide", "coding")
        assertNotNull(convId)
        assertEquals(1, repository.getConversationCount())

        val conversation = repository.getConversation(convId)
        assertNotNull(conversation)
        assertEquals("Kotlin Jetpack Compose Guide", conversation?.title)
        assertEquals("coding", conversation?.category)

        // 3. Send message via repository (persists user message + generated response)
        val result = repository.sendMessage(convId, "Explain Room database flow", "coding")
        assertTrue(result.isSuccess)

        // 4. Verify messages are persisted in Room
        val messages = repository.getMessagesList(convId)
        assertTrue(messages.size >= 2)
        assertEquals("user", messages[0].role)
        assertEquals("Explain Room database flow", messages[0].content)
        assertEquals("assistant", messages[1].role)
        assertTrue(messages[1].content.isNotBlank())

        // 5. Test search via Flow
        val searchResults = repository.searchConversations("Jetpack").first()
        assertEquals(1, searchResults.size)
        assertEquals(convId, searchResults[0].conversationId)

        // 6. Test delete conversation
        repository.deleteConversation(convId)
        assertEquals(0, repository.getConversationCount())
        val emptyMessages = repository.getMessagesList(convId)
        assertTrue(emptyMessages.isEmpty())

        db.close()
    }

    @Test
    fun `ai engine synthesizes robust response when offline`() = runBlocking {
        val response = AiEngine.generate(
            prompt = "How does binary search work?",
            mode = "code"
        )
        assertNotNull(response.text)
        assertTrue(response.text.isNotBlank())
        assertTrue(response.text.contains("Binary Search") || response.text.contains("code") || response.text.contains("SUPER AI"))
    }

    @Test
    fun `prompt repository constructs structured prompt`() {
        val repo = PromptRepository()
        val result = repo.buildEngineeredPrompt(
            category = "coding",
            userGoal = "Write an LRU Cache in Kotlin",
            styleOrTone = "Clean Architecture with Coroutines"
        )
        assertNotNull(result.masterPrompt)
        assertTrue(result.masterPrompt.contains("LRU Cache"))
        assertTrue(result.masterPrompt.contains("Principal") && result.masterPrompt.contains("Architect"))
    }

    @Test
    fun `learning curriculum contains all 6 progressive levels`() {
        val daoMock = object : com.example.data.local.LessonProgressDao {
            override fun getAllLessonProgress() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.data.local.LessonProgressEntity>())
            override suspend fun getProgressForLesson(lessonId: String) = null
            override suspend fun saveLessonProgress(progress: com.example.data.local.LessonProgressEntity) {}
            override suspend fun resetAllProgress() {}
        }
        val repo = LearningRepository(daoMock)
        val curriculum = repo.getCurriculum()
        assertEquals(6, curriculum.size)
        assertTrue(curriculum[0].title.contains("Level 1"))
        assertTrue(curriculum[5].title.contains("Level 6"))
    }

    @Test
    fun `entitlement free tier respects limits`() {
        val freePlan = SuperAiPlan.FREE
        assertEquals(50, freePlan.maxDailyRequests)
        assertEquals(4096, freePlan.maxContextTokens)

        val proPlan = SuperAiPlan.PRO
        assertEquals(1000, proPlan.maxDailyRequests)
        assertEquals(32768, proPlan.maxContextTokens)
    }

    @Test
    fun `chat ui sealed states reflect correct lifecycle and payload`() {
        val loadingState: com.example.ui.ChatUiState = com.example.ui.ChatUiState.Loading
        assertTrue(loadingState is com.example.ui.ChatUiState.Loading)

        val successState: com.example.ui.ChatUiState = com.example.ui.ChatUiState.Success(
            conversation = null,
            messages = emptyList(),
            isGenerating = true
        )
        assertTrue(successState is com.example.ui.ChatUiState.Success)
        assertTrue((successState as com.example.ui.ChatUiState.Success).isGenerating)
        assertEquals(0, successState.messages.size)

        val errorState: com.example.ui.ChatUiState = com.example.ui.ChatUiState.Error(
            errorMessage = "Network timeout communicating with AI Engine",
            canRetry = true,
            failedPrompt = "Explain coroutines"
        )
        assertTrue(errorState is com.example.ui.ChatUiState.Error)
        assertEquals("Network timeout communicating with AI Engine", (errorState as com.example.ui.ChatUiState.Error).errorMessage)
        assertTrue(errorState.canRetry)
        assertEquals("Explain coroutines", errorState.failedPrompt)
    }

    @Test
    fun `conversations ui sealed states and generic ui state work as expected`() {
        val convLoading: com.example.ui.ConversationsUiState = com.example.ui.ConversationsUiState.Loading
        assertTrue(convLoading is com.example.ui.ConversationsUiState.Loading)

        val convSuccess = com.example.ui.ConversationsUiState.Success(emptyList())
        assertEquals(0, convSuccess.conversations.size)

        val convError = com.example.ui.ConversationsUiState.Error("Room SQLite disk failure")
        assertEquals("Room SQLite disk failure", convError.errorMessage)

        val genericLoading: com.example.ui.UiState<String> = com.example.ui.UiState.Loading
        val genericSuccess: com.example.ui.UiState<String> = com.example.ui.UiState.Success("SUPER AI Payload")
        val genericError: com.example.ui.UiState<String> = com.example.ui.UiState.Error("Failed")

        assertTrue(genericLoading is com.example.ui.UiState.Loading)
        assertTrue(genericSuccess is com.example.ui.UiState.Success)
        assertEquals("SUPER AI Payload", (genericSuccess as com.example.ui.UiState.Success).data)
        assertTrue(genericError is com.example.ui.UiState.Error)
    }
}

