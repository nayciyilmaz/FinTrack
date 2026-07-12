package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.domain.repository.SavingsGoalRepository

class UpdateSavingsGoalUseCase(private val savingsGoalRepository: SavingsGoalRepository) {
    suspend operator fun invoke(id: Long, addAmount: Double?, newTargetAmount: Double?): Resource<SavingsGoal> =
        savingsGoalRepository.updateGoal(id, addAmount, newTargetAmount)
}
