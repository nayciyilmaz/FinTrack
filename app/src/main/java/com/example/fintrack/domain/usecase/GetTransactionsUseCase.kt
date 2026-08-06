package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.repository.TransactionRepository

class GetTransactionsUseCase(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(
        type: TransactionType?,
        startDate: String,
        endDate: String
    ): Resource<List<Transaction>> = transactionRepository.getTransactions(type, startDate, endDate)
}
