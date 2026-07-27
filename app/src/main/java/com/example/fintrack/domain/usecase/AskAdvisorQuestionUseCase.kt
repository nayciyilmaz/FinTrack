package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.AdvisorInsight
import com.example.fintrack.domain.repository.AdvisorRepository

class AskAdvisorQuestionUseCase(private val advisorRepository: AdvisorRepository) {
    suspend operator fun invoke(categoryKey: String, question: String): Resource<AdvisorInsight> =
        advisorRepository.askQuestion(categoryKey, question)
}
