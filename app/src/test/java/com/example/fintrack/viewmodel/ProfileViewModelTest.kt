package com.example.fintrack.viewmodel

import android.content.Context
import android.content.SharedPreferences
import com.example.fintrack.R
import com.example.fintrack.presentation.screens.profile.ProfileDialogType
import com.example.fintrack.presentation.screens.profile.ProfileViewModel
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.model.UserProfile
import com.example.fintrack.domain.usecase.GetSavingsGoalsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.GetUserProfileUseCase
import com.example.fintrack.domain.usecase.LogoutUseCase
import com.example.fintrack.domain.usecase.UpdateUserEmailUseCase
import com.example.fintrack.domain.usecase.UpdateUserNameUseCase
import com.example.fintrack.domain.usecase.UpdateUserPasswordUseCase
import com.example.fintrack.notification.ReminderNotificationScheduler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getUserProfileUseCase: GetUserProfileUseCase
    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var getSavingsGoalsUseCase: GetSavingsGoalsUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var updateUserNameUseCase: UpdateUserNameUseCase
    private lateinit var updateUserEmailUseCase: UpdateUserEmailUseCase
    private lateinit var updateUserPasswordUseCase: UpdateUserPasswordUseCase
    private lateinit var reminderNotificationScheduler: ReminderNotificationScheduler
    private lateinit var context: Context
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getUserProfileUseCase = mockk()
        getTransactionsUseCase = mockk()
        getSavingsGoalsUseCase = mockk()
        logoutUseCase = mockk()
        updateUserNameUseCase = mockk()
        updateUserEmailUseCase = mockk()
        updateUserPasswordUseCase = mockk()
        reminderNotificationScheduler = mockk(relaxed = true)
        context = mockk()

        val sharedPreferences = mockk<SharedPreferences>()
        val editor = mockk<SharedPreferences.Editor>(relaxed = true)
        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(any(), any()) } answers { secondArg() }
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor

        every { context.getString(R.string.profile_currency_try) } returns "TRY_DISPLAY"
        every { context.getString(R.string.profile_currency_eur) } returns "EUR_DISPLAY"
        every { context.getString(R.string.profile_currency_usd) } returns "USD_DISPLAY"
        every { context.getString(R.string.profile_language_tr) } returns "TR_DISPLAY"
        every { context.getString(R.string.profile_language_en) } returns "EN_DISPLAY"
        every { context.getString(R.string.profile_language_de) } returns "DE_DISPLAY"
        every { context.getString(R.string.profile_font_size_small) } returns "SMALL_DISPLAY"
        every { context.getString(R.string.profile_font_size_large) } returns "LARGE_DISPLAY"
        every { context.getString(R.string.profile_font_size_medium) } returns "MEDIUM_DISPLAY"
        every { context.getString(R.string.profile_dark_mode_light) } returns "LIGHT_DISPLAY"
        every { context.getString(R.string.profile_dark_mode_dark) } returns "DARK_DISPLAY"
        every { context.getString(R.string.profile_password_mismatch) } returns "Passwords do not match"

        viewModel = ProfileViewModel(
            getUserProfileUseCase, getTransactionsUseCase, getSavingsGoalsUseCase, logoutUseCase,
            updateUserNameUseCase, updateUserEmailUseCase, updateUserPasswordUseCase,
            reminderNotificationScheduler, context
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildProfile() = UserProfile(
        firstName = "John", lastName = "Doe", email = "john@example.com",
        createdAt = LocalDate.now().minusDays(10), passwordChangedAt = LocalDateTime.now()
    )

    @Test
    fun `initial uiState uses helper display values from context`() {
        assertEquals("TRY_DISPLAY", viewModel.uiState.value.currentCurrencyDisplay)
        assertEquals("TR_DISPLAY", viewModel.uiState.value.currentLanguageDisplay)
        assertEquals("LIGHT_DISPLAY", viewModel.uiState.value.currentThemeDisplay)
        assertEquals("MEDIUM_DISPLAY", viewModel.uiState.value.currentFontSizeDisplay)
    }

    @Test
    fun `loadData populates actionState when all calls succeed`() = runTest {
        val profile = buildProfile()
        coEvery { getUserProfileUseCase() } returns Resource.Success(profile)
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Success(
            listOf(
                Transaction(1L, TransactionType.EXPENSE, "FOOD", 10.0, null, "2026-01-01", "12:00:00", false, false, "2026-01-01T12:00:00")
            )
        )
        coEvery { getSavingsGoalsUseCase() } returns Resource.Success(
            listOf(SavingsGoal(1L, "Vacation", "TRAVEL", 5000.0, 0.0, "2026-01-01"))
        )

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.actionState.value
        assertEquals("John", state.firstName)
        assertEquals("JD", state.initials)
        assertEquals(1, state.transactionCount)
        assertEquals(1, state.activeGoalsCount)
        assertFalse(state.isError)
    }

    @Test
    fun `loadData sets error state when any call fails`() = runTest {
        coEvery { getUserProfileUseCase() } returns Resource.Success(buildProfile())
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Error("network error")
        coEvery { getSavingsGoalsUseCase() } returns Resource.Success(emptyList())

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isError)
    }

    @Test
    fun `onShowNameDialog prefills inputs from actionState`() {
        viewModel.onShowNameDialog()

        assertEquals(ProfileDialogType.NAME, viewModel.uiState.value.editState.activeDialog)
    }

    @Test
    fun `onDismissDialog clears active dialog`() {
        viewModel.onShowNameDialog()
        viewModel.onDismissDialog()

        assertNull(viewModel.uiState.value.editState.activeDialog)
    }

    @Test
    fun `onFirstNameInputChange updates input and clears error`() {
        viewModel.onFirstNameInputChange("Jane")

        assertEquals("Jane", viewModel.uiState.value.editState.firstNameInput)
        assertNull(viewModel.uiState.value.editState.firstNameError)
    }

    @Test
    fun `onConfirmLogout invokes logout and cancels reminders`() = runTest {
        coEvery { logoutUseCase() } returns Resource.Success(Unit)

        viewModel.onConfirmLogout()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { logoutUseCase() }
        verify { reminderNotificationScheduler.cancel() }
        assertFalse(viewModel.uiState.value.showLogoutConfirmDialog)
    }

    @Test
    fun `onUpdateName updates actionState when successful`() = runTest {
        viewModel.onFirstNameInputChange("Jane")
        viewModel.onLastNameInputChange("Smith")
        val updated = buildProfile().copy(firstName = "Jane", lastName = "Smith")
        coEvery { updateUserNameUseCase("Jane", "Smith") } returns Resource.Success(updated)

        viewModel.onUpdateName()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Jane", viewModel.actionState.value.firstName)
        assertNull(viewModel.uiState.value.editState.activeDialog)
    }

    @Test
    fun `onUpdateName sets field error when validation fails`() = runTest {
        viewModel.onFirstNameInputChange("Jo")
        viewModel.onLastNameInputChange("Doe")
        coEvery { updateUserNameUseCase("Jo", "Doe") } returns Resource.Error(
            "Validation failed", fieldErrors = mapOf("first_name" to "Too short")
        )

        viewModel.onUpdateName()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Too short", viewModel.uiState.value.editState.firstNameError)
    }

    @Test
    fun `onUpdateEmail closes dialog without api call when email unchanged`() = runTest {
        coEvery { getUserProfileUseCase() } returns Resource.Success(buildProfile())
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Success(emptyList())
        coEvery { getSavingsGoalsUseCase() } returns Resource.Success(emptyList())
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEmailInputChange("john@example.com")

        viewModel.onUpdateEmail()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { updateUserEmailUseCase(any()) }
    }

    @Test
    fun `onUpdateEmail updates actionState when successful`() = runTest {
        viewModel.onEmailInputChange("new@example.com")
        coEvery { updateUserEmailUseCase("new@example.com") } returns Resource.Success("new@example.com")

        viewModel.onUpdateEmail()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("new@example.com", viewModel.actionState.value.email)
    }

    @Test
    fun `onUpdatePassword sets mismatch error when passwords differ`() {
        viewModel.onNewPasswordInputChange("newpass123")
        viewModel.onConfirmPasswordInputChange("different123")

        viewModel.onUpdatePassword()

        assertEquals("Passwords do not match", viewModel.uiState.value.editState.confirmPasswordError)
    }

    @Test
    fun `onUpdatePassword updates state when successful`() = runTest {
        viewModel.onCurrentPasswordInputChange("oldpass")
        viewModel.onNewPasswordInputChange("newpass123")
        viewModel.onConfirmPasswordInputChange("newpass123")
        coEvery { updateUserPasswordUseCase("oldpass", "newpass123") } returns Resource.Success(buildProfile())

        viewModel.onUpdatePassword()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.editState.activeDialog)
    }

    @Test
    fun `onApplyCurrency marks activity for recreation when currency changed`() {
        viewModel.onShowCurrencyDialog()
        viewModel.onCurrencyOptionSelect("EUR_DISPLAY")

        viewModel.onApplyCurrency()

        assertTrue(viewModel.uiState.value.shouldRecreateActivity)
        assertEquals("EUR_DISPLAY", viewModel.uiState.value.currentCurrencyDisplay)
    }
}
