package com.example.fintrack.presentation.screens.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.R
import com.example.fintrack.core.util.LocaleHelper
import com.example.fintrack.core.util.Resource
import com.example.fintrack.core.util.apiDateFormatter
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.GetBudgetsUseCase
import com.example.fintrack.domain.usecase.GetSavingsGoalsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val getSavingsGoalsUseCase: GetSavingsGoalsUseCase,
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val locale = LocaleHelper.getLocale(context)

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(ReportsActionState())
    val actionState: StateFlow<ReportsActionState> = _actionState.asStateFlow()

    private val _pdfResult = MutableSharedFlow<PdfResult>()
    val pdfResult: SharedFlow<PdfResult> = _pdfResult.asSharedFlow()

    fun onSectionToggle(titleResId: Int) {
        val current = _uiState.value.checkedSections[titleResId] ?: false
        _uiState.value = _uiState.value.copy(
            checkedSections = _uiState.value.checkedSections + (titleResId to !current)
        )
    }

    fun loadData(offset: Int = _uiState.value.periodOffset) {
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isLoading = true, isError = false)

            val payday = tokenManager.getPayday().first()
            val today = LocalDate.now()
            val (periodStart, periodEnd) = calculatePeriodDates(payday, today, offset)
            val (prevPeriodStart, prevPeriodEnd) = calculatePeriodDates(payday, today, offset - 1)

            val transactionsDeferred = async {
                getTransactionsUseCase(
                    type = null,
                    startDate = periodStart.format(apiDateFormatter),
                    endDate = periodEnd.minusDays(1).format(apiDateFormatter)
                )
            }
            val prevTransactionsDeferred = async {
                getTransactionsUseCase(
                    type = null,
                    startDate = prevPeriodStart.format(apiDateFormatter),
                    endDate = prevPeriodEnd.minusDays(1).format(apiDateFormatter)
                )
            }
            val budgetsDeferred = async { getBudgetsUseCase() }
            val goalsDeferred = async { getSavingsGoalsUseCase() }

            val transactionsResult = transactionsDeferred.await()
            val prevTransactionsResult = prevTransactionsDeferred.await()
            val budgetsResult = budgetsDeferred.await()
            val goalsResult = goalsDeferred.await()

            if (transactionsResult is Resource.Success && budgetsResult is Resource.Success && goalsResult is Resource.Success) {
                val transactions = transactionsResult.data ?: emptyList()
                val prevTransactions = (prevTransactionsResult as? Resource.Success)?.data ?: emptyList()
                val budgets = budgetsResult.data ?: emptyList()
                val goals = goalsResult.data ?: emptyList()

                val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }.toInt()
                val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }.toInt()

                val categoryExpenses = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .groupBy { it.category }
                    .mapValues { entry -> entry.value.sumOf { it.amount }.toInt() }

                val categoryDistribution = calculateCategoryDistribution(transactions)
                val spendingTrend = calculateSpendingTrend(transactions, periodStart, periodEnd.minusDays(1))

                val canGoPrevious = prevTransactions.isNotEmpty()
                val canGoNext = offset < 0

                _uiState.value = _uiState.value.copy(periodOffset = offset)
                _actionState.value = ReportsActionState(
                    periodLabel = periodLabel(periodStart, periodEnd.minusDays(1)),
                    income = income,
                    expense = expense,
                    canGoPrevious = canGoPrevious,
                    canGoNext = canGoNext,
                    transactions = transactions,
                    categoryDistribution = categoryDistribution,
                    spendingTrend = spendingTrend,
                    budgets = budgets,
                    categoryExpenses = categoryExpenses,
                    savingsGoals = goals,
                    savingsEstimatedDates = goals.associate { it.id to calculateEstimatedDate(it) }
                )
            } else {
                Timber.w("Load reports data failed")
                _actionState.value = _actionState.value.copy(isLoading = false, isError = true)
            }
        }
    }

    fun onPreviousPeriod() {
        if (!_actionState.value.canGoPrevious) return
        loadData(_uiState.value.periodOffset - 1)
    }

    fun onNextPeriod() {
        if (!_actionState.value.canGoNext) return
        loadData(_uiState.value.periodOffset + 1)
    }

    fun generatePdf(context: Context) {
        val state = _actionState.value
        val selectedSections = _uiState.value.checkedSections.filterValues { it }.keys
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isGeneratingPdf = true, isPdfError = false)
            val success = withContext(Dispatchers.IO) {
                ReportPdfGenerator.generate(
                    context = context,
                    periodLabel = state.periodLabel,
                    income = state.income,
                    expense = state.expense,
                    transactions = state.transactions,
                    categoryDistribution = state.categoryDistribution,
                    spendingTrend = state.spendingTrend,
                    budgets = state.budgets,
                    categoryExpenses = state.categoryExpenses,
                    savingsGoals = state.savingsGoals,
                    savingsEstimatedDates = state.savingsEstimatedDates,
                    selectedSections = selectedSections
                )
            }
            if (!success) {
                Timber.w("Generate report PDF failed")
            }
            _actionState.value = _actionState.value.copy(
                isGeneratingPdf = false,
                isPdfError = !success
            )
            _pdfResult.emit(if (success) PdfResult.Success else PdfResult.Error)
        }
    }

    private fun periodLabel(periodStart: LocalDate, periodLastDay: LocalDate): String {
        val startMonth = periodStart.month.getDisplayName(TextStyle.SHORT, locale)
            .replaceFirstChar { it.uppercase(locale) }
        val endMonth = periodLastDay.month.getDisplayName(TextStyle.SHORT, locale)
            .replaceFirstChar { it.uppercase(locale) }
        return "${periodStart.dayOfMonth} $startMonth - ${periodLastDay.dayOfMonth} $endMonth"
    }

    private fun calculateEstimatedDate(goal: SavingsGoal): String? {
        if (goal.currentAmount <= 0) return null
        if (goal.currentAmount >= goal.targetAmount) return null

        val createdAt = LocalDate.parse(goal.createdAt.substring(0, 10))
        val today = LocalDate.now()
        val monthsSinceCreation = ChronoUnit.MONTHS.between(createdAt, today).coerceAtLeast(1)
        val monthlyRate = goal.currentAmount / monthsSinceCreation
        if (monthlyRate <= 0) return null

        val remaining = goal.targetAmount - goal.currentAmount
        val estimatedMonths = (remaining / monthlyRate).toLong()
        val estimatedDate = today.plusMonths(estimatedMonths)

        val month = estimatedDate.month.getDisplayName(TextStyle.SHORT, locale)
            .replaceFirstChar { it.uppercase(locale) }
        return "$month. ${estimatedDate.year}"
    }

    private fun calculateCategoryDistribution(transactions: List<Transaction>): List<ReportCategoryItem> {
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
        val totalAmount = expenseTransactions.sumOf { it.amount }
        if (totalAmount == 0.0) return emptyList()

        return expenseTransactions
            .groupBy { it.category }
            .map { (category, txs) ->
                val amount = txs.sumOf { it.amount }
                ReportCategoryItem(
                    categoryKey = category,
                    amount = amount,
                    percentage = ((amount / totalAmount) * 100).toFloat()
                )
            }
            .sortedByDescending { it.amount }
    }

    private fun calculateSpendingTrend(
        transactions: List<Transaction>,
        periodStart: LocalDate,
        periodEnd: LocalDate
    ): List<ReportTrendItem> {
        val transactionsByDate = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { LocalDate.parse(it.date) }
            .mapValues { (_, txs) -> txs.sumOf { it.amount } }

        val items = mutableListOf<ReportTrendItem>()
        var weekStart = periodStart
        var weekIndex = 0
        while (!weekStart.isAfter(periodEnd)) {
            var weekEnd = weekStart.plusDays(6)
            if (weekEnd.isAfter(periodEnd)) weekEnd = periodEnd
            var total = 0.0
            var current = weekStart
            while (!current.isAfter(weekEnd)) {
                total += transactionsByDate[current] ?: 0.0
                current = current.plusDays(1)
            }
            items.add(ReportTrendItem(context.getString(R.string.report_week_label_format, weekIndex), total.toFloat()))
            weekIndex++
            weekStart = weekEnd.plusDays(1)
        }
        return items
    }

    private fun calculatePeriodDates(payday: Int, today: LocalDate, offset: Int): Pair<LocalDate, LocalDate> {
        val currentStart = if (today.dayOfMonth >= payday) {
            today.withDayOfMonth(minOf(payday, today.lengthOfMonth()))
        } else {
            val lastMonth = today.minusMonths(1)
            lastMonth.withDayOfMonth(minOf(payday, lastMonth.lengthOfMonth()))
        }

        val targetAnchor = currentStart.plusMonths(offset.toLong())
        val start = targetAnchor.withDayOfMonth(minOf(payday, targetAnchor.lengthOfMonth()))
        val nextAnchor = targetAnchor.plusMonths(1)
        val end = nextAnchor.withDayOfMonth(minOf(payday, nextAnchor.lengthOfMonth()))
        return Pair(start, end)
    }
}
