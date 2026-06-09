package com.example.fintrack.data.remote.api

import com.example.fintrack.data.remote.dto.RegisterRequestDto
import com.example.fintrack.data.remote.dto.RegisterResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): RegisterResponseDto
}
