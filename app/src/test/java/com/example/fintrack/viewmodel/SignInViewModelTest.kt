package com.example.fintrack.viewmodel

import android.content.Context
import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.sign_in.SignInViewModel
import com.example.fintrack.domain.model.User
import com.example.fintrack.domain.usecase.GoogleSignInUseCase
import com.example.fintrack.domain.usecase.LoginUseCase
import com.example.fintrack.notification.ReminderNotificationScheduler
import io.mockk.coEvery
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignInViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var googleSignInUseCase: GoogleSignInUseCase
    private lateinit var reminderNotificationScheduler: ReminderNotificationScheduler
    private lateinit var context: Context
    private lateinit var viewModel: SignInViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        loginUseCase = mockk()
        googleSignInUseCase = mockk()
        reminderNotificationScheduler = mockk(relaxed = true)
        context = mockk()

        viewModel = SignInViewModel(loginUseCase, googleSignInUseCase, reminderNotificationScheduler, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildUser() = User(
        id = 1L, firstName = "John", lastName = "Doe", email = "john@example.com", payday = 5, token = "token"
    )

    @Test
    fun `onEmailChange updates email and clears error`() {
        viewModel.onEmailChange("john@example.com")

        assertEquals("john@example.com", viewModel.uiState.value.email)
        assertNull(viewModel.uiState.value.validationErrors.emailError)
    }

    @Test
    fun `onPasswordChange updates password and clears error`() {
        viewModel.onPasswordChange("password123")

        assertEquals("password123", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.validationErrors.passwordError)
    }

    @Test
    fun `togglePasswordVisibility flips visibility state`() {
        val initial = viewModel.uiState.value.isPasswordVisible

        viewModel.togglePasswordVisibility()

        assertEquals(!initial, viewModel.uiState.value.isPasswordVisible)
    }

    @Test
    fun `login sets success state and schedules reminders when credentials valid`() = runTest {
        viewModel.onEmailChange("john@example.com")
        viewModel.onPasswordChange("password123")
        coEvery { loginUseCase("john@example.com", "password123") } returns Resource.Success(buildUser())

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isSuccess)
        verify { reminderNotificationScheduler.schedule() }
    }

    @Test
    fun `login maps field errors when validation fails`() = runTest {
        viewModel.onEmailChange("john@example.com")
        viewModel.onPasswordChange("wrongpass")
        coEvery { loginUseCase("john@example.com", "wrongpass") } returns Resource.Error(
            "Incorrect password", fieldErrors = mapOf("password" to "Incorrect password")
        )

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Incorrect password", viewModel.uiState.value.validationErrors.passwordError)
        assertTrue(!viewModel.actionState.value.isLoading)
    }

    @Test
    fun `login shows message as email error when no field errors present`() = runTest {
        viewModel.onEmailChange("john@example.com")
        viewModel.onPasswordChange("password123")
        coEvery { loginUseCase("john@example.com", "password123") } returns Resource.Error("User not found")

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("User not found", viewModel.uiState.value.validationErrors.emailError)
    }

    @Test
    fun `login trims email before calling use case`() = runTest {
        viewModel.onEmailChange("  john@example.com  ")
        viewModel.onPasswordChange("password123")
        coEvery { loginUseCase("john@example.com", "password123") } returns Resource.Success(buildUser())

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isSuccess)
    }
}
