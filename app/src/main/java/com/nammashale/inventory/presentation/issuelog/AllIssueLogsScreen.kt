package com.nammashale.inventory.presentation.issuelog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nammashale.inventory.presentation.components.EmptyState
import com.nammashale.inventory.presentation.components.NammaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllIssueLogsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AllIssueLogsViewModel = hiltViewModel()
) {
    val issues by viewModel.allIssues.collectAsState()

    Scaffold(
        topBar = {
            NammaTopBar(title = "All Issue Logs (${issues.size})", onNavigateBack = onNavigateBack)
        }
    ) { paddingValues ->
        if (issues.isEmpty()) {
            EmptyState(
                icon = Icons.Default.ReportProblem,
                title = "No issues logged yet",
                subtitle = "Issues added to assets will appear here.",
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(issues, key = { it.id }) { issue ->
                    IssueCard(issue = issue, onMarkResolved = {})
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}
