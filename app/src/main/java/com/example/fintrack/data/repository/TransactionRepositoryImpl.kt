package com.example.fintrack.data.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.mapper.TransactionMapper
import com.example.fintrack.data.remote.api.TransactionService
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.dto.TransactionRequestDto
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.repository.TransactionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionService: TransactionService,
    private val transactionMapper: TransactionMapper,
    private val json: Json,
    @ApplicationContext private val context: Context
) : TransactionRepository {

    override suspend fun addTransaction(
        type: TransactionType,
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
                    type = type.name,
                    category = category,
                    amount = amount,
                    note = note,
                    date = date,
                    time = time,
                    recurring = recurring,
                    reminder = reminder
                )
            )
            Resource.Success(transactionMapper.toTransaction(response))
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun getTransactions(
        type: TransactionType?,
        startDate: String,
        endDate: String
    ): Resource<List<Transaction>> {
        return try {
            val response = transactionService.getTransactions(type?.name, startDate, endDate)
            Resource.Success(response.map { transactionMapper.toTransaction(it) })
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun updateTransaction(
        id: Long,
        type: TransactionType,
        category: String,
        amount: Double,
        note: String?,
        date: String,
        time: String,
        recurring: Boolean,
        reminder: Boolean
    ): Resource<Transaction> {
        return try {
            val response = transactionService.updateTransaction(
                id,
                TransactionRequestDto(
                    type = type.name,
                    category = category,
                    amount = amount,
                    note = note,
                    date = date,
                    time = time,
                    recurring = recurring,
                    reminder = reminder
                )
            )
            Resource.Success(transactionMapper.toTransaction(response))
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun deleteTransaction(id: Long): Resource<Unit> {
        return try {
            transactionService.deleteTransaction(id)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun getReminders(): Resource<List<Transaction>> {
        return try {
            val response = transactionService.getReminders()
            Resource.Success(response.map { transactionMapper.toTransaction(it) })
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }
}
