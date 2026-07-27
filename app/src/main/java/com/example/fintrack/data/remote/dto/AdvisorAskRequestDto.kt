package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AdvisorAskRequestDto(
    val categoryKey: String,
    val question: String
)
