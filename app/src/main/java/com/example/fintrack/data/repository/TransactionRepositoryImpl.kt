package com.example.fintrack.data.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.remote.api.TransactionService
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.dto.TransactionRequestDto
import com.example.fintrack.data.remote.dto.TransactionResponseDto
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.repository.TransactionRepository
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionService: TransactionService,
    private val json: Json
) : TransactionRepository {

    override suspend fun addTransaction(
        type: String,
        category: String,
        amount: Double,
        note: String?,
        date: String,
        time: String,
        recurring: Boolean,
        reminder: Boolean
    ): Resource<Transaction> {
        return try {
            val response = transactionService.addTransaction(
                TransactionRequestDto(
                    type = type,
                    category = category,
                    amount = amount,
                    note = note,
                    date = date,
                    time = time,
                    recurring = recurring,
                    reminder = reminder
                )
            )
            Resource.Success(response.toDomain())
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: "Bir hata oluştu")
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Bir hata oluştu")
        }
    }

    private fun TransactionResponseDto.toDomain() = Transaction(
        id = id,
        type = type,
        category = category,
        amount = amount,
        note = note,
        date = date,
        time = time,
        isRecurring = recurring,
        isReminder = reminder,
        createdAt = createdAt
    )
}
