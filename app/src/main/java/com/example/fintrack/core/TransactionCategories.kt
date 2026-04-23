package com.example.fintrack.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import com.example.fintrack.R
import com.example.fintrack.model.TransactionCategory

val expenseCategories = listOf(
    TransactionCategory(R.string.category_market, Icons.Filled.ShoppingCart),
    TransactionCategory(R.string.category_kira, Icons.Filled.Home),
    TransactionCategory(R.string.category_ulasim, Icons.Filled.DirectionsBus),
    TransactionCategory(R.string.category_eglence, Icons.Filled.Theaters),
    TransactionCategory(R.string.category_fatura, Icons.Filled.ElectricBolt),
    TransactionCategory(R.string.category_saglik, Icons.Filled.LocalHospital),
    TransactionCategory(R.string.category_giyim, Icons.Filled.Checkroom),
    TransactionCategory(R.string.category_spor, Icons.Filled.FitnessCenter),
    TransactionCategory(R.string.category_egitim, Icons.Filled.MenuBook),
    TransactionCategory(R.string.category_abonelik, Icons.Filled.Subscriptions),
    TransactionCategory(R.string.category_diger, Icons.Filled.MoreHoriz)
)

val incomeCategories = listOf(
    TransactionCategory(R.string.category_maas, Icons.Filled.Payments),
    TransactionCategory(R.string.category_freelance, Icons.Filled.Work),
    TransactionCategory(R.string.category_kira_geliri, Icons.Filled.Home),
    TransactionCategory(R.string.category_yatirim, Icons.Filled.TrendingUp),
    TransactionCategory(R.string.category_hediye, Icons.Filled.CardGiftcard),
    TransactionCategory(R.string.category_bonus, Icons.Filled.Star),
    TransactionCategory(R.string.category_faiz, Icons.Filled.AccountBalance),
    TransactionCategory(R.string.category_diger_gelir, Icons.Filled.MoreHoriz)
)