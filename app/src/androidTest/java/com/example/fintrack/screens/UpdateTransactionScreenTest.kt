package com.example.fintrack.screens

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fintrack.R
import com.example.fintrack.domain.usecase.DeleteRecurringItemUseCase
import com.example.fintrack.domain.usecase.DeleteTransactionUseCase
import com.example.fintrack.domain.usecase.UpdateRecurringItemUseCase
import com.example.fintrack.domain.usecase.UpdateTransactionUseCase
import com.example.fintrack.fake.FakeRecurringItemRepository
import com.example.fintrack.fake.FakeTransactionRepository
import com.example.fintrack.presentation.navigation.FinTrackScreens
import com.example.fintrack.presentation.screens.transaction_update.UpdateTransactionScreen
import com.example.fintrack.presentation.screens.transaction_update.UpdateTransactionViewModel
import org.junit.Rule
import org.junit.Test

class UpdateTransactionScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun buildViewModel(
        fakeTransactionRepository: FakeTransactionRepository = FakeTransactionRepository(),
        fakeRecurringItemRepository: FakeRecurringItemRepository = FakeRecurringItemRepository(),
        extraSavedState: Map<String, Any?> = emptyMap()
    ): UpdateTransactionViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "transactionId" to 1L,
                "type" to "EXPENSE",
                "category" to "MARKET",
                "amount" to "100",
                "date" to "2026-08-11",
                "time" to "10:00",
                "isRecurring" to "false",
                "isReminder" to "false",
                "note" to "Test note"
            ) + extraSavedState
        )

        return UpdateTransactionViewModel(
            updateTransactionUseCase = UpdateTransactionUseCase(fakeTransactionRepository),
            deleteTransactionUseCase = DeleteTransactionUseCase(fakeTransactionRepository),
            updateRecurringItemUseCase = UpdateRecurringItemUseCase(fakeRecurringItemRepository),
            deleteRecurringItemUseCase = DeleteRecurringItemUseCase(fakeRecurringItemRepository),
            savedStateHandle = savedStateHandle
        )
    }

    private fun setUpdateTransactionContent(viewModel: UpdateTransactionViewModel) {
        composeRule.setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = FinTrackScreens.UpdateTransactionScreen.route) {
                composable(route = FinTrackScreens.UpdateTransactionScreen.route) {
                    UpdateTransactionScreen(navController = navController, viewModel = viewModel)
                }
                composable(route = FinTrackScreens.HomeScreen.route) {
                    Text("HOME_PLACEHOLDER")
                }
            }
        }
    }

    @Test
    fun updateTransaction_whenAmountChanged_savesUpdatedAmount() {
        val fakeTransactionRepository = FakeTransactionRepository()
        setUpdateTransactionContent(buildViewModel(fakeTransactionRepository = fakeTransactionRepository))

        composeRule.onNodeWithText("100").performTextReplacement("250")

        val saveButtonText = context.getString(R.string.label_save)
        composeRule.onNodeWithText(saveButtonText).performScrollTo().performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("HOME_PLACEHOLDER").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("HOME_PLACEHOLDER").assertIsDisplayed()
        assert(fakeTransactionRepository.lastUpdateTransactionAmount == 250.0) {
            "Expected updated amount 250.0 but was ${fakeTransactionRepository.lastUpdateTransactionAmount}"
        }
    }

    @Test
    fun updateRecurringItem_whenSuccessful_updatesRecurringAmount() {
        val fakeRecurringItemRepository = FakeRecurringItemRepository()
        setUpdateTransactionContent(
            buildViewModel(
                fakeRecurringItemRepository = fakeRecurringItemRepository,
                extraSavedState = mapOf("recurringItemId" to 5L)
            )
        )

        val saveButtonText = context.getString(R.string.label_save)
        composeRule.onNodeWithText(saveButtonText).performScrollTo().performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("HOME_PLACEHOLDER").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("HOME_PLACEHOLDER").assertIsDisplayed()
        assert(fakeRecurringItemRepository.lastUpdateRecurringItemAmount == 100.0) {
            "Expected recurring amount 100.0 but was ${fakeRecurringItemRepository.lastUpdateRecurringItemAmount}"
        }
        assert(fakeRecurringItemRepository.lastUpdateRecurringItemDayOfMonth == 11) {
            "Expected day of month 11 but was ${fakeRecurringItemRepository.lastUpdateRecurringItemDayOfMonth}"
        }
    }

    @Test
    fun deleteTransaction_whenConfirmed_navigatesToHomeScreen() {
        setUpdateTransactionContent(buildViewModel())

        val deleteButtonText = context.getString(R.string.label_delete)
        val confirmButtonText = context.getString(R.string.label_confirm_action)

        composeRule.onNodeWithText(deleteButtonText).performClick()
        composeRule.onNodeWithText(confirmButtonText).performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("HOME_PLACEHOLDER").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("HOME_PLACEHOLDER").assertIsDisplayed()
    }

    @Test
    fun deleteDialog_whenDismissed_staysOnUpdateScreen() {
        setUpdateTransactionContent(buildViewModel())

        val deleteButtonText = context.getString(R.string.label_delete)
        val cancelButtonText = context.getString(R.string.label_cancel)
        val dialogMessage = context.getString(R.string.message_delete_transaction)

        composeRule.onNodeWithText(deleteButtonText).performScrollTo().performClick()
        composeRule.onNodeWithText(dialogMessage).assertIsDisplayed()
        composeRule.onNodeWithText(cancelButtonText).performClick()

        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText(dialogMessage).fetchSemanticsNodes().isEmpty()
        }
    }
}
