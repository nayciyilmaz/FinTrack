package com.example.fintrack.presentation.screens.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.constants.expenseCategories
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.domain.model.TransactionType
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

    private val _uiState = MutableStateFlow(BudgetLimitsUiState())
    val uiState: StateFlow<BudgetLimitsUiState> = _uiState.asStateFlow()

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

                val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }.toInt()
                val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }.toInt()

                val categoryExpenses = transactions
                    .filter { it.type == TransactionType.EXPENSE }
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

    fun onShowManageDialog() {
        val budgets = _actionState.value.budgets
        val amounts = expenseCategories.associate { category ->
            val existing = budgets.find { it.category == category.key }
            category.key to (existing?.limitAmount?.toInt()?.toString() ?: "")
        }
        val activeStates = expenseCategories.associate { category ->
            category.key to budgets.any { it.category == category.key }
        }
        _uiState.value = BudgetLimitsUiState(
            showManageDialog = true,
            dialogAmounts = amounts,
            dialogActiveStates = activeStates,
            totalLimit = calculateTotalLimit(amounts, activeStates)
        )
    }

    fun onDismissManageDialog() {
        _uiState.value = _uiState.value.copy(showManageDialog = false)
    }

    fun onDialogAmountChange(categoryKey: String, value: String) {
        val filtered = value.filter { it.isDigit() }
        val newAmounts = _uiState.value.dialogAmounts.toMutableMap().apply { put(categoryKey, filtered) }
        _uiState.value = _uiState.value.copy(
            dialogAmounts = newAmounts,
            totalLimit = calculateTotalLimit(newAmounts, _uiState.value.dialogActiveStates)
        )
    }

    fun onDialogActiveToggle(categoryKey: String) {
        val current = _uiState.value.dialogActiveStates[categoryKey] ?: false
        val newActiveStates = _uiState.value.dialogActiveStates.toMutableMap().apply { put(categoryKey, !current) }
        _uiState.value = _uiState.value.copy(
            dialogActiveStates = newActiveStates,
            totalLimit = calculateTotalLimit(_uiState.value.dialogAmounts, newActiveStates)
        )
    }

    fun saveDialogBudgets() {
        val state = _uiState.value
        if (state.totalLimit > _actionState.value.income) return

        val budgetsToSave = expenseCategories.mapNotNull { category ->
            val isActive = state.dialogActiveStates[category.key] ?: false
            val amount = state.dialogAmounts[category.key]?.toDoubleOrNull()
            if (isActive && amount != null && amount > 0) {
                category.key to amount
            } else {
                null
            }
        }

        _uiState.value = _uiState.value.copy(showManageDialog = false)
        saveBudgets(budgetsToSave)
    }

    private fun saveBudgets(budgets: List<Pair<String, Double>>) {
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

    private fun calculateTotalLimit(amounts: Map<String, String>, activeStates: Map<String, Boolean>): Int {
        return amounts.entries.sumOf { (key, amount) ->
            val isActive = activeStates[key] ?: false
            if (isActive) amount.toIntOrNull() ?: 0 else 0
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
