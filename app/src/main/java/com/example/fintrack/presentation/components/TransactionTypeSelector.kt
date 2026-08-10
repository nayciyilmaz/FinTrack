package com.example.fintrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.fintrack.R

@Composable
fun TransactionTypeSelector(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_lg)))
            .background(colorResource(id = R.color.card_background))
            .padding(dimensionResource(id = R.dimen.padding_xs)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_xs))
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = selectedIndex == index
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = if (isSelected) Color.White else colorResource(id = R.color.text_quaternary),
                textAlign = TextAlign.Center,
                modifier = modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_md)))
                    .background(if (isSelected) colorResource(id = R.color.bottom_bar_fab) else Color.Transparent)
                    .clickable { onOptionSelected(index) }
                    .padding(vertical = dimensionResource(id = R.dimen.padding_10))
            )
        }
    }
}