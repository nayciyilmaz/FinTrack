package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Budget
import com.example.fintrack.domain.repository.BudgetRepository

class SaveBudgetsUseCase(private val budgetRepository: BudgetRepository) {
    suspend operator fun invoke(budgets: List<Pair<String, Double>>): Resource<List<Budget>> =
        budgetRepository.saveBudgets(budgets)
}
