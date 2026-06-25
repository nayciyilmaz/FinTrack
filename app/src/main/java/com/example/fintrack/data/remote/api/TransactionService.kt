package com.example.fintrack.data.remote.api

import com.example.fintrack.data.remote.dto.TransactionRequestDto
import com.example.fintrack.data.remote.dto.TransactionResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TransactionService {

    @POST("api/transactions")
    suspend fun addTransaction(@Body request: TransactionRequestDto): TransactionResponseDto

    @GET("api/transactions")
    suspend fun getTransactions(
        @Query("type") type: String?,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<TransactionResponseDto>

    @PUT("api/transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: Long,
        @Body request: TransactionRequestDto
    ): TransactionResponseDto

    @DELETE("api/transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Long)
}
