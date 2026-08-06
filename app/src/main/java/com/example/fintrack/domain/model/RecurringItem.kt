package com.example.fintrack.domain.model

data class RecurringItem(
    val id: Long,
    val type: TransactionType,
    val category: String,
    val amount: Double,
    val dayOfMonth: Int
)
