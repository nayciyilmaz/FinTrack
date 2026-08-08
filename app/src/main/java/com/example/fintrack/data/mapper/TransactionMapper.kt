package com.example.fintrack.data.mapper

import com.example.fintrack.data.remote.dto.TransactionResponseDto
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionMapper @Inject constructor() {
    fun toTransaction(dto: TransactionResponseDto): Transaction = Transaction(
        id = dto.id,
        type = TransactionType.valueOf(dto.type),
        category = dto.category,
        amount = dto.amount,
        note = dto.note,
        date = dto.date,
        time = dto.time,
        isRecurring = dto.recurring,
        isReminder = dto.reminder,
        createdAt = dto.createdAt
    )
}
