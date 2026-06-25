package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.repository.TransactionRepository

class UpdateTransactionUseCase(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(
        id: Long,
        type: String,
        category: String,
        amount: Double,
        note: String?,
        date: String,
        time: String,
        recurring: Boolean,
        reminder: Boolean
    ): Resource<Transaction> = transactionRepository.updateTransaction(
        id = id,
        type = type,
        category = category,
        amount = amount,
        note = note,
        date = date,
        time = time,
        recurring = recurring,
        reminder = reminder
    )
}
