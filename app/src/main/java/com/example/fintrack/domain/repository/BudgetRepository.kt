package com.example.fintrack.domain.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Budget

interface BudgetRepository {
    suspend fun getBudgets(): Resource<List<Budget>>
    suspend fun saveBudgets(budgets: List<Pair<String, Double>>): Resource<List<Budget>>
}
