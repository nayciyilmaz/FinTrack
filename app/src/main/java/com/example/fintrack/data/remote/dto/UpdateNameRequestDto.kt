package com.example.fintrack.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateNameRequestDto(
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String
)
