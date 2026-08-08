package com.example.fintrack.data.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.mapper.RecurringItemMapper
import com.example.fintrack.data.remote.error.NetworkErrorParser
import com.example.fintrack.data.remote.api.RecurringItemService
import com.example.fintrack.data.remote.dto.RecurringItemUpdateRequestDto
import com.example.fintrack.domain.model.RecurringItem
import com.example.fintrack.domain.repository.RecurringItemRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import javax.inject.Inject

class RecurringItemRepositoryImpl @Inject constructor(
    private val recurringItemService: RecurringItemService,
    private val recurringItemMapper: RecurringItemMapper,
    private val networkErrorParser: NetworkErrorParser,
    @ApplicationContext private val context: Context
) : RecurringItemRepository {

    override suspend fun getRecurringItems(): Resource<List<RecurringItem>> {
        return try {
            Resource.Success(recurringItemService.getRecurringItems().map { recurringItemMapper.toRecurringItem(it) })
        } catch (e: HttpException) {
            Resource.Error(message = networkErrorParser.parse(e)?.message ?: context.getString(R.string.error_generic_fallback))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun updateRecurringItem(id: Long, amount: Double, dayOfMonth: Int): Resource<RecurringItem> {
        return try {
            val request = RecurringItemUpdateRequestDto(amount = amount, dayOfMonth = dayOfMonth)
            Resource.Success(recurringItemMapper.toRecurringItem(recurringItemService.updateRecurringItem(id, request)))
        } catch (e: HttpException) {
            Resource.Error(message = networkErrorParser.parse(e)?.message ?: context.getString(R.string.error_generic_fallback))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun deleteRecurringItem(id: Long): Resource<Unit> {
        return try {
            recurringItemService.deleteRecurringItem(id)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Error(message = networkErrorParser.parse(e)?.message ?: context.getString(R.string.error_generic_fallback))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }
}
