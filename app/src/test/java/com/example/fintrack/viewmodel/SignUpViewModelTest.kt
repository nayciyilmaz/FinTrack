package com.example.fintrack.viewmodel

import android.content.Context
import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.sign_up.SignUpViewModel
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.model.User
import com.example.fintrack.domain.usecase.AddTransactionUseCase
import com.example.fintrack.domain.usecase.RegisterUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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

class SignUpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var addTransactionUseCase: AddTransactionUseCase
    private lateinit var context: Context
    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        registerUseCase = mockk()
        addTransactionUseCase = mockk()
        context = mockk()

        viewModel = SignUpViewModel(registerUseCase, addTransactionUseCase, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildUser() = User(
        id = 1L, firstName = "John", lastName = "Doe", email = "john@example.com", payday = 5, token = "token"
    )

    @Test
    fun `onFirstNameChange updates value and clears error`() {
        viewModel.onFirstNameChange("John")

        assertEquals("John", viewModel.uiState.value.firstName)
        assertEquals(null, viewModel.uiState.value.validationErrors.firstNameError)
    }

    @Test
    fun `onPaydayChange filters non digit characters and limits length`() {
        viewModel.onPaydayChange("a1b2c3")

        assertEquals("12", viewModel.uiState.value.payday)
    }

    @Test
    fun `onSalaryChange keeps digits and decimal point`() {
        viewModel.onSalaryChange("1a2.b5c")

        assertEquals("12.5", viewModel.uiState.value.salary)
    }

    @Test
    fun `register succeeds without creating salary transaction when salary is zero`() = runTest {
        viewModel.onFirstNameChange("John")
        viewModel.onLastNameChange("Doe")
        viewModel.onEmailChange("john@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onPaydayChange("5")
        viewModel.onSalaryChange("0")
        coEvery {
            registerUseCase("John", "Doe", "john@example.com", "password123", 5, 0.0)
        } returns Resource.Success(buildUser())

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isSuccess)
        coVerify(exactly = 0) { addTransactionUseCase(any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `register creates salary transaction when salary is positive`() = runTest {
        viewModel.onFirstNameChange("John")
        viewModel.onLastNameChange("Doe")
        viewModel.onEmailChange("john@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onPaydayChange("5")
        viewModel.onSalaryChange("5000")
        coEvery {
            registerUseCase("John", "Doe", "john@example.com", "password123", 5, 5000.0)
        } returns Resource.Success(buildUser())
        coEvery {
            addTransactionUseCase(
                TransactionType.INCOME, "SALARY", 5000.0, null, any(), any(), true, false
            )
        } returns Resource.Success(mockk<Transaction>())

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isSuccess)
        coVerify {
            addTransactionUseCase(TransactionType.INCOME, "SALARY", 5000.0, null, any(), any(), true, false)
        }
    }

    @Test
    fun `register maps field errors when validation fails`() = runTest {
        viewModel.onFirstNameChange("Jo")
        viewModel.onLastNameChange("Doe")
        viewModel.onEmailChange("john@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onPaydayChange("5")
        viewModel.onSalaryChange("0")
        coEvery {
            registerUseCase("Jo", "Doe", "john@example.com", "password123", 5, 0.0)
        } returns Resource.Error("Validation failed", fieldErrors = mapOf("first_name" to "Too short"))

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Too short", viewModel.uiState.value.validationErrors.firstNameError)
        assertTrue(!viewModel.actionState.value.isLoading)
    }

    @Test
    fun `register shows message as email error when no field errors present`() = runTest {
        viewModel.onFirstNameChange("John")
        viewModel.onLastNameChange("Doe")
        viewModel.onEmailChange("taken@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onPaydayChange("5")
        viewModel.onSalaryChange("0")
        coEvery {
            registerUseCase("John", "Doe", "taken@example.com", "password123", 5, 0.0)
        } returns Resource.Error("Email already exists")

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Email already exists", viewModel.uiState.value.validationErrors.emailError)
    }
}
