package com.example.fintrack.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.presentation.screens.transactions.TransactionDisplayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _recentTransactions = MutableStateFlow<List<TransactionDisplayItem>>(emptyList())
    val recentTransactions: StateFlow<List<TransactionDisplayItem>> = _recentTransactions.asStateFlow()

    private val _actionState = MutableStateFlow(HomeActionState())
    val actionState: StateFlow<HomeActionState> = _actionState.asStateFlow()

    init {
        loadRecentTransactions()
    }

    fun loadRecentTransactions() {
        viewModelScope.launch {
            _actionState.value = HomeActionState(isLoading = true)

            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            when (val result = getTransactionsUseCase(
                type = null,
                startDate = today.minusYears(1).format(formatter),
                endDate = today.format(formatter)
            )) {
                is Resource.Success -> {
                    val transactions = result.data ?: emptyList()
                    _recentTransactions.value = calculateDisplayItems(transactions).take(3)
                    _actionState.value = HomeActionState()
                }
                is Resource.Error -> {
                    _actionState.value = HomeActionState(isError = true)
                }
                is Resource.Loading -> Unit
            }
        }
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
