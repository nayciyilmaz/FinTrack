package com.example.fintrack.presentation.screens.budget

import com.example.fintrack.domain.model.Budget

data class BudgetLimitsActionState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val income: Int = 0,
    val expense: Int = 0,
    val budgets: List<Budget> = emptyList(),
    val categoryExpenses: Map<String, Int> = emptyMap(),
    val isSaving: Boolean = false
)
