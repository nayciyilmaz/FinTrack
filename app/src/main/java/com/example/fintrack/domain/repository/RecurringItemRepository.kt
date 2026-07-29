package com.example.fintrack.domain.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.RecurringItem

interface RecurringItemRepository {
    suspend fun getRecurringItems(): Resource<List<RecurringItem>>
    suspend fun updateRecurringItem(id: Long, amount: Double, dayOfMonth: Int): Resource<RecurringItem>
    suspend fun deleteRecurringItem(id: Long): Resource<Unit>
}
