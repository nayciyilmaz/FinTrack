package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.RecurringItem
import com.example.fintrack.domain.repository.RecurringItemRepository

class UpdateRecurringItemUseCase(private val recurringItemRepository: RecurringItemRepository) {
    suspend operator fun invoke(id: Long, amount: Double, dayOfMonth: Int): Resource<RecurringItem> =
        recurringItemRepository.updateRecurringItem(id, amount, dayOfMonth)
}
