package com.example.fintrack.presentation.screens.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.R
import com.example.fintrack.core.util.CurrencyHelper
import com.example.fintrack.core.util.LocaleHelper
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.usecase.GetSavingsGoalsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.GetUserProfileUseCase
import com.example.fintrack.domain.usecase.LogoutUseCase
import com.example.fintrack.domain.usecase.UpdateUserEmailUseCase
import com.example.fintrack.domain.usecase.UpdateUserNameUseCase
import com.example.fintrack.domain.usecase.UpdateUserPasswordUseCase
import com.example.fintrack.notification.ReminderNotificationScheduler
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
    private val reminderNotificationScheduler: ReminderNotificationScheduler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _actionState = MutableStateFlow(ProfileActionState())
    val actionState: StateFlow<ProfileActionState> = _actionState.asStateFlow()

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            currentLanguageDisplay = languageDisplayName(LocaleHelper.getLanguage(context)),
            currentCurrencyDisplay = currencyDisplayName(CurrencyHelper.getCurrency(context))
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onShowNameDialog() {
        _uiState.value = _uiState.value.copy(
            editState = ProfileEditState(
                activeDialog = ProfileDialogType.NAME,
                firstNameInput = _actionState.value.firstName,
                lastNameInput = _actionState.value.lastName
            )
        )
    }

    fun onShowEmailDialog() {
        _uiState.value = _uiState.value.copy(
            editState = ProfileEditState(
                activeDialog = ProfileDialogType.EMAIL,
                emailInput = _actionState.value.email
            )
        )
    }

    fun onShowPasswordDialog() {
        _uiState.value = _uiState.value.copy(
            editState = ProfileEditState(activeDialog = ProfileDialogType.PASSWORD)
        )
    }

    fun onDismissDialog() {
        _uiState.value = _uiState.value.copy(editState = _uiState.value.editState.copy(activeDialog = null))
    }

    fun onFirstNameInputChange(value: String) {
        _uiState.value = _uiState.value.copy(
            editState = _uiState.value.editState.copy(firstNameInput = value, firstNameError = null)
        )
    }

    fun onLastNameInputChange(value: String) {
        _uiState.value = _uiState.value.copy(
            editState = _uiState.value.editState.copy(lastNameInput = value, lastNameError = null)
        )
    }

    fun onEmailInputChange(value: String) {
        _uiState.value = _uiState.value.copy(
            editState = _uiState.value.editState.copy(emailInput = value, emailError = null)
        )
    }

    fun onCurrentPasswordInputChange(value: String) {
        _uiState.value = _uiState.value.copy(
            editState = _uiState.value.editState.copy(currentPasswordInput = value, currentPasswordError = null)
        )
    }

    fun onNewPasswordInputChange(value: String) {
        _uiState.value = _uiState.value.copy(
            editState = _uiState.value.editState.copy(newPasswordInput = value, newPasswordError = null)
        )
    }

    fun onConfirmPasswordInputChange(value: String) {
        _uiState.value = _uiState.value.copy(
            editState = _uiState.value.editState.copy(confirmPasswordInput = value, confirmPasswordError = null)
        )
    }

    fun toggleCurrentPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            editState = _uiState.value.editState.copy(
                isCurrentPasswordVisible = !_uiState.value.editState.isCurrentPasswordVisible
            )
        )
    }

    fun toggleNewPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            editState = _uiState.value.editState.copy(
                isNewPasswordVisible = !_uiState.value.editState.isNewPasswordVisible
            )
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            editState = _uiState.value.editState.copy(
                isConfirmPasswordVisible = !_uiState.value.editState.isConfirmPasswordVisible
            )
        )
    }

    fun onShowCurrencyDialog() {
        _uiState.value = _uiState.value.copy(
            settingsDialogState = _uiState.value.settingsDialogState.copy(
                activeSettingsDialog = SettingsDialogType.CURRENCY,
                selectedCurrencyOption = currencyDisplayName(CurrencyHelper.getCurrency(context))
            )
        )
    }

    fun onShowLanguageDialog() {
        _uiState.value = _uiState.value.copy(
            settingsDialogState = _uiState.value.settingsDialogState.copy(
                activeSettingsDialog = SettingsDialogType.LANGUAGE,
                selectedLanguageOption = languageDisplayName(LocaleHelper.getLanguage(context))
            )
        )
    }

    fun onShowFontSizeDialog() {
        _uiState.value = _uiState.value.copy(
            settingsDialogState = _uiState.value.settingsDialogState.copy(
                activeSettingsDialog = SettingsDialogType.FONT_SIZE,
                selectedFontSizeOption = "Orta"
            )
        )
    }

    fun onDismissSettingsDialog() {
        _uiState.value = _uiState.value.copy(
            settingsDialogState = _uiState.value.settingsDialogState.copy(activeSettingsDialog = null)
        )
    }

    fun onCurrencyOptionSelect(value: String) {
        _uiState.value = _uiState.value.copy(
            settingsDialogState = _uiState.value.settingsDialogState.copy(selectedCurrencyOption = value)
        )
    }

    fun onApplyCurrency() {
        val selectedCurrencyDisplay = _uiState.value.settingsDialogState.selectedCurrencyOption
        val currencyCode = currencyCode(selectedCurrencyDisplay)
        val currentCurrencyCode = CurrencyHelper.getCurrency(context)

        _uiState.value = _uiState.value.copy(
            settingsDialogState = _uiState.value.settingsDialogState.copy(activeSettingsDialog = null),
            currentCurrencyDisplay = selectedCurrencyDisplay
        )

        if (currencyCode != currentCurrencyCode) {
            CurrencyHelper.saveCurrency(context, currencyCode)
            _uiState.value = _uiState.value.copy(shouldRecreateActivity = true)
        }
    }

    private fun currencyDisplayName(currencyCode: String): String {
        return when (currencyCode) {
            "TRY" -> context.getString(R.string.profile_currency_try)
            "EUR" -> context.getString(R.string.profile_currency_eur)
            "USD" -> context.getString(R.string.profile_currency_usd)
            else -> context.getString(R.string.profile_currency_try)
        }
    }

    private fun currencyCode(currencyDisplayName: String): String {
        return when (currencyDisplayName) {
            context.getString(R.string.profile_currency_try) -> "TRY"
            context.getString(R.string.profile_currency_eur) -> "EUR"
            context.getString(R.string.profile_currency_usd) -> "USD"
            else -> "TRY"
        }
    }

    fun onLanguageOptionSelect(value: String) {
        _uiState.value = _uiState.value.copy(
            settingsDialogState = _uiState.value.settingsDialogState.copy(selectedLanguageOption = value)
        )
    }

    fun onApplyLanguage() {
        val selectedLanguageDisplay = _uiState.value.settingsDialogState.selectedLanguageOption
        val languageCode = languageCode(selectedLanguageDisplay)
        val currentLanguageCode = LocaleHelper.getLanguage(context)

        _uiState.value = _uiState.value.copy(
            settingsDialogState = _uiState.value.settingsDialogState.copy(activeSettingsDialog = null),
            currentLanguageDisplay = selectedLanguageDisplay
        )

        if (languageCode != currentLanguageCode) {
            LocaleHelper.saveLanguage(context, languageCode)
            _uiState.value = _uiState.value.copy(shouldRecreateActivity = true)
        }
    }

    fun onRecreateActivityHandled() {
        _uiState.value = _uiState.value.copy(shouldRecreateActivity = false)
    }

    private fun languageDisplayName(languageCode: String): String {
        return when (languageCode) {
            "tr" -> context.getString(R.string.profile_language_tr)
            "en" -> context.getString(R.string.profile_language_en)
            "de" -> context.getString(R.string.profile_language_de)
            else -> context.getString(R.string.profile_language_tr)
        }
    }

    private fun languageCode(languageDisplayName: String): String {
        return when (languageDisplayName) {
            context.getString(R.string.profile_language_tr) -> "tr"
            context.getString(R.string.profile_language_en) -> "en"
            context.getString(R.string.profile_language_de) -> "de"
            else -> "tr"
        }
    }

    fun onFontSizeOptionSelect(value: String) {
        _uiState.value = _uiState.value.copy(
            settingsDialogState = _uiState.value.settingsDialogState.copy(selectedFontSizeOption = value)
        )
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
            reminderNotificationScheduler.cancel()
        }
    }

    fun onUpdateName() {
        val firstName = _uiState.value.editState.firstNameInput.trim()
        val lastName = _uiState.value.editState.lastNameInput.trim()

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
                    _uiState.value = _uiState.value.copy(editState = _uiState.value.editState.copy(activeDialog = null))
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        editState = _uiState.value.editState.copy(
                            firstNameError = result.fieldErrors?.get("first_name"),
                            lastNameError = result.fieldErrors?.get("last_name") ?: result.message.takeIf { result.fieldErrors.isNullOrEmpty() }
                        )
                    )
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onUpdateEmail() {
        val email = _uiState.value.editState.emailInput.trim()

        if (email == _actionState.value.email) {
            _uiState.value = _uiState.value.copy(editState = _uiState.value.editState.copy(activeDialog = null))
            return
        }

        viewModelScope.launch {
            when (val result = updateUserEmailUseCase(email)) {
                is Resource.Success -> {
                    val newEmail = result.data
                    if (newEmail != null) {
                        _actionState.value = _actionState.value.copy(email = newEmail)
                    }
                    _uiState.value = _uiState.value.copy(editState = _uiState.value.editState.copy(activeDialog = null))
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        editState = _uiState.value.editState.copy(
                            emailError = result.fieldErrors?.get("email") ?: result.message
                        )
                    )
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onUpdatePassword() {
        val currentPassword = _uiState.value.editState.currentPasswordInput
        val newPassword = _uiState.value.editState.newPasswordInput
        val confirmPassword = _uiState.value.editState.confirmPasswordInput

        if (newPassword != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                editState = _uiState.value.editState.copy(
                    confirmPasswordError = context.getString(R.string.profile_password_mismatch)
                )
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
                    _uiState.value = _uiState.value.copy(editState = _uiState.value.editState.copy(activeDialog = null))
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        editState = _uiState.value.editState.copy(
                            currentPasswordError = result.fieldErrors?.get("current_password") ?: result.message,
                            newPasswordError = result.fieldErrors?.get("new_password")
                        )
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
        val locale = LocaleHelper.getLocale(context)
        val month = dateTime.month.getDisplayName(TextStyle.FULL, locale)
            .replaceFirstChar { it.uppercase(locale) }
        val time = "%02d:%02d".format(dateTime.hour, dateTime.minute)
        return "${dateTime.dayOfMonth} $month ${dateTime.year}, $time"
    }
}
