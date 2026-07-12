package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.repository.SavingsGoalRepository

class DeleteSavingsGoalUseCase(private val savingsGoalRepository: SavingsGoalRepository) {
    suspend operator fun invoke(id: Long): Resource<Unit> = savingsGoalRepository.deleteGoal(id)
}
