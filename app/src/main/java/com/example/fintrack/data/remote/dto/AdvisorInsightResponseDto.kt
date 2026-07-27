package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AdvisorInsightResponseDto(
    val id: Long,
    val categoryKey: String,
    val question: String,
    val answer: String
)
