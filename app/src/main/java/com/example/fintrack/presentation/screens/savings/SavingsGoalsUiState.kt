package com.example.fintrack.presentation.screens.savings

import com.example.fintrack.domain.model.SavingsGoal

data class SavingsGoalsUiState(
    val showAddDialog: Boolean = false,
    val selectedGoal: SavingsGoal? = null,
    val goalName: String = "",
    val targetAmount: String = "",
    val selectedCategoryResId: Int? = null,
    val selectedCategoryName: String? = null,
    val nameError: String? = null,
    val categoryError: String? = null,
    val amountError: String? = null,
    val addAmount: String = "",
    val newTargetAmount: String = "",
    val updateError: String? = null
)
