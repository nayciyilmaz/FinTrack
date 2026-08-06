package com.example.fintrack.presentation.model

import androidx.annotation.StringRes

data class QuickQuestion(
    @StringRes val questionResId: Int,
    @StringRes val categoryResId: Int,
    @StringRes val titleResId: Int,
    val categoryKey: String
)