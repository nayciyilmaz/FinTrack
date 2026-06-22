package com.example.fintrack.domain.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Transaction

interface TransactionRepository {
    suspend fun addTransaction(
        type: String,
        category: String,
        amount: Double,
        note: String?,
        date: String,
        time: String,
        recurring: Boolean,
        reminder: Boolean
    ): Resource<Transaction>
}
