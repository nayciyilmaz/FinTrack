package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.domain.repository.SavingsGoalRepository

class GetSavingsGoalsUseCase(private val savingsGoalRepository: SavingsGoalRepository) {
    suspend operator fun invoke(): Resource<List<SavingsGoal>> = savingsGoalRepository.getGoals()
}
