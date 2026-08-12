package com.example.fintrack.viewmodel

import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.budget.BudgetLimitsViewModel
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.domain.model.Budget
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.GetBudgetsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.SaveBudgetsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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

class BudgetLimitsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var getBudgetsUseCase: GetBudgetsUseCase
    private lateinit var saveBudgetsUseCase: SaveBudgetsUseCase
    private lateinit var tokenManager: TokenManager
    private lateinit var viewModel: BudgetLimitsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getTransactionsUseCase = mockk()
        getBudgetsUseCase = mockk()
        saveBudgetsUseCase = mockk()
        tokenManager = mockk()
        every { tokenManager.getPayday() } returns flowOf(5)

        viewModel = BudgetLimitsViewModel(getTransactionsUseCase, getBudgetsUseCase, saveBudgetsUseCase, tokenManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildTransaction(type: TransactionType, category: String, amount: Double) = Transaction(
        id = 1L, type = type, category = category, amount = amount, note = null,
        date = "2026-01-15", time = "12:00:00", isRecurring = false, isReminder = false, createdAt = "2026-01-15T12:00:00"
    )

    @Test
    fun `loadData computes income expense and category expenses when calls succeed`() = runTest {
        val transactions = listOf(
            buildTransaction(TransactionType.INCOME, "SALARY", 2000.0),
            buildTransaction(TransactionType.EXPENSE, "MARKET", 300.0)
        )
        val budgets = listOf(Budget(1L, "MARKET", 500.0))
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Success(transactions)
        coEvery { getBudgetsUseCase() } returns Resource.Success(budgets)

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.actionState.value
        assertEquals(2000, state.income)
        assertEquals(300, state.expense)
        assertEquals(300, state.categoryExpenses["MARKET"])
        assertEquals(1, state.budgets.size)
    }

    @Test
    fun `loadData sets error state when a call fails`() = runTest {
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Error("network error")
        coEvery { getBudgetsUseCase() } returns Resource.Success(emptyList())

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isError)
    }

    @Test
    fun `onDialogAmountChange filters non digit characters`() {
        viewModel.onDialogAmountChange("MARKET", "a5b0c0")

        assertEquals("500", viewModel.uiState.value.dialogAmounts["MARKET"])
    }

    @Test
    fun `onDialogActiveToggle flips active state for category`() {
        viewModel.onDialogActiveToggle("MARKET")
        assertEquals(true, viewModel.uiState.value.dialogActiveStates["MARKET"])

        viewModel.onDialogActiveToggle("MARKET")
        assertEquals(false, viewModel.uiState.value.dialogActiveStates["MARKET"])
    }

    @Test
    fun `saveDialogBudgets does nothing when total limit exceeds income`() = runTest {
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Success(
            listOf(buildTransaction(TransactionType.INCOME, "SALARY", 100.0))
        )
        coEvery { getBudgetsUseCase() } returns Resource.Success(emptyList())
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onDialogAmountChange("MARKET", "500")
        viewModel.onDialogActiveToggle("MARKET")

        viewModel.saveDialogBudgets()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { saveBudgetsUseCase(any()) }
    }

    @Test
    fun `saveDialogBudgets saves only active categories with positive amounts`() = runTest {
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Success(
            listOf(buildTransaction(TransactionType.INCOME, "SALARY", 2000.0))
        )
        coEvery { getBudgetsUseCase() } returns Resource.Success(emptyList())
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onDialogAmountChange("MARKET", "500")
        viewModel.onDialogActiveToggle("MARKET")
        coEvery { saveBudgetsUseCase(listOf("MARKET" to 500.0)) } returns Resource.Success(
            listOf(Budget(1L, "MARKET", 500.0))
        )

        viewModel.saveDialogBudgets()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.actionState.value.budgets.size)
        assertTrue(!viewModel.uiState.value.showManageDialog)
    }
}
