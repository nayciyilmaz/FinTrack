package com.example.fintrack.viewmodel

import android.content.Context
import android.content.SharedPreferences
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.reports.ReportsViewModel
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.GetBudgetsUseCase
import com.example.fintrack.domain.usecase.GetSavingsGoalsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import io.mockk.every
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReportsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var getBudgetsUseCase: GetBudgetsUseCase
    private lateinit var getSavingsGoalsUseCase: GetSavingsGoalsUseCase
    private lateinit var tokenManager: TokenManager
    private lateinit var context: Context
    private lateinit var viewModel: ReportsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getTransactionsUseCase = mockk()
        getBudgetsUseCase = mockk()
        getSavingsGoalsUseCase = mockk()
        tokenManager = mockk()
        context = mockk()

        every { tokenManager.getPayday() } returns flowOf(5)

        val sharedPreferences = mockk<SharedPreferences>()
        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(any(), any()) } answers { secondArg() }
        every { context.getString(R.string.report_week_label_format, *anyVararg()) } returns "Week"

        viewModel = ReportsViewModel(getTransactionsUseCase, getBudgetsUseCase, getSavingsGoalsUseCase, tokenManager, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildTransaction(type: TransactionType, category: String, amount: Double, date: String) = Transaction(
        id = 1L, type = type, category = category, amount = amount, note = null,
        date = date, time = "12:00:00", isRecurring = false, isReminder = false, createdAt = "${date}T12:00:00"
    )

    @Test
    fun `onSectionToggle flips checked state for given section`() {
        val key = viewModel.uiState.value.checkedSections.keys.first()

        viewModel.onSectionToggle(key)

        assertEquals(true, viewModel.uiState.value.checkedSections[key])
    }

    @Test
    fun `loadData computes income and expense when calls succeed`() = runTest {
        val transactions = listOf(
            buildTransaction(TransactionType.INCOME, "SALARY", 2000.0, "2026-01-06"),
            buildTransaction(TransactionType.EXPENSE, "FOOD", 500.0, "2026-01-10")
        )
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Success(transactions)
        coEvery { getBudgetsUseCase() } returns Resource.Success(emptyList())
        coEvery { getSavingsGoalsUseCase() } returns Resource.Success(emptyList())

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.actionState.value
        assertEquals(2000, state.income)
        assertEquals(500, state.expense)
        assertTrue(!state.isError)
    }

    @Test
    fun `loadData sets error state when a call fails`() = runTest {
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Error("network error")
        coEvery { getBudgetsUseCase() } returns Resource.Success(emptyList())
        coEvery { getSavingsGoalsUseCase() } returns Resource.Success(emptyList())

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isError)
    }

    @Test
    fun `onPreviousPeriod does nothing when canGoPrevious is false`() = runTest {
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Success(emptyList())
        coEvery { getBudgetsUseCase() } returns Resource.Success(emptyList())
        coEvery { getSavingsGoalsUseCase() } returns Resource.Success(emptyList())
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        val offsetBefore = viewModel.uiState.value.periodOffset
        viewModel.onPreviousPeriod()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(offsetBefore, viewModel.uiState.value.periodOffset)
    }

    @Test
    fun `onNextPeriod does nothing when canGoNext is false`() = runTest {
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Success(emptyList())
        coEvery { getBudgetsUseCase() } returns Resource.Success(emptyList())
        coEvery { getSavingsGoalsUseCase() } returns Resource.Success(emptyList())
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        val offsetBefore = viewModel.uiState.value.periodOffset
        viewModel.onNextPeriod()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(offsetBefore, viewModel.uiState.value.periodOffset)
    }
}
