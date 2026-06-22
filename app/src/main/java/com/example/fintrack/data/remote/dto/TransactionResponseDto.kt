package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponseDto(
    val id: Long,
    val type: String,
    val category: String,
    val amount: Double,
    val note: String? = null,
    val date: String,
    val time: String,
    val recurring: Boolean,
    val reminder: Boolean,
    val createdAt: String
)
