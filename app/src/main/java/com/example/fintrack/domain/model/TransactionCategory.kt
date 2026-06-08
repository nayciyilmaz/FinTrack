package com.example.fintrack.domain.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class TransactionCategory(
    @StringRes val labelResId: Int,
    val icon: ImageVector
)