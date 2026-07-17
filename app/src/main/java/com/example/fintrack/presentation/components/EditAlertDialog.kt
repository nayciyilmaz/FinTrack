package com.example.fintrack.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.fintrack.R

@Composable
fun EditAlertDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String = stringResource(id = R.string.label_confirm_action),
    cancelText: String = stringResource(id = R.string.label_cancel),
    confirmColor: Color = colorResource(id = R.color.bottom_bar_fab)
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            EditTextButton(
                text = confirmText,
                onClick = onConfirm,
                color = confirmColor
            )
        },
        dismissButton = {
            EditTextButton(
                text = cancelText,
                onClick = onDismiss
            )
        }
    )
}
