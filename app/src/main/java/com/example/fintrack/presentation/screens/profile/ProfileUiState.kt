package com.example.fintrack.presentation.screens.profile

enum class ProfileDialogType {
    NAME, EMAIL, PASSWORD
}

enum class SettingsDialogType {
    CURRENCY, LANGUAGE, FONT_SIZE
}

data class ProfileEditState(
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
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null
)

data class SettingsDialogState(
    val activeSettingsDialog: SettingsDialogType? = null,
    val selectedCurrencyOption: String = "",
    val selectedLanguageOption: String = "",
    val selectedFontSizeOption: String = ""
)

data class ProfileUiState(
    val editState: ProfileEditState = ProfileEditState(),
    val settingsDialogState: SettingsDialogState = SettingsDialogState(),
    val showLogoutConfirmDialog: Boolean = false
)
