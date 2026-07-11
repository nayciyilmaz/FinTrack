package com.example.fintrack.data.remote.api

import com.example.fintrack.data.remote.dto.BudgetRequestDto
import com.example.fintrack.data.remote.dto.BudgetResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface BudgetService {

    @GET("api/budgets")
    suspend fun getBudgets(): List<BudgetResponseDto>

    @PUT("api/budgets")
    suspend fun saveBudgets(@Body request: List<BudgetRequestDto>): List<BudgetResponseDto>
}
