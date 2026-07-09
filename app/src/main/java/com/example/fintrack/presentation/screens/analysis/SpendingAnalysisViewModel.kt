package com.example.fintrack.presentation.screens.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SpendingAnalysisViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val locale = Locale("tr")
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    private val _selectedCategoryPeriod = MutableStateFlow("Son 1 Ay")
    val selectedCategoryPeriod: StateFlow<String> = _selectedCategoryPeriod.asStateFlow()

    private val _selectedTrendPeriod = MutableStateFlow("Son 7 Gün")
    val selectedTrendPeriod: StateFlow<String> = _selectedTrendPeriod.asStateFlow()

    private val _categoryState = MutableStateFlow(SpendingAnalysisState())
    val categoryState: StateFlow<SpendingAnalysisState> = _categoryState.asStateFlow()

    private val _trendState = MutableStateFlow(SpendingAnalysisState())
    val trendState: StateFlow<SpendingAnalysisState> = _trendState.asStateFlow()

    init {
        loadCategoryDistribution(_selectedCategoryPeriod.value)
        loadSpendingTrend(_selectedTrendPeriod.value)
    }

    fun onCategoryPeriodChanged(period: String) {
        _selectedCategoryPeriod.value = period
        loadCategoryDistribution(period)
    }

    fun onTrendPeriodChanged(period: String) {
        _selectedTrendPeriod.value = period
        loadSpendingTrend(period)
    }

    private fun loadCategoryDistribution(period: String) {
        viewModelScope.launch {
            _categoryState.value = _categoryState.value.copy(isLoading = true, isError = false)

            val (startDate, endDate) = periodToDateRange(period)
            val result = getTransactionsUseCase(
                type = "EXPENSE",
                startDate = startDate.format(formatter),
                endDate = endDate.format(formatter)
            )

            when (result) {
                is Resource.Success -> {
                    val transactions = result.data ?: emptyList()
                    _categoryState.value = SpendingAnalysisState(
                        categoryDistribution = calculateCategoryDistribution(transactions)
                    )
                }
                is Resource.Error -> {
                    _categoryState.value = SpendingAnalysisState(isError = true)
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun loadSpendingTrend(period: String) {
        viewModelScope.launch {
            _trendState.value = _trendState.value.copy(isLoading = true, isError = false)

            val (startDate, endDate) = periodToDateRange(period)
            val result = getTransactionsUseCase(
                type = "EXPENSE",
                startDate = startDate.format(formatter),
                endDate = endDate.format(formatter)
            )

            when (result) {
                is Resource.Success -> {
                    val transactions = result.data ?: emptyList()
                    val trendData = calculateSpendingTrend(transactions, period, startDate, endDate)
                    val (high, low) = calculateSummary(trendData, period)
                    _trendState.value = SpendingAnalysisState(
                        spendingTrend = trendData,
                        summaryHigh = high,
                        summaryLow = low
                    )
                }
                is Resource.Error -> {
                    _trendState.value = SpendingAnalysisState(isError = true)
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun periodToDateRange(period: String): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        val startDate = when (period) {
            "Son 7 Gün" -> today.minusDays(6)
            "Son 15 Gün" -> today.minusDays(14)
            "Son 1 Ay" -> today.minusMonths(1)
            "Son 3 Ay" -> today.minusMonths(3)
            "Son 6 Ay" -> today.minusMonths(6)
            "Son 1 Yıl" -> today.minusYears(1)
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
        period: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<SpendingTrendItem> {
        val transactionsByDate = transactions.groupBy { LocalDate.parse(it.date) }
            .mapValues { (_, txs) -> txs.sumOf { it.amount } }

        return when (period) {
            "Son 7 Gün" -> buildDailyTrend(startDate, endDate, transactionsByDate)
            "Son 15 Gün" -> buildDailyTrend(startDate, endDate, transactionsByDate)
            "Son 1 Ay" -> buildWeeklyTrend(startDate, endDate, transactionsByDate)
            "Son 3 Ay" -> buildMonthlyTrend(startDate, endDate, transactionsByDate)
            "Son 6 Ay" -> buildMonthlyTrend(startDate, endDate, transactionsByDate)
            "Son 1 Yıl" -> buildMonthlyTrend(startDate, endDate, transactionsByDate)
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
        period: String
    ): Pair<SpendingSummary?, SpendingSummary?> {
        if (trendData.isEmpty() || trendData.all { it.amount == 0f }) return Pair(null, null)

        val highItem = trendData.maxBy { it.amount }
        val lowItem = trendData.minBy { it.amount }

        val summaryLabel = when (period) {
            "Son 7 Gün", "Son 15 Gün" -> "Gün"
            "Son 1 Ay" -> "Hafta"
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
