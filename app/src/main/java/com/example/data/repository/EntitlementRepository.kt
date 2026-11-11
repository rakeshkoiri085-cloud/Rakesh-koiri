package com.example.data.repository

import com.example.data.local.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SuperAiPlan(
    val title: String,
    val priceDisplay: String,
    val maxDailyRequests: Int,
    val maxContextTokens: Int,
    val badgeColorHex: Long
) {
    FREE("SUPER AI FREE", "Always $0", 50, 4096, 0xFF64748B),
    PRO("SUPER AI PRO", "$12 / month", 1000, 32768, 0xFF6366F1),
    ADMIN("SUPER AI ADMIN", "Internal Tier", 99999, 1000000, 0xFFF59E0B)
}

data class PlanFeature(
    val name: String,
    val includedInFree: Boolean,
    val includedInPro: Boolean
)

class EntitlementRepository(
    private val userDao: UserDao
) {
    private val _currentPlan = MutableStateFlow(SuperAiPlan.FREE)
    val currentPlan: StateFlow<SuperAiPlan> = _currentPlan.asStateFlow()

    fun getFeaturesList(): List<PlanFeature> {
        return listOf(
            PlanFeature("Full AI Chat & Reasoning", true, true),
            PlanFeature("Dedicated Coding Assistant (All Languages)", true, true),
            PlanFeature("Complete 6-Level AI Curriculum & Quizzes", true, true),
            PlanFeature("Study Assistant, Flashcards & Notes", true, true),
            PlanFeature("Prompt Generator (All 8 Categories)", true, true),
            PlanFeature("Project Workspace & Interactive Tasks", true, true),
            PlanFeature("Content & Business Idea Assistants", true, true),
            PlanFeature("Conversation History & Local Persistence", true, true),
            PlanFeature("Higher Daily Limits (1,000 requests/day)", false, true),
            PlanFeature("Gemini 3.1 Pro Preview Reasoning Model", false, true),
            PlanFeature("32K Extended Context Window", false, true),
            PlanFeature("Advanced File & Multi-Code Analysis", false, true),
            PlanFeature("Automated Agent Workflows", false, true)
        )
    }

    suspend fun switchPlan(plan: SuperAiPlan) {
        _currentPlan.value = plan
        userDao.updateUserPlan("user_default_01", plan.name)
    }

    fun canAccessFeature(featureName: String): Boolean {
        if (_currentPlan.value == SuperAiPlan.ADMIN || _currentPlan.value == SuperAiPlan.PRO) {
            return true
        }
        // Free features
        return when (featureName) {
            "gemini-3.1-pro" -> false
            "extended_context" -> false
            "batch_agent_automation" -> false
            else -> true
        }
    }
}
