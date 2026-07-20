package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerifyResetCodeRequestDto(
    val email: String,
    val code: String
)
