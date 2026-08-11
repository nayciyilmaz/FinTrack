package com.example.fintrack.fake

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.RecurringItem
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.repository.RecurringItemRepository

class FakeRecurringItemRepository(
    private val updateRecurringItemResult: Resource<RecurringItem>? = null,
    private val deleteRecurringItemResult: Resource<Unit>? = null
) : RecurringItemRepository {

    var lastUpdateRecurringItemAmount: Double? = null
        private set
    var lastUpdateRecurringItemDayOfMonth: Int? = null
        private set

    override suspend fun getRecurringItems(): Resource<List<RecurringItem>> = Resource.Success(emptyList())

    override suspend fun updateRecurringItem(id: Long, amount: Double, dayOfMonth: Int): Resource<RecurringItem> {
        lastUpdateRecurringItemAmount = amount
        lastUpdateRecurringItemDayOfMonth = dayOfMonth
        return updateRecurringItemResult ?: Resource.Success(
            RecurringItem(id = id, type = TransactionType.EXPENSE, category = "OTHER", amount = amount, dayOfMonth = dayOfMonth)
        )
    }

    override suspend fun deleteRecurringItem(id: Long): Resource<Unit> {
        return deleteRecurringItemResult ?: Resource.Success(Unit)
    }
}
