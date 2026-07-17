package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmailUpdateResponseDto(
    val email: String,
    val token: String,
    val refreshToken: String
)
