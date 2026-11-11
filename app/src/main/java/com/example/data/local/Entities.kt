package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String = "user_default_01",
    val name: String = "Student & Creator",
    val email: String = "creator@superai.local",
    val plan: String = "FREE", // FREE, PRO, ADMIN
    val createdAt: Long = System.currentTimeMillis(),
    val settingsJson: String = "{}"
)

@Entity(
    tableName = "conversations",
    indices = [Index(value = ["updatedAt"])]
)
data class ConversationEntity(
    @PrimaryKey val conversationId: String,
    val userId: String = "user_default_01",
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val model: String = "gemini-3.5-flash",
    val category: String = "general", // general, coding, study, content, business
    val isPinned: Boolean = false
)

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["conversationId"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["conversationId"]), Index(value = ["createdAt"])]
)
data class MessageEntity(
    @PrimaryKey val messageId: String,
    val conversationId: String,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val codeSnippet: String? = null,
    val language: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS", // "SUCCESS", "ERROR", "STREAMING"
    val followUpsJson: String? = null // serialized JSON list of suggested questions
)

@Entity(
    tableName = "projects",
    indices = [Index(value = ["updatedAt"])]
)
data class ProjectEntity(
    @PrimaryKey val projectId: String,
    val userId: String = "user_default_01",
    val name: String,
    val description: String,
    val category: String, // "AI App", "Website", "Coding Project", "School Project", "Business Idea", "Content Project"
    val status: String, // "Planning", "In Progress", "Testing", "Completed"
    val progress: Int = 0, // 0 - 100
    val notes: String = "",
    val codeFilesJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "project_tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["projectId"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class ProjectTaskEntity(
    @PrimaryKey val taskId: String,
    val projectId: String,
    val title: String,
    val isCompleted: Boolean = false,
    val priority: String = "Medium", // Low, Medium, High
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "usage_records")
data class UsageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "user_default_01",
    val date: String, // YYYY-MM-DD
    val requests: Int = 1,
    val tokens: Int = 120,
    val featureUsage: String = "chat"
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: String,
    val levelNumber: Int,
    val isCompleted: Boolean = false,
    val quizScore: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val userId: String = "user_default_01",
    val plan: String = "FREE", // FREE, PRO, ADMIN
    val status: String = "ACTIVE", // ACTIVE, EXPIRED, CANCELLED
    val provider: String = "GooglePlay",
    val subscriptionId: String = "sub_free_tier",
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis() + (365L * 24 * 3600 * 1000)
)
