package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.repository.RecurringItemRepository

class DeleteRecurringItemUseCase(private val recurringItemRepository: RecurringItemRepository) {
    suspend operator fun invoke(id: Long): Resource<Unit> = recurringItemRepository.deleteRecurringItem(id)
}
