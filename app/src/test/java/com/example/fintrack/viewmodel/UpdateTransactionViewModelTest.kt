package com.example.fintrack.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.transaction_update.UpdateTransactionViewModel
import com.example.fintrack.domain.model.RecurringItem
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.DeleteRecurringItemUseCase
import com.example.fintrack.domain.usecase.DeleteTransactionUseCase
import com.example.fintrack.domain.usecase.UpdateRecurringItemUseCase
import com.example.fintrack.domain.usecase.UpdateTransactionUseCase
import io.mockk.coEvery
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

class UpdateTransactionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var updateTransactionUseCase: UpdateTransactionUseCase
    private lateinit var deleteTransactionUseCase: DeleteTransactionUseCase
    private lateinit var updateRecurringItemUseCase: UpdateRecurringItemUseCase
    private lateinit var deleteRecurringItemUseCase: DeleteRecurringItemUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        updateTransactionUseCase = mockk()
        deleteTransactionUseCase = mockk()
        updateRecurringItemUseCase = mockk()
        deleteRecurringItemUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(
        transactionId: Long = 1L,
        recurringItemId: Long = 0L,
        type: String = "EXPENSE",
        category: String = "MARKET",
        amount: String = "50",
        date: String = "2026-01-15",
        time: String = "12:00:00"
    ): UpdateTransactionViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "transactionId" to transactionId,
                "recurringItemId" to recurringItemId,
                "type" to type,
                "category" to category,
                "amount" to amount,
                "date" to date,
                "time" to time,
                "isRecurring" to "false",
                "isReminder" to "false",
                "note" to ""
            )
        )
        return UpdateTransactionViewModel(
            updateTransactionUseCase, deleteTransactionUseCase,
            updateRecurringItemUseCase, deleteRecurringItemUseCase, savedStateHandle
        )
    }

    @Test
    fun `constructor prefills state from saved state handle`() {
        val viewModel = buildViewModel()

        val state = viewModel.uiState.value
        assertEquals("50", state.amount)
        assertEquals("MARKET", state.selectedCategory?.key)
        assertEquals(0, state.selectedTypeIndex)
    }

    @Test
    fun `updateTransaction shows validation error when category missing`() {
        val viewModel = buildViewModel(category = "MISSING_CATEGORY")

        viewModel.updateTransaction()

        assertTrue(viewModel.uiState.value.showValidationError)
    }

    @Test
    fun `updateTransaction succeeds for normal transaction when fields valid`() = runTest {
        val viewModel = buildViewModel()
        coEvery {
            updateTransactionUseCase(1L, TransactionType.EXPENSE, "MARKET", 50.0, null, "2026-01-15", "12:00", false, false)
        } returns Resource.Success(mockk<Transaction>())

        viewModel.updateTransaction()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isSuccess)
    }

    @Test
    fun `updateTransaction sets error state when api call fails`() = runTest {
        val viewModel = buildViewModel()
        coEvery {
            updateTransactionUseCase(any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns Resource.Error("network error")

        viewModel.updateTransaction()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isError)
    }

    @Test
    fun `updateTransaction updates recurring item when editing a recurring item`() = runTest {
        val viewModel = buildViewModel(recurringItemId = 10L)
        coEvery { updateRecurringItemUseCase(10L, 50.0, 15) } returns Resource.Success(mockk<RecurringItem>())

        viewModel.updateTransaction()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isSuccess)
    }

    @Test
    fun `deleteTransaction succeeds for normal transaction`() = runTest {
        val viewModel = buildViewModel()
        coEvery { deleteTransactionUseCase(1L) } returns Resource.Success(Unit)

        viewModel.deleteTransaction()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isSuccess)
    }

    @Test
    fun `deleteTransaction deletes recurring item when editing a recurring item`() = runTest {
        val viewModel = buildViewModel(recurringItemId = 10L)
        coEvery { deleteRecurringItemUseCase(10L) } returns Resource.Success(Unit)

        viewModel.deleteTransaction()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isSuccess)
    }

    @Test
    fun `updateTransaction sets error state when recurring item update fails`() = runTest {
        val viewModel = buildViewModel(recurringItemId = 10L)
        coEvery { updateRecurringItemUseCase(10L, 50.0, 15) } returns Resource.Error("network error")

        viewModel.updateTransaction()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isError)
    }

    @Test
    fun `deleteTransaction sets error state when normal transaction delete fails`() = runTest {
        val viewModel = buildViewModel()
        coEvery { deleteTransactionUseCase(1L) } returns Resource.Error("network error")

        viewModel.deleteTransaction()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isError)
    }

    @Test
    fun `deleteTransaction sets error state when recurring item delete fails`() = runTest {
        val viewModel = buildViewModel(recurringItemId = 10L)
        coEvery { deleteRecurringItemUseCase(10L) } returns Resource.Error("network error")

        viewModel.deleteTransaction()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isError)
    }
}
