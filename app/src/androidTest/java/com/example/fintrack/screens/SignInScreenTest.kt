package com.example.fintrack.screens

import android.Manifest
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
import com.example.fintrack.domain.usecase.GoogleSignInUseCase
import com.example.fintrack.domain.usecase.LoginUseCase
import com.example.fintrack.fake.FakeAuthRepository
import com.example.fintrack.notification.ReminderNotificationScheduler
import com.example.fintrack.presentation.navigation.FinTrackScreens
import com.example.fintrack.presentation.screens.sign_in.SignInScreen
import com.example.fintrack.presentation.screens.sign_in.SignInViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignInScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun grantNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                "pm grant ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}"
            ).close()
        }
    }

    private fun buildViewModel(fakeAuthRepository: FakeAuthRepository): SignInViewModel {
        return SignInViewModel(
            loginUseCase = LoginUseCase(fakeAuthRepository),
            googleSignInUseCase = GoogleSignInUseCase(fakeAuthRepository),
            reminderNotificationScheduler = ReminderNotificationScheduler(context),
            context = context
        )
    }

    @Test
    fun login_whenCredentialsValid_navigatesToHomeScreen() {
        val viewModel = buildViewModel(FakeAuthRepository())

        val emailLabel = context.getString(R.string.sign_in_email)
        val passwordLabel = context.getString(R.string.sign_in_password)
        val loginButtonText = context.getString(R.string.sign_in_title)

        composeRule.setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = FinTrackScreens.SignInScreen.route) {
                composable(route = FinTrackScreens.SignInScreen.route) {
                    SignInScreen(navController = navController, viewModel = viewModel)
                }
                composable(route = FinTrackScreens.HomeScreen.route) {
                    Text("HOME_PLACEHOLDER")
                }
            }
        }

        composeRule.onNodeWithText(emailLabel).performTextInput("test@fintrack.com")
        composeRule.onNodeWithText(passwordLabel).performTextInput("Password123")
        composeRule.onNode(hasText(loginButtonText).and(hasClickAction())).performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText("HOME_PLACEHOLDER").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("HOME_PLACEHOLDER").assertIsDisplayed()
    }

    @Test
    fun login_whenCredentialsInvalid_showsValidationError() {
        val errorMessage = "Invalid email or password"
        val viewModel = buildViewModel(
            FakeAuthRepository(loginResult = Resource.Error(message = errorMessage))
        )

        val emailLabel = context.getString(R.string.sign_in_email)
        val passwordLabel = context.getString(R.string.sign_in_password)
        val loginButtonText = context.getString(R.string.sign_in_title)

        composeRule.setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = FinTrackScreens.SignInScreen.route) {
                composable(route = FinTrackScreens.SignInScreen.route) {
                    SignInScreen(navController = navController, viewModel = viewModel)
                }
                composable(route = FinTrackScreens.HomeScreen.route) {
                    Text("HOME_PLACEHOLDER")
                }
            }
        }

        composeRule.onNodeWithText(emailLabel).performTextInput("wrong@fintrack.com")
        composeRule.onNodeWithText(passwordLabel).performTextInput("WrongPassword")
        composeRule.onNode(hasText(loginButtonText).and(hasClickAction())).performClick()

        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithText(errorMessage).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun emailField_acceptsTextInput() {
        val viewModel = buildViewModel(FakeAuthRepository())
        val emailLabel = context.getString(R.string.sign_in_email)

        composeRule.setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = FinTrackScreens.SignInScreen.route) {
                composable(route = FinTrackScreens.SignInScreen.route) {
                    SignInScreen(navController = navController, viewModel = viewModel)
                }
                composable(route = FinTrackScreens.HomeScreen.route) {
                    Text("HOME_PLACEHOLDER")
                }
            }
        }

        composeRule.onNodeWithText(emailLabel).performTextInput("newuser@fintrack.com")
        composeRule.onNodeWithText("newuser@fintrack.com").assertIsDisplayed()
    }
}
