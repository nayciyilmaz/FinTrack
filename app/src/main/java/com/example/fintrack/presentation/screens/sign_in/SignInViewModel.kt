package com.example.fintrack.presentation.screens.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(SignInActionState())
    val actionState: StateFlow<SignInActionState> = _actionState.asStateFlow()

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
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun login() {
        viewModelScope.launch {
            _actionState.value = SignInActionState(isLoading = true)
            _uiState.value = _uiState.value.copy(validationErrors = SignInValidationErrors())
            val result = loginUseCase(
                email = _uiState.value.email.trim(),
                password = _uiState.value.password
            )
            when (result) {
                is Resource.Success -> _actionState.value = SignInActionState(isSuccess = true)
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        validationErrors = mapErrorToValidation(result.message, result.fieldErrors)
                    )
                    _actionState.value = SignInActionState(isLoading = false)
                }
                is Resource.Loading -> _actionState.value = SignInActionState(isLoading = true)
            }
        }
    }

    private fun mapErrorToValidation(
        message: String?,
        fieldErrors: Map<String, String>?
    ): SignInValidationErrors {
        return if (!fieldErrors.isNullOrEmpty()) {
            SignInValidationErrors(
                emailError = fieldErrors["email"],
                passwordError = fieldErrors["password"]
            )
        } else {
            SignInValidationErrors(emailError = message ?: "Giriş başarısız.")
        }
    }
}
