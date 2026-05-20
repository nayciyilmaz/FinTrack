package com.example.fintrack.navigation

enum class FinTrackScreens(val route: String) {
    SignInScreen("sign_in_screen"),
    SignUpScreen("sign_up_screen"),
    ForgotPasswordScreen("forgot_password_screen"),
    HomeScreen("home_screen"),
    TransactionsScreen("transactions_screen"),
    AddTransactionScreen("add_transaction_screen"),
    SpendingAnalysisScreen("spending_analysis_screen"),
    SavingsGoalsScreen("savings_goals_screen"),
    BudgetLimitsScreen("budget_limits_screen"),
    PaymentRemindersScreen("payment_reminders_screen"),
    ReportsScreen("reports_screen"),
    ProfileScreen("profile_screen"),
    AiAdvisorScreen("ai_advisor_screen")
}