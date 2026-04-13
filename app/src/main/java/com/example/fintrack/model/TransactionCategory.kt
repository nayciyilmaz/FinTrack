package com.example.fintrack.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class TransactionCategory(
    @StringRes val labelResId: Int,
    val icon: ImageVector
)