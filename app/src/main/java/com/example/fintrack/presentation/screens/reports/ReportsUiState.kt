package com.example.fintrack.presentation.screens.reports

import com.example.fintrack.core.constants.reportContentItems

data class ReportsUiState(
    val periodOffset: Int = 0,
    val checkedSections: Map<Int, Boolean> = reportContentItems.associate { it.titleResId to false }
)
