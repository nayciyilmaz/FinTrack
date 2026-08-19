package com.example.fintrack.screens

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
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
import com.example.fintrack.core.util.CurrencyHelper
import com.example.fintrack.core.util.Resource
import com.example.fintrack.core.util.apiDateFormatter
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.usecase.AddTransactionUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.fake.FakeTransactionRepository
import com.example.fintrack.presentation.navigation.FinTrackScreens
import com.example.fintrack.presentation.screens.transaction_add.AddTransactionScreen
import com.example.fintrack.presentation.screens.transaction_add.AddTransactionViewModel
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class AddTransactionScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun buildViewModel(fakeTransactionRepository: FakeTransactionRepository = FakeTransactionRepository()): AddTransactionViewModel {
        return AddTransactionViewModel(
            addTransactionUseCase = AddTransactionUseCase(fakeTransactionRepository),
            getTransactionsUseCase = GetTransactionsUseCase(fakeTransactionRepository)
        )
    }

    private fun setAddTransactionContent(viewModel: AddTransactionViewModel) {
        composeRule.setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = FinTrackScreens.AddTransactionScreen.route) {
                composable(route = FinTrackScreens.AddTransactionScreen.route) {
                    AddTransactionScreen(navController = navController, viewModel = viewModel)
                }
                composable(route = FinTrackScreens.HomeScreen.route) {
                    Text("HOME_PLACEHOLDER")
                }
            }
        }
    }

    @Test
    fun addTransaction_whenRequiredFieldsMissing_showsValidationError() {
        setAddTransactionContent(buildViewModel())

        val saveButtonText = context.getString(R.string.label_save)
        val errorText = context.getString(R.string.error_required_fields)

        composeRule.onNode(hasText(saveButtonText).and(hasClickAction())).performScrollTo().performClick()
        composeRule.onNodeWithText(errorText).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun addTransaction_whenValidDataProvided_navigatesToHomeScreen() {
        setAddTransactionContent(buildViewModel())

        val categoryText = context.getString(R.string.category_market)
        composeRule.onNodeWithText(categoryText).performClick()

        val amountPlaceholder = context.getString(R.string.label_amount_placeholder, CurrencyHelper.getSymbol(context))
        composeRule.onNodeWithText(amountPlaceholder).performTextInput("250")

        val dateLabel = context.getString(R.string.label_date)
        val timeLabel = context.getString(R.string.label_time)
        val confirmText = context.getString(R.string.label_confirm)

        composeRule.onNodeWithText(dateLabel).performClick()
        composeRule.onNodeWithText(confirmText).performClick()

        composeRule.onNodeWithText(timeLabel).performClick()
        composeRule.onNodeWithText(confirmText).performClick()

        val saveButtonText = context.getString(R.string.label_save)
        composeRule.onNode(hasText(saveButtonText).and(hasClickAction())).performScrollTo().performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("HOME_PLACEHOLDER").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("HOME_PLACEHOLDER").assertIsDisplayed()
    }

    @Test
    fun addTransaction_whenServerError_staysOnAddScreen() {
        val fakeTransactionRepository = FakeTransactionRepository(
            addTransactionResult = Resource.Error(message = "Server error")
        )
        setAddTransactionContent(buildViewModel(fakeTransactionRepository))

        val categoryText = context.getString(R.string.category_market)
        composeRule.onNodeWithText(categoryText).performClick()

        val amountPlaceholder = context.getString(R.string.label_amount_placeholder, CurrencyHelper.getSymbol(context))
        composeRule.onNodeWithText(amountPlaceholder).performTextInput("250")

        val dateLabel = context.getString(R.string.label_date)
        val timeLabel = context.getString(R.string.label_time)
        val confirmText = context.getString(R.string.label_confirm)

        composeRule.onNodeWithText(dateLabel).performClick()
        composeRule.onNodeWithText(confirmText).performClick()

        composeRule.onNodeWithText(timeLabel).performClick()
        composeRule.onNodeWithText(confirmText).performClick()

        val saveButtonText = context.getString(R.string.label_save)
        composeRule.onNode(hasText(saveButtonText).and(hasClickAction())).performScrollTo().performClick()

        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText(saveButtonText).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText("HOME_PLACEHOLDER").fetchSemanticsNodes().isEmpty()
    }

    @Test
    fun quickAddSuggestion_whenSelected_prefillsAmountField() {
        val suggestedTransaction = Transaction(
            id = 1L,
            type = TransactionType.EXPENSE,
            category = "MARKET",
            amount = 500.0,
            note = null,
            date = LocalDate.now().minusDays(1).format(apiDateFormatter),
            time = "10:00",
            isRecurring = false,
            isReminder = false,
            createdAt = LocalDate.now().minusDays(1).format(apiDateFormatter)
        )
        val fakeTransactionRepository = FakeTransactionRepository(
            getTransactionsResult = Resource.Success(listOf(suggestedTransaction))
        )
        setAddTransactionContent(buildViewModel(fakeTransactionRepository))

        val symbol = CurrencyHelper.getSymbol(context)
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("${symbol}500").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("${symbol}500").performClick()

        composeRule.onNodeWithText("500").assertIsDisplayed()
    }

    @Test
    fun transactionTypeSelector_switchesToIncomeCategories() {
        setAddTransactionContent(buildViewModel())

        val incomeOptionText = context.resources.getStringArray(R.array.transaction_type_options)[1]
        composeRule.onNodeWithText(incomeOptionText).performClick()

        val salaryCategoryText = context.getString(R.string.category_maas)
        composeRule.onNodeWithText(salaryCategoryText).assertIsDisplayed()
    }

    @Test
    fun noteField_acceptsTextInput() {
        setAddTransactionContent(buildViewModel())

        val notePlaceholder = context.getString(R.string.label_note_placeholder)
        composeRule.onNodeWithText(notePlaceholder).performTextInput("Market alisverisi")
        composeRule.onNodeWithText("Market alisverisi").assertIsDisplayed()
    }
}
