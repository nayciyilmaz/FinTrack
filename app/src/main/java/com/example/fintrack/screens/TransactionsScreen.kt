package com.example.fintrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.components.EditScaffold
import com.example.fintrack.components.PeriodSelector
import com.example.fintrack.components.TransactionRow
import com.example.fintrack.components.TransactionTypeSelector

@Composable
fun TransactionsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableIntStateOf(0) }
    var selectedPeriod by remember { mutableStateOf("Bu Ay") }

    EditScaffold(
        title = stringResource(id = R.string.title_transactions),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TransactionTypeSelector(
                options = listOf(
                    stringResource(id = R.string.label_all),
                    stringResource(id = R.string.label_expense),
                    stringResource(id = R.string.label_income)
                ),
                selectedIndex = selectedFilter,
                onOptionSelected = { selectedFilter = it }
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PeriodSelector(
                    selectedPeriod = selectedPeriod,
                    onPeriodSelected = { selectedPeriod = it }
                )
            }
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                if (selectedFilter == 0 || selectedFilter == 2) {
                    TransactionRow(
                        icon = Icons.Filled.AttachMoney,
                        title = "Maaş Ödemesi",
                        dateTime = "01 Mar 2026 · 09:00",
                        amount = "+₺25.000",
                        remainingBalance = "Kalan: ₺25.000",
                        amountColor = colorResource(id = R.color.income_green),
                        iconBackgroundColor = colorResource(id = R.color.transaction_income_background),
                        iconTint = colorResource(id = R.color.income_green),
                        showDivider = true
                    )
                }
                if (selectedFilter == 0 || selectedFilter == 1) {
                    TransactionRow(
                        icon = Icons.Filled.ShoppingCart,
                        title = "Market Alışverişi",
                        dateTime = "03 Mar 2026 · 14:20",
                        amount = "-₺450",
                        remainingBalance = "Kalan: ₺24.550",
                        amountColor = colorResource(id = R.color.expense_red),
                        iconBackgroundColor = colorResource(id = R.color.transaction_expense_background),
                        iconTint = colorResource(id = R.color.expense_red),
                        showDivider = true
                    )
                    TransactionRow(
                        icon = Icons.Filled.Home,
                        title = "Kira Ödemesi",
                        dateTime = "05 Mar 2026 · 10:00",
                        amount = "-₺8.500",
                        remainingBalance = "Kalan: ₺16.050",
                        amountColor = colorResource(id = R.color.expense_red),
                        iconBackgroundColor = colorResource(id = R.color.transaction_expense_background),
                        iconTint = colorResource(id = R.color.expense_red),
                        showDivider = true
                    )
                }
                if (selectedFilter == 0 || selectedFilter == 2) {
                    TransactionRow(
                        icon = Icons.Filled.AttachMoney,
                        title = "Freelance Ödeme",
                        dateTime = "08 Mar 2026 · 16:45",
                        amount = "+₺3.500",
                        remainingBalance = "Kalan: ₺19.550",
                        amountColor = colorResource(id = R.color.income_green),
                        iconBackgroundColor = colorResource(id = R.color.transaction_income_background),
                        iconTint = colorResource(id = R.color.income_green),
                        showDivider = selectedFilter == 0
                    )
                }
                if (selectedFilter == 0 || selectedFilter == 1) {
                    TransactionRow(
                        icon = Icons.Filled.LocalGasStation,
                        title = "Yakıt",
                        dateTime = "10 Mar 2026 · 08:30",
                        amount = "-₺1.200",
                        remainingBalance = "Kalan: ₺18.350",
                        amountColor = colorResource(id = R.color.expense_red),
                        iconBackgroundColor = colorResource(id = R.color.transaction_expense_background),
                        iconTint = colorResource(id = R.color.expense_red),
                        showDivider = true
                    )
                    TransactionRow(
                        icon = Icons.Filled.Coffee,
                        title = "Kafe",
                        dateTime = "12 Mar 2026 · 11:15",
                        amount = "-₺320",
                        remainingBalance = "Kalan: ₺18.030",
                        amountColor = colorResource(id = R.color.expense_red),
                        iconBackgroundColor = colorResource(id = R.color.transaction_expense_background),
                        iconTint = colorResource(id = R.color.expense_red),
                        showDivider = true
                    )
                    TransactionRow(
                        icon = Icons.Filled.CreditCard,
                        title = "Fatura",
                        dateTime = "14 Mar 2026 · 18:45",
                        amount = "-₺890",
                        remainingBalance = "Kalan: ₺17.140",
                        amountColor = colorResource(id = R.color.expense_red),
                        iconBackgroundColor = colorResource(id = R.color.transaction_expense_background),
                        iconTint = colorResource(id = R.color.expense_red),
                        showDivider = false
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransactionsScreenPreview() {
    TransactionsScreen(navController = rememberNavController())
}