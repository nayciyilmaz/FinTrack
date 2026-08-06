package com.example.fintrack.presentation.screens.reminders

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.core.constants.categoryKeyToIcon
import com.example.fintrack.core.constants.categoryKeyToLabelResId
import com.example.fintrack.core.util.currencySymbol
import com.example.fintrack.core.util.dateFormatter
import com.example.fintrack.domain.model.RecurringItem
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.ScreenStateContent
import com.example.fintrack.presentation.components.TransactionRow
import com.example.fintrack.presentation.components.TransactionTypeSelector
import com.example.fintrack.presentation.navigation.FinTrackScreens
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun PaymentRemindersScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PaymentRemindersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    EditScaffold(
        title = stringResource(id = R.string.title_payment_reminders),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TransactionTypeSelector(
                options = stringArrayResource(id = R.array.reminder_filter_options).toList(),
                selectedIndex = uiState.selectedFilterIndex,
                onOptionSelected = viewModel::onFilterChange
            )

            ScreenStateContent(
                isLoading = actionState.isLoading,
                isError = actionState.isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                when {
                    uiState.selectedFilterIndex == 2 -> {
                        RecurringTabContent(
                            recurringItems = actionState.recurringItems,
                            navController = navController
                        )
                    }
                    else -> {
                        val filterIndex = uiState.selectedFilterIndex
                        val filtered = viewModel.filterReminders(filterIndex, actionState.reminderTransactions)
                        ReminderTabContent(
                            transactions = filtered,
                            paymentsHeaderResId = if (filterIndex == 1) R.string.label_planned_payments else R.string.label_upcoming_payments,
                            incomesHeaderResId = if (filterIndex == 1) R.string.label_planned_incomes else R.string.label_upcoming_incomes,
                            emptyMessageResId = if (filterIndex == 1) R.string.label_no_planned else R.string.label_no_upcoming,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecurringTabContent(
    recurringItems: List<RecurringItem>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val expenseItems = recurringItems.filter { it.type == TransactionType.EXPENSE }
    val incomeItems = recurringItems.filter { it.type == TransactionType.INCOME }

    if (expenseItems.isEmpty() && incomeItems.isEmpty()) {
        EmptyMessage(textResId = R.string.label_no_recurring, modifier = modifier)
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (expenseItems.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.label_regular_payments),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(colorResource(id = R.color.card_background))
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                expenseItems.forEachIndexed { index, item ->
                    RecurringItemRow(
                        item = item,
                        showDivider = index < expenseItems.lastIndex,
                        navController = navController
                    )
                }
            }
        }
        if (incomeItems.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.label_regular_incomes),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(colorResource(id = R.color.card_background))
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                incomeItems.forEachIndexed { index, item ->
                    RecurringItemRow(
                        item = item,
                        showDivider = index < incomeItems.lastIndex,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun RecurringItemRow(
    item: RecurringItem,
    showDivider: Boolean,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val isIncome = item.type == TransactionType.INCOME

    TransactionRow(
        icon = categoryKeyToIcon(item.category),
        title = stringResource(id = categoryKeyToLabelResId(item.category)),
        dateTime = stringResource(id = R.string.label_monthly_day_format, item.dayOfMonth),
        amount = "${if (isIncome) "+" else "-"}${currencySymbol()}${item.amount}",
        remainingBalance = stringResource(id = R.string.label_monthly),
        amountColor = if (isIncome) colorResource(id = R.color.income_green) else colorResource(id = R.color.expense_red),
        iconBackgroundColor = if (isIncome) colorResource(id = R.color.transaction_income_background) else colorResource(id = R.color.transaction_expense_background),
        iconTint = if (isIncome) colorResource(id = R.color.income_green) else colorResource(id = R.color.expense_red),
        showDivider = showDivider,
        modifier = modifier,
        onClick = {
            val today = LocalDate.now()
            val date = today.withDayOfMonth(minOf(item.dayOfMonth, today.lengthOfMonth()))
            val route = "${FinTrackScreens.UpdateTransactionScreen.route}" +
                "?transactionId=0" +
                "&type=${item.type.name}" +
                "&category=${item.category}" +
                "&amount=${item.amount.toLong()}" +
                "&date=$date" +
                "&time=${Uri.encode("09:00")}" +
                "&isRecurring=true" +
                "&isReminder=false" +
                "&note=" +
                "&recurringItemId=${item.id}"
            navController.navigate(route)
        }
    )
}

@Composable
private fun ReminderTabContent(
    transactions: List<Transaction>,
    paymentsHeaderResId: Int,
    incomesHeaderResId: Int,
    emptyMessageResId: Int,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
    val incomeTransactions = transactions.filter { it.type == TransactionType.INCOME }

    if (expenseTransactions.isEmpty() && incomeTransactions.isEmpty()) {
        EmptyMessage(textResId = emptyMessageResId, modifier = modifier)
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (expenseTransactions.isNotEmpty()) {
            Text(
                text = stringResource(id = paymentsHeaderResId),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(colorResource(id = R.color.card_background))
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                expenseTransactions.forEachIndexed { index, transaction ->
                    ReminderTransactionRow(
                        transaction = transaction,
                        showDivider = index < expenseTransactions.lastIndex,
                        navController = navController
                    )
                }
            }
        }
        if (incomeTransactions.isNotEmpty()) {
            Text(
                text = stringResource(id = incomesHeaderResId),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(colorResource(id = R.color.card_background))
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                incomeTransactions.forEachIndexed { index, transaction ->
                    ReminderTransactionRow(
                        transaction = transaction,
                        showDivider = index < incomeTransactions.lastIndex,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderTransactionRow(
    transaction: Transaction,
    showDivider: Boolean,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(transaction.date))
    val remainingText = when {
        daysRemaining < 0 -> stringResource(id = R.string.label_days_overdue_format, (-daysRemaining).toInt())
        daysRemaining == 0L -> stringResource(id = R.string.label_today)
        else -> stringResource(id = R.string.label_days_remaining_format, daysRemaining.toInt())
    }

    TransactionRow(
        icon = categoryKeyToIcon(transaction.category),
        title = stringResource(id = categoryKeyToLabelResId(transaction.category)),
        dateTime = "${LocalDate.parse(transaction.date).format(dateFormatter())} · ${transaction.time}",
        amount = "${if (isIncome) "+" else "-"}${currencySymbol()}${transaction.amount}",
        remainingBalance = remainingText,
        amountColor = if (isIncome) colorResource(id = R.color.income_green) else colorResource(id = R.color.expense_red),
        iconBackgroundColor = if (isIncome) colorResource(id = R.color.transaction_income_background) else colorResource(id = R.color.transaction_expense_background),
        iconTint = if (isIncome) colorResource(id = R.color.income_green) else colorResource(id = R.color.expense_red),
        showDivider = showDivider,
        modifier = modifier,
        onClick = {
            val route = "${FinTrackScreens.UpdateTransactionScreen.route}" +
                "?transactionId=${transaction.id}" +
                "&type=${transaction.type.name}" +
                "&category=${transaction.category}" +
                "&amount=${transaction.amount.toLong()}" +
                "&date=${transaction.date}" +
                "&time=${Uri.encode(transaction.time)}" +
                "&isRecurring=${transaction.isRecurring}" +
                "&isReminder=${transaction.isReminder}" +
                "&note=${Uri.encode(transaction.note ?: "")}"
            navController.navigate(route)
        }
    )
}

@Composable
private fun EmptyMessage(
    textResId: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = textResId),
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(id = R.color.text_secondary)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PaymentRemindersScreenPreview() {
    PaymentRemindersScreen(navController = rememberNavController())
}
