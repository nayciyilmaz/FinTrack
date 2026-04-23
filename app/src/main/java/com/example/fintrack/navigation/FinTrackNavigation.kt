package com.example.fintrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.navigation.auth.authNavGraph
import com.example.fintrack.navigation.main.MainScreens
import com.example.fintrack.navigation.main.mainNavGraph

@Composable
fun FinTrackNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = MainScreens.HomeScreen.route) {
        authNavGraph(navController)
        mainNavGraph(navController)
    }
}