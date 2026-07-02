package com.example.fintrack.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponseDto(
    val token: String,
    @SerialName("refresh_token") val refreshToken: String
)
