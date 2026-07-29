package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.repository.TransactionRepository

class GetReminderTransactionsUseCase(private val transactionRepository: TransactionRepository) {
    suspend operator fun invoke(): Resource<List<Transaction>> = transactionRepository.getReminders()
}
