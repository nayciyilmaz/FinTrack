package com.example.fintrack.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.example.fintrack.presentation.screens.transactions.TransactionsScreen

@Composable
fun FinTrackNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = FinTrackScreens.HomeScreen.route) {
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