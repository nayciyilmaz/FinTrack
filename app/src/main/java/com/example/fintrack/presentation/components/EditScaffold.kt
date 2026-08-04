package com.example.fintrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fintrack.R
import com.example.fintrack.presentation.navigation.FinTrackScreens
import com.example.fintrack.presentation.navigation.bottomBarRoutes
import com.example.fintrack.presentation.navigation.bottomNavItems
import com.example.fintrack.presentation.navigation.navigateAndClearBackStack

@Composable
fun EditScaffold(
    title: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomBarRoutes
    val showBackButton = !showBottomBar

    Scaffold(
        topBar = {
            EditTopAppBar(
                title = title,
                navController = navController,
                showBackButton = showBackButton
            )
        },
        bottomBar = {
            if (showBottomBar) {
                EditBottomAppBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorResource(id = R.color.scaffold_background))
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTopAppBar(
    title: String,
    navController: NavController,
    showBackButton: Boolean = false
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackButton) {
                EditIconButton(
                    onClick = { navController.popBackStack() },
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = colorResource(id = R.color.text_primary)
                )
            }
        }
    )
}

@Composable
private fun EditBottomAppBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = colorResource(id = R.color.card_background),
        contentColor = colorResource(id = R.color.bottom_bar_fab)
    ) {
        bottomNavItems.forEachIndexed { index, item ->
            if (index == 2) {
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = {
                        FloatingActionButton(
                            onClick = {
                                navigateAndClearBackStack(
                                    navController = navController,
                                    destination = FinTrackScreens.AddTransactionScreen.route,
                                    popUpToRoute = FinTrackScreens.HomeScreen.route,
                                    inclusive = false
                                )
                            },
                            containerColor = colorResource(id = R.color.bottom_bar_fab),
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    },
                    label = null,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            } else {
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navigateAndClearBackStack(
                                navController = navController,
                                destination = item.route,
                                popUpToRoute = FinTrackScreens.HomeScreen.route,
                                inclusive = item.route == FinTrackScreens.HomeScreen.route
                            )
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = stringResource(id = item.labelResId))
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorResource(id = R.color.bottom_bar_fab),
                        unselectedIconColor = colorResource(id = R.color.text_secondary),
                        selectedTextColor = colorResource(id = R.color.bottom_bar_fab),
                        unselectedTextColor = colorResource(id = R.color.text_secondary),
                        indicatorColor = colorResource(id = R.color.icon_orange).copy(alpha = 0.15f)
                    )
                )
            }
        }
    }
}