package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SuperAiViewModel
import com.example.ui.components.AppScreen
import com.example.ui.components.SuperAiBottomNav
import com.example.ui.components.SuperAiNavRail
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CodingScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.PromptScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudyScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.theme.SuperAiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperAiTheme {
                SuperAiApp()
            }
        }
    }
}

@Composable
fun SuperAiApp(viewModel: SuperAiViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()
    val allConversations by viewModel.allConversations.collectAsState()
    val activeConversationId by viewModel.activeConversationId.collectAsState()
    val chatUiState by viewModel.chatUiState.collectAsState()
    val conversationsUiState by viewModel.conversationsUiState.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val lessonProgress by viewModel.lessonProgress.collectAsState()
    val usageHistory by viewModel.usageHistory.collectAsState()

    val currentConversation = allConversations.find { it.conversationId == activeConversationId }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            bottomBar = {
                if (!isWideScreen) {
                    SuperAiBottomNav(
                        currentScreen = currentScreen,
                        onSelectScreen = { viewModel.navigateTo(it) }
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    SuperAiNavRail(
                        currentScreen = currentScreen,
                        onSelectScreen = { viewModel.navigateTo(it) }
                    )
                }

                val contentModifier = Modifier
                    .weight(1f)
                    .fillMaxSize()

                when (currentScreen) {
                    AppScreen.HOME -> HomeScreen(
                        currentPlan = currentPlan,
                        recentConversations = allConversations,
                        onNavigate = { viewModel.navigateTo(it) },
                        onStartPromptInChat = { prompt -> viewModel.startPromptInChat(prompt) },
                        onSelectConversation = { id -> viewModel.selectConversation(id) },
                        modifier = contentModifier
                    )

                    AppScreen.CHAT -> ChatScreen(
                        uiState = chatUiState,
                        onSendMessage = { prompt -> viewModel.sendMessage(prompt, "chat") },
                        onNewConversation = { viewModel.createNewConversation() },
                        onRenameConversation = { id, title -> viewModel.renameConversation(id, title) },
                        onDeleteConversation = { id -> viewModel.deleteConversation(id) },
                        onClearConversation = { id -> viewModel.clearConversation(id) },
                        onRegenerateResponse = { viewModel.regenerateLastResponse() },
                        onStopGeneration = { viewModel.stopGeneration() },
                        onRetryMessage = { prompt -> viewModel.retryMessage(prompt) },
                        onExplainCodeInChat = { code -> viewModel.startPromptInChat("Explain this code in detail:\n\n```\n$code\n```") },
                        onDismissError = { viewModel.dismissChatError() },
                        modifier = contentModifier
                    )

                    AppScreen.CODE -> CodingScreen(
                        onOpenInChat = { prompt -> viewModel.startPromptInChat(prompt, "code") },
                        onSaveToProject = { code ->
                            viewModel.navigateTo(AppScreen.PROJECTS)
                        },
                        modifier = contentModifier
                    )

                    AppScreen.LEARN -> LearnScreen(
                        curriculum = viewModel.learningRepository.getCurriculum(),
                        progressList = lessonProgress,
                        onMarkCompleted = { lessonId, level, score ->
                            viewModel.markLessonCompleted(lessonId, level, score)
                        },
                        onTryInChat = { prompt -> viewModel.startPromptInChat(prompt, "learn") },
                        modifier = contentModifier
                    )

                    AppScreen.STUDY -> StudyScreen(
                        studyTopics = viewModel.studyRepository.getPreloadedTopics(),
                        onOpenInChat = { prompt -> viewModel.startPromptInChat(prompt, "study") },
                        modifier = contentModifier
                    )

                    AppScreen.PROJECTS -> ProjectsScreen(
                        projects = allProjects,
                        projectRepository = viewModel.projectRepository,
                        onCreateProject = { name, desc, cat -> viewModel.createProject(name, desc, cat) },
                        onDeleteProject = { id -> viewModel.deleteProject(id) },
                        onConsultAiAboutProject = { prompt -> viewModel.startPromptInChat(prompt, "project") },
                        modifier = contentModifier
                    )

                    AppScreen.TOOLS -> ToolsScreen(
                        repository = viewModel.toolsRepository,
                        onOpenInChat = { prompt -> viewModel.startPromptInChat(prompt, "tools") },
                        modifier = contentModifier
                    )

                    AppScreen.HISTORY -> HistoryScreen(
                        uiState = conversationsUiState,
                        onSelectConversation = { id -> viewModel.selectConversation(id) },
                        onRenameConversation = { id, title -> viewModel.renameConversation(id, title) },
                        onDeleteConversation = { id -> viewModel.deleteConversation(id) },
                        onClearAllConversations = { viewModel.clearAllConversations() },
                        modifier = contentModifier
                    )

                    AppScreen.SETTINGS -> SettingsScreen(
                        currentPlan = currentPlan,
                        entitlementRepository = viewModel.entitlementRepository,
                        usageHistory = usageHistory,
                        onClearAllData = { viewModel.wipeAllData() },
                        modifier = contentModifier
                    )
                }
            }
        }
    }
}
