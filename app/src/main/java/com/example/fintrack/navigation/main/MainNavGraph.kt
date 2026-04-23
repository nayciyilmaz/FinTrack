package com.example.fintrack.navigation.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.fintrack.screens.AddTransactionScreen
import com.example.fintrack.screens.AiAdvisorScreen
import com.example.fintrack.screens.BudgetLimitsScreen
import com.example.fintrack.screens.HomeScreen
import com.example.fintrack.screens.PaymentRemindersScreen
import com.example.fintrack.screens.ProfileScreen
import com.example.fintrack.screens.ReportsScreen
import com.example.fintrack.screens.SavingsGoalsScreen
import com.example.fintrack.screens.SpendingAnalysisScreen
import com.example.fintrack.screens.TransactionsScreen

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable(route = MainScreens.HomeScreen.route) {
        HomeScreen(navController = navController)
    }
    composable(route = MainScreens.TransactionsScreen.route) {
        TransactionsScreen(navController = navController)
    }
    composable(route = MainScreens.AddTransactionScreen.route) {
        AddTransactionScreen(navController = navController)
    }
    composable(route = MainScreens.SpendingAnalysisScreen.route) {
        SpendingAnalysisScreen(navController = navController)
    }
    composable(route = MainScreens.SavingsGoalsScreen.route) {
        SavingsGoalsScreen(navController = navController)
    }
    composable(route = MainScreens.BudgetLimitsScreen.route) {
        BudgetLimitsScreen(navController = navController)
    }
    composable(route = MainScreens.PaymentRemindersScreen.route) {
        PaymentRemindersScreen(navController = navController)
    }
    composable(route = MainScreens.ReportsScreen.route) {
        ReportsScreen(navController = navController)
    }
    composable(route = MainScreens.ProfileScreen.route) {
        ProfileScreen(navController = navController)
    }
    composable(route = MainScreens.AiAdvisorScreen.route) {
        AiAdvisorScreen(navController = navController)
    }
}