package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecurringItemUpdateRequestDto(
    val amount: Double,
    val dayOfMonth: Int
)
