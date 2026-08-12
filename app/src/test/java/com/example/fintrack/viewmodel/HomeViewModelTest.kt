package com.example.fintrack.viewmodel

import android.content.Context
import android.content.SharedPreferences
import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.home.HomeViewModel
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import io.mockk.coEvery
import io.mockk.every
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

class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var tokenManager: TokenManager
    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getTransactionsUseCase = mockk()
        tokenManager = mockk()
        context = mockk()

        val sharedPreferences = mockk<SharedPreferences>()
        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(any(), any()) } returns "tr"

        every { tokenManager.getPayday() } returns flowOf(5)

        viewModel = HomeViewModel(getTransactionsUseCase, tokenManager, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildTransaction(type: TransactionType, amount: Double) = Transaction(
        id = 1L, type = type, category = "CATEGORY", amount = amount, note = null,
        date = "2026-01-15", time = "12:00:00", isRecurring = false, isReminder = false, createdAt = "2026-01-15T12:00:00"
    )

    @Test
    fun `loadHomeData computes income expense and remaining balance when calls succeed`() = runTest {
        val transactions = listOf(
            buildTransaction(TransactionType.INCOME, 1000.0),
            buildTransaction(TransactionType.EXPENSE, 400.0)
        )
        coEvery { getTransactionsUseCase(isNull(), any(), any()) } returns Resource.Success(transactions)

        viewModel.loadHomeData()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.actionState.value
        assertEquals(1000, state.income)
        assertEquals(400, state.expense)
        assertEquals(600, state.remainingBalance)
        assertTrue(!state.isError)
        assertTrue(!state.isLoading)
    }

    @Test
    fun `loadHomeData sets error state when transactions call fails`() = runTest {
        coEvery { getTransactionsUseCase(isNull(), any(), any()) } returns Resource.Error("network error")

        viewModel.loadHomeData()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.actionState.value
        assertTrue(state.isError)
        assertTrue(!state.isLoading)
    }

    @Test
    fun `loadHomeData limits recent transactions to three items`() = runTest {
        val transactions = (1..5).map { buildTransaction(TransactionType.EXPENSE, 10.0 * it) }
        coEvery { getTransactionsUseCase(isNull(), any(), any()) } returns Resource.Success(transactions)

        viewModel.loadHomeData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.actionState.value.recentTransactions.size)
    }
}
