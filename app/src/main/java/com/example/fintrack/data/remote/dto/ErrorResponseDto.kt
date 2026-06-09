package com.example.fintrack.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    val code: Int,
    val message: String,
    @SerialName("fieldErrors") val fieldErrors: Map<String, String>? = null
)
