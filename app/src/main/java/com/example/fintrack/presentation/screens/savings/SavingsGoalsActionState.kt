package com.example.fintrack.presentation.screens.savings

import com.example.fintrack.domain.model.SavingsGoal

data class SavingsGoalsActionState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val lastMonthSavings: Int = 0,
    val totalSavings: Int = 0,
    val goals: List<SavingsGoal> = emptyList(),
    val estimatedDates: Map<Long, String?> = emptyMap(),
    val isSaving: Boolean = false
)
