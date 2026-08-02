package com.example.fintrack.presentation.screens.reports

import com.example.fintrack.domain.model.Budget
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.domain.model.Transaction

data class ReportsActionState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val periodLabel: String = "",
    val income: Int = 0,
    val expense: Int = 0,
    val canGoPrevious: Boolean = false,
    val canGoNext: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val categoryDistribution: List<ReportCategoryItem> = emptyList(),
    val spendingTrend: List<ReportTrendItem> = emptyList(),
    val trendHigh: ReportTrendSummary? = null,
    val trendLow: ReportTrendSummary? = null,
    val budgets: List<Budget> = emptyList(),
    val categoryExpenses: Map<String, Int> = emptyMap(),
    val savingsGoals: List<SavingsGoal> = emptyList(),
    val savingsEstimatedDates: Map<Long, String?> = emptyMap(),
    val isGeneratingPdf: Boolean = false,
    val isPdfError: Boolean = false
)

data class ReportCategoryItem(
    val categoryKey: String,
    val amount: Double,
    val percentage: Float
)

data class ReportTrendItem(
    val label: String,
    val detailLabel: String,
    val amount: Float
)

data class ReportTrendSummary(
    val label: String,
    val date: String,
    val amount: Double
)

sealed class PdfResult {
    data object Success : PdfResult()
    data object Error : PdfResult()
}
