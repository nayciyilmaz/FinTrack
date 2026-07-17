package com.example.fintrack.presentation.screens.transaction_update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.core.constants.expenseCategories
import com.example.fintrack.core.constants.incomeCategories
import com.example.fintrack.core.util.dateFormatter
import com.example.fintrack.core.util.timeFormatter
import com.example.fintrack.presentation.components.AmountInput
import com.example.fintrack.presentation.components.CategorySelector
import com.example.fintrack.presentation.components.DateTimeSection
import com.example.fintrack.presentation.components.EditAlertDialog
import com.example.fintrack.presentation.components.EditButton
import com.example.fintrack.presentation.components.EditDatePicker
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.EditTimePicker
import com.example.fintrack.presentation.components.NoteInput
import com.example.fintrack.presentation.components.RecurringPaymentSection
import com.example.fintrack.presentation.components.TransactionTypeSelector
import com.example.fintrack.presentation.components.ValidationErrorText
import com.example.fintrack.presentation.navigation.FinTrackScreens
import com.example.fintrack.presentation.navigation.navigateAndClearBackStack
import java.time.LocalTime

@Composable
fun UpdateTransactionScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: UpdateTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val isIncome = uiState.selectedTypeIndex == 1

    LaunchedEffect(actionState.isSuccess) {
        if (actionState.isSuccess) {
            navigateAndClearBackStack(
                navController = navController,
                destination = FinTrackScreens.HomeScreen.route,
                popUpToRoute = FinTrackScreens.HomeScreen.route,
                inclusive = true
            )
        }
    }

    if (uiState.showDeleteDialog) {
        EditAlertDialog(
            title = stringResource(id = R.string.title_delete_dialog),
            message = stringResource(id = R.string.message_delete_transaction),
            onDismiss = viewModel::onDismissDeleteDialog,
            onConfirm = {
                viewModel.onDismissDeleteDialog()
                viewModel.deleteTransaction()
            }
        )
    }

    if (uiState.showDatePicker) {
        EditDatePicker(
            currentMonth = uiState.currentMonth,
            tempSelectedDate = uiState.tempSelectedDate,
            onMonthChange = viewModel::onMonthChange,
            onDateSelect = viewModel::onTempDateChange,
            onDismiss = viewModel::onDatePickerDismiss,
            onConfirm = viewModel::onDateConfirm
        )
    }

    if (uiState.showTimePicker) {
        EditTimePicker(
            initialTime = uiState.selectedTime ?: LocalTime.now(),
            onDismiss = viewModel::onTimePickerDismiss,
            onConfirm = viewModel::onTimeConfirm
        )
    }

    EditScaffold(
        title = stringResource(id = R.string.title_update_transaction),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TransactionTypeSelector(
                options = listOf(
                    stringResource(id = R.string.label_expense),
                    stringResource(id = R.string.label_income)
                ),
                selectedIndex = uiState.selectedTypeIndex,
                onOptionSelected = viewModel::onTypeChange
            )
            CategorySelector(
                categories = if (isIncome) incomeCategories else expenseCategories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = viewModel::onCategoryChange
            )
            AmountInput(
                amount = uiState.amount,
                onAmountChange = viewModel::onAmountChange
            )
            NoteInput(
                note = uiState.note,
                onNoteChange = viewModel::onNoteChange
            )
            DateTimeSection(
                date = uiState.selectedDate?.format(dateFormatter) ?: "",
                time = uiState.selectedTime?.format(timeFormatter) ?: "",
                onDateClick = viewModel::onDatePickerShow,
                onTimeClick = viewModel::onTimePickerShow
            )
            RecurringPaymentSection(
                isIncome = isIncome,
                isRecurring = uiState.isRecurring,
                onRecurringChange = viewModel::onRecurringChange,
                isReminder = uiState.isReminder,
                onReminderChange = viewModel::onReminderChange
            )
            if (uiState.showValidationError) {
                ValidationErrorText(
                    error = stringResource(id = R.string.error_required_fields)
                )
            }
            EditButton(
                onClick = viewModel::onShowDeleteDialog,
                text = stringResource(id = R.string.label_delete),
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.expense_red)
                )
            )
            EditButton(
                onClick = viewModel::updateTransaction,
                text = stringResource(id = R.string.label_save),
                modifier = modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UpdateTransactionScreenPreview() {
    UpdateTransactionScreen(navController = rememberNavController())
}