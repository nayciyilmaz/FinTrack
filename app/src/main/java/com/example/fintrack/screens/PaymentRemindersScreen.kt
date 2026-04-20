package com.example.fintrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.components.EditScaffold
import com.example.fintrack.components.TransactionRow
import com.example.fintrack.components.TransactionTypeSelector

@Composable
fun PaymentRemindersScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableIntStateOf(0) }

    EditScaffold(
        title = stringResource(id = R.string.title_payment_reminders),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TransactionTypeSelector(
                options = listOf(
                    stringResource(id = R.string.label_upcoming),
                    stringResource(id = R.string.label_planned),
                    stringResource(id = R.string.label_regular)
                ),
                selectedIndex = selectedFilter,
                onOptionSelected = { selectedFilter = it }
            )

            if (selectedFilter == 0) {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_upcoming_payments),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White)
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        TransactionRow(
                            icon = Icons.Filled.ElectricBolt,
                            title = "Elektrik Faturası",
                            dateTime = "19 Nis 2026 · 09:00",
                            amount = "-₺650",
                            remainingBalance = "2 gün kaldı",
                            amountColor = colorResource(id = R.color.expense_red),
                            iconBackgroundColor = colorResource(id = R.color.transaction_expense_background),
                            iconTint = colorResource(id = R.color.expense_red),
                            showDivider = true
                        )
                        TransactionRow(
                            icon = Icons.Filled.Home,
                            title = "Kira Ödemesi",
                            dateTime = "19 Nis 2026 · 10:00",
                            amount = "-₺8.500",
                            remainingBalance = "2 gün kaldı",
                            amountColor = colorResource(id = R.color.expense_red),
                            iconBackgroundColor = colorResource(id = R.color.transaction_expense_background),
                            iconTint = colorResource(id = R.color.expense_red),
                            showDivider = false
                        )
                    }
                }

                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_upcoming_incomes),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White)
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        TransactionRow(
                            icon = Icons.Filled.AttachMoney,
                            title = "Freelance Ödeme",
                            dateTime = "19 Nis 2026 · 14:00",
                            amount = "+₺3.500",
                            remainingBalance = "2 gün kaldı",
                            amountColor = colorResource(id = R.color.income_green),
                            iconBackgroundColor = colorResource(id = R.color.transaction_income_background),
                            iconTint = colorResource(id = R.color.income_green),
                            showDivider = true
                        )
                        TransactionRow(
                            icon = Icons.Filled.Payments,
                            title = "Maaş",
                            dateTime = "19 Nis 2026 · 09:00",
                            amount = "+₺25.000",
                            remainingBalance = "2 gün kaldı",
                            amountColor = colorResource(id = R.color.income_green),
                            iconBackgroundColor = colorResource(id = R.color.transaction_income_background),
                            iconTint = colorResource(id = R.color.income_green),
                            showDivider = false
                        )
                    }
                }
            }

            if (selectedFilter == 1) {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_planned_payments),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White)
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        TransactionRow(
                            icon = Icons.Filled.DirectionsBus,
                            title = "Ulaşım Kartı",
                            dateTime = "24 Nis 2026 · 08:00",
                            amount = "-₺500",
                            remainingBalance = "7 gün kaldı",
                            amountColor = colorResource(id = R.color.expense_red),
                            iconBackgroundColor = colorResource(id = R.color.transaction_expense_background),
                            iconTint = colorResource(id = R.color.expense_red),
                            showDivider = true
                        )
                        TransactionRow(
                            icon = Icons.Filled.ShoppingCart,
                            title = "Market Alışverişi",
                            dateTime = "25 Nis 2026 · 11:00",
                            amount = "-₺800",
                            remainingBalance = "8 gün kaldı",
                            amountColor = colorResource(id = R.color.expense_red),
                            iconBackgroundColor = colorResource(id = R.color.transaction_expense_background),
                            iconTint = colorResource(id = R.color.expense_red),
                            showDivider = false
                        )
                    }
                }

                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_planned_incomes),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White)
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        TransactionRow(
                            icon = Icons.Filled.AttachMoney,
                            title = "Freelance Ödeme",
                            dateTime = "24 Nis 2026 · 14:00",
                            amount = "+₺3.500",
                            remainingBalance = "7 gün kaldı",
                            amountColor = colorResource(id = R.color.income_green),
                            iconBackgroundColor = colorResource(id = R.color.transaction_income_background),
                            iconTint = colorResource(id = R.color.income_green),
                            showDivider = true
                        )
                        TransactionRow(
                            icon = Icons.Filled.FitnessCenter,
                            title = "Spor Salonu İadesi",
                            dateTime = "26 Nis 2026 · 10:00",
                            amount = "+₺450",
                            remainingBalance = "9 gün kaldı",
                            amountColor = colorResource(id = R.color.income_green),
                            iconBackgroundColor = colorResource(id = R.color.transaction_income_background),
                            iconTint = colorResource(id = R.color.income_green),
                            showDivider = false
                        )
                    }
                }
            }

            if (selectedFilter == 2) {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_regular_payments),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White)
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        TransactionRow(
                            icon = Icons.Filled.Home,
                            title = "Kira Ödemesi",
                            dateTime = "Her ayın 1'i",
                            amount = "-₺8.500",
                            remainingBalance = "Aylık",
                            amountColor = colorResource(id = R.color.expense_red),
                            iconBackgroundColor = colorResource(id = R.color.transaction_expense_background),
                            iconTint = colorResource(id = R.color.expense_red),
                            showDivider = true
                        )
                        TransactionRow(
                            icon = Icons.Filled.ElectricBolt,
                            title = "İnternet Faturası",
                            dateTime = "Her ayın 5'i",
                            amount = "-₺350",
                            remainingBalance = "Aylık",
                            amountColor = colorResource(id = R.color.expense_red),
                            iconBackgroundColor = colorResource(id = R.color.transaction_expense_background),
                            iconTint = colorResource(id = R.color.expense_red),
                            showDivider = false
                        )
                    }
                }

                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_regular_incomes),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White)
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        TransactionRow(
                            icon = Icons.Filled.Payments,
                            title = "Maaş",
                            dateTime = "Her ayın 5'i",
                            amount = "+₺25.000",
                            remainingBalance = "Aylık",
                            amountColor = colorResource(id = R.color.income_green),
                            iconBackgroundColor = colorResource(id = R.color.transaction_income_background),
                            iconTint = colorResource(id = R.color.income_green),
                            showDivider = true
                        )
                        TransactionRow(
                            icon = Icons.Filled.TrendingUp,
                            title = "Yatırım Getirisi",
                            dateTime = "Her ayın 10'u",
                            amount = "+₺1.200",
                            remainingBalance = "Aylık",
                            amountColor = colorResource(id = R.color.income_green),
                            iconBackgroundColor = colorResource(id = R.color.transaction_income_background),
                            iconTint = colorResource(id = R.color.income_green),
                            showDivider = false
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PaymentRemindersScreenPreview() {
    PaymentRemindersScreen(navController = rememberNavController())
}