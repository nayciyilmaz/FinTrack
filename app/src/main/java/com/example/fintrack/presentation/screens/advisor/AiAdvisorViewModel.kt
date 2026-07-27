package com.example.fintrack.presentation.screens.advisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.usecase.AskAdvisorQuestionUseCase
import com.example.fintrack.domain.usecase.GetAdvisorInsightsUseCase
import com.example.fintrack.domain.usecase.GetAdvisorSummaryUseCase
import com.example.fintrack.domain.usecase.RefreshAdvisorInsightUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiAdvisorViewModel @Inject constructor(
    private val getAdvisorSummaryUseCase: GetAdvisorSummaryUseCase,
    private val getAdvisorInsightsUseCase: GetAdvisorInsightsUseCase,
    private val askAdvisorQuestionUseCase: AskAdvisorQuestionUseCase,
    private val refreshAdvisorInsightUseCase: RefreshAdvisorInsightUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiAdvisorUiState())
    val uiState: StateFlow<AiAdvisorUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(AiAdvisorActionState())
    val actionState: StateFlow<AiAdvisorActionState> = _actionState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isLoading = true, isError = false)

            val summaryDeferred = async { getAdvisorSummaryUseCase() }
            val insightsDeferred = async { getAdvisorInsightsUseCase() }

            val summaryResult = summaryDeferred.await()
            val insightsResult = insightsDeferred.await()

            if (summaryResult is Resource.Success && insightsResult is Resource.Success) {
                val summary = summaryResult.data
                _actionState.value = _actionState.value.copy(
                    isLoading = false,
                    isError = false,
                    periodText = summary?.periodText ?: "",
                    healthScore = summary?.healthScore ?: 0,
                    riskLevel = summary?.riskLevel ?: "",
                    insights = insightsResult.data ?: emptyList()
                )
            } else {
                _actionState.value = _actionState.value.copy(isLoading = false, isError = true)
            }
        }
    }

    fun onShowQuickQuestionsDialog() {
        _uiState.value = _uiState.value.copy(showQuickQuestionsDialog = true)
    }

    fun onDismissQuickQuestionsDialog() {
        _uiState.value = _uiState.value.copy(showQuickQuestionsDialog = false)
    }

    fun onQuestionAsked(categoryKey: String, question: String) {
        _uiState.value = _uiState.value.copy(showQuickQuestionsDialog = false)

        val alreadyAsked = _actionState.value.insights.any { it.question == question }
        if (alreadyAsked) return

        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isLoading = true, isError = false)

            when (val result = askAdvisorQuestionUseCase(categoryKey, question)) {
                is Resource.Success -> {
                    val newInsight = result.data
                    _actionState.value = _actionState.value.copy(
                        isLoading = false,
                        insights = if (newInsight != null) {
                            listOf(newInsight) + _actionState.value.insights
                        } else {
                            _actionState.value.insights
                        }
                    )
                }
                is Resource.Error -> {
                    _actionState.value = _actionState.value.copy(isLoading = false, isError = true)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onInsightRefreshRequested(insightId: Long) {
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(refreshingInsightId = insightId)

            when (val result = refreshAdvisorInsightUseCase(insightId)) {
                is Resource.Success -> {
                    val updated = result.data
                    val newInsights = if (updated != null) {
                        _actionState.value.insights.map { if (it.id == updated.id) updated else it }
                    } else {
                        _actionState.value.insights
                    }
                    _actionState.value = _actionState.value.copy(
                        refreshingInsightId = null,
                        insights = newInsights
                    )
                }
                is Resource.Error -> {
                    _actionState.value = _actionState.value.copy(refreshingInsightId = null)
                }
                is Resource.Loading -> Unit
            }
        }
    }
}
