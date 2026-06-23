package com.example.fintrack.presentation.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.core.constants.categoryKeyToIcon
import com.example.fintrack.core.constants.categoryKeyToLabelResId
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.PeriodSelector
import com.example.fintrack.presentation.components.TransactionRow
import com.example.fintrack.presentation.components.TransactionTypeSelector

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
                options = listOf(
                    stringResource(id = R.string.label_all),
                    stringResource(id = R.string.label_expense),
                    stringResource(id = R.string.label_income)
                ),
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
            TransactionsContent(
                uiState = uiState,
                actionState = actionState,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun TransactionsContent(
    uiState: TransactionsUiState,
    actionState: TransactionsActionState,
    modifier: Modifier = Modifier
) {
    when {
        actionState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.bottom_bar_fab)
                )
            }
        }
        actionState.isError -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.error_required_fields),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.text_secondary)
                )
            }
        }
        uiState.transactions.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.label_no_transactions),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.text_secondary)
                )
            }
        }
        else -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                itemsIndexed(uiState.transactions) { index, item ->
                    val isIncome = item.transaction.type == "INCOME"
                    TransactionRow(
                        icon = categoryKeyToIcon(item.transaction.category),
                        title = stringResource(id = categoryKeyToLabelResId(item.transaction.category)),
                        dateTime = "${item.transaction.date} · ${item.transaction.time}",
                        amount = "${if (isIncome) "+" else "-"}₺${item.transaction.amount}",
                        remainingBalance = "Kalan: ₺%.2f".format(item.remainingBalance),
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
                        showDivider = index < uiState.transactions.lastIndex
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