package com.example.fintrack.data.remote.api

import com.example.fintrack.data.remote.dto.AdvisorAskRequestDto
import com.example.fintrack.data.remote.dto.AdvisorInsightResponseDto
import com.example.fintrack.data.remote.dto.AdvisorSummaryResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AdvisorService {

    @GET("api/advisor/summary")
    suspend fun getSummary(): AdvisorSummaryResponseDto

    @GET("api/advisor/insights")
    suspend fun getInsights(): List<AdvisorInsightResponseDto>

    @POST("api/advisor/ask")
    suspend fun askQuestion(@Body request: AdvisorAskRequestDto): AdvisorInsightResponseDto

    @POST("api/advisor/insights/{id}/refresh")
    suspend fun refreshInsight(@Path("id") id: Long): AdvisorInsightResponseDto
}
