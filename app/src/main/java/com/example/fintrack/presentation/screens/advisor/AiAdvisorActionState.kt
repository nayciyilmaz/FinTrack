package com.example.fintrack.presentation.screens.advisor

import com.example.fintrack.domain.model.AdvisorInsight

data class AiAdvisorActionState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val periodText: String = "",
    val healthScore: Int = 0,
    val riskLevel: String = "",
    val insights: List<AdvisorInsight> = emptyList(),
    val refreshingInsightId: Long? = null
)
