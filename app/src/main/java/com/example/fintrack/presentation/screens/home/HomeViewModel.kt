package com.example.fintrack.presentation.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.LocaleHelper
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.presentation.screens.transactions.TransactionDisplayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _actionState = MutableStateFlow(HomeActionState())
    val actionState: StateFlow<HomeActionState> = _actionState.asStateFlow()

    fun loadHomeData() {
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isLoading = true, isError = false)

            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val payday = tokenManager.getPayday().first()
            val (periodStart, periodEnd) = calculatePeriodDates(payday, today)
            val periodText = formatPeriodText(periodStart, periodEnd)

            val periodDeferred = async {
                getTransactionsUseCase(
                    type = null,
                    startDate = periodStart.format(formatter),
                    endDate = periodEnd.minusDays(1).format(formatter)
                )
            }

            val recentDeferred = async {
                getTransactionsUseCase(
                    type = null,
                    startDate = today.minusYears(1).format(formatter),
                    endDate = today.format(formatter)
                )
            }

            val periodResult = periodDeferred.await()
            val recentResult = recentDeferred.await()

            if (periodResult is Resource.Success && recentResult is Resource.Success) {
                val periodTransactions = periodResult.data ?: emptyList()
                val recentTransactions = recentResult.data ?: emptyList()

                val income = periodTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }.toInt()
                val expense = periodTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }.toInt()
                val remainingBalance = income - expense
                val daysRemaining = ChronoUnit.DAYS.between(today, periodEnd).toInt()
                val dailyLimit = if (remainingBalance > 0 && daysRemaining > 0) remainingBalance / daysRemaining else 0
                val spendingRatio = if (income > 0) (expense * 100) / income else 0

                _actionState.value = HomeActionState(
                    periodText = periodText,
                    remainingBalance = remainingBalance,
                    dailyLimit = dailyLimit,
                    income = income,
                    expense = expense,
                    spendingRatio = spendingRatio,
                    recentTransactions = calculateDisplayItems(recentTransactions).take(3)
                )
            } else {
                _actionState.value = HomeActionState(isError = true)
            }
        }
    }

    private fun calculatePeriodDates(payday: Int, today: LocalDate): Pair<LocalDate, LocalDate> {
        return if (today.dayOfMonth >= payday) {
            val start = today.withDayOfMonth(minOf(payday, today.lengthOfMonth()))
            val nextMonth = today.plusMonths(1)
            val end = nextMonth.withDayOfMonth(minOf(payday, nextMonth.lengthOfMonth()))
            Pair(start, end)
        } else {
            val lastMonth = today.minusMonths(1)
            val start = lastMonth.withDayOfMonth(minOf(payday, lastMonth.lengthOfMonth()))
            val end = today.withDayOfMonth(minOf(payday, today.lengthOfMonth()))
            Pair(start, end)
        }
    }

    private fun formatPeriodText(start: LocalDate, end: LocalDate): String {
        val locale = LocaleHelper.getLocale(context)
        val startMonth = start.month.getDisplayName(TextStyle.FULL, locale)
            .replaceFirstChar { it.uppercase(locale) }
        val endMonth = end.month.getDisplayName(TextStyle.FULL, locale)
            .replaceFirstChar { it.uppercase(locale) }
        return "${start.dayOfMonth} $startMonth - ${end.dayOfMonth} $endMonth"
    }

    private fun calculateDisplayItems(transactions: List<Transaction>): List<TransactionDisplayItem> {
        val signedAmounts = transactions.map {
            if (it.type == "INCOME") it.amount else -it.amount
        }
        val totalBalance = signedAmounts.sum()

        return transactions.mapIndexed { index, transaction ->
            TransactionDisplayItem(
                transaction = transaction,
                remainingBalance = totalBalance - signedAmounts.take(index).sum()
            )
        }
    }
}
