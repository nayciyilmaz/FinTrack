package com.example.fintrack.data.mapper

import com.example.fintrack.data.remote.dto.AdvisorInsightResponseDto
import com.example.fintrack.data.remote.dto.AdvisorSummaryResponseDto
import com.example.fintrack.domain.model.AdvisorInsight
import com.example.fintrack.domain.model.AdvisorSummary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdvisorMapper @Inject constructor() {
    fun toAdvisorSummary(dto: AdvisorSummaryResponseDto): AdvisorSummary = AdvisorSummary(
        periodText = dto.periodText,
        healthScore = dto.healthScore,
        riskLevel = dto.riskLevel
    )

    fun toAdvisorInsight(dto: AdvisorInsightResponseDto): AdvisorInsight = AdvisorInsight(
        id = dto.id,
        categoryKey = dto.categoryKey,
        question = dto.question,
        answer = dto.answer
    )
}
