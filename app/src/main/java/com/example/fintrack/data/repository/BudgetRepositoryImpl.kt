package com.example.fintrack.data.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.remote.api.BudgetService
import com.example.fintrack.data.remote.dto.BudgetRequestDto
import com.example.fintrack.data.remote.dto.BudgetResponseDto
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.domain.model.Budget
import com.example.fintrack.domain.repository.BudgetRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetService: BudgetService,
    private val json: Json,
    @ApplicationContext private val context: Context
) : BudgetRepository {

    override suspend fun getBudgets(): Resource<List<Budget>> {
        return try {
            val response = budgetService.getBudgets()
            Resource.Success(response.map { it.toDomain() })
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun saveBudgets(budgets: List<Pair<String, Double>>): Resource<List<Budget>> {
        return try {
            val request = budgets.map { BudgetRequestDto(category = it.first, limitAmount = it.second) }
            val response = budgetService.saveBudgets(request)
            Resource.Success(response.map { it.toDomain() })
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    private fun BudgetResponseDto.toDomain() = Budget(
        id = id,
        category = category,
        limitAmount = limitAmount
    )
}
