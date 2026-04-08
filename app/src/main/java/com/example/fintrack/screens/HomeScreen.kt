package com.example.fintrack.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.components.EditScaffold
import com.example.fintrack.components.EditTextButton
import com.example.fintrack.features.main.MainScreens
import com.example.fintrack.features.main.quickActionItems
import com.example.fintrack.util.navigateAndClearBackStack

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    EditScaffold(
        title = stringResource(id = R.string.title_home),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            BudgetHeader(
                remainingBalance = 7500,
                dailyLimit = 312
            )
            BudgetDetails(
                income = 25000,
                expense = 17500
            )
            QuickActions(navController = navController)
            RecentTransactions(navController = navController)
        }
    }
}

@Composable
private fun BudgetHeader(
    remainingBalance: Int,
    dailyLimit: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(colorResource(id = R.color.bottom_bar_fab))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Nisan 2026",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BudgetHeaderInfoItem(
                label = "Kalan tutar",
                value = "₺$remainingBalance",
                modifier = modifier.weight(1f)
            )
            BudgetHeaderInfoItem(
                label = "Günlük limit",
                value = "₺$dailyLimit",
                modifier = modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BudgetHeaderInfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}

@Composable
private fun BudgetDetails(
    income: Int,
    expense: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BudgetDetailsInfoItem(
                label = "Gelir",
                value = "₺$income",
                valueColor = colorResource(id = R.color.income_green),
                borderColor = colorResource(id = R.color.income_green),
                modifier = modifier.weight(1f)
            )
            BudgetDetailsInfoItem(
                label = "Gider",
                value = "₺$expense",
                valueColor = colorResource(id = R.color.expense_red),
                borderColor = colorResource(id = R.color.expense_red),
                modifier = modifier.weight(1f)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Harcama oranı",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = colorResource(id = R.color.text_tertiary)
                )
                Text(
                    text = "%${(expense * 100) / income} harcandı",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = colorResource(id = R.color.bottom_bar_fab)
                )
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp))
                    .background(colorResource(id = R.color.progress_track))
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = "",
                    modifier = modifier
                        .fillMaxWidth(expense.toFloat() / income.toFloat())
                        .clip(RoundedCornerShape(50.dp))
                        .background(colorResource(id = R.color.bottom_bar_fab))
                        .padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun BudgetDetailsInfoItem(
    label: String,
    value: String,
    valueColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    val borderWidth = 3.dp

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.surface_gray))
            .drawBehind {
                drawRect(
                    color = borderColor,
                    size = size.copy(width = borderWidth.toPx())
                )
            }
            .padding(start = borderWidth + 13.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = colorResource(id = R.color.text_quaternary)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
            color = valueColor
        )
    }
}

@Composable
private fun QuickActions(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.quick_actions_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                quickActionItems.forEach { item ->
                    Column(
                        modifier = modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                navigateAndClearBackStack(
                                    navController = navController,
                                    destination = item.route,
                                    popUpToRoute = MainScreens.HomeScreen.route,
                                    inclusive = false
                                )
                            }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = colorResource(id = item.iconTintRes),
                            modifier = modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorResource(id = item.iconBackgroundColorRes))
                                .padding(14.dp)
                        )
                        Text(
                            text = stringResource(id = item.labelResId),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            color = colorResource(id = R.color.text_tertiary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentTransactions(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Son İşlemler",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            EditTextButton(
                onClick = {
                    navigateAndClearBackStack(
                        navController = navController,
                        destination = MainScreens.TransactionsScreen.route,
                        popUpToRoute = MainScreens.HomeScreen.route,
                        inclusive = false
                    )
                },
                text = "Tüm İşlemler",
                color = Color.Black
            )
        }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            TransactionRow(
                icon = Icons.Filled.ShoppingCart,
                title = "Market Alışverişi",
                dateTime = "14 Mar 2026 · 18:45",
                amount = "-₺300",
                remainingBalance = "Kalan: ₺7.500",
                amountColor = colorResource(id = R.color.expense_red),
                iconBackgroundColor = colorResource(id = R.color.transaction_expense_background),
                iconTint = colorResource(id = R.color.expense_red),
                showDivider = true
            )
            TransactionRow(
                icon = Icons.Filled.AttachMoney,
                title = "Maaş Ödemesi",
                dateTime = "01 Mar 2026 · 09:00",
                amount = "+₺25.000",
                remainingBalance = "Kalan: ₺7.800",
                amountColor = colorResource(id = R.color.income_green),
                iconBackgroundColor = colorResource(id = R.color.transaction_income_background),
                iconTint = colorResource(id = R.color.income_green),
                showDivider = false
            )
        }
    }
}

@Composable
private fun TransactionRow(
    icon: ImageVector,
    title: String,
    dateTime: String,
    amount: String,
    remainingBalance: String,
    amountColor: Color,
    iconBackgroundColor: Color,
    iconTint: Color,
    showDivider: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBackgroundColor)
                    .padding(11.dp)
            )
            Column(
                modifier = modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = colorResource(id = R.color.text_primary)
                )
                Text(
                    text = dateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(id = R.color.text_secondary)
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = amountColor
                )
                Text(
                    text = remainingBalance,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(id = R.color.text_secondary)
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(color = colorResource(id = R.color.divider_color))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}