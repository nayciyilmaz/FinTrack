package com.example.fintrack.domain.model

data class Transaction(
    val id: Long,
    val type: String,
    val category: String,
    val amount: Double,
    val note: String?,
    val date: String,
    val time: String,
    val isRecurring: Boolean,
    val isReminder: Boolean,
    val createdAt: String
)
