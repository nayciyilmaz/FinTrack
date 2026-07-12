package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SavingsGoalResponseDto(
    val id: Long,
    val name: String,
    val category: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val createdAt: String
)
