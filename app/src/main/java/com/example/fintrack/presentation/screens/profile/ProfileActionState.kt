package com.example.fintrack.presentation.screens.profile

data class ProfileActionState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val initials: String = "",
    val transactionCount: Int = 0,
    val activeGoalsCount: Int = 0,
    val usageDays: Int = 0,
    val usageMonths: Int = 0,
    val showUsageInDays: Boolean = true
)
