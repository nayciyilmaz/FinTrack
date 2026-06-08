package com.example.fintrack.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PieChart
import com.example.fintrack.R
import com.example.fintrack.presentation.navigation.FinTrackScreens
import com.example.fintrack.model.QuickActionItem

val quickActionItems = listOf(
    QuickActionItem(
        icon = Icons.Filled.Notifications,
        labelResId = R.string.quick_action_reminders,
        iconBackgroundColorRes = R.color.quick_action_background,
        iconTintRes = R.color.bottom_bar_fab,
        route = FinTrackScreens.PaymentRemindersScreen.route
    ),
    QuickActionItem(
        icon = Icons.Filled.AccountBalance,
        labelResId = R.string.quick_action_savings,
        iconBackgroundColorRes = R.color.quick_action_background,
        iconTintRes = R.color.bottom_bar_fab,
        route = FinTrackScreens.SavingsGoalsScreen.route
    ),
    QuickActionItem(
        icon = Icons.Filled.PieChart,
        labelResId = R.string.quick_action_budget,
        iconBackgroundColorRes = R.color.quick_action_background,
        iconTintRes = R.color.bottom_bar_fab,
        route = FinTrackScreens.BudgetLimitsScreen.route
    ),
    QuickActionItem(
        icon = Icons.Filled.Description,
        labelResId = R.string.quick_action_reports,
        iconBackgroundColorRes = R.color.quick_action_background,
        iconTintRes = R.color.bottom_bar_fab,
        route = FinTrackScreens.ReportsScreen.route
    )
)