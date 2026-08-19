package com.example.fintrack.fake

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.domain.repository.SavingsGoalRepository

class FakeSavingsGoalRepository(
    private val getGoalsResult: Resource<List<SavingsGoal>>? = null,
    private val addGoalResult: Resource<SavingsGoal>? = null,
    private val updateGoalResult: Resource<SavingsGoal>? = null,
    private val deleteGoalResult: Resource<Unit>? = null
) : SavingsGoalRepository {

    var lastAddGoalName: String? = null
        private set
    var lastAddGoalCategory: String? = null
        private set

    override suspend fun getGoals(): Resource<List<SavingsGoal>> = getGoalsResult ?: Resource.Success(emptyList())

    override suspend fun addGoal(name: String, category: String, targetAmount: Double): Resource<SavingsGoal> {
        lastAddGoalName = name
        lastAddGoalCategory = category
        return addGoalResult ?: Resource.Success(
            SavingsGoal(
                id = 1L,
                name = name,
                category = category,
                targetAmount = targetAmount,
                currentAmount = 0.0,
                createdAt = "2026-01-01T00:00:00"
            )
        )
    }

    override suspend fun updateGoal(id: Long, addAmount: Double?, newTargetAmount: Double?): Resource<SavingsGoal> {
        return updateGoalResult ?: Resource.Success(
            SavingsGoal(
                id = id,
                name = "",
                category = "",
                targetAmount = newTargetAmount ?: 0.0,
                currentAmount = addAmount ?: 0.0,
                createdAt = "2026-01-01T00:00:00"
            )
        )
    }

    override suspend fun deleteGoal(id: Long): Resource<Unit> {
        return deleteGoalResult ?: Resource.Success(Unit)
    }
}
