package com.example.fintrack.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val route: String,
    val icon: ImageVector,
    @StringRes val labelResId: Int
)