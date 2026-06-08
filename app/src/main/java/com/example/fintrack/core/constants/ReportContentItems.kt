package com.example.fintrack.core.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.TrendingUp
import com.example.fintrack.R
import com.example.fintrack.model.ReportContentItem

val reportContentItems = listOf(
    ReportContentItem(Icons.Filled.List, R.string.report_content_transaction_summary, R.string.report_content_transaction_summary_desc),
    ReportContentItem(Icons.Filled.PieChart, R.string.report_content_category_analysis, R.string.report_content_category_analysis_desc),
    ReportContentItem(Icons.Filled.TrendingUp, R.string.report_content_spending_trend, R.string.report_content_spending_trend_desc),
    ReportContentItem(Icons.Filled.Tune, R.string.title_budget_limits, R.string.report_content_budget_limits_desc),
    ReportContentItem(Icons.Filled.Star, R.string.title_savings_goals, R.string.report_content_savings_goals_desc)
)