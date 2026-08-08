package com.example.fintrack.presentation.screens.analysis

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.LocaleHelper
import com.example.fintrack.core.util.Resource
import com.example.fintrack.core.util.apiDateFormatter
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import javax.inject.Inject

@HiltViewModel
class SpendingAnalysisViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val locale = LocaleHelper.getLocale(context)

    private val _uiState = MutableStateFlow(SpendingAnalysisUiState())
    val uiState: StateFlow<SpendingAnalysisUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(SpendingAnalysisActionState())
    val actionState: StateFlow<SpendingAnalysisActionState> = _actionState.asStateFlow()

    init {
        loadCategoryDistribution(_uiState.value.selectedCategoryPeriodIndex)
        loadSpendingTrend(_uiState.value.selectedTrendPeriodIndex)
    }

    fun onCategoryPeriodChanged(periodIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedCategoryPeriodIndex = periodIndex)
        loadCategoryDistribution(periodIndex)
    }

    fun onTrendPeriodChanged(periodIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTrendPeriodIndex = periodIndex)
        loadSpendingTrend(periodIndex)
    }

    private fun loadCategoryDistribution(periodIndex: Int) {
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isCategoryLoading = true, isCategoryError = false)

            val (startDate, endDate) = periodToDateRange(periodIndex)
            val result = getTransactionsUseCase(
                type = TransactionType.EXPENSE,
                startDate = startDate.format(apiDateFormatter),
                endDate = endDate.format(apiDateFormatter)
            )

            when (result) {
                is Resource.Success -> {
                    val transactions = result.data ?: emptyList()
                    _actionState.value = _actionState.value.copy(
                        isCategoryLoading = false,
                        categoryDistribution = calculateCategoryDistribution(transactions)
                    )
                }
                is Resource.Error -> {
                    _actionState.value = _actionState.value.copy(
                        isCategoryLoading = false,
                        isCategoryError = true
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun loadSpendingTrend(periodIndex: Int) {
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isTrendLoading = true, isTrendError = false)

            val (startDate, endDate) = periodToDateRange(periodIndex)
            val result = getTransactionsUseCase(
                type = TransactionType.EXPENSE,
                startDate = startDate.format(apiDateFormatter),
                endDate = endDate.format(apiDateFormatter)
            )

            when (result) {
                is Resource.Success -> {
                    val transactions = result.data ?: emptyList()
                    val trendData = calculateSpendingTrend(transactions, periodIndex, startDate, endDate)
                    val (high, low) = calculateSummary(trendData, periodIndex)
                    _actionState.value = _actionState.value.copy(
                        isTrendLoading = false,
                        spendingTrend = trendData,
                        summaryHigh = high,
                        summaryLow = low
                    )
                }
                is Resource.Error -> {
                    _actionState.value = _actionState.value.copy(
                        isTrendLoading = false,
                        isTrendError = true
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun periodToDateRange(periodIndex: Int): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        val startDate = when (periodIndex) {
            0 -> today.minusDays(6)
            1 -> today.minusDays(14)
            3 -> today.minusMonths(1)
            4 -> today.minusMonths(3)
            5 -> today.minusMonths(6)
            7 -> today.minusYears(1)
            else -> today.minusDays(6)
        }
        return Pair(startDate, today)
    }

    private fun calculateCategoryDistribution(transactions: List<Transaction>): List<CategoryItem> {
        val totalAmount = transactions.sumOf { it.amount }
        if (totalAmount == 0.0) return emptyList()

        val grouped = transactions.groupBy { it.category }
            .map { (category, txs) -> Pair(category, txs.sumOf { it.amount }) }
            .sortedByDescending { it.second }

        val top3 = grouped.take(3)
        val rest = grouped.drop(3)
        val restTotal = rest.sumOf { it.second }

        val items = top3.map { (category, amount) ->
            CategoryItem(
                name = category,
                categoryKey = category,
                percentage = ((amount / totalAmount) * 100).toFloat(),
                amount = amount
            )
        }.toMutableList()

        if (restTotal > 0) {
            items.add(
                CategoryItem(
                    name = "OTHER",
                    categoryKey = "OTHER",
                    percentage = ((restTotal / totalAmount) * 100).toFloat(),
                    amount = restTotal
                )
            )
        }

        return items
    }

    private fun calculateSpendingTrend(
        transactions: List<Transaction>,
        periodIndex: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<SpendingTrendItem> {
        val transactionsByDate = transactions.groupBy { LocalDate.parse(it.date) }
            .mapValues { (_, txs) -> txs.sumOf { it.amount } }

        return when (periodIndex) {
            0, 1 -> buildDailyTrend(startDate, endDate, transactionsByDate)
            3 -> buildWeeklyTrend(startDate, endDate, transactionsByDate)
            4, 5, 7 -> buildMonthlyTrend(startDate, endDate, transactionsByDate)
            else -> buildDailyTrend(startDate, endDate, transactionsByDate)
        }
    }

    private fun buildDailyTrend(
        startDate: LocalDate,
        endDate: LocalDate,
        transactionsByDate: Map<LocalDate, Double>
    ): List<SpendingTrendItem> {
        val items = mutableListOf<SpendingTrendItem>()
        var current = startDate
        val isLongRange = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) > 7
        while (!current.isAfter(endDate)) {
            val dayName = current.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
                .replaceFirstChar { it.uppercase(locale) }
            val monthName = current.month.getDisplayName(TextStyle.FULL, locale)
                .replaceFirstChar { it.uppercase(locale) }
            val label = if (isLongRange) current.dayOfMonth.toString() else dayName
            val detailLabel = "${current.dayOfMonth} $monthName"
            items.add(SpendingTrendItem(label, detailLabel, (transactionsByDate[current] ?: 0.0).toFloat()))
            current = current.plusDays(1)
        }
        return items
    }

    private fun buildWeeklyTrend(
        startDate: LocalDate,
        endDate: LocalDate,
        transactionsByDate: Map<LocalDate, Double>
    ): List<SpendingTrendItem> {
        val items = mutableListOf<SpendingTrendItem>()
        var weekStart = startDate
        var weekIndex = 1
        while (!weekStart.isAfter(endDate)) {
            var weekEnd = weekStart.plusDays(6)
            if (weekEnd.isAfter(endDate)) weekEnd = endDate
            var total = 0.0
            var current = weekStart
            while (!current.isAfter(weekEnd)) {
                total += transactionsByDate[current] ?: 0.0
                current = current.plusDays(1)
            }
            items.add(SpendingTrendItem("${weekIndex}.Hf", "${weekIndex}. Hafta", total.toFloat()))
            weekIndex++
            weekStart = weekEnd.plusDays(1)
        }
        return items
    }

    private fun buildMonthlyTrend(
        startDate: LocalDate,
        endDate: LocalDate,
        transactionsByDate: Map<LocalDate, Double>
    ): List<SpendingTrendItem> {
        val items = mutableListOf<SpendingTrendItem>()
        var current = startDate.withDayOfMonth(1)
        val endMonth = endDate.withDayOfMonth(1)
        while (!current.isAfter(endMonth)) {
            val monthName = current.month.getDisplayName(TextStyle.SHORT, locale)
                .replaceFirstChar { it.uppercase(locale) }
            val monthTotal = transactionsByDate.entries
                .filter { it.key.month == current.month && it.key.year == current.year }
                .sumOf { it.value }
            items.add(SpendingTrendItem(monthName, "$monthName ${current.year}", monthTotal.toFloat()))
            current = current.plusMonths(1)
        }
        return items
    }

    private fun calculateSummary(
        trendData: List<SpendingTrendItem>,
        periodIndex: Int
    ): Pair<SpendingSummary?, SpendingSummary?> {
        if (trendData.isEmpty() || trendData.all { it.amount == 0f }) return Pair(null, null)

        val highItem = trendData.maxBy { it.amount }
        val lowItem = trendData.minBy { it.amount }

        val summaryLabel = when (periodIndex) {
            0, 1 -> "Gün"
            3 -> "Hafta"
            else -> "Ay"
        }

        return Pair(
            SpendingSummary(
                label = "En Çok Harcanan $summaryLabel",
                date = highItem.detailLabel,
                amount = highItem.amount.toDouble()
            ),
            SpendingSummary(
                label = "En Az Harcanan $summaryLabel",
                date = lowItem.detailLabel,
                amount = lowItem.amount.toDouble()
            )
        )
    }
}
