package com.example.fintrack.presentation.screens.transaction_update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.core.constants.expenseCategories
import com.example.fintrack.core.constants.incomeCategories
import com.example.fintrack.core.util.dateFormatter
import com.example.fintrack.core.util.timeFormatter
import com.example.fintrack.domain.model.TransactionCategory
import com.example.fintrack.presentation.components.AmountInput
import com.example.fintrack.presentation.components.CategorySelector
import com.example.fintrack.presentation.components.DateTimeSection
import com.example.fintrack.presentation.components.EditButton
import com.example.fintrack.presentation.components.EditDatePicker
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.EditTimePicker
import com.example.fintrack.presentation.components.NoteInput
import com.example.fintrack.presentation.components.RecurringPaymentSection
import com.example.fintrack.presentation.components.TransactionTypeSelector
import com.example.fintrack.presentation.components.ValidationErrorText
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@Composable
fun UpdateTransactionScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedTypeIndex by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf<TransactionCategory?>(null) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var tempSelectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var isRecurring by remember { mutableStateOf(false) }
    var isReminder by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showValidationError by remember { mutableStateOf(false) }

    val isIncome = selectedTypeIndex == 1

    if (showDatePicker) {
        EditDatePicker(
            currentMonth = currentMonth,
            tempSelectedDate = tempSelectedDate,
            onMonthChange = { currentMonth = it },
            onDateSelect = { tempSelectedDate = it },
            onDismiss = { showDatePicker = false },
            onConfirm = {
                selectedDate = tempSelectedDate
                showDatePicker = false
                showValidationError = false
            }
        )
    }

    if (showTimePicker) {
        EditTimePicker(
            initialTime = selectedTime ?: LocalTime.now(),
            onDismiss = { showTimePicker = false },
            onConfirm = {
                selectedTime = it
                showTimePicker = false
                showValidationError = false
            }
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
                selectedIndex = selectedTypeIndex,
                onOptionSelected = {
                    selectedTypeIndex = it
                    selectedCategory = null
                    amount = ""
                    note = ""
                }
            )
            CategorySelector(
                categories = if (isIncome) incomeCategories else expenseCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = {
                    selectedCategory = it
                    showValidationError = false
                }
            )
            AmountInput(
                amount = amount,
                onAmountChange = {
                    amount = it
                    showValidationError = false
                }
            )
            NoteInput(
                note = note,
                onNoteChange = { note = it }
            )
            DateTimeSection(
                date = selectedDate?.format(dateFormatter) ?: "",
                time = selectedTime?.format(timeFormatter) ?: "",
                onDateClick = {
                    tempSelectedDate = selectedDate ?: LocalDate.now()
                    showDatePicker = true
                },
                onTimeClick = {
                    selectedTime = selectedTime ?: LocalTime.now()
                    showTimePicker = true
                }
            )
            RecurringPaymentSection(
                isIncome = isIncome,
                isRecurring = isRecurring,
                onRecurringChange = { isRecurring = it },
                isReminder = isReminder,
                onReminderChange = { isReminder = it }
            )
            if (showValidationError) {
                ValidationErrorText(
                    error = stringResource(id = R.string.error_required_fields)
                )
            }
            EditButton(
                onClick = {},
                text = stringResource(id = R.string.label_delete),
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.expense_red)
                )
            )
            EditButton(
                onClick = {},
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