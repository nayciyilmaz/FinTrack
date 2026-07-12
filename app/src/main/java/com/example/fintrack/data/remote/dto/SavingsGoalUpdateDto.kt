package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SavingsGoalUpdateDto(
    val addAmount: Double? = null,
    val newTargetAmount: Double? = null
)
