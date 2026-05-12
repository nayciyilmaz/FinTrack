package com.example.fintrack.model

import androidx.annotation.StringRes

data class QuickQuestion(
    @StringRes val questionResId: Int,
    @StringRes val categoryResId: Int
)