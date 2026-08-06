package com.example.fintrack.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import com.example.fintrack.R
import com.example.fintrack.presentation.model.NavigationItem

val bottomBarRoutes = setOf(
    FinTrackScreens.HomeScreen.route,
    FinTrackScreens.SpendingAnalysisScreen.route,
    FinTrackScreens.AddTransactionScreen.route,
    FinTrackScreens.AiAdvisorScreen.route,
    FinTrackScreens.ProfileScreen.route
)

val bottomNavItems = listOf(
    NavigationItem(
        route = FinTrackScreens.HomeScreen.route,
        icon = Icons.Filled.Home,
        labelResId = R.string.nav_home
    ),
    NavigationItem(
        route = FinTrackScreens.SpendingAnalysisScreen.route,
        icon = Icons.Filled.Analytics,
        labelResId = R.string.nav_analysis
    ),
    NavigationItem(
        route = "",
        icon = Icons.Filled.Home,
        labelResId = R.string.nav_home
    ),
    NavigationItem(
        route = FinTrackScreens.AiAdvisorScreen.route,
        icon = Icons.Filled.SmartToy,
        labelResId = R.string.nav_ai_advisor
    ),
    NavigationItem(
        route = FinTrackScreens.ProfileScreen.route,
        icon = Icons.Filled.Person,
        labelResId = R.string.nav_profile
    )
)