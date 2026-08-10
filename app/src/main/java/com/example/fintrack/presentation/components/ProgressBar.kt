package com.example.fintrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.example.fintrack.R

@Composable
fun ProgressBar(
    progress: Float,
    trackColor: Color,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    val pillRadius = dimensionResource(id = R.dimen.radius_pill)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(pillRadius))
            .background(trackColor)
    ) {
        Text(
            text = "",
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(pillRadius))
                .background(progressColor)
                .padding(vertical = dimensionResource(id = R.dimen.padding_hairline))
        )
    }
}