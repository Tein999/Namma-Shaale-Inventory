package com.nammashale.inventory.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nammashale.inventory.domain.model.Asset
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.presentation.theme.BrokenRedLight
import com.nammashale.inventory.presentation.theme.NeedsRepairAmberLight
import com.nammashale.inventory.presentation.theme.TextSecondary
import com.nammashale.inventory.presentation.theme.WorkingGreen
import com.nammashale.inventory.presentation.theme.WorkingGreenLight
import com.nammashale.inventory.utils.DateUtils
import java.io.File

/**
 * Displays a single asset as a card in the list.
 * Shows photo thumbnail, name, serial number, condition chip, and date.
 */
@Composable
fun AssetCard(
    asset: Asset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = when (asset.condition) {
        AssetCondition.WORKING -> WorkingGreenLight
        AssetCondition.NEEDS_REPAIR -> NeedsRepairAmberLight
        AssetCondition.BROKEN -> BrokenRedLight
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo thumbnail OR placeholder
            AssetPhoto(
                photoUri = asset.photoUri,
                contentDescription = asset.name
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (asset.serialNumber.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "S/N: ${asset.serialNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (asset.location.isNotBlank()) {
                    Text(
                        text = "📍 ${asset.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(condition = asset.condition)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = DateUtils.timeAgo(asset.dateAdded),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetPhoto(
    photoUri: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val size = 64.dp
    val shape = RoundedCornerShape(8.dp)

    if (photoUri != null && File(photoUri).exists()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(File(photoUri))
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(shape)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(WorkingGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = "No photo",
                tint = WorkingGreen,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
