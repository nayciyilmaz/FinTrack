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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
                title = "Aylık Bütçe",
                totalBudget = 14000,
                usedBudget = 11240
            )
        }
    }
}

@Composable
private fun BudgetCard(
    title: String,
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.75f)
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
                        text = "Kullanılan",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Text(
                        text = "₺${"%,d".format(usedBudget).replace(",", ".")}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color(0xFFFFE0B2)
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BudgetLimitsScreenPreview() {
    BudgetLimitsScreen(navController = rememberNavController())
}