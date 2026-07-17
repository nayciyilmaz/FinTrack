package com.example.fintrack.presentation.screens.profile

enum class ProfileDialogType {
    NAME, EMAIL, PASSWORD
}

enum class SettingsDialogType {
    CURRENCY, LANGUAGE, FONT_SIZE
}

data class ProfileUiState(
    val activeDialog: ProfileDialogType? = null,
    val firstNameInput: String = "",
    val lastNameInput: String = "",
    val emailInput: String = "",
    val currentPasswordInput: String = "",
    val newPasswordInput: String = "",
    val confirmPasswordInput: String = "",
    val isCurrentPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val activeSettingsDialog: SettingsDialogType? = null,
    val selectedCurrencyOption: String = "",
    val selectedLanguageOption: String = "",
    val selectedFontSizeOption: String = ""
)