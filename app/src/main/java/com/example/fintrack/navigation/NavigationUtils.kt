package com.example.fintrack.navigation

import androidx.navigation.NavController

fun navigateAndClearBackStack(
    navController: NavController,
    destination: String,
    popUpToRoute: String,
    inclusive: Boolean = false
) {
    navController.navigate(destination) {
        popUpTo(popUpToRoute) {
            this.inclusive = inclusive
        }
        launchSingleTop = true
    }
}