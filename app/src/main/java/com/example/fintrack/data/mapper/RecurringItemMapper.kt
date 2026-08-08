package com.example.fintrack.data.mapper

import com.example.fintrack.data.remote.dto.RecurringItemResponseDto
import com.example.fintrack.domain.model.RecurringItem
import com.example.fintrack.domain.model.TransactionType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurringItemMapper @Inject constructor() {
    fun toRecurringItem(dto: RecurringItemResponseDto): RecurringItem = RecurringItem(
        id = dto.id,
        type = TransactionType.valueOf(dto.type),
        category = dto.category,
        amount = dto.amount,
        dayOfMonth = dto.dayOfMonth
    )
}
