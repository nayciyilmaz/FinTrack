package com.example.fintrack.presentation.screens.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.PeriodSelector

@Composable
fun SpendingAnalysisScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val labels = listOf("Yemek", "Ulaşım", "Eğlence", "Diğer")
    val percentages = listOf(35f, 25f, 20f, 20f)
    val colors = listOf(
        Color(0xFFE65100),
        Color(0xFF1565C0),
        Color(0xFF2E7D32),
        Color(0xFF6A1B9A)
    )
    var selectedPeriod by remember { mutableStateOf("Bu Ay") }
    var selectedSpendingPeriod by remember { mutableStateOf("Son 7 Gün") }

    EditScaffold(
        title = stringResource(id = R.string.title_spending_analysis),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryDistributionSection(
                labels = labels,
                percentages = percentages,
                colors = colors,
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { selectedPeriod = it }
            )
            SpendingTrendSection(
                selectedPeriod = selectedSpendingPeriod,
                onPeriodSelected = { selectedSpendingPeriod = it }
            )
        }
    }
}

@Composable
private fun CategoryDistributionSection(
    labels: List<String>,
    percentages: List<Float>,
    colors: List<Color>,
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.label_category_distribution),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        PeriodSelector(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected,
            excludePeriods = listOf("Bu Ay", "Bu Yıl")
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        DonutChart(
            percentages = percentages,
            colors = colors,
            modifier = modifier.size(140.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            labels.forEachIndexed { index, label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(colors[index])
                            .padding(6.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        modifier = modifier.weight(1f)
                    )
                    Text(
                        text = "%${percentages[index].toInt()}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors[index]
                    )
                }
            }
        }
    }
}

@Composable
private fun SpendingTrendSection(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val barData = when (selectedPeriod) {
        "Son 7 Gün" -> listOf(
            Pair("Pzt", 320f), Pair("Sal", 150f), Pair("Çar", 480f),
            Pair("Per", 90f), Pair("Cum", 560f), Pair("Cmt", 720f), Pair("Paz", 210f)
        )
        "Son 15 Gün" -> listOf(
            Pair("1", 320f), Pair("2", 150f), Pair("3", 480f), Pair("4", 90f),
            Pair("5", 560f), Pair("6", 720f), Pair("7", 210f), Pair("8", 430f),
            Pair("9", 380f), Pair("10", 290f), Pair("11", 610f), Pair("12", 180f),
            Pair("13", 540f), Pair("14", 360f), Pair("15", 450f)
        )
        "Son 1 Ay" -> listOf(
            Pair("1.Hf", 2100f), Pair("2.Hf", 3400f), Pair("3.Hf", 1800f), Pair("4.Hf", 4200f)
        )
        "Son 3 Ay" -> listOf(
            Pair("May", 9100f), Pair("Haz", 7200f), Pair("Tem", 8400f)
        )
        "Son 6 Ay" -> listOf(
            Pair("Şub", 8200f), Pair("Mar", 12500f), Pair("Nis", 8500f),
            Pair("May", 7200f), Pair("Haz", 9100f), Pair("Tem", 6800f)
        )
        "Son 1 Yıl" -> listOf(
            Pair("Ağu", 8500f), Pair("Eyl", 7200f), Pair("Eki", 9100f),
            Pair("Kas", 6800f), Pair("Ara", 11200f), Pair("Oca", 9800f),
            Pair("Şub", 7600f), Pair("Mar", 8900f), Pair("Nis", 10200f),
            Pair("May", 7400f), Pair("Haz", 9600f), Pair("Tem", 12500f)
        )
        else -> listOf(
            Pair("1.Hf", 2100f), Pair("2.Hf", 3400f), Pair("3.Hf", 1800f), Pair("4.Hf", 4200f)
        )
    }

    val isWeekly = selectedPeriod == "Son 1 Ay"
    val isMonthly = selectedPeriod == "Son 3 Ay" || selectedPeriod == "Son 6 Ay" || selectedPeriod == "Son 1 Yıl"

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.label_spending_distribution),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        PeriodSelector(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected,
            excludePeriods = listOf("Bu Ay", "Bu Yıl")
        )
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val maxValue = barData.maxOf { it.second }
        val barColor = colorResource(id = R.color.bottom_bar_fab)

        BarChart(
            barData = barData,
            maxValue = maxValue,
            barColor = barColor
        )

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            barData.forEach { pair ->
                Text(
                    text = pair.first,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color(0xFF888888),
                    textAlign = TextAlign.Center,
                    modifier = modifier.weight(1f)
                )
            }
        }
    }

    SpendingSummaryCards(
        isWeekly = isWeekly,
        isMonthly = isMonthly,
        selectedPeriod = selectedPeriod
    )
}

