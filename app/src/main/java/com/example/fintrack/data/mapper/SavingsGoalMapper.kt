package com.example.fintrack.data.mapper

import com.example.fintrack.data.remote.dto.SavingsGoalResponseDto
import com.example.fintrack.domain.model.SavingsGoal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavingsGoalMapper @Inject constructor() {
    fun toSavingsGoal(dto: SavingsGoalResponseDto): SavingsGoal = SavingsGoal(
        id = dto.id,
        name = dto.name,
        category = dto.category,
        targetAmount = dto.targetAmount,
        currentAmount = dto.currentAmount,
        createdAt = dto.createdAt
    )
}
