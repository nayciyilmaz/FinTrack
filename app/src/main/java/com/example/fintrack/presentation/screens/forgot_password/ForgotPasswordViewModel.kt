package com.example.fintrack.presentation.screens.forgot_password

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.usecase.ResetPasswordUseCase
import com.example.fintrack.domain.usecase.SendPasswordResetCodeUseCase
import com.example.fintrack.domain.usecase.VerifyPasswordResetCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val sendPasswordResetCodeUseCase: SendPasswordResetCodeUseCase,
    private val verifyPasswordResetCodeUseCase: VerifyPasswordResetCodeUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(ForgotPasswordActionState())
    val actionState: StateFlow<ForgotPasswordActionState> = _actionState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            validationErrors = _uiState.value.validationErrors.copy(emailError = null)
        )
    }

    fun sendCode() {
        viewModelScope.launch {
            _actionState.value = ForgotPasswordActionState(isLoading = true)
            _uiState.value = _uiState.value.copy(validationErrors = ForgotPasswordValidationErrors())

            when (val result = sendPasswordResetCodeUseCase(_uiState.value.email.trim())) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(step = ForgotPasswordStep.CODE)
                    _actionState.value = ForgotPasswordActionState()
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        validationErrors = ForgotPasswordValidationErrors(
                            emailError = result.fieldErrors?.get("email") ?: result.message
                            ?: context.getString(R.string.forgot_password_error_generic)
                        )
                    )
                    _actionState.value = ForgotPasswordActionState(isLoading = false)
                }
                is Resource.Loading -> _actionState.value = ForgotPasswordActionState(isLoading = true)
            }
        }
    }

    fun onCodeDigitChange(index: Int, value: String) {
        val updatedDigits = _uiState.value.codeDigits.toMutableList()
        updatedDigits[index] = value
        _uiState.value = _uiState.value.copy(
            codeDigits = updatedDigits,
            validationErrors = _uiState.value.validationErrors.copy(codeError = null)
        )
    }

    fun verifyCode() {
        val code = _uiState.value.codeDigits.joinToString("")

        viewModelScope.launch {
            _actionState.value = ForgotPasswordActionState(isLoading = true)
            _uiState.value = _uiState.value.copy(validationErrors = ForgotPasswordValidationErrors())

            when (val result = verifyPasswordResetCodeUseCase(_uiState.value.email.trim(), code)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        resetToken = result.data.orEmpty(),
                        step = ForgotPasswordStep.NEW_PASSWORD
                    )
                    _actionState.value = ForgotPasswordActionState()
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        validationErrors = ForgotPasswordValidationErrors(
                            codeError = result.fieldErrors?.get("code") ?: result.message
                            ?: context.getString(R.string.forgot_password_error_generic)
                        )
                    )
                    _actionState.value = ForgotPasswordActionState(isLoading = false)
                }
                is Resource.Loading -> _actionState.value = ForgotPasswordActionState(isLoading = true)
            }
        }
    }

    fun goBackToEmail() {
        _uiState.value = _uiState.value.copy(
            step = ForgotPasswordStep.EMAIL,
            validationErrors = ForgotPasswordValidationErrors()
        )
    }

    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            newPassword = value,
            validationErrors = _uiState.value.validationErrors.copy(newPasswordError = null)
        )
    }

    fun toggleNewPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isNewPasswordVisible = !_uiState.value.isNewPasswordVisible)
    }

    fun onNewPasswordRepeatChange(value: String) {
        _uiState.value = _uiState.value.copy(
            newPasswordRepeat = value,
            validationErrors = _uiState.value.validationErrors.copy(confirmPasswordError = null)
        )
    }

    fun toggleNewPasswordRepeatVisibility() {
        _uiState.value = _uiState.value.copy(isNewPasswordRepeatVisible = !_uiState.value.isNewPasswordRepeatVisible)
    }

    fun goBackToCode() {
        _uiState.value = _uiState.value.copy(
            step = ForgotPasswordStep.CODE,
            validationErrors = ForgotPasswordValidationErrors()
        )
    }

    fun resetPassword() {
        val newPassword = _uiState.value.newPassword
        val newPasswordRepeat = _uiState.value.newPasswordRepeat

        if (newPassword != newPasswordRepeat) {
            _uiState.value = _uiState.value.copy(
                validationErrors = _uiState.value.validationErrors.copy(
                    confirmPasswordError = context.getString(R.string.profile_password_mismatch)
                )
            )
            return
        }

        viewModelScope.launch {
            _actionState.value = ForgotPasswordActionState(isLoading = true)
            _uiState.value = _uiState.value.copy(
                validationErrors = _uiState.value.validationErrors.copy(
                    newPasswordError = null,
                    confirmPasswordError = null
                )
            )

            when (val result = resetPasswordUseCase(_uiState.value.resetToken, newPassword)) {
                is Resource.Success -> _actionState.value = ForgotPasswordActionState(isSuccess = true)
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        validationErrors = _uiState.value.validationErrors.copy(
                            newPasswordError = result.fieldErrors?.get("new_password") ?: result.message
                            ?: context.getString(R.string.forgot_password_error_generic)
                        )
                    )
                    _actionState.value = ForgotPasswordActionState(isLoading = false)
                }
                is Resource.Loading -> _actionState.value = ForgotPasswordActionState(isLoading = true)
            }
        }
    }
}
