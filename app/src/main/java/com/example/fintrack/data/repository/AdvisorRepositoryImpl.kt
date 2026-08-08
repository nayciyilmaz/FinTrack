package com.example.fintrack.data.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.mapper.AdvisorMapper
import com.example.fintrack.data.remote.api.AdvisorService
import com.example.fintrack.data.remote.dto.AdvisorAskRequestDto
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.domain.model.AdvisorInsight
import com.example.fintrack.domain.model.AdvisorSummary
import com.example.fintrack.domain.repository.AdvisorRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject

class AdvisorRepositoryImpl @Inject constructor(
    private val advisorService: AdvisorService,
    private val advisorMapper: AdvisorMapper,
    private val json: Json,
    @ApplicationContext private val context: Context
) : AdvisorRepository {

    override suspend fun getSummary(): Resource<AdvisorSummary> {
        return try {
            Resource.Success(advisorMapper.toAdvisorSummary(advisorService.getSummary()))
        } catch (e: HttpException) {
            Resource.Error(message = extractErrorMessage(e))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun getInsights(): Resource<List<AdvisorInsight>> {
        return try {
            Resource.Success(advisorService.getInsights().map { advisorMapper.toAdvisorInsight(it) })
        } catch (e: HttpException) {
            Resource.Error(message = extractErrorMessage(e))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun askQuestion(categoryKey: String, question: String): Resource<AdvisorInsight> {
        return try {
            val request = AdvisorAskRequestDto(categoryKey = categoryKey, question = question)
            Resource.Success(advisorMapper.toAdvisorInsight(advisorService.askQuestion(request)))
        } catch (e: HttpException) {
            Resource.Error(message = extractErrorMessage(e))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun refreshInsight(id: Long): Resource<AdvisorInsight> {
        return try {
            Resource.Success(advisorMapper.toAdvisorInsight(advisorService.refreshInsight(id)))
        } catch (e: HttpException) {
            Resource.Error(message = extractErrorMessage(e))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    private fun extractErrorMessage(e: HttpException): String {
        val errorDto = e.response()?.errorBody()?.string()?.let {
            runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
        }
        return errorDto?.message ?: context.getString(R.string.error_generic_fallback)
    }
}
