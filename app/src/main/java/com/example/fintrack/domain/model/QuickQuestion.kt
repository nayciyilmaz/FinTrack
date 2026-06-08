package com.example.fintrack.domain.model

import androidx.annotation.StringRes

data class QuickQuestion(
    @StringRes val questionResId: Int,
    @StringRes val categoryResId: Int
)