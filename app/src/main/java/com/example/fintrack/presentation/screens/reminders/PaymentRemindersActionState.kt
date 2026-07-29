package com.example.fintrack.presentation.screens.reminders

import com.example.fintrack.domain.model.RecurringItem
import com.example.fintrack.domain.model.Transaction

data class PaymentRemindersActionState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val recurringItems: List<RecurringItem> = emptyList(),
    val reminderTransactions: List<Transaction> = emptyList()
)
