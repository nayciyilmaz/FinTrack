package com.example.fintrack.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class QuickActionItem(
    val icon: ImageVector,
    @StringRes val labelResId: Int,
    @ColorRes val iconBackgroundColorRes: Int,
    @ColorRes val iconTintRes: Int,
    val route: String
)