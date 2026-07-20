package com.example.fintrack.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequestDto(
    @SerialName("reset_token") val resetToken: String,
    @SerialName("new_password") val newPassword: String
)
