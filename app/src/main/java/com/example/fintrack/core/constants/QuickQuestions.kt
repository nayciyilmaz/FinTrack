package com.example.fintrack.core.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.fintrack.R
import com.example.fintrack.domain.model.QuickQuestion

const val QUICK_QUESTION_CATEGORY_EXPENSE = "EXPENSE"
const val QUICK_QUESTION_CATEGORY_ANALYSIS = "ANALYSIS"
const val QUICK_QUESTION_CATEGORY_BUDGET = "BUDGET"
const val QUICK_QUESTION_CATEGORY_SAVINGS = "SAVINGS"
const val QUICK_QUESTION_CATEGORY_GENERAL = "GENERAL"
const val QUICK_QUESTION_CATEGORY_INCOME = "INCOME"
const val QUICK_QUESTION_CATEGORY_PAYMENTS = "PAYMENTS"

val quickQuestions = listOf(
    QuickQuestion(R.string.quick_question_expense_1, R.string.quick_question_category_expense, R.string.quick_question_title_expense_1, QUICK_QUESTION_CATEGORY_EXPENSE),
    QuickQuestion(R.string.quick_question_expense_2, R.string.quick_question_category_expense, R.string.quick_question_title_expense_2, QUICK_QUESTION_CATEGORY_EXPENSE),
    QuickQuestion(R.string.quick_question_expense_3, R.string.quick_question_category_expense, R.string.quick_question_title_expense_3, QUICK_QUESTION_CATEGORY_EXPENSE),
    QuickQuestion(R.string.quick_question_analysis_1, R.string.quick_question_category_analysis, R.string.quick_question_title_analysis_1, QUICK_QUESTION_CATEGORY_ANALYSIS),
    QuickQuestion(R.string.quick_question_budget_1, R.string.quick_question_category_budget, R.string.quick_question_title_budget_1, QUICK_QUESTION_CATEGORY_BUDGET),
    QuickQuestion(R.string.quick_question_budget_2, R.string.quick_question_category_budget, R.string.quick_question_title_budget_2, QUICK_QUESTION_CATEGORY_BUDGET),
    QuickQuestion(R.string.quick_question_savings_1, R.string.quick_question_category_savings, R.string.quick_question_title_savings_1, QUICK_QUESTION_CATEGORY_SAVINGS),
    QuickQuestion(R.string.quick_question_savings_2, R.string.quick_question_category_savings, R.string.quick_question_title_savings_2, QUICK_QUESTION_CATEGORY_SAVINGS),
    QuickQuestion(R.string.quick_question_savings_3, R.string.quick_question_category_savings, R.string.quick_question_title_savings_3, QUICK_QUESTION_CATEGORY_SAVINGS),
    QuickQuestion(R.string.quick_question_general_1, R.string.quick_question_category_general, R.string.quick_question_title_general_1, QUICK_QUESTION_CATEGORY_GENERAL),
    QuickQuestion(R.string.quick_question_general_2, R.string.quick_question_category_general, R.string.quick_question_title_general_2, QUICK_QUESTION_CATEGORY_GENERAL),
    QuickQuestion(R.string.quick_question_general_3, R.string.quick_question_category_general, R.string.quick_question_title_general_3, QUICK_QUESTION_CATEGORY_GENERAL),
    QuickQuestion(R.string.quick_question_general_4, R.string.quick_question_category_general, R.string.quick_question_title_general_4, QUICK_QUESTION_CATEGORY_GENERAL),
    QuickQuestion(R.string.quick_question_income_1, R.string.quick_question_category_income, R.string.quick_question_title_income_1, QUICK_QUESTION_CATEGORY_INCOME),
    QuickQuestion(R.string.quick_question_income_2, R.string.quick_question_category_income, R.string.quick_question_title_income_2, QUICK_QUESTION_CATEGORY_INCOME),
    QuickQuestion(R.string.quick_question_payments_1, R.string.quick_question_category_payments, R.string.quick_question_title_payments_1, QUICK_QUESTION_CATEGORY_PAYMENTS)
)

fun quickQuestionCategoryLabelResId(categoryKey: String) = quickQuestions
    .find { it.categoryKey == categoryKey }?.categoryResId
    ?: R.string.quick_question_category_general

@Composable
fun quickQuestionTitleFor(question: String): String {
    val match = quickQuestions.find { stringResource(id = it.questionResId) == question }
    return if (match != null) stringResource(id = match.titleResId) else question
}

fun quickQuestionCategoryIcon(categoryKey: String) = when (categoryKey) {
    QUICK_QUESTION_CATEGORY_EXPENSE -> Icons.Filled.TrendingDown
    QUICK_QUESTION_CATEGORY_ANALYSIS -> Icons.Filled.Insights
    QUICK_QUESTION_CATEGORY_BUDGET -> Icons.Filled.AccountBalanceWallet
    QUICK_QUESTION_CATEGORY_SAVINGS -> Icons.Filled.Savings
    QUICK_QUESTION_CATEGORY_GENERAL -> Icons.Filled.Psychology
    QUICK_QUESTION_CATEGORY_INCOME -> Icons.Filled.AttachMoney
    QUICK_QUESTION_CATEGORY_PAYMENTS -> Icons.Filled.Payments
    else -> Icons.Filled.Star
}