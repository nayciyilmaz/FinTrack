package com.example.fintrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AdvisorSummaryResponseDto(
    val periodText: String,
    val healthScore: Int,
    val riskLevel: String
)
