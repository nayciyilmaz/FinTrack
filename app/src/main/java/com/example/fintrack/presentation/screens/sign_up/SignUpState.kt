package com.example.fintrack.presentation.screens.sign_up

data class SignUpUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val validationErrors: SignUpValidationErrors = SignUpValidationErrors()
)
