package com.example.fintrack.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenCrypto
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.domain.usecase.AddSavingsGoalUseCase
import com.example.fintrack.domain.usecase.DeleteSavingsGoalUseCase
import com.example.fintrack.domain.usecase.GetSavingsGoalsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.UpdateSavingsGoalUseCase
import com.example.fintrack.fake.FakeSavingsGoalRepository
import com.example.fintrack.fake.FakeTransactionRepository
import com.example.fintrack.presentation.navigation.FinTrackScreens
import com.example.fintrack.presentation.screens.savings.SavingsGoalsScreen
import com.example.fintrack.presentation.screens.savings.SavingsGoalsViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class SavingsGoalsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun buildViewModel(
        fakeSavingsGoalRepository: FakeSavingsGoalRepository = FakeSavingsGoalRepository(),
        fakeTransactionRepository: FakeTransactionRepository = FakeTransactionRepository()
    ): SavingsGoalsViewModel {
        val tokenManager = TokenManager(context, TokenCrypto())
        runBlocking { tokenManager.savePayday(1) }

        return SavingsGoalsViewModel(
            getTransactionsUseCase = GetTransactionsUseCase(fakeTransactionRepository),
            getSavingsGoalsUseCase = GetSavingsGoalsUseCase(fakeSavingsGoalRepository),
            addSavingsGoalUseCase = AddSavingsGoalUseCase(fakeSavingsGoalRepository),
            updateSavingsGoalUseCase = UpdateSavingsGoalUseCase(fakeSavingsGoalRepository),
            deleteSavingsGoalUseCase = DeleteSavingsGoalUseCase(fakeSavingsGoalRepository),
            tokenManager = tokenManager,
            context = context
        )
    }

    private fun setSavingsGoalsContent(viewModel: SavingsGoalsViewModel) {
        composeRule.setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = FinTrackScreens.SavingsGoalsScreen.route) {
                composable(route = FinTrackScreens.SavingsGoalsScreen.route) {
                    SavingsGoalsScreen(navController = navController, viewModel = viewModel)
                }
            }
        }
    }

    @Test
    fun loadData_whenSuccessful_showsGoalCard() {
        val goal = SavingsGoal(
            id = 1L,
            name = "Tatil Fonu",
            category = "Seyahat",
            targetAmount = 5000.0,
            currentAmount = 1000.0,
            createdAt = "2026-01-01T00:00:00"
        )
        val fakeSavingsGoalRepository = FakeSavingsGoalRepository(getGoalsResult = Resource.Success(listOf(goal)))
        setSavingsGoalsContent(buildViewModel(fakeSavingsGoalRepository = fakeSavingsGoalRepository))

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("Tatil Fonu").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Tatil Fonu").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun loadData_whenError_showsErrorMessage() {
        val fakeSavingsGoalRepository = FakeSavingsGoalRepository(getGoalsResult = Resource.Error(message = "Server error"))
        setSavingsGoalsContent(buildViewModel(fakeSavingsGoalRepository = fakeSavingsGoalRepository))

        val errorText = context.getString(R.string.error_general)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(errorText).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(errorText).assertIsDisplayed()
    }

    @Test
    fun addGoal_whenRequiredFieldsMissing_showsValidationErrors() {
        setSavingsGoalsContent(buildViewModel())

        val newGoalText = context.getString(R.string.label_new_goal)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(newGoalText).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(newGoalText).performClick()

        val addText = context.getString(R.string.label_add)
        composeRule.onNodeWithText(addText).performClick()

        composeRule.onNodeWithText(context.getString(R.string.savings_goal_error_category_required)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.savings_goal_error_name_required)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.savings_goal_error_invalid_amount)).assertIsDisplayed()
    }

    @Test
    fun addGoal_whenServerError_keepsDialogOpenWithoutAddingGoal() {
        val fakeSavingsGoalRepository = FakeSavingsGoalRepository(
            addGoalResult = Resource.Error(message = "Server error")
        )
        setSavingsGoalsContent(buildViewModel(fakeSavingsGoalRepository = fakeSavingsGoalRepository))

        val newGoalText = context.getString(R.string.label_new_goal)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(newGoalText).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(newGoalText).performClick()

        val categoryText = context.getString(R.string.goal_category_seyahat)
        composeRule.onNodeWithText(categoryText).performClick()

        composeRule.onNodeWithText(context.getString(R.string.savings_goal_name_label)).performTextInput("Tatil Fonu")
        composeRule.onNodeWithText(context.getString(R.string.savings_goal_amount_label)).performTextInput("5000")

        val addText = context.getString(R.string.label_add)
        composeRule.onNodeWithText(addText).performClick()

        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText(addText).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText("Tatil Fonu").fetchSemanticsNodes().let { nodes ->
            assert(nodes.size == 1) {
                "Expected goal name only in the dialog field, not added to the list, but found ${nodes.size} matches"
            }
        }
    }

    @Test
    fun addGoal_whenValidDataProvided_addsGoalToList() {
        val fakeSavingsGoalRepository = FakeSavingsGoalRepository()
        setSavingsGoalsContent(buildViewModel(fakeSavingsGoalRepository = fakeSavingsGoalRepository))

        val newGoalText = context.getString(R.string.label_new_goal)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(newGoalText).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(newGoalText).performClick()

        val categoryText = context.getString(R.string.goal_category_seyahat)
        composeRule.onNodeWithText(categoryText).performClick()

        composeRule.onNodeWithText(context.getString(R.string.savings_goal_name_label)).performTextInput("Tatil Fonu")
        composeRule.onNodeWithText(context.getString(R.string.savings_goal_amount_label)).performTextInput("5000")

        val addText = context.getString(R.string.label_add)
        composeRule.onNodeWithText(addText).performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("Tatil Fonu").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Tatil Fonu").performScrollTo().assertIsDisplayed()
        assert(fakeSavingsGoalRepository.lastAddGoalCategory == categoryText) {
            "Expected category $categoryText but was ${fakeSavingsGoalRepository.lastAddGoalCategory}"
        }
    }
}
