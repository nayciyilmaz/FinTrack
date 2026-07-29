package com.example.fintrack.presentation.screens.transaction_update

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.constants.expenseCategories
import com.example.fintrack.core.constants.incomeCategories
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.TransactionCategory
import com.example.fintrack.domain.usecase.DeleteRecurringItemUseCase
import com.example.fintrack.domain.usecase.DeleteTransactionUseCase
import com.example.fintrack.domain.usecase.UpdateRecurringItemUseCase
import com.example.fintrack.domain.usecase.UpdateTransactionUseCase
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
class UpdateTransactionViewModel @Inject constructor(
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val updateRecurringItemUseCase: UpdateRecurringItemUseCase,
    private val deleteRecurringItemUseCase: DeleteRecurringItemUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionId: Long = savedStateHandle.get<Long>("transactionId") ?: 0L
    private val recurringItemId: Long = savedStateHandle.get<Long>("recurringItemId") ?: 0L
    private val isRecurringItemEdit: Boolean = recurringItemId > 0L
    private val allCategories = expenseCategories + incomeCategories
    private val initialType = savedStateHandle.get<String>("type") ?: "EXPENSE"
    private val initialCategory = savedStateHandle.get<String>("category") ?: ""
    private val initialAmount = savedStateHandle.get<String>("amount") ?: ""
    private val initialDate = savedStateHandle.get<String>("date") ?: ""
    private val initialTime = savedStateHandle.get<String>("time") ?: ""
    private val initialIsRecurring = savedStateHandle.get<String>("isRecurring") == "true"
    private val initialIsReminder = savedStateHandle.get<String>("isReminder") == "true"
    private val initialNote = savedStateHandle.get<String>("note") ?: ""
    private val parsedDate = if (initialDate.isNotEmpty()) LocalDate.parse(initialDate) else null
    private val parsedTime = if (initialTime.isNotEmpty()) LocalTime.parse(initialTime) else null

    private val _uiState = MutableStateFlow(
        UpdateTransactionUiState(
            selectedTypeIndex = if (initialType == "INCOME") 1 else 0,
            selectedCategory = allCategories.find { it.key == initialCategory },
            amount = initialAmount,
            note = initialNote,
            selectedDate = parsedDate,
            currentMonth = if (parsedDate != null) YearMonth.from(parsedDate) else YearMonth.now(),
            selectedTime = parsedTime,
            isRecurring = initialIsRecurring,
            isReminder = initialIsReminder
        )
    )
    val uiState: StateFlow<UpdateTransactionUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(UpdateTransactionActionState())
    val actionState: StateFlow<UpdateTransactionActionState> = _actionState.asStateFlow()

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

    fun onRecurringChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(isRecurring = value)
    }

    fun onReminderChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(isReminder = value)
    }

    fun onShowDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }

    fun onDismissDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }

    fun updateTransaction() {
        val state = _uiState.value
        if (state.selectedCategory == null || state.amount.isBlank() ||
            state.selectedDate == null || state.selectedTime == null
        ) {
            _uiState.value = state.copy(showValidationError = true)
            return
        }
        viewModelScope.launch {
            _actionState.value = UpdateTransactionActionState(isLoading = true)

            if (isRecurringItemEdit) {
                when (
                    val result = updateRecurringItemUseCase(
                        id = recurringItemId,
                        amount = state.amount.toDouble(),
                        dayOfMonth = state.selectedDate!!.dayOfMonth
                    )
                ) {
                    is Resource.Success -> _actionState.value = UpdateTransactionActionState(isSuccess = true)
                    is Resource.Error -> _actionState.value = UpdateTransactionActionState(isError = true)
                    is Resource.Loading -> Unit
                }
                return@launch
            }

            when (
                val result = updateTransactionUseCase(
                    id = transactionId,
                    type = if (state.selectedTypeIndex == 0) "EXPENSE" else "INCOME",
                    category = state.selectedCategory!!.key,
                    amount = state.amount.toDouble(),
                    note = state.note.takeIf { it.isNotBlank() },
                    date = state.selectedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    time = state.selectedTime!!.format(DateTimeFormatter.ofPattern("HH:mm")),
                    recurring = state.isRecurring,
                    reminder = state.isReminder
                )
            ) {
                is Resource.Success -> _actionState.value = UpdateTransactionActionState(isSuccess = true)
                is Resource.Error -> _actionState.value = UpdateTransactionActionState(isError = true)
                is Resource.Loading -> Unit
            }
        }
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            _actionState.value = UpdateTransactionActionState(isLoading = true)

            if (isRecurringItemEdit) {
                when (val result = deleteRecurringItemUseCase(recurringItemId)) {
                    is Resource.Success -> _actionState.value = UpdateTransactionActionState(isSuccess = true)
                    is Resource.Error -> _actionState.value = UpdateTransactionActionState(isError = true)
                    is Resource.Loading -> Unit
                }
                return@launch
            }

            when (val result = deleteTransactionUseCase(transactionId)) {
                is Resource.Success -> _actionState.value = UpdateTransactionActionState(isSuccess = true)
                is Resource.Error -> _actionState.value = UpdateTransactionActionState(isError = true)
                is Resource.Loading -> Unit
            }
        }
    }
}
