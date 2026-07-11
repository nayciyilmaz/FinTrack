package com.example.fintrack.presentation.screens.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.domain.usecase.GetBudgetsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.SaveBudgetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class BudgetLimitsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val saveBudgetsUseCase: SaveBudgetsUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _actionState = MutableStateFlow(BudgetLimitsActionState())
    val actionState: StateFlow<BudgetLimitsActionState> = _actionState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isLoading = true, isError = false)

            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val payday = tokenManager.getPayday().first()
            val (periodStart, periodEnd) = calculatePeriodDates(payday, today)

            val transactionsDeferred = async {
                getTransactionsUseCase(
                    type = null,
                    startDate = periodStart.format(formatter),
                    endDate = periodEnd.minusDays(1).format(formatter)
                )
            }

            val budgetsDeferred = async {
                getBudgetsUseCase()
            }

            val transactionsResult = transactionsDeferred.await()
            val budgetsResult = budgetsDeferred.await()

            if (transactionsResult is Resource.Success && budgetsResult is Resource.Success) {
                val transactions = transactionsResult.data ?: emptyList()
                val budgets = budgetsResult.data ?: emptyList()

                val income = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }.toInt()
                val expense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }.toInt()

                val categoryExpenses = transactions
                    .filter { it.type == "EXPENSE" }
                    .groupBy { it.category }
                    .mapValues { entry -> entry.value.sumOf { it.amount }.toInt() }

                _actionState.value = BudgetLimitsActionState(
                    income = income,
                    expense = expense,
                    budgets = budgets,
                    categoryExpenses = categoryExpenses
                )
            } else {
                _actionState.value = BudgetLimitsActionState(isError = true)
            }
        }
    }

    fun saveBudgets(budgets: List<Pair<String, Double>>) {
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isSaving = true)

            when (val result = saveBudgetsUseCase(budgets)) {
                is Resource.Success -> {
                    _actionState.value = _actionState.value.copy(
                        budgets = result.data ?: emptyList(),
                        isSaving = false
                    )
                }
                is Resource.Error -> {
                    _actionState.value = _actionState.value.copy(isSaving = false)
                }
                is Resource.Loading -> Unit
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
}
