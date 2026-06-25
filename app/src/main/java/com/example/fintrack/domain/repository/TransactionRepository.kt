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

    suspend fun getTransactions(
        type: String?,
        startDate: String,
        endDate: String
    ): Resource<List<Transaction>>

    suspend fun updateTransaction(
        id: Long,
        type: String,
        category: String,
        amount: Double,
        note: String?,
        date: String,
        time: String,
        recurring: Boolean,
        reminder: Boolean
    ): Resource<Transaction>

    suspend fun deleteTransaction(id: Long): Resource<Unit>
}
