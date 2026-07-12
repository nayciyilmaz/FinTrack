package com.example.fintrack.presentation.screens.budget

data class BudgetLimitsUiState(
    val showManageDialog: Boolean = false,
    val dialogAmounts: Map<String, String> = emptyMap(),
    val dialogActiveStates: Map<String, Boolean> = emptyMap(),
    val totalLimit: Int = 0
)
