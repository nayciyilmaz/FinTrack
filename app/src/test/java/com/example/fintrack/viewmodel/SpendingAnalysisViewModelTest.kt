package com.example.fintrack.viewmodel

import android.content.Context
import android.content.SharedPreferences
import com.example.fintrack.core.util.LocaleHelper
import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.analysis.SpendingAnalysisViewModel
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import com.example.fintrack.R
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

class SpendingAnalysisViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var context: Context
    private lateinit var viewModel: SpendingAnalysisViewModel

    private fun buildTransaction(category: String, amount: Double, date: LocalDate = LocalDate.now()) = Transaction(
        id = 1L, type = TransactionType.EXPENSE, category = category, amount = amount, note = null,
        date = date.toString(), time = "12:00:00", isRecurring = false, isReminder = false, createdAt = "${date}T12:00:00"
    )

    private fun setUpViewModel(initialResult: Resource<List<Transaction>> = Resource.Success(emptyList())) {
        Dispatchers.setMain(testDispatcher)

        getTransactionsUseCase = mockk()
        context = mockk()

        val sharedPreferences = mockk<SharedPreferences>()
        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(any(), any()) } answers { secondArg() }

        mockkObject(LocaleHelper)
        every { LocaleHelper.setLocale(any(), any()) } returns context

        every { context.getString(R.string.label_spending_period_day) } returns "Day"
        every { context.getString(R.string.label_spending_period_week) } returns "Week"
        every { context.getString(R.string.label_spending_period_month) } returns "Month"
        every { context.getString(R.string.label_spending_summary_highest, any()) } returns "Highest"
        every { context.getString(R.string.label_spending_summary_lowest, any()) } returns "Lowest"

        coEvery { getTransactionsUseCase(TransactionType.EXPENSE, any(), any()) } returns initialResult

        viewModel = SpendingAnalysisViewModel(getTransactionsUseCase, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkObject(LocaleHelper)
    }

    @Test
    fun `init loads category distribution and trend on construction`() = runTest {
        setUpViewModel(Resource.Success(listOf(buildTransaction("FOOD", 100.0))))

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.actionState.value
        assertTrue(!state.isCategoryLoading)
        assertTrue(!state.isTrendLoading)
        assertEquals(1, state.categoryDistribution.size)
    }

    @Test
    fun `category distribution groups top three categories and lumps rest into OTHER`() = runTest {
        val today = LocalDate.now()
        val transactions = listOf(
            buildTransaction("FOOD", 400.0, today),
            buildTransaction("RENT", 300.0, today),
            buildTransaction("TRANSPORT", 200.0, today),
            buildTransaction("HEALTH", 100.0, today)
        )
        setUpViewModel(Resource.Success(transactions))

        testDispatcher.scheduler.advanceUntilIdle()

        val distribution = viewModel.actionState.value.categoryDistribution
        assertEquals(4, distribution.size)
        assertEquals("OTHER", distribution.last().categoryKey)
        assertEquals(100.0, distribution.last().amount, 0.001)
    }

    @Test
    fun `loadCategoryDistribution sets error state when call fails`() = runTest {
        setUpViewModel(Resource.Error("network error"))

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isCategoryError)
    }

    @Test
    fun `onCategoryPeriodChanged updates selected index and reloads`() = runTest {
        setUpViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onCategoryPeriodChanged(4)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(4, viewModel.uiState.value.selectedCategoryPeriodIndex)
    }

    @Test
    fun `onTrendPeriodChanged updates selected index and reloads`() = runTest {
        setUpViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onTrendPeriodChanged(3)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.selectedTrendPeriodIndex)
    }
}
