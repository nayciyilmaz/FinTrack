package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecurringItemResponseDto(
    val id: Long,
    val type: String,
    val category: String,
    val amount: Double,
    val dayOfMonth: Int
)
