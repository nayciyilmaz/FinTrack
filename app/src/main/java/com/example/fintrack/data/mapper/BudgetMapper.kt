package com.example.fintrack.data.mapper

import com.example.fintrack.data.remote.dto.BudgetResponseDto
import com.example.fintrack.domain.model.Budget
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetMapper @Inject constructor() {
    fun toBudget(dto: BudgetResponseDto): Budget = Budget(
        id = dto.id,
        category = dto.category,
        limitAmount = dto.limitAmount
    )
}
