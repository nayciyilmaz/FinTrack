package com.example.fintrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import com.example.fintrack.R
import com.example.fintrack.navigation.main.MainScreens
import com.example.fintrack.model.NavigationItem

val bottomBarRoutes = setOf(
    MainScreens.HomeScreen.route,
    MainScreens.SpendingAnalysisScreen.route,
    MainScreens.AddTransactionScreen.route,
    MainScreens.AiAdvisorScreen.route,
    MainScreens.ProfileScreen.route
)

val bottomNavItems = listOf(
    NavigationItem(
        route = MainScreens.HomeScreen.route,
        icon = Icons.Filled.Home,
        labelResId = R.string.nav_home
    ),
    NavigationItem(
        route = MainScreens.SpendingAnalysisScreen.route,
        icon = Icons.Filled.Analytics,
        labelResId = R.string.nav_analysis
    ),
    NavigationItem(
        route = "",
        icon = Icons.Filled.Home,
        labelResId = R.string.nav_home
    ),
    NavigationItem(
        route = MainScreens.AiAdvisorScreen.route,
        icon = Icons.Filled.SmartToy,
        labelResId = R.string.nav_ai_advisor
    ),
    NavigationItem(
        route = MainScreens.ProfileScreen.route,
        icon = Icons.Filled.Person,
        labelResId = R.string.nav_profile
    )
)