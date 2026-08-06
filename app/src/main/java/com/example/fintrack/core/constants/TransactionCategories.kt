package com.example.fintrack.core.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import com.example.fintrack.R
import com.example.fintrack.presentation.model.GoalCategory
import com.example.fintrack.presentation.model.TransactionCategory

val expenseCategories = listOf(
    TransactionCategory(R.string.category_market, Icons.Filled.ShoppingCart, "MARKET"),
    TransactionCategory(R.string.category_kira, Icons.Filled.Home, "RENT"),
    TransactionCategory(R.string.category_ulasim, Icons.Filled.DirectionsBus, "TRANSPORT"),
    TransactionCategory(R.string.category_eglence, Icons.Filled.Theaters, "ENTERTAINMENT"),
    TransactionCategory(R.string.category_fatura, Icons.Filled.ElectricBolt, "BILL"),
    TransactionCategory(R.string.category_saglik, Icons.Filled.LocalHospital, "HEALTH"),
    TransactionCategory(R.string.category_giyim, Icons.Filled.Checkroom, "CLOTHING"),
    TransactionCategory(R.string.category_spor, Icons.Filled.FitnessCenter, "SPORT"),
    TransactionCategory(R.string.category_egitim, Icons.Filled.MenuBook, "EDUCATION"),
    TransactionCategory(R.string.category_abonelik, Icons.Filled.Subscriptions, "SUBSCRIPTION"),
    TransactionCategory(R.string.category_diger, Icons.Filled.MoreHoriz, "OTHER")
)

val incomeCategories = listOf(
    TransactionCategory(R.string.category_maas, Icons.Filled.Payments, "SALARY"),
    TransactionCategory(R.string.category_freelance, Icons.Filled.Work, "FREELANCE"),
    TransactionCategory(R.string.category_kira_geliri, Icons.Filled.Home, "RENT_INCOME"),
    TransactionCategory(R.string.category_yatirim, Icons.Filled.TrendingUp, "INVESTMENT"),
    TransactionCategory(R.string.category_hediye, Icons.Filled.CardGiftcard, "GIFT"),
    TransactionCategory(R.string.category_bonus, Icons.Filled.Star, "BONUS"),
    TransactionCategory(R.string.category_faiz, Icons.Filled.AccountBalance, "INTEREST"),
    TransactionCategory(R.string.category_diger_gelir, Icons.Filled.MoreHoriz, "OTHER_INCOME")
)

private val allCategories = expenseCategories + incomeCategories
fun categoryKeyToIcon(key: String) = allCategories.find { it.key == key }?.icon ?: Icons.Filled.MoreHoriz
fun categoryKeyToLabelResId(key: String) = allCategories.find { it.key == key }?.labelResId ?: R.string.category_diger

val goalCategories = listOf(
    GoalCategory(Icons.Filled.Home, R.string.goal_category_konut),
    GoalCategory(Icons.Filled.DirectionsCar, R.string.goal_category_tasit),
    GoalCategory(Icons.Filled.BeachAccess, R.string.goal_category_seyahat),
    GoalCategory(Icons.Filled.Smartphone, R.string.goal_category_teknoloji),
    GoalCategory(Icons.Filled.MenuBook, R.string.category_egitim),
    GoalCategory(Icons.Filled.FitnessCenter, R.string.category_saglik),
    GoalCategory(Icons.Filled.TrendingUp, R.string.category_yatirim),
    GoalCategory(Icons.Filled.AccountBalance, R.string.goal_category_emeklilik),
    GoalCategory(Icons.Filled.CardGiftcard, R.string.category_hediye),
    GoalCategory(Icons.Filled.MoreHoriz, R.string.category_diger)
)