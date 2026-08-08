package com.example.fintrack.presentation.screens.transaction_add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.constants.expenseCategories
import com.example.fintrack.core.constants.incomeCategories
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.presentation.model.TransactionCategory
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.AddTransactionUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(AddTransactionActionState())
    val actionState: StateFlow<AddTransactionActionState> = _actionState.asStateFlow()

    init {
        loadQuickAddSuggestions()
    }

    fun onQuickAddSelected(transaction: Transaction) {
        val categories = if (transaction.type == TransactionType.INCOME) incomeCategories else expenseCategories
        val category = categories.find { it.key == transaction.category } ?: return

        _uiState.value = _uiState.value.copy(
            selectedTypeIndex = if (transaction.type == TransactionType.INCOME) 1 else 0,
            selectedCategory = category,
            amount = transaction.amount.toInt().toString(),
            note = transaction.note ?: "",
            selectedDate = LocalDate.now(),
            selectedTime = LocalTime.now(),
            isRecurring = transaction.isRecurring,
            isReminder = transaction.isReminder,
            showValidationError = false
        )
    }

    private fun loadQuickAddSuggestions() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE

            val result = getTransactionsUseCase(
                type = null,
                startDate = today.minusDays(QUICK_ADD_LOOKBACK_DAYS).format(formatter),
                endDate = today.format(formatter)
            )

            if (result is Resource.Success) {
                _uiState.value = _uiState.value.copy(
                    quickAddSuggestions = calculateQuickAddSuggestions(result.data ?: emptyList())
                )
            }
        }
    }

    private fun calculateQuickAddSuggestions(transactions: List<Transaction>): List<Transaction> {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        return transactions
            .groupBy { Triple(it.type, it.category, it.amount) }
            .map { (_, group) ->
                val mostRecent = group.maxBy { LocalDate.parse(it.date, formatter) }
                val daysSinceLastSeen = ChronoUnit.DAYS.between(LocalDate.parse(mostRecent.date, formatter), today)
                val score = group.size.toDouble() / (daysSinceLastSeen + 1)
                mostRecent to score
            }
            .sortedByDescending { it.second }
            .take(QUICK_ADD_SUGGESTION_LIMIT)
            .map { it.first }
    }

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
                type = if (state.selectedTypeIndex == 0) TransactionType.EXPENSE else TransactionType.INCOME,
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

    private companion object {
        const val QUICK_ADD_LOOKBACK_DAYS = 90L
        const val QUICK_ADD_SUGGESTION_LIMIT = 5
    }
}
