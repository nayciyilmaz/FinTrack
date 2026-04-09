package com.example.fintrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

@Composable
fun TransactionsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf("1 Ay") }

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
            TransactionsHeader(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { selectedPeriod = it }
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
                    title = "Maaş Ödemesi",
                    dateTime = "01 Mar 2026 · 09:00",
                    amount = "+₺25.000",
                    remainingBalance = "Kalan: ₺25.000",
                    amountColor = colorResource(id = R.color.income_green),
                    iconBackgroundColor = colorResource(id = R.color.transaction_income_background),
                    iconTint = colorResource(id = R.color.income_green),
                    showDivider = true
                )
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
                TransactionRow(
                    icon = Icons.Filled.AttachMoney,
                    title = "Freelance Ödeme",
                    dateTime = "08 Mar 2026 · 16:45",
                    amount = "+₺3.500",
                    remainingBalance = "Kalan: ₺19.550",
                    amountColor = colorResource(id = R.color.income_green),
                    iconBackgroundColor = colorResource(id = R.color.transaction_income_background),
                    iconTint = colorResource(id = R.color.income_green),
                    showDivider = true
                )
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

@Composable
private fun TransactionsHeader(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val periods = listOf("1 Hafta", "15 Gün", "1 Ay", "3 Ay", "6 Ay")
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Tüm İşlemler",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Column {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = selectedPeriod,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                periods.forEach { period ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = period,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                        },
                        onClick = {
                            onPeriodSelected(period)
                            expanded = false
                        }
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