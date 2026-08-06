package com.example.fintrack.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.example.fintrack.R

@Composable
fun ScreenStateContent(
    isLoading: Boolean,
    isError: Boolean,
    isEmpty: Boolean = false,
    emptyMessageResId: Int? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.bottom_bar_fab)
                )
            }
        }
        isError -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.error_general),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.text_secondary)
                )
            }
        }
        isEmpty && emptyMessageResId != null -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = emptyMessageResId),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.text_secondary)
                )
            }
        }
        else -> content()
    }
}
