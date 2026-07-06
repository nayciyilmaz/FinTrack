package com.example.fintrack.presentation.screens.home

data class HomeActionState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val periodText: String = "",
    val remainingBalance: Int = 0,
    val dailyLimit: Int = 0,
    val income: Int = 0,
    val expense: Int = 0,
    val spendingRatio: Int = 0
)
