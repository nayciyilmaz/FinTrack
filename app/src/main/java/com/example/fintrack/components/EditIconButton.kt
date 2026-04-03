package com.example.fintrack.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import com.example.fintrack.R

@Composable
fun EditIconButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    tint: Color = colorResource(id = R.color.icon_orange)
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = tint
        )
    }
}