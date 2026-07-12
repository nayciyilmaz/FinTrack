package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SavingsGoalRequestDto(
    val name: String,
    val category: String,
    val targetAmount: Double
)
