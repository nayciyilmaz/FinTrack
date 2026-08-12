package com.example.fintrack.viewmodel

import android.content.Context
import android.content.SharedPreferences
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.savings.SavingsGoalsViewModel
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.AddSavingsGoalUseCase
import com.example.fintrack.domain.usecase.DeleteSavingsGoalUseCase
import com.example.fintrack.domain.usecase.GetSavingsGoalsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.UpdateSavingsGoalUseCase
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SavingsGoalsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var getSavingsGoalsUseCase: GetSavingsGoalsUseCase
    private lateinit var addSavingsGoalUseCase: AddSavingsGoalUseCase
    private lateinit var updateSavingsGoalUseCase: UpdateSavingsGoalUseCase
    private lateinit var deleteSavingsGoalUseCase: DeleteSavingsGoalUseCase
    private lateinit var tokenManager: TokenManager
    private lateinit var context: Context
    private lateinit var viewModel: SavingsGoalsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getTransactionsUseCase = mockk()
        getSavingsGoalsUseCase = mockk()
        addSavingsGoalUseCase = mockk()
        updateSavingsGoalUseCase = mockk()
        deleteSavingsGoalUseCase = mockk()
        tokenManager = mockk()
        context = mockk()

        every { tokenManager.getPayday() } returns flowOf(5)

        val sharedPreferences = mockk<SharedPreferences>()
        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(any(), any()) } answers { secondArg() }

        every { context.getString(R.string.savings_goal_error_category_required) } returns "Category required"
        every { context.getString(R.string.savings_goal_error_name_required) } returns "Name required"
        every { context.getString(R.string.savings_goal_error_invalid_amount) } returns "Invalid amount"
        every { context.getString(R.string.savings_goal_error_fields_required) } returns "Fields required"
        every { context.getString(R.string.savings_goal_error_invalid_target_amount) } returns "Invalid target amount"

        viewModel = SavingsGoalsViewModel(
            getTransactionsUseCase, getSavingsGoalsUseCase, addSavingsGoalUseCase,
            updateSavingsGoalUseCase, deleteSavingsGoalUseCase, tokenManager, context
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildGoal(id: Long = 1L, currentAmount: Double = 0.0) = SavingsGoal(
        id = id, name = "Vacation", category = "TRAVEL", targetAmount = 5000.0,
        currentAmount = currentAmount, createdAt = "2026-01-01T00:00:00"
    )

    @Test
    fun `onGoalNameChange updates value and clears error`() {
        viewModel.onGoalNameChange("Vacation")

        assertEquals("Vacation", viewModel.uiState.value.goalName)
        assertEquals(null, viewModel.uiState.value.nameError)
    }

    @Test
    fun `onTargetAmountChange filters non digit characters`() {
        viewModel.onTargetAmountChange("a5b0c0")

        assertEquals("500", viewModel.uiState.value.targetAmount)
    }

    @Test
    fun `addGoal sets validation errors when required fields missing`() {
        viewModel.addGoal()

        assertEquals("Category required", viewModel.uiState.value.categoryError)
        assertEquals("Name required", viewModel.uiState.value.nameError)
        assertEquals("Invalid amount", viewModel.uiState.value.amountError)
    }

    @Test
    fun `addGoal adds new goal to front of list when successful`() = runTest {
        viewModel.onGoalNameChange("Vacation")
        viewModel.onCategoryChange(1, "TRAVEL")
        viewModel.onTargetAmountChange("5000")
        val newGoal = buildGoal()
        coEvery { addSavingsGoalUseCase("Vacation", "TRAVEL", 5000.0) } returns Resource.Success(newGoal)

        viewModel.addGoal()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.actionState.value.goals.size)
        assertTrue(!viewModel.uiState.value.showAddDialog)
    }

    @Test
    fun `addGoal keeps isSaving false when api call fails`() = runTest {
        viewModel.onGoalNameChange("Vacation")
        viewModel.onCategoryChange(1, "TRAVEL")
        viewModel.onTargetAmountChange("5000")
        coEvery { addSavingsGoalUseCase("Vacation", "TRAVEL", 5000.0) } returns Resource.Error("network error")

        viewModel.addGoal()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(!viewModel.actionState.value.isSaving)
        assertEquals(0, viewModel.actionState.value.goals.size)
    }

    @Test
    fun `updateGoal sets error when both fields empty`() {
        viewModel.selectGoal(buildGoal())

        viewModel.updateGoal()

        assertEquals("Fields required", viewModel.uiState.value.updateError)
    }

    @Test
    fun `updateGoal updates matching goal when successful`() = runTest {
        val goal = buildGoal()
        viewModel.selectGoal(goal)
        viewModel.onAddAmountChange("200")
        val updated = goal.copy(currentAmount = 200.0)
        coEvery { updateSavingsGoalUseCase(1L, 200.0, null) } returns Resource.Success(updated)

        viewModel.updateGoal()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(!viewModel.actionState.value.isSaving)
    }

    @Test
    fun `deleteGoal removes goal from list when successful`() = runTest {
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Success(emptyList())
        coEvery { getSavingsGoalsUseCase() } returns Resource.Success(listOf(buildGoal(1L)))
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectGoal(buildGoal(1L))
        coEvery { deleteSavingsGoalUseCase(1L) } returns Resource.Success(Unit)

        viewModel.deleteGoal()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.actionState.value.goals.size)
    }

    @Test
    fun `loadData computes total and last month savings when calls succeed`() = runTest {
        val transactions = listOf(
            Transaction(1L, TransactionType.INCOME, "SALARY", 3000.0, null, "2026-01-05", "09:00:00", false, false, "2026-01-05T09:00:00"),
            Transaction(2L, TransactionType.EXPENSE, "FOOD", 1000.0, null, "2026-01-10", "12:00:00", false, false, "2026-01-10T12:00:00")
        )
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Success(transactions)
        coEvery { getSavingsGoalsUseCase() } returns Resource.Success(emptyList())

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2000, viewModel.actionState.value.totalSavings)
        assertTrue(!viewModel.actionState.value.isError)
    }

    @Test
    fun `loadData computes estimated date for goal in progress`() = runTest {
        val goalInProgress = buildGoal(currentAmount = 1000.0).copy(createdAt = "2025-01-01T00:00:00")
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns Resource.Success(emptyList())
        coEvery { getSavingsGoalsUseCase() } returns Resource.Success(listOf(goalInProgress))

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.actionState.value.estimatedDates[goalInProgress.id])
    }
}
