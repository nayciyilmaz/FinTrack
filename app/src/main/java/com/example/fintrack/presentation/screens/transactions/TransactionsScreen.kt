package com.example.fintrack.presentation.screens.transactions

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.core.constants.categoryKeyToIcon
import com.example.fintrack.core.constants.categoryKeyToLabelResId
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.core.util.currencySymbol
import com.example.fintrack.core.util.dateFormatter
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.PeriodSelector
import com.example.fintrack.presentation.components.ScreenStateContent
import com.example.fintrack.presentation.components.TransactionRow
import com.example.fintrack.presentation.components.TransactionTypeSelector
import com.example.fintrack.presentation.navigation.FinTrackScreens
import java.time.LocalDate

@Composable
fun TransactionsScreen(
    navController: NavController,
    viewModel: TransactionsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()

    EditScaffold(
        title = stringResource(id = R.string.title_transactions),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TransactionTypeSelector(
                options = stringArrayResource(id = R.array.transaction_filter_options).toList(),
                selectedIndex = uiState.selectedFilterIndex,
                onOptionSelected = viewModel::onFilterChange
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PeriodSelector(
                    selectedPeriod = uiState.selectedPeriod,
                    onPeriodSelected = viewModel::onPeriodChange
                )
            }
            ScreenStateContent(
                isLoading = actionState.isLoading,
                isError = actionState.isError,
                isEmpty = uiState.transactions.isEmpty(),
                emptyMessageResId = R.string.label_no_transactions,
                modifier = modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(colorResource(id = R.color.card_background))
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    itemsIndexed(uiState.transactions) { index, item ->
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
                            showDivider = index < uiState.transactions.lastIndex,
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
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransactionsScreenPreview() {
    TransactionsScreen(navController = rememberNavController())
}