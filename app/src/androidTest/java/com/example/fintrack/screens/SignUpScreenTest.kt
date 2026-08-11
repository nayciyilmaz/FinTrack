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

    private fun buildViewModel(fakeAuthRepository: FakeAuthRepository): SignUpViewModel {
        return SignUpViewModel(
            registerUseCase = RegisterUseCase(fakeAuthRepository),
            addTransactionUseCase = AddTransactionUseCase(FakeTransactionRepository()),
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
    fun firstNameField_acceptsTextInput() {
        val viewModel = buildViewModel(FakeAuthRepository())
        setSignUpContent(viewModel)

        composeRule.onNodeWithText(context.getString(R.string.sign_up_first_name)).performTextInput("Ayse")
        composeRule.onNodeWithText("Ayse").assertIsDisplayed()
    }
}
