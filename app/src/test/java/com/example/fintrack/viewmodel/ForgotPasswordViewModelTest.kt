package com.example.fintrack.viewmodel

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.forgot_password.ForgotPasswordStep
import com.example.fintrack.presentation.screens.forgot_password.ForgotPasswordViewModel
import com.example.fintrack.domain.usecase.ResetPasswordUseCase
import com.example.fintrack.domain.usecase.SendPasswordResetCodeUseCase
import com.example.fintrack.domain.usecase.VerifyPasswordResetCodeUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ForgotPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var sendPasswordResetCodeUseCase: SendPasswordResetCodeUseCase
    private lateinit var verifyPasswordResetCodeUseCase: VerifyPasswordResetCodeUseCase
    private lateinit var resetPasswordUseCase: ResetPasswordUseCase
    private lateinit var context: Context
    private lateinit var viewModel: ForgotPasswordViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        sendPasswordResetCodeUseCase = mockk()
        verifyPasswordResetCodeUseCase = mockk()
        resetPasswordUseCase = mockk()
        context = mockk()
        every { context.getString(R.string.profile_password_mismatch) } returns "Passwords do not match"

        viewModel = ForgotPasswordViewModel(sendPasswordResetCodeUseCase, verifyPasswordResetCodeUseCase, resetPasswordUseCase, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEmailChange updates value and clears error`() {
        viewModel.onEmailChange("john@example.com")

        assertEquals("john@example.com", viewModel.uiState.value.email)
        assertEquals(null, viewModel.uiState.value.validationErrors.emailError)
    }

    @Test
    fun `sendCode moves to code step when successful`() = runTest {
        viewModel.onEmailChange("john@example.com")
        coEvery { sendPasswordResetCodeUseCase("john@example.com") } returns Resource.Success(Unit)

        viewModel.sendCode()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(ForgotPasswordStep.CODE, viewModel.uiState.value.step)
    }

    @Test
    fun `sendCode sets email error when user not found`() = runTest {
        viewModel.onEmailChange("missing@example.com")
        coEvery { sendPasswordResetCodeUseCase("missing@example.com") } returns Resource.Error(
            "User not found", fieldErrors = mapOf("email" to "User not found")
        )

        viewModel.sendCode()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("User not found", viewModel.uiState.value.validationErrors.emailError)
        assertEquals(ForgotPasswordStep.EMAIL, viewModel.uiState.value.step)
    }

    @Test
    fun `onCodeDigitChange updates only the targeted digit`() {
        viewModel.onCodeDigitChange(2, "5")

        assertEquals("5", viewModel.uiState.value.codeDigits[2])
        assertEquals("", viewModel.uiState.value.codeDigits[0])
    }

    @Test
    fun `verifyCode sets resetToken and moves to new password step when successful`() = runTest {
        viewModel.onEmailChange("john@example.com")
        repeat(6) { viewModel.onCodeDigitChange(it, "1") }
        coEvery { verifyPasswordResetCodeUseCase("john@example.com", "111111") } returns Resource.Success("reset-token-uuid")

        viewModel.verifyCode()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("reset-token-uuid", viewModel.uiState.value.resetToken)
        assertEquals(ForgotPasswordStep.NEW_PASSWORD, viewModel.uiState.value.step)
    }

    @Test
    fun `verifyCode sets code error when code invalid`() = runTest {
        viewModel.onEmailChange("john@example.com")
        repeat(6) { viewModel.onCodeDigitChange(it, "9") }
        coEvery { verifyPasswordResetCodeUseCase("john@example.com", "999999") } returns Resource.Error(
            "Invalid code", fieldErrors = mapOf("code" to "Invalid code")
        )

        viewModel.verifyCode()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Invalid code", viewModel.uiState.value.validationErrors.codeError)
    }

    @Test
    fun `resetPassword sets mismatch error without calling use case when passwords differ`() {
        viewModel.onNewPasswordChange("newpass123")
        viewModel.onNewPasswordRepeatChange("different123")

        viewModel.resetPassword()

        assertEquals("Passwords do not match", viewModel.uiState.value.validationErrors.confirmPasswordError)
        coVerify(exactly = 0) { resetPasswordUseCase(any(), any()) }
    }

    @Test
    fun `resetPassword sets success state when passwords match and api succeeds`() = runTest {
        viewModel.onNewPasswordChange("newpass123")
        viewModel.onNewPasswordRepeatChange("newpass123")
        coEvery { resetPasswordUseCase("", "newpass123") } returns Resource.Success(Unit)

        viewModel.resetPassword()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isSuccess)
    }

    @Test
    fun `resetPassword sets error when reset token invalid`() = runTest {
        viewModel.onNewPasswordChange("newpass123")
        viewModel.onNewPasswordRepeatChange("newpass123")
        coEvery { resetPasswordUseCase("", "newpass123") } returns Resource.Error("Invalid reset token")

        viewModel.resetPassword()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Invalid reset token", viewModel.uiState.value.validationErrors.newPasswordError)
    }
}
