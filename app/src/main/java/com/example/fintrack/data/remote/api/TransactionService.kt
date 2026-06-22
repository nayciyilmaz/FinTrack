package com.example.fintrack.data.remote.api

import com.example.fintrack.data.remote.dto.TransactionRequestDto
import com.example.fintrack.data.remote.dto.TransactionResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionService {

    @POST("api/transactions")
    suspend fun addTransaction(@Body request: TransactionRequestDto): TransactionResponseDto
}
