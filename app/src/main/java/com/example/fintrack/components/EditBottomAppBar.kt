package com.example.fintrack.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fintrack.R
import com.example.fintrack.features.main.MainScreens
import com.example.fintrack.features.main.leftItems
import com.example.fintrack.features.main.rightItems
import com.example.fintrack.model.NavigationItem
import com.example.fintrack.util.navigateAndClearBackStack

@Composable
fun EditBottomAppBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        BottomBarBackground()

        Row(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                leftItems.forEach { item ->
                    BottomNavItemView(
                        item = item,
                        selected = currentRoute == item.route,
                        onClick = {
                            navigateAndClearBackStack(
                                navController = navController,
                                destination = item.route,
                                popUpToRoute = MainScreens.HomeScreen.route,
                                inclusive = item.route == MainScreens.HomeScreen.route
                            )
                        }
                    )
                }
            }

            Row(
                modifier = modifier.weight(0.5f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {}

            Row(
                modifier = modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                rightItems.forEach { item ->
                    BottomNavItemView(
                        item = item,
                        selected = currentRoute == item.route,
                        onClick = {
                            navigateAndClearBackStack(
                                navController = navController,
                                destination = item.route,
                                popUpToRoute = MainScreens.HomeScreen.route,
                                inclusive = false
                            )
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navigateAndClearBackStack(
                    navController = navController,
                    destination = MainScreens.AddTransactionScreen.route,
                    popUpToRoute = MainScreens.HomeScreen.route,
                    inclusive = false
                )
            },
            modifier = modifier
                .wrapContentSize()
                .align(Alignment.TopCenter)
                .offset(y = 16.dp)
                .border(width = 2.5.dp, color = Color.White, shape = CircleShape),
            shape = CircleShape,
            containerColor = colorResource(id = R.color.bottom_bar_fab)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun BottomBarBackground(modifier: Modifier = Modifier) {
    val barColor = colorResource(id = R.color.bottom_bar_background)
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val bumpRadius = 100f
        val bumpDepth = 60f
        val barTop = bumpDepth

        val path = Path().apply {
            moveTo(0f, barTop)
            lineTo(cx - bumpRadius - 40f, barTop)
            cubicTo(
                cx - bumpRadius, barTop,
                cx - bumpRadius, barTop - bumpDepth,
                cx, barTop - bumpDepth
            )
            cubicTo(
                cx + bumpRadius, barTop - bumpDepth,
                cx + bumpRadius, barTop,
                cx + bumpRadius + 40f, barTop
            )
            lineTo(w, barTop)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(path = path, color = barColor)
    }
}

@Composable
private fun BottomNavItemView(
    item: NavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = if (selected) Color.White else Color.White.copy(alpha = 0.5f)
        )
        Text(
            text = stringResource(id = item.labelResId),
            color = if (selected) Color.White else Color.White.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelSmall
        )
    }
}