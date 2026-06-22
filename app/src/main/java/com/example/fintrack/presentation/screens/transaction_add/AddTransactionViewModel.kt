package com.example.fintrack.presentation.screens.transaction_add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.TransactionCategory
import com.example.fintrack.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(AddTransactionActionState())
    val actionState: StateFlow<AddTransactionActionState> = _actionState.asStateFlow()

    fun onTypeChange(index: Int) {
        _uiState.value = _uiState.value.copy(
            selectedTypeIndex = index,
            selectedCategory = null,
            amount = "",
            note = ""
        )
    }

    fun onCategoryChange(category: TransactionCategory) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            showValidationError = false
        )
    }

    fun onAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(
            amount = value,
            showValidationError = false
        )
    }

    fun onNoteChange(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun onDatePickerShow() {
        _uiState.value = _uiState.value.copy(
            showDatePicker = true,
            tempSelectedDate = _uiState.value.selectedDate ?: LocalDate.now()
        )
    }

    fun onDatePickerDismiss() {
        _uiState.value = _uiState.value.copy(showDatePicker = false)
    }

    fun onTempDateChange(date: LocalDate?) {
        _uiState.value = _uiState.value.copy(tempSelectedDate = date)
    }

    fun onMonthChange(month: YearMonth) {
        _uiState.value = _uiState.value.copy(currentMonth = month)
    }

    fun onDateConfirm() {
        _uiState.value = _uiState.value.copy(
            selectedDate = _uiState.value.tempSelectedDate,
            showDatePicker = false,
            showValidationError = false
        )
    }

    fun onTimePickerShow() {
        _uiState.value = _uiState.value.copy(
            showTimePicker = true,
            selectedTime = _uiState.value.selectedTime ?: LocalTime.now()
        )
    }

    fun onTimePickerDismiss() {
        _uiState.value = _uiState.value.copy(showTimePicker = false)
    }

    fun onTimeConfirm(time: LocalTime) {
        _uiState.value = _uiState.value.copy(
            selectedTime = time,
            showTimePicker = false,
            showValidationError = false
        )
    }

    fun onImageChange(uri: Uri?) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }

    fun onRecurringChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(isRecurring = value)
    }

    fun onReminderChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(isReminder = value)
    }

    fun addTransaction() {
        val state = _uiState.value

        if (state.selectedCategory == null || state.amount.isBlank() ||
            state.selectedDate == null || state.selectedTime == null
        ) {
            _uiState.value = state.copy(showValidationError = true)
            return
        }

        viewModelScope.launch {
            _actionState.value = AddTransactionActionState(isLoading = true)

            val result = addTransactionUseCase(
                type = if (state.selectedTypeIndex == 0) "EXPENSE" else "INCOME",
                category = state.selectedCategory.key,
                amount = state.amount.toDouble(),
                note = state.note.takeIf { it.isNotBlank() },
                date = state.selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                time = state.selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                recurring = state.isRecurring,
                reminder = state.isReminder
            )

            when (result) {
                is Resource.Success -> _actionState.value = AddTransactionActionState(isSuccess = true)
                is Resource.Error -> _actionState.value = AddTransactionActionState(isLoading = false)
                is Resource.Loading -> Unit
            }
        }
    }
}
