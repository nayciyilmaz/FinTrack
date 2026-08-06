package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.repository.TransactionRepository

class AddTransactionUseCase(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(
        type: TransactionType,
        category: String,
        amount: Double,
        note: String?,
        date: String,
        time: String,
        recurring: Boolean,
        reminder: Boolean
    ): Resource<Transaction> = transactionRepository.addTransaction(
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
