package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.AdvisorInsight
import com.example.fintrack.domain.repository.AdvisorRepository

class GetAdvisorInsightsUseCase(private val advisorRepository: AdvisorRepository) {
    suspend operator fun invoke(): Resource<List<AdvisorInsight>> = advisorRepository.getInsights()
}
