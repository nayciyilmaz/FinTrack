package com.example.fintrack.domain.model

data class AdvisorSummary(
    val periodText: String,
    val healthScore: Int,
    val riskLevel: String
)

data class AdvisorInsight(
    val id: Long,
    val categoryKey: String,
    val question: String,
    val answer: String
)
