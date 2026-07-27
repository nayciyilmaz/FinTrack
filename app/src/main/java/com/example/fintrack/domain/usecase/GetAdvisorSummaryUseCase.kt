package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.AdvisorSummary
import com.example.fintrack.domain.repository.AdvisorRepository

class GetAdvisorSummaryUseCase(private val advisorRepository: AdvisorRepository) {
    suspend operator fun invoke(): Resource<AdvisorSummary> = advisorRepository.getSummary()
}
