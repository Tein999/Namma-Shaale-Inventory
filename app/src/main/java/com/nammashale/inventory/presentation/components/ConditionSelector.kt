package com.nammashale.inventory.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nammashale.inventory.domain.model.AssetCondition
import com.nammashale.inventory.presentation.theme.Blue500

/**
 * Horizontal row of condition chips that act as a single-select toggle group.
 */
@Composable
fun ConditionSelector(
    selectedCondition: AssetCondition,
    onConditionSelected: (AssetCondition) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssetCondition.entries.forEach { condition ->
            val isSelected = condition == selectedCondition
            StatusChip(
                condition = condition,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .then(
                        if (isSelected) Modifier.border(
                            width = 2.dp,
                            color = Blue500,
                            shape = RoundedCornerShape(50)
                        ) else Modifier
                    )
                    .clickable { onConditionSelected(condition) }
                    .padding(vertical = 4.dp)
            )
        }
    }
}
