package com.example.fintrack.features.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PieChart
import com.example.fintrack.R
import com.example.fintrack.model.QuickActionItem

val quickActionItems = listOf(
    QuickActionItem(
        icon = Icons.Filled.Notifications,
        labelResId = R.string.quick_action_reminders,
        iconBackgroundColorRes = R.color.quick_action_background,
        iconTintRes = R.color.bottom_bar_fab,
        route = MainScreens.PaymentRemindersScreen.route
    ),
    QuickActionItem(
        icon = Icons.Filled.AccountBalance,
        labelResId = R.string.quick_action_savings,
        iconBackgroundColorRes = R.color.quick_action_background,
        iconTintRes = R.color.bottom_bar_fab,
        route = MainScreens.SavingsGoalsScreen.route
    ),
    QuickActionItem(
        icon = Icons.Filled.PieChart,
        labelResId = R.string.quick_action_budget,
        iconBackgroundColorRes = R.color.quick_action_background,
        iconTintRes = R.color.bottom_bar_fab,
        route = MainScreens.BudgetLimitsScreen.route
    ),
    QuickActionItem(
        icon = Icons.Filled.Description,
        labelResId = R.string.quick_action_reports,
        iconBackgroundColorRes = R.color.quick_action_background,
        iconTintRes = R.color.bottom_bar_fab,
        route = MainScreens.ReportsScreen.route
    )
)