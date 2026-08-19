package com.example.fintrack.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fintrack.R
import com.example.fintrack.core.util.CurrencyHelper
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenCrypto
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.domain.model.Budget
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.GetBudgetsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.SaveBudgetsUseCase
import com.example.fintrack.fake.FakeBudgetRepository
import com.example.fintrack.fake.FakeTransactionRepository
import com.example.fintrack.presentation.navigation.FinTrackScreens
import com.example.fintrack.presentation.screens.budget.BudgetLimitsScreen
import com.example.fintrack.presentation.screens.budget.BudgetLimitsViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class BudgetLimitsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun buildViewModel(
        fakeBudgetRepository: FakeBudgetRepository = FakeBudgetRepository(),
        fakeTransactionRepository: FakeTransactionRepository = FakeTransactionRepository()
    ): BudgetLimitsViewModel {
        val tokenManager = TokenManager(context, TokenCrypto())
        runBlocking { tokenManager.savePayday(1) }

        return BudgetLimitsViewModel(
            getTransactionsUseCase = GetTransactionsUseCase(fakeTransactionRepository),
            getBudgetsUseCase = GetBudgetsUseCase(fakeBudgetRepository),
            saveBudgetsUseCase = SaveBudgetsUseCase(fakeBudgetRepository),
            tokenManager = tokenManager
        )
    }

    private fun setBudgetLimitsContent(viewModel: BudgetLimitsViewModel) {
        composeRule.setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = FinTrackScreens.BudgetLimitsScreen.route) {
                composable(route = FinTrackScreens.BudgetLimitsScreen.route) {
                    BudgetLimitsScreen(navController = navController, viewModel = viewModel)
                }
            }
        }
    }

    private fun incomeTransaction(amount: Double) = Transaction(
        id = 1L,
        type = TransactionType.INCOME,
        category = "SALARY",
        amount = amount,
        note = null,
        date = "2026-08-01",
        time = "09:00",
        isRecurring = false,
        isReminder = false,
        createdAt = "2026-08-01"
    )

    @Test
    fun loadData_whenSuccessful_showsCategoryLimitCard() {
        val budget = Budget(id = 1L, category = "MARKET", limitAmount = 500.0)
        val fakeBudgetRepository = FakeBudgetRepository(getBudgetsResult = Resource.Success(listOf(budget)))
        setBudgetLimitsContent(buildViewModel(fakeBudgetRepository = fakeBudgetRepository))

        val marketCategoryText = context.getString(R.string.category_market)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(marketCategoryText).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(marketCategoryText).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun loadData_whenError_showsErrorMessage() {
        val fakeBudgetRepository = FakeBudgetRepository(getBudgetsResult = Resource.Error(message = "Server error"))
        setBudgetLimitsContent(buildViewModel(fakeBudgetRepository = fakeBudgetRepository))

        val errorText = context.getString(R.string.error_general)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(errorText).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(errorText).assertIsDisplayed()
    }

    @Test
    fun loadData_whenExpenseExceedsLimit_showsExceededStatus() {
        val budget = Budget(id = 1L, category = "MARKET", limitAmount = 500.0)
        val fakeBudgetRepository = FakeBudgetRepository(getBudgetsResult = Resource.Success(listOf(budget)))
        val overLimitExpense = Transaction(
            id = 2L,
            type = TransactionType.EXPENSE,
            category = "MARKET",
            amount = 600.0,
            note = null,
            date = "2026-08-05",
            time = "10:00",
            isRecurring = false,
            isReminder = false,
            createdAt = "2026-08-05"
        )
        val fakeTransactionRepository = FakeTransactionRepository(
            getTransactionsResult = Resource.Success(listOf(overLimitExpense))
        )
        setBudgetLimitsContent(
            buildViewModel(fakeBudgetRepository = fakeBudgetRepository, fakeTransactionRepository = fakeTransactionRepository)
        )

        val exceededText = context.getString(R.string.budget_status_exceeded)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(exceededText).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(exceededText).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun manageLimits_whenServerError_keepsPreviousLimit() {
        val budget = Budget(id = 1L, category = "MARKET", limitAmount = 500.0)
        val fakeBudgetRepository = FakeBudgetRepository(
            getBudgetsResult = Resource.Success(listOf(budget)),
            saveBudgetsResult = Resource.Error(message = "Server error")
        )
        val fakeTransactionRepository = FakeTransactionRepository(
            getTransactionsResult = Resource.Success(listOf(incomeTransaction(10000.0)))
        )
        setBudgetLimitsContent(
            buildViewModel(fakeBudgetRepository = fakeBudgetRepository, fakeTransactionRepository = fakeTransactionRepository)
        )

        val marketCategoryText = context.getString(R.string.category_market)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(marketCategoryText).fetchSemanticsNodes().isNotEmpty()
        }

        val editText = context.getString(R.string.label_edit)
        composeRule.onNodeWithText(editText).performClick()

        composeRule.onNodeWithText("500").performTextReplacement("750")

        val saveText = context.getString(R.string.label_save)
        composeRule.onNodeWithText(saveText).performClick()

        composeRule.waitUntil(timeoutMillis = 3000) {
            fakeBudgetRepository.lastSavedBudgets != null
        }

        val symbol = CurrencyHelper.getSymbol(context)
        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("${symbol}500").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("${symbol}500").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun manageLimits_whenAmountChanged_savesUpdatedLimit() {
        val budget = Budget(id = 1L, category = "MARKET", limitAmount = 500.0)
        val fakeBudgetRepository = FakeBudgetRepository(getBudgetsResult = Resource.Success(listOf(budget)))
        val fakeTransactionRepository = FakeTransactionRepository(
            getTransactionsResult = Resource.Success(listOf(incomeTransaction(10000.0)))
        )
        setBudgetLimitsContent(
            buildViewModel(fakeBudgetRepository = fakeBudgetRepository, fakeTransactionRepository = fakeTransactionRepository)
        )

        val marketCategoryText = context.getString(R.string.category_market)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(marketCategoryText).fetchSemanticsNodes().isNotEmpty()
        }

        val editText = context.getString(R.string.label_edit)
        composeRule.onNodeWithText(editText).performClick()

        composeRule.onNodeWithText("500").performTextReplacement("750")

        val saveText = context.getString(R.string.label_save)
        composeRule.onNodeWithText(saveText).performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            fakeBudgetRepository.lastSavedBudgets != null
        }
        assert(fakeBudgetRepository.lastSavedBudgets?.contains("MARKET" to 750.0) == true) {
            "Expected MARKET budget of 750.0 but was ${fakeBudgetRepository.lastSavedBudgets}"
        }

        val symbol = CurrencyHelper.getSymbol(context)
        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("${symbol}750").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("${symbol}750").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun manageLimits_whenTotalExceedsIncome_showsWarningAndBlocksSave() {
        val budget = Budget(id = 1L, category = "MARKET", limitAmount = 5000.0)
        val fakeBudgetRepository = FakeBudgetRepository(getBudgetsResult = Resource.Success(listOf(budget)))
        val fakeTransactionRepository = FakeTransactionRepository(
            getTransactionsResult = Resource.Success(listOf(incomeTransaction(1000.0)))
        )
        setBudgetLimitsContent(
            buildViewModel(fakeBudgetRepository = fakeBudgetRepository, fakeTransactionRepository = fakeTransactionRepository)
        )

        val marketCategoryText = context.getString(R.string.category_market)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(marketCategoryText).fetchSemanticsNodes().isNotEmpty()
        }

        val editText = context.getString(R.string.label_edit)
        composeRule.onNodeWithText(editText).performClick()

        val warningText = context.getString(R.string.warning_budget_exceeds_income)
        composeRule.onNodeWithText(warningText).assertIsDisplayed()

        val saveText = context.getString(R.string.label_save)
        composeRule.onNodeWithText(saveText).performClick()

        composeRule.onNodeWithText(warningText).assertIsDisplayed()
        assert(fakeBudgetRepository.lastSavedBudgets == null) {
            "Expected no save call but was ${fakeBudgetRepository.lastSavedBudgets}"
        }
    }
}
