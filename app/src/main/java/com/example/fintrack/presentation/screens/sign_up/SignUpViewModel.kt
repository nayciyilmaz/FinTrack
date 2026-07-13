package com.example.fintrack.presentation.screens.sign_up

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.usecase.AddTransactionUseCase
import com.example.fintrack.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(SignUpActionState())
    val actionState: StateFlow<SignUpActionState> = _actionState.asStateFlow()

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            firstName = value,
            validationErrors = _uiState.value.validationErrors.copy(firstNameError = null)
        )
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            lastName = value,
            validationErrors = _uiState.value.validationErrors.copy(lastNameError = null)
        )
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            validationErrors = _uiState.value.validationErrors.copy(emailError = null)
        )
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            validationErrors = _uiState.value.validationErrors.copy(passwordError = null)
        )
    }

    fun onPaydayChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(2)
        _uiState.value = _uiState.value.copy(
            payday = filtered,
            validationErrors = _uiState.value.validationErrors.copy(paydayError = null)
        )
    }

    fun onSalaryChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' }
        _uiState.value = _uiState.value.copy(
            salary = filtered,
            validationErrors = _uiState.value.validationErrors.copy(salaryError = null)
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun register() {
        val payday = _uiState.value.payday.toIntOrNull() ?: 0
        val salary = _uiState.value.salary.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _actionState.value = SignUpActionState(isLoading = true)
            _uiState.value = _uiState.value.copy(validationErrors = SignUpValidationErrors())

            val result = registerUseCase(
                firstName = _uiState.value.firstName.trim(),
                lastName = _uiState.value.lastName.trim(),
                email = _uiState.value.email.trim(),
                password = _uiState.value.password,
                payday = payday,
                salary = salary
            )

            when (result) {
                is Resource.Success -> {
                    if (salary > 0) {
                        createSalaryTransaction(payday, salary)
                    }
                    _actionState.value = SignUpActionState(isSuccess = true)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        validationErrors = mapErrorToValidation(result.message, result.fieldErrors)
                    )
                    _actionState.value = SignUpActionState(isLoading = false)
                }
                is Resource.Loading -> {
                    _actionState.value = SignUpActionState(isLoading = true)
                }
            }
        }
    }

    private suspend fun createSalaryTransaction(payday: Int, salary: Double) {
        val today = LocalDate.now()
        val targetMonth = if (today.dayOfMonth < payday) today.minusMonths(1) else today
        val maxDay = targetMonth.lengthOfMonth()
        val adjustedPayday = payday.coerceAtMost(maxDay)
        val salaryDate = targetMonth.withDayOfMonth(adjustedPayday)
        val dateStr = salaryDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val timeStr = LocalTime.of(9, 0).format(DateTimeFormatter.ISO_LOCAL_TIME)

        addTransactionUseCase(
            type = "INCOME",
            category = "SALARY",
            amount = salary,
            note = null,
            date = dateStr,
            time = timeStr,
            recurring = true,
            reminder = false
        )
    }

    private fun mapErrorToValidation(
        message: String?,
        fieldErrors: Map<String, String>?
    ): SignUpValidationErrors {
        return if (!fieldErrors.isNullOrEmpty()) {
            SignUpValidationErrors(
                firstNameError = fieldErrors["first_name"],
                lastNameError = fieldErrors["last_name"],
                emailError = fieldErrors["email"],
                passwordError = fieldErrors["password"],
                paydayError = fieldErrors["payday"],
                salaryError = fieldErrors["salary"]
            )
        } else {
            SignUpValidationErrors(emailError = message ?: context.getString(R.string.sign_up_error_generic))
        }
    }
}
