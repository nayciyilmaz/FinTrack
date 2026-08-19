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
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.usecase.AddTransactionUseCase
import com.example.fintrack.domain.usecase.RegisterUseCase
import com.example.fintrack.fake.FakeAuthRepository
import com.example.fintrack.fake.FakeTransactionRepository
import com.example.fintrack.presentation.navigation.FinTrackScreens
import com.example.fintrack.presentation.screens.sign_up.SignUpScreen
import com.example.fintrack.presentation.screens.sign_up.SignUpViewModel
import org.junit.Rule
import org.junit.Test

class SignUpScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun buildViewModel(
        fakeAuthRepository: FakeAuthRepository,
        fakeTransactionRepository: FakeTransactionRepository = FakeTransactionRepository()
    ): SignUpViewModel {
        return SignUpViewModel(
            registerUseCase = RegisterUseCase(fakeAuthRepository),
            addTransactionUseCase = AddTransactionUseCase(fakeTransactionRepository),
            context = context
        )
    }

    private fun setSignUpContent(viewModel: SignUpViewModel) {
        composeRule.setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = FinTrackScreens.SignUpScreen.route) {
                composable(route = FinTrackScreens.SignUpScreen.route) {
                    SignUpScreen(navController = navController, viewModel = viewModel)
                }
                composable(route = FinTrackScreens.SignInScreen.route) {
                    Text("SIGN_IN_PLACEHOLDER")
                }
            }
        }
    }

    @Test
    fun register_whenSuccessful_navigatesToSignInScreen() {
        val viewModel = buildViewModel(FakeAuthRepository())
        setSignUpContent(viewModel)

        composeRule.onNodeWithText(context.getString(R.string.sign_up_first_name)).performTextInput("Test")
        composeRule.onNodeWithText(context.getString(R.string.sign_up_last_name)).performTextInput("User")
        composeRule.onNodeWithText(context.getString(R.string.sign_in_email)).performTextInput("newuser@fintrack.com")
        composeRule.onNodeWithText(context.getString(R.string.sign_in_password)).performTextInput("Password123")
        composeRule.onNodeWithText(context.getString(R.string.sign_up_payday)).performTextInput("1")

        val registerButtonText = context.getString(R.string.sign_up_title)

        composeRule.onNode(hasText(registerButtonText).and(hasClickAction())).performClick()
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("SIGN_IN_PLACEHOLDER").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("SIGN_IN_PLACEHOLDER").assertIsDisplayed()
    }

    @Test
    fun register_whenInvalid_showsValidationError() {
        val errorMessage = "Email already in use"
        val viewModel = buildViewModel(
            FakeAuthRepository(registerResult = Resource.Error(message = errorMessage))
        )
        setSignUpContent(viewModel)

        composeRule.onNodeWithText(context.getString(R.string.sign_up_first_name)).performTextInput("Test")
        composeRule.onNodeWithText(context.getString(R.string.sign_up_last_name)).performTextInput("User")
        composeRule.onNodeWithText(context.getString(R.string.sign_in_email)).performTextInput("existing@fintrack.com")
        composeRule.onNodeWithText(context.getString(R.string.sign_in_password)).performTextInput("Password123")
        composeRule.onNodeWithText(context.getString(R.string.sign_up_payday)).performTextInput("1")

        val registerButtonText = context.getString(R.string.sign_up_title)

        composeRule.onNode(hasText(registerButtonText).and(hasClickAction())).performClick()
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(errorMessage).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun register_whenPaydayFieldErrorReturned_showsPaydayError() {
        val paydayErrorMessage = "Payday must be between 1 and 31"
        val viewModel = buildViewModel(
            FakeAuthRepository(
                registerResult = Resource.Error(
                    message = "Validation failed",
                    fieldErrors = mapOf("payday" to paydayErrorMessage)
                )
            )
        )
        setSignUpContent(viewModel)

        composeRule.onNodeWithText(context.getString(R.string.sign_up_first_name)).performTextInput("Test")
        composeRule.onNodeWithText(context.getString(R.string.sign_up_last_name)).performTextInput("User")
        composeRule.onNodeWithText(context.getString(R.string.sign_in_email)).performTextInput("newuser@fintrack.com")
        composeRule.onNodeWithText(context.getString(R.string.sign_in_password)).performTextInput("Password123")
        composeRule.onNodeWithText(context.getString(R.string.sign_up_payday)).performTextInput("99")

        val registerButtonText = context.getString(R.string.sign_up_title)

        composeRule.onNode(hasText(registerButtonText).and(hasClickAction())).performClick()
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(paydayErrorMessage).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(paydayErrorMessage).assertIsDisplayed()
        composeRule.onAllNodesWithText("SIGN_IN_PLACEHOLDER").fetchSemanticsNodes().isEmpty()
    }

    @Test
    fun register_whenSalaryProvided_createsSalaryTransaction() {
        val fakeTransactionRepository = FakeTransactionRepository()
        val viewModel = buildViewModel(FakeAuthRepository(), fakeTransactionRepository)
        setSignUpContent(viewModel)

        composeRule.onNodeWithText(context.getString(R.string.sign_up_first_name)).performTextInput("Test")
        composeRule.onNodeWithText(context.getString(R.string.sign_up_last_name)).performTextInput("User")
        composeRule.onNodeWithText(context.getString(R.string.sign_in_email)).performTextInput("newuser@fintrack.com")
        composeRule.onNodeWithText(context.getString(R.string.sign_in_password)).performTextInput("Password123")
        composeRule.onNodeWithText(context.getString(R.string.sign_up_payday)).performTextInput("15")
        composeRule.onNodeWithText(context.getString(R.string.sign_up_salary)).performTextInput("25000")

        val registerButtonText = context.getString(R.string.sign_up_title)

        composeRule.onNode(hasText(registerButtonText).and(hasClickAction())).performClick()
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("SIGN_IN_PLACEHOLDER").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("SIGN_IN_PLACEHOLDER").assertIsDisplayed()
        assert(fakeTransactionRepository.lastAddTransactionCategory == "SALARY") {
            "Expected SALARY category but was ${fakeTransactionRepository.lastAddTransactionCategory}"
        }
        assert(fakeTransactionRepository.lastAddTransactionAmount == 25000.0) {
            "Expected amount 25000.0 but was ${fakeTransactionRepository.lastAddTransactionAmount}"
        }
    }

    @Test
    fun register_whenSalaryEmpty_doesNotCreateSalaryTransaction() {
        val fakeTransactionRepository = FakeTransactionRepository()
        val viewModel = buildViewModel(FakeAuthRepository(), fakeTransactionRepository)
        setSignUpContent(viewModel)

        composeRule.onNodeWithText(context.getString(R.string.sign_up_first_name)).performTextInput("Test")
        composeRule.onNodeWithText(context.getString(R.string.sign_up_last_name)).performTextInput("User")
        composeRule.onNodeWithText(context.getString(R.string.sign_in_email)).performTextInput("newuser@fintrack.com")
        composeRule.onNodeWithText(context.getString(R.string.sign_in_password)).performTextInput("Password123")
        composeRule.onNodeWithText(context.getString(R.string.sign_up_payday)).performTextInput("15")

        val registerButtonText = context.getString(R.string.sign_up_title)

        composeRule.onNode(hasText(registerButtonText).and(hasClickAction())).performClick()
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("SIGN_IN_PLACEHOLDER").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("SIGN_IN_PLACEHOLDER").assertIsDisplayed()
        assert(fakeTransactionRepository.lastAddTransactionCategory == null) {
            "Expected no salary transaction but category was ${fakeTransactionRepository.lastAddTransactionCategory}"
        }
    }

    @Test
    fun firstNameField_acceptsTextInput() {
        val viewModel = buildViewModel(FakeAuthRepository())
        setSignUpContent(viewModel)

        composeRule.onNodeWithText(context.getString(R.string.sign_up_first_name)).performTextInput("Ayse")
        composeRule.onNodeWithText("Ayse").assertIsDisplayed()
    }
}
