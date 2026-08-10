package com.example.fintrack.presentation.screens.reports

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fintrack.R
import com.example.fintrack.core.constants.reportContentItems
import com.example.fintrack.core.util.currencySymbol
import com.example.fintrack.presentation.model.ReportContentItem
import com.example.fintrack.presentation.components.EditButton
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.ScreenStateContent
import com.example.fintrack.presentation.navigation.FinTrackScreens
import com.example.fintrack.presentation.navigation.navigateAndClearBackStack

@Composable
fun ReportsScreen(
    navController: NavController,
    viewModel: ReportsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.pdfResult.collect { result ->
            when (result) {
                PdfResult.Success -> {
                    Toast.makeText(context, context.getString(R.string.report_pdf_saved), Toast.LENGTH_SHORT).show()
                    navigateAndClearBackStack(
                        navController = navController,
                        destination = FinTrackScreens.HomeScreen.route,
                        popUpToRoute = FinTrackScreens.HomeScreen.route,
                        inclusive = false
                    )
                }
                PdfResult.Error -> {
                    Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    EditScaffold(
        title = stringResource(id = R.string.title_reports),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_lg)),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))
            ) {
                Text(
                    text = stringResource(id = R.string.report_period),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                ReportPeriodSelector(
                    periodLabel = actionState.periodLabel,
                    canGoPrevious = actionState.canGoPrevious,
                    canGoNext = actionState.canGoNext,
                    onPrevious = viewModel::onPreviousPeriod,
                    onNext = viewModel::onNextPeriod
                )
                ReportHeaderCard(
                    income = actionState.income,
                    expense = actionState.expense
                )
                Text(
                    text = stringResource(id = R.string.report_content),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                ScreenStateContent(
                    isLoading = actionState.isLoading,
                    isError = actionState.isError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(id = R.dimen.padding_md))
                ) {
                    ReportContentCard(
                        checkedStates = uiState.checkedSections,
                        onCheckedChange = { titleResId, _ -> viewModel.onSectionToggle(titleResId) }
                    )
                }
            }
            EditButton(
                onClick = { viewModel.generatePdf(context) },
                text = stringResource(id = R.string.report_create_pdf),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(id = R.dimen.padding_sm))
            )
        }
    }
}

@Composable
private fun ReportHeaderCard(
    income: Int,
    expense: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_2xl)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_md)),
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
                .padding(dimensionResource(id = R.dimen.padding_xl)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_lg))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_10))
            ) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = modifier
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_10)))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(dimensionResource(id = R.dimen.padding_sm))
                )
                Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_2xs))) {
                    Text(
                        text = stringResource(id = R.string.report_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = stringResource(id = R.string.report_subtitle),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_10))
            ) {
                Column(
                    modifier = modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_md)))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(dimensionResource(id = R.dimen.padding_md)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_xs))
                ) {
                    Text(
                        text = stringResource(id = R.string.report_total_income),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = "${currencySymbol()}${"%,d".format(income).replace(",", ".")}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
                Column(
                    modifier = modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_md)))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(dimensionResource(id = R.dimen.padding_md)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_xs))
                ) {
                    Text(
                        text = stringResource(id = R.string.report_total_expense),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = "${currencySymbol()}${"%,d".format(expense).replace(",", ".")}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportPeriodSelector(
    periodLabel: String,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(id = R.dimen.padding_sm)),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_xl)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_sm)),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_background))
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.padding_md), vertical = dimensionResource(id = R.dimen.padding_sm)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ChevronLeft,
                contentDescription = null,
                tint = if (canGoPrevious) colorResource(id = R.color.bottom_bar_fab) else colorResource(id = R.color.text_secondary).copy(alpha = 0.4f),
                modifier = modifier
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_10)))
                    .background(colorResource(id = R.color.quick_action_background))
                    .clickable(enabled = canGoPrevious, onClick = onPrevious)
                    .padding(dimensionResource(id = R.dimen.padding_sm))
            )
            Text(
                text = periodLabel,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colorResource(id = R.color.text_primary)
            )
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = if (canGoNext) colorResource(id = R.color.bottom_bar_fab) else colorResource(id = R.color.text_secondary).copy(alpha = 0.4f),
                modifier = modifier
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_10)))
                    .background(colorResource(id = R.color.quick_action_background))
                    .clickable(enabled = canGoNext, onClick = onNext)
                    .padding(dimensionResource(id = R.dimen.padding_sm))
            )
        }
    }
}

@Composable
private fun ReportContentCard(
    checkedStates: Map<Int, Boolean>,
    onCheckedChange: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_xl)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_sm)),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_background))
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.padding_lg))
        ) {
            reportContentItems.forEachIndexed { index, item ->
                ReportContentRow(
                    item = item,
                    isChecked = checkedStates[item.titleResId] ?: false,
                    onCheckedChange = { onCheckedChange(item.titleResId, it) },
                    showDivider = index < reportContentItems.size - 1
                )
            }
        }
    }
}

@Composable
private fun ReportContentRow(
    item: ReportContentItem,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean,
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!isChecked) }
                .padding(vertical = dimensionResource(id = R.dimen.padding_sm)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_10))
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = colorResource(id = R.color.bottom_bar_fab),
                    modifier = modifier
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_10)))
                        .background(colorResource(id = R.color.quick_action_background))
                        .padding(dimensionResource(id = R.dimen.padding_sm))
                )
                Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_2xs))) {
                    Text(
                        text = stringResource(id = item.titleResId),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = colorResource(id = R.color.text_primary)
                    )
                    Text(
                        text = stringResource(id = item.subtitleResId),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = colorResource(id = R.color.text_secondary)
                    )
                }
            }
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = colorResource(id = R.color.bottom_bar_fab),
                    uncheckedColor = colorResource(id = R.color.text_secondary),
                    checkmarkColor = Color.White
                )
            )
        }
        if (showDivider) {
            HorizontalDivider(color = colorResource(id = R.color.divider_color))
        }
    }
}
