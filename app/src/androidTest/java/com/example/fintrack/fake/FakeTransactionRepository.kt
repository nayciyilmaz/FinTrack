package com.example.fintrack.fake

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.repository.TransactionRepository

class FakeTransactionRepository(
    private val addTransactionResult: Resource<Transaction>? = null,
    private val updateTransactionResult: Resource<Transaction>? = null,
    private val deleteTransactionResult: Resource<Unit>? = null,
    private val getTransactionsResult: Resource<List<Transaction>>? = null
) : TransactionRepository {

    var lastUpdateTransactionAmount: Double? = null
        private set
    var lastAddTransactionCategory: String? = null
        private set
    var lastAddTransactionAmount: Double? = null
        private set

    override suspend fun addTransaction(
        type: TransactionType,
        category: String,
        amount: Double,
        note: String?,
        date: String,
        time: String,
        recurring: Boolean,
        reminder: Boolean
    ): Resource<Transaction> {
        lastAddTransactionCategory = category
        lastAddTransactionAmount = amount
        return addTransactionResult ?: Resource.Success(
            Transaction(
                id = 1L,
                type = type,
                category = category,
                amount = amount,
                note = note,
                date = date,
                time = time,
                isRecurring = recurring,
                isReminder = reminder,
                createdAt = date
            )
        )
    }

    override suspend fun getTransactions(
        type: TransactionType?,
        startDate: String,
        endDate: String
    ): Resource<List<Transaction>> = getTransactionsResult ?: Resource.Success(emptyList())

    override suspend fun updateTransaction(
        id: Long,
        type: TransactionType,
        category: String,
        amount: Double,
        note: String?,
        date: String,
        time: String,
        recurring: Boolean,
        reminder: Boolean
    ): Resource<Transaction> {
        lastUpdateTransactionAmount = amount
        return updateTransactionResult ?: Resource.Success(
            Transaction(
                id = id,
                type = type,
                category = category,
                amount = amount,
                note = note,
                date = date,
                time = time,
                isRecurring = recurring,
                isReminder = reminder,
                createdAt = date
            )
        )
    }

    override suspend fun deleteTransaction(id: Long): Resource<Unit> {
        return deleteTransactionResult ?: Resource.Success(Unit)
    }

    override suspend fun getReminders(): Resource<List<Transaction>> = Resource.Success(emptyList())
}
