package com.example.fintrack.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.fintrack.R
import com.example.fintrack.features.main.MainScreens
import com.example.fintrack.features.main.bottomNavItems
import com.example.fintrack.util.navigateAndClearBackStack

@Composable
fun EditBottomAppBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
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
                                    destination = MainScreens.AddTransactionScreen.route,
                                    popUpToRoute = MainScreens.HomeScreen.route,
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
                                popUpToRoute = MainScreens.HomeScreen.route,
                                inclusive = item.route == MainScreens.HomeScreen.route
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
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = colorResource(id = R.color.bottom_bar_fab),
                        unselectedTextColor = Color.Gray,
                        indicatorColor = colorResource(id = R.color.icon_orange).copy(alpha = 0.15f)
                    )
                )
            }
        }
    }
}