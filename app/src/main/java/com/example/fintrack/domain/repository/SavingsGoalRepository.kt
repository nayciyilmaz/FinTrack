package com.example.fintrack.domain.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.SavingsGoal

interface SavingsGoalRepository {
    suspend fun getGoals(): Resource<List<SavingsGoal>>
    suspend fun addGoal(name: String, category: String, targetAmount: Double): Resource<SavingsGoal>
    suspend fun updateGoal(id: Long, addAmount: Double?, newTargetAmount: Double?): Resource<SavingsGoal>
    suspend fun deleteGoal(id: Long): Resource<Unit>
}
