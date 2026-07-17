package com.example.fintrack.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.usecase.GetSavingsGoalsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getSavingsGoalsUseCase: GetSavingsGoalsUseCase
) : ViewModel() {

    private val _actionState = MutableStateFlow(ProfileActionState())
    val actionState: StateFlow<ProfileActionState> = _actionState.asStateFlow()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onShowNameDialog() {
        _uiState.value = ProfileUiState(
            activeDialog = ProfileDialogType.NAME,
            firstNameInput = _actionState.value.firstName,
            lastNameInput = _actionState.value.lastName
        )
    }

    fun onShowEmailDialog() {
        _uiState.value = ProfileUiState(
            activeDialog = ProfileDialogType.EMAIL,
            emailInput = _actionState.value.email
        )
    }

    fun onShowPasswordDialog() {
        _uiState.value = ProfileUiState(activeDialog = ProfileDialogType.PASSWORD)
    }

    fun onDismissDialog() {
        _uiState.value = _uiState.value.copy(activeDialog = null)
    }

    fun onFirstNameInputChange(value: String) {
        _uiState.value = _uiState.value.copy(firstNameInput = value)
    }

    fun onLastNameInputChange(value: String) {
        _uiState.value = _uiState.value.copy(lastNameInput = value)
    }

    fun onEmailInputChange(value: String) {
        _uiState.value = _uiState.value.copy(emailInput = value)
    }

    fun onCurrentPasswordInputChange(value: String) {
        _uiState.value = _uiState.value.copy(currentPasswordInput = value)
    }

    fun onNewPasswordInputChange(value: String) {
        _uiState.value = _uiState.value.copy(newPasswordInput = value)
    }

    fun onConfirmPasswordInputChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPasswordInput = value)
    }

    fun toggleCurrentPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isCurrentPasswordVisible = !_uiState.value.isCurrentPasswordVisible)
    }

    fun toggleNewPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isNewPasswordVisible = !_uiState.value.isNewPasswordVisible)
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible)
    }

    fun onShowCurrencyDialog() {
        _uiState.value = _uiState.value.copy(
            activeSettingsDialog = SettingsDialogType.CURRENCY,
            selectedCurrencyOption = "₺ TRY"
        )
    }

    fun onShowLanguageDialog() {
        _uiState.value = _uiState.value.copy(
            activeSettingsDialog = SettingsDialogType.LANGUAGE,
            selectedLanguageOption = "Türkçe"
        )
    }

    fun onShowFontSizeDialog() {
        _uiState.value = _uiState.value.copy(
            activeSettingsDialog = SettingsDialogType.FONT_SIZE,
            selectedFontSizeOption = "Orta"
        )
    }

    fun onDismissSettingsDialog() {
        _uiState.value = _uiState.value.copy(activeSettingsDialog = null)
    }

    fun onCurrencyOptionSelect(value: String) {
        _uiState.value = _uiState.value.copy(selectedCurrencyOption = value)
    }

    fun onLanguageOptionSelect(value: String) {
        _uiState.value = _uiState.value.copy(selectedLanguageOption = value)
    }

    fun onFontSizeOptionSelect(value: String) {
        _uiState.value = _uiState.value.copy(selectedFontSizeOption = value)
    }

    fun loadData() {
        viewModelScope.launch {
            _actionState.value = _actionState.value.copy(isLoading = true, isError = false)

            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val today = LocalDate.now()

            val profileDeferred = async { getUserProfileUseCase() }
            val transactionsDeferred = async {
                getTransactionsUseCase(
                    type = null,
                    startDate = LocalDate.of(2020, 1, 1).format(formatter),
                    endDate = today.format(formatter)
                )
            }
            val goalsDeferred = async { getSavingsGoalsUseCase() }

            val profileResult = profileDeferred.await()
            val transactionsResult = transactionsDeferred.await()
            val goalsResult = goalsDeferred.await()

            if (profileResult is Resource.Success && transactionsResult is Resource.Success && goalsResult is Resource.Success) {
                val profile = profileResult.data
                val transactions = transactionsResult.data ?: emptyList()
                val goals = goalsResult.data ?: emptyList()

                if (profile != null) {
                    val initials = buildString {
                        profile.firstName.firstOrNull()?.let { append(it.uppercaseChar()) }
                        profile.lastName.firstOrNull()?.let { append(it.uppercaseChar()) }
                    }
                    val usageDays = ChronoUnit.DAYS.between(profile.createdAt, today).toInt().coerceAtLeast(0)
                    val usageMonths = ChronoUnit.MONTHS.between(profile.createdAt, today).toInt().coerceAtLeast(0)

                    _actionState.value = ProfileActionState(
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        email = profile.email,
                        initials = initials,
                        transactionCount = transactions.size,
                        activeGoalsCount = goals.size,
                        usageDays = usageDays,
                        usageMonths = usageMonths,
                        showUsageInDays = usageDays < 90,
                        passwordChangedAtDisplay = formatPasswordChangedAt(profile.passwordChangedAt)
                    )
                } else {
                    _actionState.value = ProfileActionState(isError = true)
                }
            } else {
                _actionState.value = ProfileActionState(isError = true)
            }
        }
    }

    private fun formatPasswordChangedAt(dateTime: java.time.LocalDateTime): String {
        val locale = Locale("tr")
        val month = dateTime.month.getDisplayName(TextStyle.FULL, locale)
            .replaceFirstChar { it.uppercase(locale) }
        val time = "%02d:%02d".format(dateTime.hour, dateTime.minute)
        return "${dateTime.dayOfMonth} $month ${dateTime.year}, $time"
    }
}
