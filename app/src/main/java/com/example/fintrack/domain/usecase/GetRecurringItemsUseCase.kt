package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.RecurringItem
import com.example.fintrack.domain.repository.RecurringItemRepository

class GetRecurringItemsUseCase(private val recurringItemRepository: RecurringItemRepository) {
    suspend operator fun invoke(): Resource<List<RecurringItem>> = recurringItemRepository.getRecurringItems()
}
