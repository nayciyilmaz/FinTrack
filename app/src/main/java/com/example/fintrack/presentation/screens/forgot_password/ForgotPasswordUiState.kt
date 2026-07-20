package com.example.fintrack.presentation.screens.forgot_password

enum class ForgotPasswordStep {
    EMAIL, CODE, NEW_PASSWORD
}

data class ForgotPasswordValidationErrors(
    val emailError: String? = null,
    val codeError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null
)

data class ForgotPasswordUiState(
    val step: ForgotPasswordStep = ForgotPasswordStep.EMAIL,
    val email: String = "",
    val codeDigits: List<String> = List(6) { "" },
    val resetToken: String = "",
    val newPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val newPasswordRepeat: String = "",
    val isNewPasswordRepeatVisible: Boolean = false,
    val validationErrors: ForgotPasswordValidationErrors = ForgotPasswordValidationErrors()
)
