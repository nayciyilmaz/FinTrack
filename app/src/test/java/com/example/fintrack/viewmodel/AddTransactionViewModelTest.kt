package com.example.fintrack.viewmodel

import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.transaction_add.AddTransactionViewModel
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.AddTransactionUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.presentation.model.TransactionCategory
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
import java.time.LocalTime

class AddTransactionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var addTransactionUseCase: AddTransactionUseCase
    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var viewModel: AddTransactionViewModel

    private fun setUpViewModel(initialResult: Resource<List<Transaction>> = Resource.Success(emptyList())) {
        Dispatchers.setMain(testDispatcher)
        addTransactionUseCase = mockk()
        getTransactionsUseCase = mockk()
        coEvery { getTransactionsUseCase(any(), any(), any()) } returns initialResult
        viewModel = AddTransactionViewModel(addTransactionUseCase, getTransactionsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildCategory(key: String) = TransactionCategory(
        labelResId = 1, icon = mockk(), key = key
    )

    @Test
    fun `init loads quick add suggestions on construction`() = runTest {
        val transaction = Transaction(
            1L, TransactionType.EXPENSE, "FOOD", 50.0, null,
            LocalDate.now().toString(), "12:00:00", false, false, "2026-01-15T12:00:00"
        )
        setUpViewModel(Resource.Success(listOf(transaction)))

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.quickAddSuggestions.size)
    }

    @Test
    fun `onTypeChange resets category amount and note`() {
        setUpViewModel()
        viewModel.onCategoryChange(buildCategory("FOOD"))
        viewModel.onAmountChange("100")
        viewModel.onNoteChange("lunch")

        viewModel.onTypeChange(1)

        assertEquals(1, viewModel.uiState.value.selectedTypeIndex)
        assertEquals(null, viewModel.uiState.value.selectedCategory)
        assertEquals("", viewModel.uiState.value.amount)
        assertEquals("", viewModel.uiState.value.note)
    }

    @Test
    fun `onCategoryChange updates category and clears validation error`() {
        setUpViewModel()
        val category = buildCategory("FOOD")

        viewModel.onCategoryChange(category)

        assertEquals(category, viewModel.uiState.value.selectedCategory)
        assertTrue(!viewModel.uiState.value.showValidationError)
    }

    @Test
    fun `addTransaction shows validation error when required fields missing`() {
        setUpViewModel()

        viewModel.addTransaction()

        assertTrue(viewModel.uiState.value.showValidationError)
    }

    @Test
    fun `addTransaction sets success state when all fields valid`() = runTest {
        setUpViewModel()
        viewModel.onCategoryChange(buildCategory("FOOD"))
        viewModel.onAmountChange("50")
        viewModel.onDatePickerShow()
        viewModel.onTempDateChange(LocalDate.of(2026, 1, 15))
        viewModel.onDateConfirm()
        viewModel.onTimeConfirm(LocalTime.of(12, 0))
        coEvery { addTransactionUseCase(any(), any(), any(), any(), any(), any(), any(), any()) } returns
            Resource.Success(mockk())

        viewModel.addTransaction()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isSuccess)
    }

    @Test
    fun `addTransaction sets loading false when api call fails`() = runTest {
        setUpViewModel()
        viewModel.onCategoryChange(buildCategory("FOOD"))
        viewModel.onAmountChange("50")
        viewModel.onDatePickerShow()
        viewModel.onTempDateChange(LocalDate.of(2026, 1, 15))
        viewModel.onDateConfirm()
        viewModel.onTimeConfirm(LocalTime.of(12, 0))
        coEvery { addTransactionUseCase(any(), any(), any(), any(), any(), any(), any(), any()) } returns
            Resource.Error("network error")

        viewModel.addTransaction()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(!viewModel.actionState.value.isLoading)
        assertTrue(!viewModel.actionState.value.isSuccess)
    }
}
