package com.example.fintrack.presentation.screens.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
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

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun register() {
        viewModelScope.launch {
            _actionState.value = SignUpActionState(isLoading = true)
            _uiState.value = _uiState.value.copy(validationErrors = SignUpValidationErrors())

            val result = registerUseCase(
                firstName = _uiState.value.firstName.trim(),
                lastName = _uiState.value.lastName.trim(),
                email = _uiState.value.email.trim(),
                password = _uiState.value.password
            )

            when (result) {
                is Resource.Success -> {
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

    private fun mapErrorToValidation(
        message: String?,
        fieldErrors: Map<String, String>?
    ): SignUpValidationErrors {
        return if (!fieldErrors.isNullOrEmpty()) {
            SignUpValidationErrors(
                firstNameError = fieldErrors["first_name"],
                lastNameError = fieldErrors["last_name"],
                emailError = fieldErrors["email"],
                passwordError = fieldErrors["password"]
            )
        } else {
            SignUpValidationErrors(emailError = message ?: "Kayıt başarısız.")
        }
    }
}
