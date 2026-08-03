package com.example.fintrack.presentation.screens.savings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.R
import com.example.fintrack.core.util.LocaleHelper
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.domain.usecase.AddSavingsGoalUseCase
import com.example.fintrack.domain.usecase.DeleteSavingsGoalUseCase
import com.example.fintrack.domain.usecase.GetSavingsGoalsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.UpdateSavingsGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SavingsGoalsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getSavingsGoalsUseCase: GetSavingsGoalsUseCase,
    private val addSavingsGoalUseCase: AddSavingsGoalUseCase,
    private val updateSavingsGoalUseCase: UpdateSavingsGoalUseCase,
    private val deleteSavingsGoalUseCase: DeleteSavingsGoalUseCase,
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavingsGoalsUiState())
    val uiState: StateFlow<SavingsGoalsUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(SavingsGoalsActionState())
    val actionState: StateFlow<SavingsGoalsActionState> = _actionState.asStateFlow()

    fun onGoalNameChange(value: String) {
        _uiState.value = _uiState.value.copy(goalName = value, nameError = null)
    }

    fun onTargetAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(
            targetAmount = value.filter { it.isDigit() },
            amountError = null
        )
    }

    fun onCategoryChange(resId: Int, name: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategoryResId = resId,
            selectedCategoryName = name,
            categoryError = null
        )
    }

    fun onAddAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(
            addAmount = value.filter { it.isDigit() },
            updateError = null
        )
    }

    fun onNewTargetAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(
            newTargetAmount = value.filter { it.isDigit() },
            updateError = null
        )
    }

    fun showAddDialog() {
        _uiState.value = SavingsGoalsUiState(showAddDialog = true)
    }

    fun dismissAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
    }

    fun selectGoal(goal: SavingsGoal) {
        _uiState.value = SavingsGoalsUiState(selectedGoal = goal)
    }

    fun dismissGoalDetail() {
        _uiState.value = _uiState.value.copy(selectedGoal = null)
    }

    fun onRequestDeleteConfirm() {
        _uiState.value = _uiState.value.copy(isConfirmingDelete = true)
    }

    fun onCancelDeleteConfirm() {
        _uiState.value = _uiState.value.copy(isConfirmingDelete = false)
    }

    fun loadData() {
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isLoading = true, isError = false)

            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val payday = tokenManager.getPayday().first()
            val (currentPeriodStart, _) = calculatePeriodDates(payday, today)

            val prevMonth = currentPeriodStart.minusMonths(1)
            val prevPeriodStart = prevMonth.withDayOfMonth(minOf(payday, prevMonth.lengthOfMonth()))

            val prevPeriodDeferred = async {
                getTransactionsUseCase(
                    type = null,
                    startDate = prevPeriodStart.format(formatter),
                    endDate = currentPeriodStart.minusDays(1).format(formatter)
                )
            }

            val allTransactionsDeferred = async {
                getTransactionsUseCase(
                    type = null,
                    startDate = LocalDate.of(2020, 1, 1).format(formatter),
                    endDate = currentPeriodStart.minusDays(1).format(formatter)
                )
            }

            val goalsDeferred = async {
                getSavingsGoalsUseCase()
            }

            val prevResult = prevPeriodDeferred.await()
            val allResult = allTransactionsDeferred.await()
            val goalsResult = goalsDeferred.await()

            if (prevResult is Resource.Success && allResult is Resource.Success && goalsResult is Resource.Success) {
                val prevTransactions = prevResult.data ?: emptyList()
                val allTransactions = allResult.data ?: emptyList()
                val goals = goalsResult.data ?: emptyList()

                val prevIncome = prevTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }.toInt()
                val prevExpense = prevTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }.toInt()
                val lastMonthSavings = prevIncome - prevExpense

                val totalIncome = allTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }.toInt()
                val totalExpense = allTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }.toInt()
                val totalSavings = totalIncome - totalExpense

                _actionState.value = SavingsGoalsActionState(
                    lastMonthSavings = lastMonthSavings,
                    totalSavings = totalSavings,
                    goals = goals,
                    estimatedDates = goals.associate { it.id to calculateEstimatedDate(it) }
                )
            } else {
                _actionState.value = SavingsGoalsActionState(isError = true)
            }
        }
    }

    fun addGoal() {
        val state = _uiState.value
        var hasError = false
        var updated = state

        if (state.selectedCategoryName == null) {
            updated = updated.copy(categoryError = context.getString(R.string.savings_goal_error_category_required))
            hasError = true
        }
        if (state.goalName.isBlank()) {
            updated = updated.copy(nameError = context.getString(R.string.savings_goal_error_name_required))
            hasError = true
        }
        val amount = state.targetAmount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            updated = updated.copy(amountError = context.getString(R.string.savings_goal_error_invalid_amount))
            hasError = true
        }
        if (hasError) {
            _uiState.value = updated
            return
        }

        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isSaving = true)
            when (val result = addSavingsGoalUseCase(state.goalName.trim(), state.selectedCategoryName!!, amount!!)) {
                is Resource.Success -> {
                    val newGoal = result.data
                    if (newGoal != null) {
                        val updatedGoals = listOf(newGoal) + _actionState.value.goals
                        _actionState.value = _actionState.value.copy(
                            goals = updatedGoals,
                            estimatedDates = updatedGoals.associate { it.id to calculateEstimatedDate(it) },
                            isSaving = false
                        )
                        _uiState.value = SavingsGoalsUiState()
                    }
                }
                is Resource.Error -> {
                    _actionState.value = _actionState.value.copy(isSaving = false)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun updateGoal() {
        val state = _uiState.value
        val goalId = state.selectedGoal?.id ?: return

        val add = state.addAmount.toDoubleOrNull()
        val newTarget = state.newTargetAmount.toDoubleOrNull()

        if (add == null && newTarget == null) {
            _uiState.value = state.copy(updateError = context.getString(R.string.savings_goal_error_fields_required))
            return
        }
        if (add != null && add <= 0) {
            _uiState.value = state.copy(updateError = context.getString(R.string.savings_goal_error_invalid_amount))
            return
        }
        if (newTarget != null && newTarget <= 0) {
            _uiState.value = state.copy(updateError = context.getString(R.string.savings_goal_error_invalid_target_amount))
            return
        }

        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isSaving = true)
            when (val result = updateSavingsGoalUseCase(goalId, add, newTarget)) {
                is Resource.Success -> {
                    val updatedGoal = result.data
                    if (updatedGoal != null) {
                        val updatedGoals = _actionState.value.goals.map {
                            if (it.id == goalId) updatedGoal else it
                        }
                        _actionState.value = _actionState.value.copy(
                            goals = updatedGoals,
                            estimatedDates = updatedGoals.associate { it.id to calculateEstimatedDate(it) },
                            isSaving = false
                        )
                        _uiState.value = SavingsGoalsUiState()
                    }
                }
                is Resource.Error -> {
                    _actionState.value = _actionState.value.copy(isSaving = false)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun deleteGoal() {
        val goalId = _uiState.value.selectedGoal?.id ?: return

        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isSaving = true)
            when (deleteSavingsGoalUseCase(goalId)) {
                is Resource.Success -> {
                    val updatedGoals = _actionState.value.goals.filter { it.id != goalId }
                    _actionState.value = _actionState.value.copy(
                        goals = updatedGoals,
                        estimatedDates = updatedGoals.associate { it.id to calculateEstimatedDate(it) },
                        isSaving = false
                    )
                    _uiState.value = SavingsGoalsUiState()
                }
                is Resource.Error -> {
                    _actionState.value = _actionState.value.copy(isSaving = false)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun calculateEstimatedDate(goal: SavingsGoal): String? {
        if (goal.currentAmount <= 0) return null
        if (goal.currentAmount >= goal.targetAmount) return null

        val createdAt = LocalDate.parse(goal.createdAt.substring(0, 10))
        val today = LocalDate.now()
        val monthsSinceCreation = ChronoUnit.MONTHS.between(createdAt, today).coerceAtLeast(1)
        val monthlyRate = goal.currentAmount / monthsSinceCreation
        if (monthlyRate <= 0) return null

        val remaining = goal.targetAmount - goal.currentAmount
        val estimatedMonths = (remaining / monthlyRate).toLong()
        val estimatedDate = today.plusMonths(estimatedMonths)

        val locale = LocaleHelper.getLocale(context)
        val month = estimatedDate.month.getDisplayName(TextStyle.SHORT, locale)
            .replaceFirstChar { it.uppercase(locale) }
        return "$month. ${estimatedDate.year}"
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
