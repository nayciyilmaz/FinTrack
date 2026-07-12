package com.example.fintrack.domain.model

data class SavingsGoal(
    val id: Long,
    val name: String,
    val category: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val createdAt: String
)
