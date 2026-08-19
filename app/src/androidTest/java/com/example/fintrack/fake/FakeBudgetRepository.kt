package com.example.fintrack.fake

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Budget
import com.example.fintrack.domain.repository.BudgetRepository

class FakeBudgetRepository(
    private val getBudgetsResult: Resource<List<Budget>>? = null,
    private val saveBudgetsResult: Resource<List<Budget>>? = null
) : BudgetRepository {

    var lastSavedBudgets: List<Pair<String, Double>>? = null
        private set

    override suspend fun getBudgets(): Resource<List<Budget>> = getBudgetsResult ?: Resource.Success(emptyList())

    override suspend fun saveBudgets(budgets: List<Pair<String, Double>>): Resource<List<Budget>> {
        lastSavedBudgets = budgets
        return saveBudgetsResult ?: Resource.Success(
            budgets.map { (category, amount) -> Budget(id = 1L, category = category, limitAmount = amount) }
        )
    }
}
