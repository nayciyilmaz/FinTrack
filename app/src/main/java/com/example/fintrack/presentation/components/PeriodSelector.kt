package com.example.fintrack.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import com.example.fintrack.R

@Composable
fun PeriodSelector(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    excludePeriods: List<String> = emptyList()
) {
    val periods = stringArrayResource(id = R.array.transaction_periods)
        .filter { it !in excludePeriods }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_xl)))
                .border(
                    dimensionResource(id = R.dimen.border_hairline),
                    colorResource(id = R.color.text_primary).copy(alpha = 0.15f),
                    RoundedCornerShape(dimensionResource(id = R.dimen.radius_xl))
                )
                .clickable { expanded = true }
                .padding(
                    horizontal = dimensionResource(id = R.dimen.padding_md),
                    vertical = dimensionResource(id = R.dimen.padding_sm)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_xs))
        ) {
            Text(
                text = selectedPeriod,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colorResource(id = R.color.text_primary)
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = colorResource(id = R.color.text_primary)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            periods.forEach { period ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = period,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorResource(id = R.color.text_primary)
                        )
                    },
                    onClick = {
                        onPeriodSelected(period)
                        expanded = false
                    }
                )
            }
        }
    }
}