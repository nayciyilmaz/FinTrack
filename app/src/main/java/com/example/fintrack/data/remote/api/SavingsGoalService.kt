package com.example.fintrack.data.remote.api

import com.example.fintrack.data.remote.dto.SavingsGoalRequestDto
import com.example.fintrack.data.remote.dto.SavingsGoalResponseDto
import com.example.fintrack.data.remote.dto.SavingsGoalUpdateDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SavingsGoalService {

    @GET("api/savings-goals")
    suspend fun getGoals(): List<SavingsGoalResponseDto>

    @POST("api/savings-goals")
    suspend fun addGoal(@Body request: SavingsGoalRequestDto): SavingsGoalResponseDto

    @PUT("api/savings-goals/{id}")
    suspend fun updateGoal(
        @Path("id") id: Long,
        @Body request: SavingsGoalUpdateDto
    ): SavingsGoalResponseDto

    @DELETE("api/savings-goals/{id}")
    suspend fun deleteGoal(@Path("id") id: Long)
}
