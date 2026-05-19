package com.nammashale.inventory.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nammashale.inventory.presentation.components.AssetCard
import com.nammashale.inventory.presentation.components.NammaTopBar
import com.nammashale.inventory.presentation.components.StatCard
import com.nammashale.inventory.presentation.theme.GradientAmber1
import com.nammashale.inventory.presentation.theme.GradientAmber2
import com.nammashale.inventory.presentation.theme.GradientBlue1
import com.nammashale.inventory.presentation.theme.GradientBlue2
import com.nammashale.inventory.presentation.theme.GradientGreen1
import com.nammashale.inventory.presentation.theme.GradientGreen2
import com.nammashale.inventory.presentation.theme.GradientRed1
import com.nammashale.inventory.presentation.theme.GradientRed2
import com.nammashale.inventory.presentation.theme.WorkingGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAssets: () -> Unit,
    onNavigateToAddAsset: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToAllIssues: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val recentAssets by viewModel.recentAssets.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show health check message
    LaunchedEffect(uiState.healthCheckMessage) {
        uiState.healthCheckMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearHealthCheckMessage()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            NammaTopBar(
                title = "Namma-Shaale 🏫",
                actions = {
                    IconButton(onClick = onNavigateToReport) {
                        Icon(Icons.Default.Assessment, "Report", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddAsset,
                icon = { Icon(Icons.Default.Add, "Add Asset") },
                text = { Text("Add Asset") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ─── Welcome Banner ───────────────────────────────────────────
            item {
                Column {
                    Text(
                        text = "Asset Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Monitor and manage your school's assets",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ─── Health Score ─────────────────────────────────────────────
            item {
                HealthScoreBanner(
                    healthPercentage = stats.healthPercentage,
                    onHealthCheckClick = viewModel::performHealthCheck
                )
            }

            // ─── Stat Cards Grid ──────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "Total Assets",
                        count = stats.totalAssets,
                        icon = Icons.Default.Inventory2,
                        gradientColors = listOf(GradientBlue1, GradientBlue2),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Working",
                        count = stats.workingCount,
                        icon = Icons.Default.CheckCircle,
                        gradientColors = listOf(GradientGreen1, GradientGreen2),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "Needs Repair",
                        count = stats.needsRepairCount,
                        icon = Icons.Default.Build,
                        gradientColors = listOf(GradientAmber1, GradientAmber2),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Broken",
                        count = stats.brokenCount,
                        icon = Icons.Default.BrokenImage,
                        gradientColors = listOf(GradientRed1, GradientRed2),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ─── Quick Actions ────────────────────────────────────────────
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickActionButton(
                        label = "All Assets",
                        icon = Icons.Default.Inventory2,
                        onClick = onNavigateToAssets,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        label = "Issue Log",
                        icon = Icons.Default.ReportProblem,
                        onClick = onNavigateToAllIssues,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        label = "Report",
                        icon = Icons.Default.Assessment,
                        onClick = onNavigateToReport,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ─── Recent Assets ────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Assets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (recentAssets.isNotEmpty()) {
                        Text(
                            text = "View All →",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onNavigateToAssets() }
                        )
                    }
                }
            }

            if (recentAssets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Inventory2,
                                null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "No assets yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Tap '+' to add your first asset",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(recentAssets.take(5)) { asset ->
                    AssetCard(
                        asset = asset,
                        onClick = onNavigateToAssets
                    )
                }
            }

            // Bottom padding for FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun HealthScoreBanner(
    healthPercentage: Float,
    onHealthCheckClick: () -> Unit
) {
    val color = when {
        healthPercentage >= 80f -> WorkingGreen
        healthPercentage >= 50f -> GradientAmber1
        else -> GradientRed1
    }
    val emoji = when {
        healthPercentage >= 80f -> "✅"
        healthPercentage >= 50f -> "⚠️"
        else -> "❌"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("$emoji Health Score", style = MaterialTheme.typography.labelLarge, color = color)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${"%.1f".format(healthPercentage)}%",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
                Text(
                    "of assets are working",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onHealthCheckClick,
                colors = ButtonDefaults.buttonColors(containerColor = color),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.HealthAndSafety, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Health\nCheck", fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
