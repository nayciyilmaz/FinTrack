package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequestDto(
    val type: String,
    val category: String,
    val amount: Double,
    val note: String? = null,
    val date: String,
    val time: String,
    val recurring: Boolean = false,
    val reminder: Boolean = false
)
