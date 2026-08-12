package com.example.fintrack.viewmodel

import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.transactions.TransactionsViewModel
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
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

class TransactionsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var viewModel: TransactionsViewModel

    private fun buildTransaction(id: Long, type: TransactionType, amount: Double) = Transaction(
        id = id, type = type, category = "CATEGORY", amount = amount, note = null,
        date = "2026-01-15", time = "12:00:00", isRecurring = false, isReminder = false, createdAt = "2026-01-15T12:00:00"
    )

    private fun setUpViewModel(initialResult: Resource<List<Transaction>> = Resource.Success(emptyList())) {
        Dispatchers.setMain(testDispatcher)
        getTransactionsUseCase = mockk()
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns initialResult
        viewModel = TransactionsViewModel(getTransactionsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads transactions with default filter and period`() = runTest {
        setUpViewModel(Resource.Success(listOf(buildTransaction(1L, TransactionType.EXPENSE, 100.0))))

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.transactions.size)
        assertTrue(!viewModel.actionState.value.isError)
    }

    @Test
    fun `onFilterChange uses expense type when index is one`() = runTest {
        setUpViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onFilterChange(1)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.selectedFilterIndex)
        coVerify { getTransactionsUseCase(TransactionType.EXPENSE, any(), any()) }
    }

    @Test
    fun `onFilterChange uses income type when index is two`() = runTest {
        setUpViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onFilterChange(2)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { getTransactionsUseCase(TransactionType.INCOME, any(), any()) }
    }

    @Test
    fun `onPeriodChange updates period and reloads transactions`() = runTest {
        setUpViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onPeriodChange("Bu Ay")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Bu Ay", viewModel.uiState.value.selectedPeriod)
    }

    @Test
    fun `loadTransactions computes remaining balance from newest to oldest`() = runTest {
        val transactions = listOf(
            buildTransaction(1L, TransactionType.INCOME, 1000.0),
            buildTransaction(2L, TransactionType.EXPENSE, 300.0)
        )
        setUpViewModel(Resource.Success(transactions))

        testDispatcher.scheduler.advanceUntilIdle()

        val items = viewModel.uiState.value.transactions
        assertEquals(700.0, items[0].remainingBalance, 0.001)
        assertEquals(-300.0, items[1].remainingBalance, 0.001)
    }

    @Test
    fun `loadTransactions sets error state when call fails`() = runTest {
        setUpViewModel(Resource.Error("network error"))

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isError)
    }
}
