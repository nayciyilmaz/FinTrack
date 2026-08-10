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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fintrack.R
import com.example.fintrack.core.constants.categoryKeyToLabelResId
import com.example.fintrack.core.util.CurrencyHelper
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.PeriodSelector
import androidx.compose.ui.res.integerArrayResource
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun SpendingAnalysisScreen(
    navController: NavController,
    viewModel: SpendingAnalysisViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val periods = stringArrayResource(id = R.array.transaction_periods)

    EditScaffold(
        title = stringResource(id = R.string.title_spending_analysis),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_lg))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_md))
        ) {
            CategoryDistributionSection(
                items = actionState.categoryDistribution,
                selectedPeriod = periods[uiState.selectedCategoryPeriodIndex],
                onPeriodSelected = { selected -> viewModel.onCategoryPeriodChanged(periods.indexOf(selected)) }
            )
            SpendingTrendSection(
                trendData = actionState.spendingTrend,
                summaryHigh = actionState.summaryHigh,
                summaryLow = actionState.summaryLow,
                selectedPeriod = periods[uiState.selectedTrendPeriodIndex],
                onPeriodSelected = { selected -> viewModel.onTrendPeriodChanged(periods.indexOf(selected)) }
            )
        }
    }
}

@Composable
private fun CategoryDistributionSection(
    items: List<CategoryItem>,
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val percentages = items.map { it.percentage }
    val colors = integerArrayResource(id = R.array.chart_colors).map { Color(it) }

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
            excludePeriods = stringArrayResource(id = R.array.transaction_periods).let { listOf(it[2], it[6]) }
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_3xl)))
            .background(colorResource(id = R.color.card_background))
            .padding(dimensionResource(id = R.dimen.padding_xl)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_2xl))
    ) {
        DonutChart(
            percentages = percentages,
            colors = colors,
            modifier = Modifier.size(dimensionResource(id = R.dimen.size_donut_chart))
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_md))
        ) {
            items.forEachIndexed { index, item ->
                val label = if (item.categoryKey == "OTHER") {
                    stringResource(id = R.string.category_diger)
                } else {
                    context.getString(categoryKeyToLabelResId(item.categoryKey))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))
                ) {
                    Box(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_xs))
                            .clip(CircleShape)
                            .background(colors.getOrElse(index) { colors.last() })
                            .padding(dimensionResource(id = R.dimen.padding_xs))
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "%${item.percentage.toInt()}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.getOrElse(index) { colors.last() }
                    )
                }
            }
        }
    }
}

@Composable
private fun SpendingTrendSection(
    trendData: List<SpendingTrendItem>,
    summaryHigh: SpendingSummary?,
    summaryLow: SpendingSummary?,
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
            text = stringResource(id = R.string.label_spending_distribution),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        PeriodSelector(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected,
            excludePeriods = stringArrayResource(id = R.array.transaction_periods).let { listOf(it[2], it[6]) }
        )
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_3xl)))
            .background(colorResource(id = R.color.card_background))
            .padding(dimensionResource(id = R.dimen.padding_xl)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_lg))
    ) {
        val barData = trendData.map { Pair(it.label, it.amount) }
        val maxValue = barData.maxOfOrNull { it.second } ?: 0f
        val barColor = colorResource(id = R.color.bottom_bar_fab)

        BarChart(
            barData = barData,
            maxValue = if (maxValue == 0f) 1f else maxValue,
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
                    color = colorResource(id = R.color.text_quaternary),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (summaryHigh != null && summaryLow != null) {
        SpendingSummaryCards(
            summaryHigh = summaryHigh,
            summaryLow = summaryLow
        )
    }
}

@Composable
private fun BarChart(
    barData: List<Pair<String, Float>>,
    maxValue: Float,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val barChartHeight = dimensionResource(id = R.dimen.size_bar_chart_height)
    val barCornerRadius = dimensionResource(id = R.dimen.radius_sm)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(barChartHeight)
    ) {
        val barCount = barData.size
        if (barCount == 0) return@Canvas
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
                cornerRadius = CornerRadius(barCornerRadius.toPx())
            )
        }
    }
}

@Composable
private fun SpendingSummaryCards(
    summaryHigh: SpendingSummary,
    summaryLow: SpendingSummary,
    modifier: Modifier = Modifier
) {
    val currencyCode = CurrencyHelper.getCurrency(LocalContext.current)
    val numberFormat = remember(currencyCode) {
        NumberFormat.getCurrencyInstance(Locale("tr", "TR")).apply {
            currency = Currency.getInstance(currencyCode)
            maximumFractionDigits = 0
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_md))
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_xl)))
                .background(colorResource(id = R.color.card_background))
                .padding(dimensionResource(id = R.dimen.padding_md)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_xs))
        ) {
            Text(
                text = summaryHigh.label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = colorResource(id = R.color.text_quaternary)
            )
            Text(
                text = summaryHigh.date,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colorResource(id = R.color.expense_red)
            )
            Text(
                text = numberFormat.format(summaryHigh.amount),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_xl)))
                .background(colorResource(id = R.color.card_background))
                .padding(dimensionResource(id = R.dimen.padding_md)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_xs))
        ) {
            Text(
                text = summaryLow.label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = colorResource(id = R.color.text_quaternary)
            )
            Text(
                text = summaryLow.date,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colorResource(id = R.color.income_green)
            )
            Text(
                text = numberFormat.format(summaryLow.amount),
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
    val totalPercentage = percentages.sum()
    val emptyArcColor = colorResource(id = R.color.divider_color)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.minDimension * 0.14f
            val radius = (size.minDimension - strokeWidth) / 2f
            val topLeft = Offset(
                (size.width - radius * 2) / 2f,
                (size.height - radius * 2) / 2f
            )
            val arcSize = Size(radius * 2, radius * 2)

            if (totalPercentage == 0f) {
                drawArc(
                    color = emptyArcColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )
                return@Canvas
            }
            var startAngle = -90f

            percentages.forEachIndexed { index, percentage ->
                val sweepAngle = percentage / totalPercentage * 360f
                drawArc(
                    color = colors.getOrElse(index) { colors.last() },
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
            text = if (totalPercentage > 0f) "100%" else "0%",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}
