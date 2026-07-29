package com.example.fintrack.presentation.screens.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.usecase.GetRecurringItemsUseCase
import com.example.fintrack.domain.usecase.GetReminderTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentRemindersViewModel @Inject constructor(
    private val getRecurringItemsUseCase: GetRecurringItemsUseCase,
    private val getReminderTransactionsUseCase: GetReminderTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentRemindersUiState())
    val uiState: StateFlow<PaymentRemindersUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(PaymentRemindersActionState())
    val actionState: StateFlow<PaymentRemindersActionState> = _actionState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isLoading = true, isError = false)

            val recurringDeferred = async { getRecurringItemsUseCase() }
            val remindersDeferred = async { getReminderTransactionsUseCase() }

            val recurringResult = recurringDeferred.await()
            val remindersResult = remindersDeferred.await()

            if (recurringResult is Resource.Success && remindersResult is Resource.Success) {
                _actionState.value = _actionState.value.copy(
                    isLoading = false,
                    isError = false,
                    recurringItems = recurringResult.data ?: emptyList(),
                    reminderTransactions = remindersResult.data ?: emptyList()
                )
            } else {
                _actionState.value = _actionState.value.copy(isLoading = false, isError = true)
            }
        }
    }

    fun onFilterChange(index: Int) {
        _uiState.value = _uiState.value.copy(selectedFilterIndex = index)
    }
}
