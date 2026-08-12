package com.example.fintrack.viewmodel

import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.reminders.PaymentRemindersViewModel
import com.example.fintrack.domain.model.RecurringItem
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.GetRecurringItemsUseCase
import com.example.fintrack.domain.usecase.GetReminderTransactionsUseCase
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
import java.time.LocalDate

class PaymentRemindersViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getRecurringItemsUseCase: GetRecurringItemsUseCase
    private lateinit var getReminderTransactionsUseCase: GetReminderTransactionsUseCase
    private lateinit var viewModel: PaymentRemindersViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getRecurringItemsUseCase = mockk()
        getReminderTransactionsUseCase = mockk()
        viewModel = PaymentRemindersViewModel(getRecurringItemsUseCase, getReminderTransactionsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildTransaction(date: LocalDate) = Transaction(
        id = 1L, type = TransactionType.EXPENSE, category = "RENT", amount = 500.0, note = null,
        date = date.toString(), time = "09:00:00", isRecurring = true, isReminder = true, createdAt = "2026-01-01T00:00:00"
    )

    @Test
    fun `loadData populates actionState when both calls succeed`() = runTest {
        val recurringItem = RecurringItem(1L, TransactionType.EXPENSE, "RENT", 500.0, 1)
        val reminder = buildTransaction(LocalDate.now().plusDays(5))
        coEvery { getRecurringItemsUseCase() } returns Resource.Success(listOf(recurringItem))
        coEvery { getReminderTransactionsUseCase() } returns Resource.Success(listOf(reminder))

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.actionState.value
        assertEquals(1, state.recurringItems.size)
        assertEquals(1, state.reminderTransactions.size)
        assertTrue(!state.isError)
    }

    @Test
    fun `loadData sets error state when a call fails`() = runTest {
        coEvery { getRecurringItemsUseCase() } returns Resource.Error("network error")
        coEvery { getReminderTransactionsUseCase() } returns Resource.Success(emptyList())

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isError)
    }

    @Test
    fun `onFilterChange updates selected filter index`() {
        viewModel.onFilterChange(1)

        assertEquals(1, viewModel.uiState.value.selectedFilterIndex)
    }

    @Test
    fun `filterReminders returns items due in three or more days when filterIndex is one`() {
        val soon = buildTransaction(LocalDate.now().plusDays(1))
        val later = buildTransaction(LocalDate.now().plusDays(5))

        val result = viewModel.filterReminders(1, listOf(soon, later))

        assertEquals(1, result.size)
        assertEquals(later.date, result.first().date)
    }

    @Test
    fun `filterReminders returns items due within three days when filterIndex is not one`() {
        val soon = buildTransaction(LocalDate.now().plusDays(1))
        val later = buildTransaction(LocalDate.now().plusDays(5))

        val result = viewModel.filterReminders(0, listOf(soon, later))

        assertEquals(1, result.size)
        assertEquals(soon.date, result.first().date)
    }
}
