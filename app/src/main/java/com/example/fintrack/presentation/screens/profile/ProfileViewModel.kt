package com.example.fintrack.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.usecase.GetSavingsGoalsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getSavingsGoalsUseCase: GetSavingsGoalsUseCase
) : ViewModel() {

    private val _actionState = MutableStateFlow(ProfileActionState())
    val actionState: StateFlow<ProfileActionState> = _actionState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isLoading = true, isError = false)

            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val today = LocalDate.now()

            val profileDeferred = async { getUserProfileUseCase() }
            val transactionsDeferred = async {
                getTransactionsUseCase(
                    type = null,
                    startDate = LocalDate.of(2020, 1, 1).format(formatter),
                    endDate = today.format(formatter)
                )
            }
            val goalsDeferred = async { getSavingsGoalsUseCase() }

            val profileResult = profileDeferred.await()
            val transactionsResult = transactionsDeferred.await()
            val goalsResult = goalsDeferred.await()

            if (profileResult is Resource.Success && transactionsResult is Resource.Success && goalsResult is Resource.Success) {
                val profile = profileResult.data
                val transactions = transactionsResult.data ?: emptyList()
                val goals = goalsResult.data ?: emptyList()

                if (profile != null) {
                    val initials = buildString {
                        profile.firstName.firstOrNull()?.let { append(it.uppercaseChar()) }
                        profile.lastName.firstOrNull()?.let { append(it.uppercaseChar()) }
                    }
                    val usageDays = ChronoUnit.DAYS.between(profile.createdAt, today).toInt().coerceAtLeast(0)
                    val usageMonths = ChronoUnit.MONTHS.between(profile.createdAt, today).toInt().coerceAtLeast(0)

                    _actionState.value = ProfileActionState(
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        email = profile.email,
                        initials = initials,
                        transactionCount = transactions.size,
                        activeGoalsCount = goals.size,
                        usageDays = usageDays,
                        usageMonths = usageMonths,
                        showUsageInDays = usageDays < 90
                    )
                } else {
                    _actionState.value = ProfileActionState(isError = true)
                }
            } else {
                _actionState.value = ProfileActionState(isError = true)
            }
        }
    }
}
