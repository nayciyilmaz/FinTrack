package com.example.fintrack.presentation.screens.home

import android.net.Uri
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.core.constants.categoryKeyToIcon
import com.example.fintrack.core.constants.categoryKeyToLabelResId
import com.example.fintrack.core.constants.quickActionItems
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.core.util.currencySymbol
import com.example.fintrack.core.util.dateFormatter
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.EditTextButton
import com.example.fintrack.presentation.components.ProgressBar
import com.example.fintrack.presentation.components.ScreenStateContent
import com.example.fintrack.presentation.components.SectionHeaderRow
import com.example.fintrack.presentation.components.TransactionRow
import com.example.fintrack.presentation.navigation.FinTrackScreens
import com.example.fintrack.presentation.navigation.navigateAndClearBackStack
import com.example.fintrack.presentation.screens.transactions.TransactionDisplayItem
import java.time.LocalDate

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadHomeData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    EditScaffold(
        title = stringResource(id = R.string.title_home),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_lg))
                .verticalScroll(rememberScrollState())
        ) {
            BudgetHeader(
                periodText = actionState.periodText,
                remainingBalance = actionState.remainingBalance,
                dailyLimit = actionState.dailyLimit
            )
            BudgetDetails(
                income = actionState.income,
                expense = actionState.expense,
                spendingRatio = actionState.spendingRatio
            )
            QuickActions(navController = navController)
            RecentTransactions(
                transactions = actionState.recentTransactions,
                actionState = actionState,
                navController = navController
            )
        }
    }
}

