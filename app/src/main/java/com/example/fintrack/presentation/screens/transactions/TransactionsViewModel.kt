package com.example.fintrack.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.Resource
import com.example.fintrack.core.util.apiDateFormatter
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(TransactionsActionState())
    val actionState: StateFlow<TransactionsActionState> = _actionState.asStateFlow()

    init {
        loadTransactions()
    }

    fun onFilterChange(index: Int) {
        _uiState.value = _uiState.value.copy(selectedFilterIndex = index)
        loadTransactions()
    }

    fun onPeriodChange(period: String) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period)
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _actionState.value = TransactionsActionState(isLoading = true)

            val state = _uiState.value
            val type = when (state.selectedFilterIndex) {
                1 -> TransactionType.EXPENSE
                2 -> TransactionType.INCOME
                else -> null
            }

            val (startDate, endDate) = periodToDateRange(state.selectedPeriod)

            when (val result = getTransactionsUseCase(
                type = type,
                startDate = startDate.format(apiDateFormatter),
                endDate = endDate.format(apiDateFormatter)
            )) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        transactions = calculateDisplayItems(result.data ?: emptyList())
                    )
                    _actionState.value = TransactionsActionState()
                }
                is Resource.Error -> _actionState.value = TransactionsActionState(isError = true)
                is Resource.Loading -> Unit
            }
        }
    }

    private fun periodToDateRange(period: String): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        return when (period) {
            "Son 7 Gün" -> today.minusDays(6) to today
            "Son 15 Gün" -> today.minusDays(14) to today
            "Bu Ay" -> today.withDayOfMonth(1) to today
            "Son 1 Ay" -> today.minusMonths(1) to today
            "Son 3 Ay" -> today.minusMonths(3) to today
            "Son 6 Ay" -> today.minusMonths(6) to today
            "Bu Yıl" -> today.withDayOfYear(1) to today
            "Son 1 Yıl" -> today.minusYears(1) to today
            else -> today.withDayOfMonth(1) to today
        }
    }

    private fun calculateDisplayItems(transactions: List<Transaction>): List<TransactionDisplayItem> {
        val signedAmounts = transactions.map {
            if (it.type == TransactionType.INCOME) it.amount else -it.amount
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
