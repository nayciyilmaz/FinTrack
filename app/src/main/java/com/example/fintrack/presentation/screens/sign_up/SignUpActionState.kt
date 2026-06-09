package com.example.fintrack.presentation.screens.sign_up

data class SignUpActionState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

data class SignUpValidationErrors(
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)
