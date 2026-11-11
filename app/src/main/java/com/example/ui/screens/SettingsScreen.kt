package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UsageEntity
import com.example.data.remote.AiEngine
import com.example.data.remote.SuperAiModel
import com.example.data.repository.EntitlementRepository
import com.example.data.repository.SuperAiPlan
import com.example.ui.components.PlanBadge
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperEmerald
import com.example.ui.theme.SuperIndigoPrimary
import com.example.ui.theme.SuperRose
import kotlinx.coroutines.launch

val AvailableAgentArchitectures = listOf(
    "General Assistant Agent",
    "Autonomous Coding Agent",
    "Socratic Study Coach Agent",
    "Content Strategist Agent",
    "Startup Co-Founder Agent",
    "Full-Stack App Builder Agent"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentPlan: SuperAiPlan,
    entitlementRepository: EntitlementRepository,
    usageHistory: List<UsageEntity>,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var customKey by remember { mutableStateOf(AiEngine.customApiKey ?: "") }
    var selectedModel by remember { mutableStateOf(AiEngine.selectedModel) }
    var modelDropdownExpanded by remember { mutableStateOf(false) }

    var selectedAgent by remember { mutableStateOf(AvailableAgentArchitectures[0]) }
    var agentDropdownExpanded by remember { mutableStateOf(false) }

    var showResetDialog by remember { mutableStateOf(false) }

    val features = remember { entitlementRepository.getFeaturesList() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen")
    ) {
        TopAppBar(
            title = {
                Column {
                    Text("Settings & Architecture", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Plan entitlements, model routing & privacy controls", fontSize = 11.sp, color = SuperCyanAccent)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Plan Card & Tier Switcher
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_plan_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Subscription Tier", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(currentPlan.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        }
                        PlanBadge(plan = currentPlan)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Daily limit: ${currentPlan.maxDailyRequests} requests/day • Context: ${currentPlan.maxContextTokens} tokens",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Switch Plan Tier (Evaluation & Architecture Demo):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SuperAiPlan.values().forEach { plan ->
                            Button(
                                onClick = {
                                    scope.launch {
                                        entitlementRepository.switchPlan(plan)
                                        Toast.makeText(context, "Switched to ${plan.title}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentPlan == plan) SuperIndigoPrimary else MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("switch_plan_button_${plan.name}")
                            ) {
                                Text(
                                    plan.name,
                                    fontSize = 11.sp,
                                    color = if (currentPlan == plan) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // AI Model Configuration
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = SuperCyanAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Active AI Model", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = modelDropdownExpanded,
                        onExpandedChange = { modelDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedModel.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Model Engine") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth()
                                .testTag("model_selector")
                        )
                        ExposedDropdownMenu(
                            expanded = modelDropdownExpanded,
                            onDismissRequest = { modelDropdownExpanded = false }
                        ) {
                            SuperAiModel.values().forEach { model ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(model.displayName, fontWeight = FontWeight.SemiBold)
                                            if (model.proOnly) {
                                                Text("Included in PRO & ADMIN tiers", fontSize = 10.sp, color = SuperIndigoPrimary)
                                            }
                                        }
                                    },
                                    onClick = {
                                        if (model.proOnly && currentPlan == SuperAiPlan.FREE) {
                                            Toast.makeText(context, "Gemini 3.1 Pro requires SUPER AI PRO tier", Toast.LENGTH_SHORT).show()
                                        } else {
                                            selectedModel = model
                                            AiEngine.selectedModel = model
                                        }
                                        modelDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Custom API Key Override Card (Section 19: Security & API key management)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = SuperIndigoPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gemini API Key Configuration", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "SUPER AI works right out of the box with built-in synthesis. You can also inject a personal Gemini API Key directly:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customKey,
                        onValueChange = {
                            customKey = it
                            AiEngine.customApiKey = it
                        },
                        label = { Text("Custom GEMINI_API_KEY") },
                        placeholder = { Text("AIzaSy...") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("api_key_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "🔒 Client Security: Keys are kept locally in memory and never sent to secondary servers.",
                        fontSize = 11.sp,
                        color = SuperCyanAccent
                    )
                }
            }

            // Specialized Agent Architecture Selector (Section 12)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = SuperCyanAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agent Orchestrator", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Select specialized agent personality and system routing:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = agentDropdownExpanded,
                        onExpandedChange = { agentDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedAgent,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Active Agent Architecture") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = agentDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth()
                                .testTag("agent_architecture_selector")
                        )
                        ExposedDropdownMenu(
                            expanded = agentDropdownExpanded,
                            onDismissRequest = { agentDropdownExpanded = false }
                        ) {
                            AvailableAgentArchitectures.forEach { agent ->
                                DropdownMenuItem(
                                    text = { Text(agent) },
                                    onClick = {
                                        selectedAgent = agent
                                        agentDropdownExpanded = false
                                        Toast.makeText(context, "Agent changed to $agent", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Usage & Analytics Summary
            val totalRequests = usageHistory.sumOf { it.requests }
            val totalTokens = usageHistory.sumOf { it.tokens }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Local Usage & Telemetry", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Requests", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$totalRequests", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column {
                            Text("Estimated Tokens", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$totalTokens", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SuperCyanAccent)
                        }
                        Column {
                            Text("Data Engine", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Room SQLite", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SuperEmerald)
                        }
                    }
                }
            }

            // Plan Feature Matrix Preview
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Feature Entitlement Matrix", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    features.take(6).forEach { feat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(feat.name, fontSize = 12.sp)
                            val isIncluded = if (currentPlan == SuperAiPlan.FREE) feat.includedInFree else true
                            Icon(
                                imageVector = if (isIncluded) Icons.Default.CheckCircle else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isIncluded) SuperEmerald else Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Privacy & Data Reset Button
            Button(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SuperRose),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_all_data_button")
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Wipe All Local Chats & Data", fontSize = 13.sp)
            }

            // About Brand
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("SUPER AI Platform v1.0.0 (Production Free Edition)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Learn. Create. Code. With SUPER AI.", fontSize = 11.sp, color = SuperCyanAccent, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Wipe All Local Data?") },
            text = { Text("This will erase all conversation histories, progress metrics, and project records.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllData()
                        showResetDialog = false
                        Toast.makeText(context, "All data successfully reset", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Confirm Reset", color = SuperRose)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
