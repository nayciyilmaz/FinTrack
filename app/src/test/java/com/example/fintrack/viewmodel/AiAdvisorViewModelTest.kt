package com.example.fintrack.viewmodel

import com.example.fintrack.core.util.Resource
import com.example.fintrack.presentation.screens.advisor.AiAdvisorViewModel
import com.example.fintrack.domain.model.AdvisorInsight
import com.example.fintrack.domain.model.AdvisorSummary
import com.example.fintrack.domain.usecase.AskAdvisorQuestionUseCase
import com.example.fintrack.domain.usecase.GetAdvisorInsightsUseCase
import com.example.fintrack.domain.usecase.GetAdvisorSummaryUseCase
import com.example.fintrack.domain.usecase.RefreshAdvisorInsightUseCase
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AiAdvisorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getAdvisorSummaryUseCase: GetAdvisorSummaryUseCase
    private lateinit var getAdvisorInsightsUseCase: GetAdvisorInsightsUseCase
    private lateinit var askAdvisorQuestionUseCase: AskAdvisorQuestionUseCase
    private lateinit var refreshAdvisorInsightUseCase: RefreshAdvisorInsightUseCase
    private lateinit var viewModel: AiAdvisorViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getAdvisorSummaryUseCase = mockk()
        getAdvisorInsightsUseCase = mockk()
        askAdvisorQuestionUseCase = mockk()
        refreshAdvisorInsightUseCase = mockk()

        viewModel = AiAdvisorViewModel(
            getAdvisorSummaryUseCase, getAdvisorInsightsUseCase, askAdvisorQuestionUseCase, refreshAdvisorInsightUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildInsight(id: Long, question: String, answer: String) =
        AdvisorInsight(id = id, categoryKey = "BUDGET", question = question, answer = answer)

    @Test
    fun `loadData populates actionState when both calls succeed`() = runTest {
        val summary = AdvisorSummary(periodText = "1-31 Ocak", healthScore = 80, riskLevel = "Düşük")
        val insights = listOf(buildInsight(1L, "q1", "a1"))
        coEvery { getAdvisorSummaryUseCase() } returns Resource.Success(summary)
        coEvery { getAdvisorInsightsUseCase() } returns Resource.Success(insights)

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.actionState.value
        assertEquals(80, state.healthScore)
        assertEquals("Düşük", state.riskLevel)
        assertEquals(1, state.insights.size)
        assertTrue(!state.isError)
    }

    @Test
    fun `loadData sets error state when summary call fails`() = runTest {
        coEvery { getAdvisorSummaryUseCase() } returns Resource.Error("network error")
        coEvery { getAdvisorInsightsUseCase() } returns Resource.Success(emptyList())

        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isError)
    }

    @Test
    fun `onShowQuickQuestionsDialog and onDismiss toggle dialog visibility`() {
        viewModel.onShowQuickQuestionsDialog()
        assertTrue(viewModel.uiState.value.showQuickQuestionsDialog)

        viewModel.onDismissQuickQuestionsDialog()
        assertTrue(!viewModel.uiState.value.showQuickQuestionsDialog)
    }

    @Test
    fun `onQuestionAsked adds new insight to front of list when successful`() = runTest {
        val newInsight = buildInsight(2L, "new question", "new answer")
        coEvery { askAdvisorQuestionUseCase("BUDGET", "new question") } returns Resource.Success(newInsight)

        viewModel.onQuestionAsked("BUDGET", "new question")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.actionState.value.insights.size)
        assertEquals("new answer", viewModel.actionState.value.insights.first().answer)
    }

    @Test
    fun `onQuestionAsked skips api call when question already asked`() = runTest {
        val existingInsight = buildInsight(1L, "existing question", "existing answer")
        coEvery { getAdvisorSummaryUseCase() } returns Resource.Success(
            AdvisorSummary(periodText = "", healthScore = 0, riskLevel = "")
        )
        coEvery { getAdvisorInsightsUseCase() } returns Resource.Success(listOf(existingInsight))
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onQuestionAsked("BUDGET", "existing question")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { askAdvisorQuestionUseCase(any(), any()) }
    }

    @Test
    fun `onQuestionAsked sets error state when api call fails`() = runTest {
        coEvery { askAdvisorQuestionUseCase("BUDGET", "q") } returns Resource.Error("AI service unavailable")

        viewModel.onQuestionAsked("BUDGET", "q")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.actionState.value.isError)
        assertTrue(!viewModel.actionState.value.isLoading)
    }

    @Test
    fun `onInsightRefreshRequested updates matching insight when successful`() = runTest {
        val original = buildInsight(1L, "q1", "old answer")
        coEvery { getAdvisorSummaryUseCase() } returns Resource.Success(
            AdvisorSummary(periodText = "", healthScore = 0, riskLevel = "")
        )
        coEvery { getAdvisorInsightsUseCase() } returns Resource.Success(listOf(original))
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        val updated = buildInsight(1L, "q1", "new answer")
        coEvery { refreshAdvisorInsightUseCase(1L) } returns Resource.Success(updated)

        viewModel.onInsightRefreshRequested(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("new answer", viewModel.actionState.value.insights.first().answer)
        assertNull(viewModel.actionState.value.refreshingInsightId)
    }

    @Test
    fun `onInsightRefreshRequested clears refreshingInsightId when call fails`() = runTest {
        coEvery { refreshAdvisorInsightUseCase(1L) } returns Resource.Error("not found")

        viewModel.onInsightRefreshRequested(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.actionState.value.refreshingInsightId)
    }
}
