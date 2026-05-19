package com.nammashale.inventory.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.presentation.theme.BrokenRed
import com.nammashale.inventory.presentation.theme.BrokenRedLight
import com.nammashale.inventory.presentation.theme.NeedsRepairAmber
import com.nammashale.inventory.presentation.theme.NeedsRepairAmberLight
import com.nammashale.inventory.presentation.theme.WorkingGreen
import com.nammashale.inventory.presentation.theme.WorkingGreenLight

/**
 * A reusable status chip with a colored dot and label.
 * Colors:  Working = Green | Needs Repair = Amber | Broken = Red
 */
@Composable
fun StatusChip(
    condition: AssetCondition,
    modifier: Modifier = Modifier,
    dotSize: Dp = 8.dp
) {
    val (backgroundColor, dotColor, textColor) = when (condition) {
        AssetCondition.WORKING -> Triple(WorkingGreenLight, WorkingGreen, WorkingGreen)
        AssetCondition.NEEDS_REPAIR -> Triple(NeedsRepairAmberLight, NeedsRepairAmber, Color(0xFFF57F17))
        AssetCondition.BROKEN -> Triple(BrokenRedLight, BrokenRed, BrokenRed)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = dotColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = condition.label,
            fontSize = 12.sp,
            color = textColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
