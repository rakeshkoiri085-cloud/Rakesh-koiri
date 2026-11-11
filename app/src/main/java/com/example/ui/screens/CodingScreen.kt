package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.AiEngine
import com.example.ui.components.CodeBlockView
import com.example.ui.components.MarkdownText
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperIndigoPrimary
import kotlinx.coroutines.launch

enum class CodingAction(val title: String, val promptPrefix: String) {
    GENERATE("Generate Code", "Write production-ready, clean code for: "),
    EXPLAIN("Explain Code", "Explain the following code step-by-step for a beginner: "),
    DEBUG("Debug Code", "Identify bugs, syntax errors, or logical flaws in this code and provide fixes: "),
    IMPROVE("Improve Code", "Refactor and optimize the structure, readability, and algorithmic speed of this code: "),
    CONVERT("Convert Code", "Convert this code to the selected target language: "),
    ADD_FEATURE("Add Feature", "Add the requested feature to this code: "),
    PROJECT_GEN("Project Generator", "Generate a complete starter project architecture, file structure, and starter code for: "),
    ERROR_EXPLAIN("Error Explainer", "Explain this compiler or runtime error in simple terms and show how to fix it: ")
}

val SupportedLanguages = listOf(
    "Python", "JavaScript", "TypeScript", "HTML/CSS", "SQL", "Java", "C++", "C", "Kotlin", "Go", "Rust"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodingScreen(
    onOpenInChat: (String) -> Unit,
    onSaveToProject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedAction by remember { mutableStateOf(CodingAction.GENERATE) }
    var selectedLanguage by remember { mutableStateOf("Python") }
    var targetLanguage by remember { mutableStateOf("JavaScript") }
    var languageDropdownExpanded by remember { mutableStateOf(false) }

    var userCodeInput by remember {
        mutableStateOf("def calculate_fibonacci(n):\n    if n <= 1: return n\n    return calculate_fibonacci(n-1) + calculate_fibonacci(n-2)")
    }
    var promptInput by remember {
        mutableStateOf("Optimize this recursive Fibonacci function using memoization or DP")
    }

    var isLoading by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("coding_screen")
    ) {
        TopAppBar(
            title = {
                Column {
                    Text("Coding Assistant", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Multi-language code generation, debugging & refactoring", fontSize = 11.sp, color = SuperCyanAccent)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Action Mode Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CodingAction.values().forEach { action ->
                    FilterChip(
                        selected = selectedAction == action,
                        onClick = { selectedAction = action },
                        label = { Text(action.title, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SuperIndigoPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("coding_chip_${action.name}")
                    )
                }
            }

            // Language Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = languageDropdownExpanded,
                    onExpandedChange = { languageDropdownExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Language") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                            .testTag("language_selector")
                    )
                    ExposedDropdownMenu(
                        expanded = languageDropdownExpanded,
                        onDismissRequest = { languageDropdownExpanded = false }
                    ) {
                        SupportedLanguages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = {
                                    selectedLanguage = lang
                                    languageDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedAction == CodingAction.CONVERT) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "to",
                        tint = SuperCyanAccent
                    )
                    OutlinedTextField(
                        value = targetLanguage,
                        onValueChange = { targetLanguage = it },
                        label = { Text("Target Lang") },
                        modifier = Modifier.weight(1f).testTag("target_language_input")
                    )
                }
            }

            // Code / Request Input
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                label = { Text("What do you want SUPER AI to do?") },
                placeholder = { Text("Describe requirements, features, or errors...") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("coding_prompt_input")
            )

            if (selectedAction != CodingAction.GENERATE && selectedAction != CodingAction.PROJECT_GEN) {
                OutlinedTextField(
                    value = userCodeInput,
                    onValueChange = { userCodeInput = it },
                    label = { Text("Existing Code or Error Stack Trace") },
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    minLines = 4,
                    maxLines = 10,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SuperCyanAccent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("coding_code_input")
                )
            }

            // Run Button
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        val fullPrompt = buildString {
                            append(selectedAction.promptPrefix)
                            append("\nLanguage: $selectedLanguage")
                            if (selectedAction == CodingAction.CONVERT) {
                                append(" -> Target Language: $targetLanguage")
                            }
                            append("\nRequest: $promptInput")
                            if (userCodeInput.isNotBlank() && selectedAction != CodingAction.GENERATE && selectedAction != CodingAction.PROJECT_GEN) {
                                append("\n\n```$selectedLanguage\n$userCodeInput\n```")
                            }
                        }

                        val result = AiEngine.generate(
                            prompt = fullPrompt,
                            mode = "code",
                            systemInstruction = "You are SUPER AI Senior Software Engineer. Deliver optimal, production-ready code with clean comments, complexity analysis, and beginner explanations."
                        )
                        generatedResult = result.text
                        isLoading = false
                    }
                },
                enabled = !isLoading && promptInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SuperIndigoPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("run_coding_action_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("SUPER AI is generating code...")
                } else {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(selectedAction.title)
                }
            }

            // Output Card
            if (generatedResult != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("coding_result_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Generated Output",
                                fontWeight = FontWeight.Bold,
                                color = SuperCyanAccent,
                                fontSize = 14.sp
                            )
                            Row {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Code Result", generatedResult ?: ""))
                                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp).testTag("copy_coding_result_button")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { onOpenInChat(generatedResult ?: "") },
                                    modifier = Modifier.size(32.dp).testTag("open_in_chat_button")
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Discuss in chat", modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        MarkdownText(
                            markdown = generatedResult ?: "",
                            onExplainCode = onOpenInChat,
                            onSaveToProject = onSaveToProject
                        )
                    }
                }
            }
        }
    }
}
