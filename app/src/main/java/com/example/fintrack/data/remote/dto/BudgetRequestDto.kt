package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BudgetRequestDto(
    val category: String,
    val limitAmount: Double
)
