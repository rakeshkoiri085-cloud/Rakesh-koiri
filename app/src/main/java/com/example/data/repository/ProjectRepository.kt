package com.example.data.repository

import com.example.data.local.ProjectDao
import com.example.data.local.ProjectEntity
import com.example.data.local.ProjectTaskDao
import com.example.data.local.ProjectTaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val projectTaskDao: ProjectTaskDao
) {
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    suspend fun getProject(id: String): ProjectEntity? = projectDao.getProjectById(id)

    suspend fun getProjectCount(): Int = projectDao.getProjectCount()

    suspend fun clearAllProjects() {
        projectDao.clearAllProjects()
    }

    fun getTasks(projectId: String): Flow<List<ProjectTaskEntity>> =
        projectTaskDao.getTasksForProject(projectId)

    suspend fun createProject(
        name: String,
        description: String,
        category: String,
        status: String = "Planning"
    ): String {
        val id = UUID.randomUUID().toString()
        val project = ProjectEntity(
            projectId = id,
            name = name,
            description = description,
            category = category,
            status = status,
            progress = when (status) {
                "Planning" -> 15
                "In Progress" -> 50
                "Testing" -> 85
                "Completed" -> 100
                else -> 0
            }
        )
        projectDao.insertProject(project)

        // Add starter default tasks based on project category
        val defaultTasks = when (category) {
            "AI App" -> listOf("Define user persona & LLM prompt", "Setup Android Jetpack Compose UI", "Integrate Gemini API endpoints", "Test response latency & edge cases")
            "Website" -> listOf("Wireframe landing page", "Build responsive layout", "Add contact form & analytics", "Deploy to production")
            "Coding Project" -> listOf("Initialize repository", "Write core business logic", "Write unit test suite", "Optimize algorithm runtime")
            "Business Idea" -> listOf("Draft 1-page lean canvas", "Conduct 5 target user interviews", "Analyze top 3 competitors", "Calculate break-even pricing")
            else -> listOf("Define project milestones", "Draft initial notes", "Review progress with SUPER AI")
        }

        for (taskTitle in defaultTasks) {
            addTask(id, taskTitle, "Medium")
        }

        return id
    }

    suspend fun updateProject(project: ProjectEntity) {
        projectDao.updateProject(project)
    }

    suspend fun updateStatus(id: String, status: String, progress: Int) {
        projectDao.updateProjectStatus(id, status, progress)
    }

    suspend fun deleteProject(id: String) {
        projectDao.deleteProjectById(id)
    }

    suspend fun addTask(projectId: String, title: String, priority: String = "Medium") {
        val task = ProjectTaskEntity(
            taskId = UUID.randomUUID().toString(),
            projectId = projectId,
            title = title,
            priority = priority
        )
        projectTaskDao.insertTask(task)
    }

    suspend fun toggleTask(taskId: String, isCompleted: Boolean) {
        projectTaskDao.setTaskCompleted(taskId, isCompleted)
    }

    suspend fun deleteTask(taskId: String) {
        projectTaskDao.deleteTask(taskId)
    }
}
