package com.example.fintrack.presentation.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class TransactionCategory(
    @StringRes val labelResId: Int,
    val icon: ImageVector,
    val key: String
)