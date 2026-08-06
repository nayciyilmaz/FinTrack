package com.example.fintrack.presentation.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class ReportContentItem(
    val icon: ImageVector,
    @StringRes val titleResId: Int,
    @StringRes val subtitleResId: Int
)