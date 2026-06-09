package com.example.fintrack.presentation.screens.sign_in

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val validationErrors: SignInValidationErrors = SignInValidationErrors()
)
