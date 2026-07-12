package com.example.fintrack.presentation.screens.analysis

data class SpendingAnalysisActionState(
    val isCategoryLoading: Boolean = false,
    val isCategoryError: Boolean = false,
    val categoryDistribution: List<CategoryItem> = emptyList(),
    val isTrendLoading: Boolean = false,
    val isTrendError: Boolean = false,
    val spendingTrend: List<SpendingTrendItem> = emptyList(),
    val summaryHigh: SpendingSummary? = null,
    val summaryLow: SpendingSummary? = null
)

data class CategoryItem(
    val name: String,
    val categoryKey: String,
    val percentage: Float,
    val amount: Double
)

data class SpendingTrendItem(
    val label: String,
    val detailLabel: String,
    val amount: Float
)

data class SpendingSummary(
    val label: String,
    val date: String,
    val amount: Double
)
