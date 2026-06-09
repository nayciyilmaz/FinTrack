package com.example.fintrack.presentation.screens.sign_in

data class SignInActionState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

data class SignInValidationErrors(
    val emailError: String? = null,
    val passwordError: String? = null
)
