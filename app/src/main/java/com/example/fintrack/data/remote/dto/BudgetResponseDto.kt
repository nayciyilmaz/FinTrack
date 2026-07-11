package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BudgetResponseDto(
    val id: Long,
    val category: String,
    val limitAmount: Double,
    val createdAt: String
)
