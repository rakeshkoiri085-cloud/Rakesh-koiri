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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProjectEntity
import com.example.data.local.ProjectTaskEntity
import com.example.data.repository.ProjectRepository
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperEmerald
import com.example.ui.theme.SuperIndigoPrimary
import com.example.ui.theme.SuperRose
import kotlinx.coroutines.flow.flowOf

val ProjectCategories = listOf(
    "AI App", "Website", "Coding Project", "School Project", "Business Idea", "Content Project"
)

val ProjectStatuses = listOf(
    "Planning", "In Progress", "Testing", "Completed"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    projects: List<ProjectEntity>,
    projectRepository: ProjectRepository,
    onCreateProject: (String, String, String) -> Unit,
    onDeleteProject: (String) -> Unit,
    onConsultAiAboutProject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var selectedProject by remember { mutableStateOf<ProjectEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("projects_screen")
    ) {
        TopAppBar(
            title = {
                Column {
                    Text("Project Workspace", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Manage AI apps, code tasks, checklists & notes", fontSize = 11.sp, color = SuperCyanAccent)
                }
            },
            actions = {
                IconButton(
                    onClick = { showNewProjectDialog = true },
                    modifier = Modifier.testTag("add_project_top_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Project")
                }
            }
        )

        if (projects.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = SuperIndigoPrimary,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("No projects yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Create your first project to track milestones, tasks, and get direct assistance from SUPER AI.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showNewProjectDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuperIndigoPrimary),
                    modifier = Modifier.testTag("empty_state_create_project_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Project")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(projects) { project ->
                    val tasksFlow = remember(project.projectId) { projectRepository.getTasks(project.projectId) }
                    val tasks by tasksFlow.collectAsState(initial = emptyList())

                    ProjectCard(
                        project = project,
                        tasks = tasks,
                        onToggleTask = { taskId, done ->
                            // coroutine handled in viewmodel or launched
                        },
                        onDeleteProject = { onDeleteProject(project.projectId) },
                        onConsultAi = {
                            val prompt = "Here is my project '${project.name}' (${project.category}): ${project.description}. What are the next 3 recommended steps and potential bottlenecks?"
                            onConsultAiAboutProject(prompt)
                        },
                        onSelect = { selectedProject = project }
                    )
                }
            }
        }
    }

    // New Project Dialog
    if (showNewProjectDialog) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var selectedCat by remember { mutableStateOf(ProjectCategories[0]) }
        var catExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showNewProjectDialog = false },
            title = { Text("Create New Project") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Project Name") },
                        placeholder = { Text("e.g. AI Study Assistant App") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_project_name_input")
                    )

                    ExposedDropdownMenuBox(
                        expanded = catExpanded,
                        onExpandedChange = { catExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCat,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = catExpanded,
                            onDismissRequest = { catExpanded = false }
                        ) {
                            ProjectCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        selectedCat = cat
                                        catExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Goals") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("new_project_desc_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onCreateProject(name.trim(), description.trim(), selectedCat)
                            showNewProjectDialog = false
                        }
                    },
                    enabled = name.isNotBlank(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_create_project_button")
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewProjectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProjectCard(
    project: ProjectEntity,
    tasks: List<ProjectTaskEntity>,
    onToggleTask: (String, Boolean) -> Unit,
    onDeleteProject: () -> Unit,
    onConsultAi: () -> Unit,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_card_${project.projectId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SuperIndigoPrimary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = project.category,
                        color = SuperIndigoPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (project.status) {
                            "Completed" -> SuperEmerald.copy(alpha = 0.2f)
                            "Testing" -> SuperCyanAccent.copy(alpha = 0.2f)
                            else -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                        }
                    ) {
                        Text(
                            text = project.status,
                            color = when (project.status) {
                                "Completed" -> SuperEmerald
                                "Testing" -> SuperCyanAccent
                                else -> Color(0xFFF59E0B)
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteProject,
                        modifier = Modifier.size(28.dp).padding(start = 4.dp).testTag("delete_project_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SuperRose, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(project.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (project.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(project.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { project.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = SuperCyanAccent
            )

            // Tasks Preview (up to 3)
            if (tasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text("Key Tasks (${tasks.count { it.isCompleted }}/${tasks.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                tasks.take(3).forEach { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (task.isCompleted) SuperEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = task.title,
                            fontSize = 12.sp,
                            color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bridge to SUPER AI Chat
            Button(
                onClick = onConsultAi,
                colors = ButtonDefaults.buttonColors(containerColor = SuperIndigoPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("consult_ai_project_button")
            ) {
                Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ask SUPER AI about this project", fontSize = 12.sp)
            }
        }
    }
}
