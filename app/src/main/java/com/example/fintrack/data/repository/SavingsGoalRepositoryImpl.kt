package com.example.fintrack.data.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.remote.api.SavingsGoalService
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.dto.SavingsGoalRequestDto
import com.example.fintrack.data.remote.dto.SavingsGoalResponseDto
import com.example.fintrack.data.remote.dto.SavingsGoalUpdateDto
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.domain.repository.SavingsGoalRepository
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject

class SavingsGoalRepositoryImpl @Inject constructor(
    private val savingsGoalService: SavingsGoalService,
    private val json: Json
) : SavingsGoalRepository {

    override suspend fun getGoals(): Resource<List<SavingsGoal>> {
        return try {
            val response = savingsGoalService.getGoals()
            Resource.Success(response.map { it.toDomain() })
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: "Bir hata oluştu")
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Bir hata oluştu")
        }
    }

    override suspend fun addGoal(name: String, category: String, targetAmount: Double): Resource<SavingsGoal> {
        return try {
            val response = savingsGoalService.addGoal(
                SavingsGoalRequestDto(name = name, category = category, targetAmount = targetAmount)
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

    override suspend fun updateGoal(id: Long, addAmount: Double?, newTargetAmount: Double?): Resource<SavingsGoal> {
        return try {
            val response = savingsGoalService.updateGoal(
                id, SavingsGoalUpdateDto(addAmount = addAmount, newTargetAmount = newTargetAmount)
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

    override suspend fun deleteGoal(id: Long): Resource<Unit> {
        return try {
            savingsGoalService.deleteGoal(id)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: "Bir hata oluştu")
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Bir hata oluştu")
        }
    }

    private fun SavingsGoalResponseDto.toDomain() = SavingsGoal(
        id = id,
        name = name,
        category = category,
        targetAmount = targetAmount,
        currentAmount = currentAmount,
        createdAt = createdAt
    )
}
