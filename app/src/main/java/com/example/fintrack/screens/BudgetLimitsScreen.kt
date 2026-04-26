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
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.components.EditScaffold

@Composable
fun BudgetLimitsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    EditScaffold(
        title = stringResource(id = R.string.title_budget_limits),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BudgetCard(
                totalBudget = 14000,
                usedBudget = 11240
            )
            Text(
                text = "Kategori Limitleri",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = modifier.padding(top = 4.dp)
            )
            CategoryLimitCard(
                icon = Icons.Filled.ShoppingCart,
                categoryName = "Market",
                usedAmount = 1740,
                limitAmount = 2000
            )
            CategoryLimitCard(
                icon = Icons.Filled.Home,
                categoryName = "Kira",
                usedAmount = 9900,
                limitAmount = 9000
            )
            CategoryLimitCard(
                icon = Icons.Filled.DirectionsBus,
                categoryName = "Ulaşım",
                usedAmount = 150,
                limitAmount = 800
            )
        }
    }
}

@Composable
private fun BudgetCard(
    totalBudget: Int,
    usedBudget: Int,
    modifier: Modifier = Modifier
) {
    val progress = usedBudget.toFloat() / totalBudget.toFloat()
    val remaining = totalBudget - usedBudget
    val percentage = (progress * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            colorResource(id = R.color.bottom_bar_fab),
                            colorResource(id = R.color.bottom_bar_background)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(id = R.string.label_monthly_budget),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White
                    )
                    Text(
                        text = "₺${"%,d".format(totalBudget).replace(",", ".")}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_used),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Text(
                        text = "₺${"%,d".format(usedBudget).replace(",", ".")}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = colorResource(id = R.color.budget_used_amount)
                    )
                }
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = "",
                    modifier = modifier
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.85f))
                        .padding(vertical = 4.dp)
                )
            }

            Text(
                text = "Bütçenin %$percentage'ini kullandın · ₺${"%,d".format(remaining).replace(",", ".")} kaldı",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun CategoryLimitCard(
    icon: ImageVector,
    categoryName: String,
    usedAmount: Int,
    limitAmount: Int,
    modifier: Modifier = Modifier
) {
    val progress = usedAmount.toFloat() / limitAmount.toFloat()
    val percentage = (progress * 100).toInt()

    val isOverLimit = progress >= 1f
    val isWarning = progress >= 0.75f

    val activeColor = when {
        isOverLimit -> colorResource(id = R.color.expense_red)
        isWarning -> colorResource(id = R.color.bottom_bar_fab)
        else -> colorResource(id = R.color.income_green)
    }

    val activeBackgroundColor = when {
        isOverLimit -> colorResource(id = R.color.transaction_expense_background)
        isWarning -> colorResource(id = R.color.quick_action_background)
        else -> colorResource(id = R.color.transaction_income_background)
    }

    val statusText = if (isOverLimit) "Limit aşıldı" else "Limitin %$percentage'si kullanıldı"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = activeColor,
                        modifier = modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(activeBackgroundColor)
                            .padding(6.dp)
                    )
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = colorResource(id = R.color.text_primary)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₺${"%,d".format(usedAmount).replace(",", ".")}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        color = colorResource(id = R.color.text_primary)
                    )
                    Text(
                        text = "/ ₺${"%,d".format(limitAmount).replace(",", ".")}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                        color = colorResource(id = R.color.text_secondary)
                    )
                }
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp))
                    .background(activeBackgroundColor)
            ) {
                Text(
                    text = "",
                    modifier = modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(50.dp))
                        .background(activeColor)
                        .padding(vertical = 0.5.dp)
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(activeBackgroundColor)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = activeColor
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = activeColor
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BudgetLimitsScreenPreview() {
    BudgetLimitsScreen(navController = rememberNavController())
}