package com.example.fintrack.presentation.screens.transaction_update

import com.example.fintrack.domain.model.TransactionCategory
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

data class UpdateTransactionUiState(
    val selectedTypeIndex: Int = 0,
    val selectedCategory: TransactionCategory? = null,
    val amount: String = "",
    val note: String = "",
    val selectedDate: LocalDate? = null,
    val tempSelectedDate: LocalDate? = null,
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedTime: LocalTime? = null,
    val isRecurring: Boolean = false,
    val isReminder: Boolean = false,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val showValidationError: Boolean = false,
    val showDeleteDialog: Boolean = false
)
