package com.example.fintrack.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fintrack.presentation.screens.advisor.AiAdvisorScreen
import com.example.fintrack.presentation.screens.analysis.SpendingAnalysisScreen
import com.example.fintrack.presentation.screens.budget.BudgetLimitsScreen
import com.example.fintrack.presentation.screens.forgot_password.ForgotPasswordScreen
import com.example.fintrack.presentation.screens.home.HomeScreen
import com.example.fintrack.presentation.screens.profile.ProfileScreen
import com.example.fintrack.presentation.screens.reminders.PaymentRemindersScreen
import com.example.fintrack.presentation.screens.reports.ReportsScreen
import com.example.fintrack.presentation.screens.savings.SavingsGoalsScreen
import com.example.fintrack.presentation.screens.sign_in.SignInScreen
import com.example.fintrack.presentation.screens.sign_up.SignUpScreen
import com.example.fintrack.presentation.screens.transaction_add.AddTransactionScreen
import com.example.fintrack.presentation.screens.transaction_update.UpdateTransactionScreen
import com.example.fintrack.presentation.screens.transactions.TransactionsScreen

@Composable
fun FinTrackNavigation(viewModel: MainViewModel = hiltViewModel()) {
    val destination by viewModel.destination.collectAsState()
    val startDestination = destination ?: return
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.sessionExpired.collect {
            navController.navigate(FinTrackScreens.SignInScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = FinTrackScreens.SignInScreen.route) {
            SignInScreen(navController = navController)
        }
        composable(route = FinTrackScreens.SignUpScreen.route) {
            SignUpScreen(navController = navController)
        }
        composable(route = FinTrackScreens.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(route = FinTrackScreens.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(route = FinTrackScreens.TransactionsScreen.route) {
            TransactionsScreen(navController = navController)
        }
        composable(route = FinTrackScreens.AddTransactionScreen.route) {
            AddTransactionScreen(navController = navController)
        }
        composable(
            route = "${FinTrackScreens.UpdateTransactionScreen.route}?transactionId={transactionId}&type={type}&category={category}&amount={amount}&date={date}&time={time}&isRecurring={isRecurring}&isReminder={isReminder}&note={note}",
            arguments = listOf(
                navArgument("transactionId") { type = NavType.LongType; defaultValue = 0L },
                navArgument("type") { type = NavType.StringType; defaultValue = "EXPENSE" },
                navArgument("category") { type = NavType.StringType; defaultValue = "" },
                navArgument("amount") { type = NavType.StringType; defaultValue = "" },
                navArgument("date") { type = NavType.StringType; defaultValue = "" },
                navArgument("time") { type = NavType.StringType; defaultValue = "" },
                navArgument("isRecurring") { type = NavType.StringType; defaultValue = "false" },
                navArgument("isReminder") { type = NavType.StringType; defaultValue = "false" },
                navArgument("note") { type = NavType.StringType; defaultValue = "" }
            )
        ) {
            UpdateTransactionScreen(navController = navController)
        }
        composable(route = FinTrackScreens.SpendingAnalysisScreen.route) {
            SpendingAnalysisScreen(navController = navController)
        }
        composable(route = FinTrackScreens.SavingsGoalsScreen.route) {
            SavingsGoalsScreen(navController = navController)
        }
        composable(route = FinTrackScreens.BudgetLimitsScreen.route) {
            BudgetLimitsScreen(navController = navController)
        }
        composable(route = FinTrackScreens.PaymentRemindersScreen.route) {
            PaymentRemindersScreen(navController = navController)
        }
        composable(route = FinTrackScreens.ReportsScreen.route) {
            ReportsScreen(navController = navController)
        }
        composable(route = FinTrackScreens.ProfileScreen.route) {
            ProfileScreen(navController = navController)
        }
        composable(route = FinTrackScreens.AiAdvisorScreen.route) {
            AiAdvisorScreen(navController = navController)
        }
    }
}