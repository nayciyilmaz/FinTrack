package com.example.fintrack.navigation.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.fintrack.screens.ForgotPasswordScreen
import com.example.fintrack.screens.SignInScreen
import com.example.fintrack.screens.SignUpScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    composable(route = AuthScreens.SignInScreen.route) {
        SignInScreen(navController = navController)
    }
    composable(route = AuthScreens.SignUpScreen.route) {
        SignUpScreen(navController = navController)
    }
    composable(route = AuthScreens.ForgotPasswordScreen.route) {
        ForgotPasswordScreen(navController = navController)
    }
}