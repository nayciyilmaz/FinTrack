package com.example.fintrack.data.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.remote.api.RecurringItemService
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.dto.RecurringItemResponseDto
import com.example.fintrack.data.remote.dto.RecurringItemUpdateRequestDto
import com.example.fintrack.domain.model.RecurringItem
import com.example.fintrack.domain.repository.RecurringItemRepository
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject

class RecurringItemRepositoryImpl @Inject constructor(
    private val recurringItemService: RecurringItemService,
    private val json: Json
) : RecurringItemRepository {

    override suspend fun getRecurringItems(): Resource<List<RecurringItem>> {
        return try {
            Resource.Success(recurringItemService.getRecurringItems().map { it.toDomain() })
        } catch (e: HttpException) {
            Resource.Error(message = extractErrorMessage(e))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Bir hata oluştu")
        }
    }

    override suspend fun updateRecurringItem(id: Long, amount: Double, dayOfMonth: Int): Resource<RecurringItem> {
        return try {
            val request = RecurringItemUpdateRequestDto(amount = amount, dayOfMonth = dayOfMonth)
            Resource.Success(recurringItemService.updateRecurringItem(id, request).toDomain())
        } catch (e: HttpException) {
            Resource.Error(message = extractErrorMessage(e))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Bir hata oluştu")
        }
    }

    override suspend fun deleteRecurringItem(id: Long): Resource<Unit> {
        return try {
            recurringItemService.deleteRecurringItem(id)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Error(message = extractErrorMessage(e))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Bir hata oluştu")
        }
    }

    private fun extractErrorMessage(e: HttpException): String {
        val errorDto = e.response()?.errorBody()?.string()?.let {
            runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
        }
        return errorDto?.message ?: "Bir hata oluştu"
    }

    private fun RecurringItemResponseDto.toDomain() = RecurringItem(
        id = id,
        type = type,
        category = category,
        amount = amount,
        dayOfMonth = dayOfMonth
    )
}
