package com.example.fintrack.features.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Theaters
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
    TransactionCategory(R.string.category_abonelik, Icons.Filled.Subscriptions)
)