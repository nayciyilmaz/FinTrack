package com.example.fintrack.presentation.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class GoalCategory(
    val icon: ImageVector,
    @StringRes val nameResId: Int
)