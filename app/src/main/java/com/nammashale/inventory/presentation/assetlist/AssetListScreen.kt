package com.nammashale.inventory.presentation.assetlist

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.presentation.components.AssetCard
import com.nammashale.inventory.presentation.components.EmptyState
import com.nammashale.inventory.presentation.components.NammaTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddAsset: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: AssetListViewModel = hiltViewModel()
) {
    val assets by viewModel.assets.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val conditionFilter by viewModel.conditionFilter.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            NammaTopBar(
                title = "Assets (${assets.size})",
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddAsset,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Add Asset") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {

            // ─── Search Bar ───────────────────────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search by name, serial, location…") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // ─── Filter Chips ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" chip
                FilterChip(
                    selected = conditionFilter == null,
                    onClick = { viewModel.onConditionFilterChange(null) },
                    label = { Text("All") },
                    leadingIcon = if (conditionFilter == null) {
                        { Icon(Icons.Default.FilterList, null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
                // Per-condition chips
                AssetCondition.entries.forEach { condition ->
                    FilterChip(
                        selected = conditionFilter == condition,
                        onClick = {
                            viewModel.onConditionFilterChange(
                                if (conditionFilter == condition) null else condition
                            )
                        },
                        label = { Text(condition.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (condition) {
                                AssetCondition.WORKING -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                AssetCondition.NEEDS_REPAIR -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                AssetCondition.BROKEN -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            }
                        )
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // ─── Asset List ───────────────────────────────────────────────
            if (assets.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Inventory2,
                    title = if (searchQuery.isBlank() && conditionFilter == null)
                        "No assets found" else "No matching assets",
                    subtitle = if (searchQuery.isBlank() && conditionFilter == null)
                        "Tap '+' to add your first asset"
                    else "Try adjusting your search or filter"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 4.dp, bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = assets,
                        key = { it.id }
                    ) { asset ->
                        AssetCard(
                            asset = asset,
                            onClick = { onNavigateToDetail(asset.id) }
                        )
                    }
                }
            }
        }
    }
}
