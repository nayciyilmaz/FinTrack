package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateEmailRequestDto(
    val email: String
)
