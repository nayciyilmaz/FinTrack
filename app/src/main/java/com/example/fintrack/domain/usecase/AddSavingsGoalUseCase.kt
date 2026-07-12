package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.domain.repository.SavingsGoalRepository

class AddSavingsGoalUseCase(private val savingsGoalRepository: SavingsGoalRepository) {
    suspend operator fun invoke(name: String, category: String, targetAmount: Double): Resource<SavingsGoal> =
        savingsGoalRepository.addGoal(name, category, targetAmount)
}
