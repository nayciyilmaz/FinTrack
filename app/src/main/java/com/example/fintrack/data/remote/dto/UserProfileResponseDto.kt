package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponseDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val createdAt: String,
    val passwordChangedAt: String
)
