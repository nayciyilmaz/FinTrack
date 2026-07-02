package com.example.fintrack.presentation.screens.transactions

import com.example.fintrack.domain.model.Transaction

data class TransactionsUiState(
    val selectedFilterIndex: Int = 0,
    val selectedPeriod: String = "Son 7 Gün",
    val transactions: List<TransactionDisplayItem> = emptyList()
)

data class TransactionDisplayItem(
    val transaction: Transaction,
    val remainingBalance: Double
)
