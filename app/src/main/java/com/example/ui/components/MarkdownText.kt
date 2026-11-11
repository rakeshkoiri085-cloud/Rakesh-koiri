package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperIndigoPrimary

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    onExplainCode: ((String) -> Unit)? = null,
    onSaveToProject: ((String) -> Unit)? = null
) {
    val lines = markdown.lines()
    var inCodeBlock = false
    var codeLanguage = "python"
    val currentCodeLines = mutableListOf<String>()

    Column(modifier = modifier.fillMaxWidth()) {
        for (line in lines) {
            val trimmed = line.trim()

            if (trimmed.startsWith("```")) {
                if (inCodeBlock) {
                    // Ending code block
                    val codeContent = currentCodeLines.joinToString("\n")
                    CodeBlockView(
                        code = codeContent,
                        language = codeLanguage,
                        onExplainCode = onExplainCode,
                        onSaveToProject = onSaveToProject,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    currentCodeLines.clear()
                    inCodeBlock = false
                } else {
                    // Starting code block
                    codeLanguage = trimmed.removePrefix("```").trim().ifEmpty { "python" }
                    inCodeBlock = true
                    currentCodeLines.clear()
                }
                continue
            }

            if (inCodeBlock) {
                currentCodeLines.add(line)
                continue
            }

            when {
                trimmed.startsWith("###") -> {
                    Text(
                        text = parseFormattedText(trimmed.removePrefix("###").trim()),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SuperCyanAccent,
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                    )
                }
                trimmed.startsWith("##") -> {
                    Text(
                        text = parseFormattedText(trimmed.removePrefix("##").trim()),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SuperIndigoPrimary,
                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                    )
                }
                trimmed.startsWith("# ") -> {
                    Text(
                        text = parseFormattedText(trimmed.removePrefix("#").trim()),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 14.dp, bottom = 8.dp)
                    )
                }
                trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                    Row(modifier = Modifier.padding(vertical = 3.dp, horizontal = 4.dp)) {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = null,
                            tint = SuperCyanAccent,
                            modifier = Modifier
                                .padding(top = 7.dp, end = 8.dp)
                                .size(5.dp)
                        )
                        Text(
                            text = parseFormattedText(trimmed.substring(2)),
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }
                trimmed.isEmpty() -> {
                    // Paragraph break
                }
                else -> {
                    Text(
                        text = parseFormattedText(trimmed),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
            }
        }

        // Handle unclosed code block if any
        if (inCodeBlock && currentCodeLines.isNotEmpty()) {
            CodeBlockView(
                code = currentCodeLines.joinToString("\n"),
                language = codeLanguage,
                onExplainCode = onExplainCode,
                onSaveToProject = onSaveToProject,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

private fun parseFormattedText(text: String) = buildAnnotatedString {
    var i = 0
    while (i < text.length) {
        if (text.startsWith("**", i)) {
            val end = text.indexOf("**", i + 2)
            if (end != -1) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(text.substring(i + 2, end))
                }
                i = end + 2
                continue
            }
        }
        if (text.startsWith("`", i) && !text.startsWith("```", i)) {
            val end = text.indexOf("`", i + 1)
            if (end != -1) {
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = Color(0x336366F1),
                        color = Color(0xFF38BDF8)
                    )
                ) {
                    append(text.substring(i + 1, end))
                }
                i = end + 1
                continue
            }
        }
        append(text[i])
        i++
    }
}
