package com.example.fintrack.domain.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType

interface TransactionRepository {
    suspend fun addTransaction(
        type: TransactionType,
        category: String,
        amount: Double,
        note: String?,
        date: String,
        time: String,
        recurring: Boolean,
        reminder: Boolean
    ): Resource<Transaction>

    suspend fun getTransactions(
        type: TransactionType?,
        startDate: String,
        endDate: String
    ): Resource<List<Transaction>>

    suspend fun updateTransaction(
        id: Long,
        type: TransactionType,
        category: String,
        amount: Double,
        note: String?,
        date: String,
        time: String,
        recurring: Boolean,
        reminder: Boolean
    ): Resource<Transaction>

    suspend fun deleteTransaction(id: Long): Resource<Unit>

    suspend fun getReminders(): Resource<List<Transaction>>
}
