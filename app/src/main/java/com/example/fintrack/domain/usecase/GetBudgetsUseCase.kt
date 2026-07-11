package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Budget
import com.example.fintrack.domain.repository.BudgetRepository

class GetBudgetsUseCase(private val budgetRepository: BudgetRepository) {
    suspend operator fun invoke(): Resource<List<Budget>> = budgetRepository.getBudgets()
}
