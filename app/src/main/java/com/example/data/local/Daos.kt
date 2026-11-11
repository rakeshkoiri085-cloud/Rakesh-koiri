package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE conversationId = :id")
    suspend fun getConversationById(id: String): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE title LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchConversations(query: String): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Query("UPDATE conversations SET title = :newTitle, updatedAt = :updatedAt WHERE conversationId = :id")
    suspend fun renameConversation(id: String, newTitle: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM conversations WHERE conversationId = :id")
    suspend fun deleteConversationById(id: String)

    @Query("SELECT COUNT(*) FROM conversations")
    suspend fun getConversationCount(): Int

    @Query("DELETE FROM conversations")
    suspend fun clearAllConversations()
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    suspend fun getMessagesList(conversationId: String): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE messageId = :messageId")
    suspend fun deleteMessageById(messageId: String)

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: String)

    @Query("UPDATE messages SET status = :status, content = :content WHERE messageId = :messageId")
    suspend fun updateMessageContentAndStatus(messageId: String, content: String, status: String)
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE projectId = :id")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("UPDATE projects SET status = :status, progress = :progress, updatedAt = :updatedAt WHERE projectId = :id")
    suspend fun updateProjectStatus(id: String, status: String, progress: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectCount(): Int

    @Query("DELETE FROM projects WHERE projectId = :id")
    suspend fun deleteProjectById(id: String)

    @Query("DELETE FROM projects")
    suspend fun clearAllProjects()
}

@Dao
interface ProjectTaskDao {
    @Query("SELECT * FROM project_tasks WHERE projectId = :projectId ORDER BY isCompleted ASC, createdAt ASC")
    fun getTasksForProject(projectId: String): Flow<List<ProjectTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ProjectTaskEntity)

    @Query("UPDATE project_tasks SET isCompleted = :isCompleted WHERE taskId = :taskId")
    suspend fun setTaskCompleted(taskId: String, isCompleted: Boolean)

    @Query("DELETE FROM project_tasks WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: String)
}

@Dao
interface LessonProgressDao {
    @Query("SELECT * FROM lesson_progress")
    fun getAllLessonProgress(): Flow<List<LessonProgressEntity>>

    @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId")
    suspend fun getProgressForLesson(lessonId: String): LessonProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLessonProgress(progress: LessonProgressEntity)

    @Query("DELETE FROM lesson_progress")
    suspend fun resetAllProgress()
}

@Dao
interface UsageDao {
    @Query("SELECT * FROM usage_records ORDER BY id DESC")
    fun getAllUsage(): Flow<List<UsageEntity>>

    @Query("SELECT * FROM usage_records ORDER BY id DESC LIMIT 50")
    fun getRecentUsage(): Flow<List<UsageEntity>>

    @Query("SELECT SUM(requests) FROM usage_records")
    fun getTotalRequests(): Flow<Int?>

    @Query("SELECT SUM(tokens) FROM usage_records")
    fun getTotalTokens(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun logUsage(usage: UsageEntity)

    @Query("DELETE FROM usage_records")
    suspend fun clearUsage()
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserFlow(userId: String = "user_default_01"): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String = "user_default_01"): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserEntity)

    @Query("UPDATE users SET plan = :plan WHERE userId = :userId")
    suspend fun updateUserPlan(userId: String, plan: String)
}
