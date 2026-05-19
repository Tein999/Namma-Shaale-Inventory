package com.nammashale.inventory.presentation.addasset

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nammashale.inventory.presentation.components.ConditionSelector
import com.nammashale.inventory.presentation.components.NammaTopBar
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCamera: () -> Unit,
    capturedPhotoPath: String?,
    onPhotoPathConsumed: () -> Unit,
    viewModel: AddAssetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // When camera returns with a photo, update ViewModel
    LaunchedEffect(capturedPhotoPath) {
        capturedPhotoPath?.let {
            viewModel.onPhotoTaken(it)
            onPhotoPathConsumed()
        }
    }

    // Navigate back after successful save
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = { NammaTopBar(title = "Add New Asset", onNavigateBack = onNavigateBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // ─── Photo Section ─────────────────────────────────────────────
            Text("Asset Photo", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            PhotoPicker(
                photoPath = uiState.photoPath,
                onPickPhoto = onNavigateToCamera
            )

            Spacer(Modifier.height(20.dp))

            // ─── Asset Name ───────────────────────────────────────────────
            SectionLabel("Asset Name *")
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Projector, Laptop, Chair…") },
                isError = uiState.nameError != null,
                supportingText = { uiState.nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(Modifier.height(12.dp))

            // ─── Serial Number ────────────────────────────────────────────
            SectionLabel("Serial Number *")
            OutlinedTextField(
                value = uiState.serialNumber,
                onValueChange = viewModel::onSerialNumberChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. SN-2024-001") },
                isError = uiState.serialError != null,
                supportingText = { uiState.serialError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(Modifier.height(12.dp))

            // ─── Location ─────────────────────────────────────────────────
            SectionLabel("Location / Room")
            OutlinedTextField(
                value = uiState.location,
                onValueChange = viewModel::onLocationChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Computer Lab, Room 101…") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(Modifier.height(12.dp))

            // ─── Description ──────────────────────────────────────────────
            SectionLabel("Description / Notes")
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("Optional notes about this asset…") },
                maxLines = 4,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(Modifier.height(20.dp))

            // ─── Condition ────────────────────────────────────────────────
            SectionLabel("Initial Condition")
            Spacer(Modifier.height(8.dp))
            ConditionSelector(
                selectedCondition = uiState.condition,
                onConditionSelected = viewModel::onConditionChange
            )

            Spacer(Modifier.height(32.dp))

            // ─── Save Button ──────────────────────────────────────────────
            Button(
                onClick = viewModel::saveAsset,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                    Text("  Save Asset", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun PhotoPicker(photoPath: String?, onPickPhoto: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), shape)
            .clickable(onClick = onPickPhoto),
        contentAlignment = Alignment.Center
    ) {
        if (photoPath != null && File(photoPath).exists()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(File(photoPath)).crossfade(true).build(),
                contentDescription = "Asset photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(shape)
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.AddAPhoto,
                    null,
                    modifier = Modifier.size(42.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(8.dp))
                Text("Tap to take photo", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
