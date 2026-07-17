package com.example.fintrack.presentation.screens.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.usecase.GetSavingsGoalsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.GetUserProfileUseCase
import com.example.fintrack.domain.usecase.LogoutUseCase
import com.example.fintrack.domain.usecase.UpdateUserEmailUseCase
import com.example.fintrack.domain.usecase.UpdateUserNameUseCase
import com.example.fintrack.domain.usecase.UpdateUserPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val getSavingsGoalsUseCase: GetSavingsGoalsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val updateUserEmailUseCase: UpdateUserEmailUseCase,
    private val updateUserPasswordUseCase: UpdateUserPasswordUseCase,
    @ApplicationContext private val context: Context
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
        _uiState.value = _uiState.value.copy(firstNameInput = value, firstNameError = null)
    }

    fun onLastNameInputChange(value: String) {
        _uiState.value = _uiState.value.copy(lastNameInput = value, lastNameError = null)
    }

    fun onEmailInputChange(value: String) {
        _uiState.value = _uiState.value.copy(emailInput = value, emailError = null)
    }

    fun onCurrentPasswordInputChange(value: String) {
        _uiState.value = _uiState.value.copy(currentPasswordInput = value, currentPasswordError = null)
    }

    fun onNewPasswordInputChange(value: String) {
        _uiState.value = _uiState.value.copy(newPasswordInput = value, newPasswordError = null)
    }

    fun onConfirmPasswordInputChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPasswordInput = value, confirmPasswordError = null)
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
                    val initials = computeInitials(profile.firstName, profile.lastName)
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

    fun onRequestLogout() {
        _uiState.value = _uiState.value.copy(showLogoutConfirmDialog = true)
    }

    fun onCancelLogout() {
        _uiState.value = _uiState.value.copy(showLogoutConfirmDialog = false)
    }

    fun onConfirmLogout() {
        _uiState.value = _uiState.value.copy(showLogoutConfirmDialog = false)
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun onUpdateName() {
        val firstName = _uiState.value.firstNameInput.trim()
        val lastName = _uiState.value.lastNameInput.trim()

        viewModelScope.launch {
            when (val result = updateUserNameUseCase(firstName, lastName)) {
                is Resource.Success -> {
                    val profile = result.data
                    if (profile != null) {
                        _actionState.value = _actionState.value.copy(
                            firstName = profile.firstName,
                            lastName = profile.lastName,
                            initials = computeInitials(profile.firstName, profile.lastName)
                        )
                    }
                    _uiState.value = _uiState.value.copy(activeDialog = null)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        firstNameError = result.fieldErrors?.get("first_name"),
                        lastNameError = result.fieldErrors?.get("last_name") ?: result.message.takeIf { result.fieldErrors.isNullOrEmpty() }
                    )
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onUpdateEmail() {
        val email = _uiState.value.emailInput.trim()

        if (email == _actionState.value.email) {
            _uiState.value = _uiState.value.copy(activeDialog = null)
            return
        }

        viewModelScope.launch {
            when (val result = updateUserEmailUseCase(email)) {
                is Resource.Success -> {
                    val newEmail = result.data
                    if (newEmail != null) {
                        _actionState.value = _actionState.value.copy(email = newEmail)
                    }
                    _uiState.value = _uiState.value.copy(activeDialog = null)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        emailError = result.fieldErrors?.get("email") ?: result.message
                    )
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onUpdatePassword() {
        val currentPassword = _uiState.value.currentPasswordInput
        val newPassword = _uiState.value.newPasswordInput
        val confirmPassword = _uiState.value.confirmPasswordInput

        if (newPassword != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                confirmPasswordError = context.getString(R.string.profile_password_mismatch)
            )
            return
        }

        viewModelScope.launch {
            when (val result = updateUserPasswordUseCase(currentPassword, newPassword)) {
                is Resource.Success -> {
                    val profile = result.data
                    if (profile != null) {
                        _actionState.value = _actionState.value.copy(
                            passwordChangedAtDisplay = formatPasswordChangedAt(profile.passwordChangedAt)
                        )
                    }
                    _uiState.value = _uiState.value.copy(activeDialog = null)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        currentPasswordError = result.fieldErrors?.get("current_password") ?: result.message,
                        newPasswordError = result.fieldErrors?.get("new_password")
                    )
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun computeInitials(firstName: String, lastName: String): String {
        return buildString {
            firstName.firstOrNull()?.let { append(it.uppercaseChar()) }
            lastName.firstOrNull()?.let { append(it.uppercaseChar()) }
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
