package com.example.fintrack.domain.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.AdvisorInsight
import com.example.fintrack.domain.model.AdvisorSummary

interface AdvisorRepository {
    suspend fun getSummary(): Resource<AdvisorSummary>
    suspend fun getInsights(): Resource<List<AdvisorInsight>>
    suspend fun askQuestion(categoryKey: String, question: String): Resource<AdvisorInsight>
    suspend fun refreshInsight(id: Long): Resource<AdvisorInsight>
}