@Composable
private fun BudgetHeader(
    periodText: String,
    remainingBalance: Int,
    dailyLimit: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(
                topStart = dimensionResource(id = R.dimen.radius_3xl),
                topEnd = dimensionResource(id = R.dimen.radius_3xl)
            ))
            .background(colorResource(id = R.color.bottom_bar_fab))
            .padding(horizontal = dimensionResource(id = R.dimen.padding_xl), vertical = dimensionResource(id = R.dimen.padding_lg)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_md))
    ) {
        Text(
            text = periodText,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_md))
        ) {
            BudgetHeaderInfoItem(
                label = stringResource(id = R.string.label_remaining_balance),
                value = "${currencySymbol()}$remainingBalance",
                modifier = modifier.weight(1f)
            )
            BudgetHeaderInfoItem(
                label = stringResource(id = R.string.label_daily_limit),
                value = "${currencySymbol()}$dailyLimit",
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
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_lg)))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = dimensionResource(id = R.dimen.padding_md), vertical = dimensionResource(id = R.dimen.padding_10)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_2xs))
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
    spendingRatio: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(
                bottomStart = dimensionResource(id = R.dimen.radius_3xl),
                bottomEnd = dimensionResource(id = R.dimen.radius_3xl)
            ))
            .background(colorResource(id = R.color.card_background))
            .padding(horizontal = dimensionResource(id = R.dimen.padding_xl), vertical = dimensionResource(id = R.dimen.padding_lg)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_lg))
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_md))
        ) {
            BudgetDetailsInfoItem(
                label = stringResource(id = R.string.label_income),
                value = "${currencySymbol()}$income",
                valueColor = colorResource(id = R.color.income_green),
                borderColor = colorResource(id = R.color.income_green),
                modifier = modifier.weight(1f)
            )
            BudgetDetailsInfoItem(
                label = stringResource(id = R.string.label_expense),
                value = "${currencySymbol()}$expense",
                valueColor = colorResource(id = R.color.expense_red),
                borderColor = colorResource(id = R.color.expense_red),
                modifier = modifier.weight(1f)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.label_spending_rate),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = colorResource(id = R.color.text_tertiary)
                )
                Text(
                    text = stringResource(id = R.string.home_spending_ratio_format, "%$spendingRatio"),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = colorResource(id = R.color.bottom_bar_fab)
                )
            }
            ProgressBar(
                progress = if (income > 0) minOf(expense.toFloat() / income.toFloat(), 1f) else 0f,
                trackColor = colorResource(id = R.color.progress_track),
                progressColor = colorResource(id = R.color.bottom_bar_fab)
            )
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
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_lg)))
            .background(colorResource(id = R.color.surface_gray))
            .drawBehind {
                drawRect(
                    color = borderColor,
                    size = size.copy(width = borderWidth.toPx())
                )
            }
            .padding(start = borderWidth + 13.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_xs))
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
            .padding(top = dimensionResource(id = R.dimen.padding_md)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))
    ) {
        Text(
            text = stringResource(id = R.string.quick_actions_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_3xl)))
                .background(colorResource(id = R.color.card_background))
                .padding(horizontal = dimensionResource(id = R.dimen.padding_xl), vertical = dimensionResource(id = R.dimen.padding_xs))
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                quickActionItems.forEach { item ->
                    Column(
                        modifier = modifier
                            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_md)))
                            .clickable {
                                navigateAndClearBackStack(
                                    navController = navController,
                                    destination = item.route,
                                    popUpToRoute = FinTrackScreens.HomeScreen.route,
                                    inclusive = false
                                )
                            }
                            .padding(dimensionResource(id = R.dimen.padding_sm)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = colorResource(id = item.iconTintRes),
                            modifier = modifier
                                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_lg)))
                                .background(colorResource(id = item.iconBackgroundColorRes))
                                .padding(dimensionResource(id = R.dimen.padding_md))
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
    transactions: List<TransactionDisplayItem>,
    actionState: HomeActionState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(id = R.dimen.padding_xs)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_2xs))
    ) {
        SectionHeaderRow(
            title = stringResource(id = R.string.label_recent_transactions),
            actionText = stringResource(id = R.string.label_all_transactions),
            onActionClick = {
                navigateAndClearBackStack(
                    navController = navController,
                    destination = FinTrackScreens.TransactionsScreen.route,
                    popUpToRoute = FinTrackScreens.HomeScreen.route,
                    inclusive = false
                )
            }
        )
        ScreenStateContent(
            isLoading = actionState.isLoading,
            isError = actionState.isError,
            isEmpty = transactions.isEmpty(),
            emptyMessageResId = R.string.label_no_transactions,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.padding_md))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_3xl)))
                    .background(colorResource(id = R.color.card_background))
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_xl), vertical = dimensionResource(id = R.dimen.padding_xs))
            ) {
                transactions.forEachIndexed { index, item ->
                    val isIncome = item.transaction.type == TransactionType.INCOME
                    TransactionRow(
                        icon = categoryKeyToIcon(item.transaction.category),
                        title = stringResource(id = categoryKeyToLabelResId(item.transaction.category)),
                        dateTime = "${LocalDate.parse(item.transaction.date).format(dateFormatter())} · ${item.transaction.time}",
                        amount = "${if (isIncome) "+" else "-"}${currencySymbol()}${item.transaction.amount}",
                        remainingBalance = "Kalan: ${currencySymbol()}%.2f".format(item.remainingBalance),
                        amountColor = if (isIncome)
                            colorResource(id = R.color.income_green)
                        else
                            colorResource(id = R.color.expense_red),
                        iconBackgroundColor = if (isIncome)
                            colorResource(id = R.color.transaction_income_background)
                        else
                            colorResource(id = R.color.transaction_expense_background),
                        iconTint = if (isIncome)
                            colorResource(id = R.color.income_green)
                        else
                            colorResource(id = R.color.expense_red),
                        showDivider = index < transactions.lastIndex,
                        onClick = {
                            val t = item.transaction
                            val route = "${FinTrackScreens.UpdateTransactionScreen.route}" +
                                "?transactionId=${t.id}" +
                                "&type=${t.type.name}" +
                                "&category=${t.category}" +
                                "&amount=${t.amount.toLong()}" +
                                "&date=${t.date}" +
                                "&time=${Uri.encode(t.time)}" +
                                "&isRecurring=${t.isRecurring}" +
                                "&isReminder=${t.isReminder}" +
                                "&note=${Uri.encode(t.note ?: "")}"
                            navController.navigate(route)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}