package com.example.fintrack.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.example.fintrack.R

@Composable
fun ValidationErrorText(
    error: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = error,
        style = MaterialTheme.typography.bodySmall,
        color = Color.Red,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(id = R.dimen.padding_xs),
                top = dimensionResource(id = R.dimen.padding_xs)
            )
    )
}
