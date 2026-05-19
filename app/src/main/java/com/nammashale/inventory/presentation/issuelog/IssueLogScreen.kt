package com.nammashale.inventory.presentation.issuelog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nammashale.inventory.domain.model.IssueLog
import com.nammashale.inventory.presentation.components.EmptyState
import com.nammashale.inventory.presentation.components.NammaTopBar
import com.nammashale.inventory.presentation.theme.NeedsRepairAmber
import com.nammashale.inventory.presentation.theme.NeedsRepairAmberLight
import com.nammashale.inventory.presentation.theme.WorkingGreen
import com.nammashale.inventory.presentation.theme.WorkingGreenLight
import com.nammashale.inventory.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueLogScreen(
    assetId: Long,
    onNavigateBack: () -> Unit,
    viewModel: IssueLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val issues by viewModel.issues.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }

    Scaffold(
        topBar = {
            NammaTopBar(
                title = "Issue Log – ${uiState.assetName}",
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {

            // ─── Add Issue Section ────────────────────────────────────────
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Log New Issue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.descriptionInput,
                        onValueChange = viewModel::onDescriptionChange,
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        placeholder = { Text("Describe the issue…") },
                        maxLines = 4,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // AI Generate button
                        Button(
                            onClick = viewModel::generateAiDescription,
                            enabled = !uiState.isGeneratingAi,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeedsRepairAmberLight,
                                contentColor = NeedsRepairAmber
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            if (uiState.isGeneratingAi) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = NeedsRepairAmber)
                                Spacer(Modifier.width(6.dp))
                                Text("Generating…", style = MaterialTheme.typography.labelSmall)
                            } else {
                                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("AI Suggest", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        // Submit button
                        Button(
                            onClick = viewModel::addIssue,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Log Issue", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // ─── Issues List ──────────────────────────────────────────────
            if (issues.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.ReportProblem,
                    title = "No issues logged",
                    subtitle = "When you log an issue, it will appear here."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(issues, key = { it.id }) { issue ->
                        IssueCard(
                            issue = issue,
                            onMarkResolved = { viewModel.markResolved(issue.id) }
                        )
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun IssueCard(issue: IssueLog, onMarkResolved: () -> Unit) {
    val cardBg = if (issue.isResolved) WorkingGreenLight else Color.White
    val titleDecoration = if (issue.isResolved) TextDecoration.LineThrough else TextDecoration.None

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(if (issue.isResolved) 0.dp else 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        issue.description,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        textDecoration = titleDecoration
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        DateUtils.formatDateTime(issue.dateLogged),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!issue.isResolved) {
                    TextButton(onClick = onMarkResolved) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(14.dp), tint = WorkingGreen)
                        Spacer(Modifier.width(4.dp))
                        Text("Resolve", style = MaterialTheme.typography.labelSmall, color = WorkingGreen)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(14.dp), tint = WorkingGreen)
                        Spacer(Modifier.width(4.dp))
                        Text("Resolved", style = MaterialTheme.typography.labelSmall, color = WorkingGreen)
                    }
                }
            }

            // AI suggestion if present
            if (issue.aiSuggestion.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NeedsRepairAmberLight, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row {
                        Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(14.dp), tint = NeedsRepairAmber)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            issue.aiSuggestion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}