@Composable
private fun BarChart(
    barData: List<Pair<String, Float>>,
    maxValue: Float,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        val barCount = barData.size
        val totalSpacing = size.width * 0.4f
        val barWidth = (size.width - totalSpacing) / barCount
        val spacing = totalSpacing / (barCount + 1)

        barData.forEachIndexed { index, pair ->
            val barHeight = (pair.second / maxValue) * size.height * 0.85f
            val x = spacing + index * (barWidth + spacing)
            val y = size.height - barHeight

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8.dp.toPx())
            )
        }
    }
}

@Composable
private fun SpendingSummaryCards(
    isWeekly: Boolean,
    isMonthly: Boolean,
    selectedPeriod: String,
    modifier: Modifier = Modifier
) {
    val highLabel = when {
        isMonthly -> "En Çok Harcanan Ay"
        isWeekly -> "En Çok Harcanan Hafta"
        else -> "En Çok Harcanan Gün"
    }
    val lowLabel = when {
        isMonthly -> "En Az Harcanan Ay"
        isWeekly -> "En Az Harcanan Hafta"
        else -> "En Az Harcanan Gün"
    }

    val highDate = when (selectedPeriod) {
        "Son 7 Gün" -> "12 Nisan, Cumartesi"
        "Son 15 Gün" -> "6 Nisan, Pazartesi"
        "Son 1 Ay" -> "2. Hafta"
        "Son 3 Ay" -> "Mayıs 2026"
        "Son 6 Ay" -> "Mart 2026"
        "Son 1 Yıl" -> "Aralık 2025"
        else -> "Mayıs 2026"
    }
    val lowDate = when (selectedPeriod) {
        "Son 7 Gün" -> "7 Nisan, Salı"
        "Son 15 Gün" -> "4 Nisan, Cuma"
        "Son 1 Ay" -> "3. Hafta"
        "Son 3 Ay" -> "Haziran 2026"
        "Son 6 Ay" -> "Temmuz 2026"
        "Son 1 Yıl" -> "Kasım 2025"
        else -> "Haziran 2026"
    }
    val highAmount = when (selectedPeriod) {
        "Son 7 Gün" -> "₺1.840"
        "Son 15 Gün" -> "₺2.150"
        "Son 1 Ay" -> "₺4.200"
        "Son 3 Ay" -> "₺9.100"
        "Son 6 Ay" -> "₺12.500"
        "Son 1 Yıl" -> "₺12.500"
        else -> "₺12.500"
    }
    val lowAmount = when (selectedPeriod) {
        "Son 7 Gün" -> "₺95"
        "Son 15 Gün" -> "₺120"
        "Son 1 Ay" -> "₺1.800"
        "Son 3 Ay" -> "₺7.200"
        "Son 6 Ay" -> "₺6.800"
        "Son 1 Yıl" -> "₺6.800"
        else -> "₺6.800"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = highLabel,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF888888)
            )
            Text(
                text = highDate,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colorResource(id = R.color.expense_red)
            )
            Text(
                text = highAmount,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        Column(
            modifier = modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = lowLabel,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF888888)
            )
            Text(
                text = lowDate,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colorResource(id = R.color.income_green)
            )
            Text(
                text = lowAmount,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun DonutChart(
    percentages: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val strokeWidth = size.minDimension * 0.14f
            val radius = (size.minDimension - strokeWidth) / 2f
            val topLeft = Offset(
                (size.width - radius * 2) / 2f,
                (size.height - radius * 2) / 2f
            )
            val arcSize = Size(radius * 2, radius * 2)
            var startAngle = -90f

            percentages.forEachIndexed { index, percentage ->
                val sweepAngle = percentage / 100f * 360f
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle - 2f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )
                startAngle += sweepAngle
            }
        }
        Text(
            text = "100%",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SpendingAnalysisScreenPreview() {
    SpendingAnalysisScreen(navController = rememberNavController())
}