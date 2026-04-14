package com.example.fintrack.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.components.EditButton
import com.example.fintrack.components.EditDatePicker
import com.example.fintrack.components.EditOutlinedTextField
import com.example.fintrack.components.EditScaffold
import com.example.fintrack.components.EditTimePicker
import com.example.fintrack.features.main.expenseCategories
import com.example.fintrack.features.main.incomeCategories
import com.example.fintrack.model.TransactionCategory
import com.example.fintrack.util.dateFormatter
import com.example.fintrack.util.timeFormatter
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@Composable
fun AddTransactionScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var isIncome by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Int?>(null) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var tempSelectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isRecurring by remember { mutableStateOf(false) }
    var isReminder by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> selectedImageUri = uri }

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
            }
        )
    }

    EditScaffold(
        title = stringResource(id = R.string.title_add_transaction),
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
                isIncome = isIncome,
                onTypeSelected = {
                    isIncome = it
                    selectedCategory = null
                    amount = ""
                    note = ""
                }
            )

            CategorySelector(
                categories = if (isIncome) incomeCategories else expenseCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
            AmountInput(
                amount = amount,
                onAmountChange = { amount = it }
            )
            NoteInput(
                note = note,
                onNoteChange = { note = it }
            )
            DateTimeSection(
                date = selectedDate?.format(dateFormatter) ?: "",
                time = selectedTime?.format(timeFormatter) ?: "",
                onDateClick = { showDatePicker = true },
                onTimeClick = { showTimePicker = true }
            )
            PhotoSection(
                selectedImageUri = selectedImageUri,
                onPickPhoto = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemovePhoto = { selectedImageUri = null }
            )
            RecurringPaymentSection(
                isIncome = isIncome,
                isRecurring = isRecurring,
                onRecurringChange = { isRecurring = it },
                isReminder = isReminder,
                onReminderChange = { isReminder = it }
            )
            EditButton(
                onClick = {},
                text = stringResource(id = R.string.label_save),
                modifier = modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TransactionTypeSelector(
    isIncome: Boolean,
    onTypeSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_expense),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = if (!isIncome) Color.White else colorResource(id = R.color.text_quaternary),
            textAlign = TextAlign.Center,
            modifier = modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (!isIncome) colorResource(id = R.color.bottom_bar_fab) else Color.Transparent)
                .clickable { onTypeSelected(false) }
                .padding(vertical = 10.dp)
        )
        Text(
            text = stringResource(id = R.string.label_income),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = if (isIncome) Color.White else colorResource(id = R.color.text_quaternary),
            textAlign = TextAlign.Center,
            modifier = modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isIncome) colorResource(id = R.color.bottom_bar_fab) else Color.Transparent)
                .clickable { onTypeSelected(true) }
                .padding(vertical = 10.dp)
        )
    }
}

@Composable
private fun CategorySelector(
    categories: List<TransactionCategory>,
    selectedCategory: Int?,
    onCategorySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_category),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category.labelResId
                CategoryItem(
                    category = category,
                    isSelected = isSelected,
                    onCategorySelected = { onCategorySelected(category.labelResId) }
                )
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: TransactionCategory,
    isSelected: Boolean,
    onCategorySelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) colorResource(id = R.color.bottom_bar_fab) else Color.White)
            .clickable { onCategorySelected() }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else colorResource(id = R.color.bottom_bar_fab),
            modifier = modifier.padding(2.dp)
        )
        Text(
            text = stringResource(id = category.labelResId),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = if (isSelected) Color.White else colorResource(id = R.color.bottom_bar_fab)
        )
    }
}

@Composable
private fun AmountInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_amount),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        EditOutlinedTextField(
            value = amount,
            onValueChange = { onAmountChange(it.filter { c -> c.isDigit() }) },
            modifier = modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.label_amount_placeholder),
                    color = colorResource(id = R.color.text_secondary)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(id = R.color.bottom_bar_fab),
                unfocusedBorderColor = colorResource(id = R.color.text_secondary),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
private fun NoteInput(
    note: String,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_note),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        EditOutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            modifier = modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.label_note_placeholder),
                    color = colorResource(id = R.color.text_secondary)
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(id = R.color.bottom_bar_fab),
                unfocusedBorderColor = colorResource(id = R.color.text_secondary),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
private fun DateTimeSection(
    date: String,
    time: String,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_date_time),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EditOutlinedTextField(
                value = date,
                onValueChange = {},
                modifier = modifier
                    .weight(1f)
                    .clickable { onDateClick() },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.label_date),
                        color = colorResource(id = R.color.text_secondary)
                    )
                },
                enabled = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = colorResource(id = R.color.bottom_bar_fab)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = colorResource(id = R.color.text_secondary),
                    disabledContainerColor = Color.White,
                    disabledTextColor = colorResource(id = R.color.text_primary),
                    disabledLeadingIconColor = colorResource(id = R.color.bottom_bar_fab),
                    disabledPlaceholderColor = colorResource(id = R.color.text_secondary)
                )
            )
            EditOutlinedTextField(
                value = time,
                onValueChange = {},
                modifier = modifier
                    .weight(1f)
                    .clickable { onTimeClick() },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.label_time),
                        color = colorResource(id = R.color.text_secondary)
                    )
                },
                enabled = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = colorResource(id = R.color.bottom_bar_fab)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = colorResource(id = R.color.text_secondary),
                    disabledContainerColor = Color.White,
                    disabledTextColor = colorResource(id = R.color.text_primary),
                    disabledLeadingIconColor = colorResource(id = R.color.bottom_bar_fab),
                    disabledPlaceholderColor = colorResource(id = R.color.text_secondary)
                )
            )
        }
    }
}

@Composable
private fun PhotoSection(
    selectedImageUri: Uri?,
    onPickPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = colorResource(id = R.color.bottom_bar_fab)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.label_photo),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            if (selectedImageUri == null) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colorResource(id = R.color.quick_action_background))
                        .drawBehind {
                            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12.dp.toPx(), 8.dp.toPx()), 0f)
                            drawRoundRect(
                                color = borderColor,
                                cornerRadius = CornerRadius(14.dp.toPx()),
                                style = Stroke(width = 2.dp.toPx(), pathEffect = pathEffect)
                            )
                        }
                        .clickable { onPickPhoto() }
                        .padding(vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = null,
                        tint = colorResource(id = R.color.bottom_bar_fab)
                    )
                    Text(
                        text = stringResource(id = R.string.label_photo_select),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colorResource(id = R.color.bottom_bar_fab)
                    )
                    Text(
                        text = stringResource(id = R.string.label_photo_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = colorResource(id = R.color.text_secondary)
                    )
                }
            } else {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = null,
                        tint = colorResource(id = R.color.bottom_bar_fab)
                    )
                    Text(
                        text = selectedImageUri.lastPathSegment ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colorResource(id = R.color.text_primary),
                        modifier = modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Box(
                        modifier = modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(colorResource(id = R.color.transaction_expense_background))
                            .clickable { onRemovePhoto() }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            tint = colorResource(id = R.color.expense_red)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecurringPaymentSection(
    isIncome: Boolean,
    isRecurring: Boolean,
    onRecurringChange: (Boolean) -> Unit,
    isReminder: Boolean,
    onReminderChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        RecurringPaymentRow(
            icon = Icons.Filled.Repeat,
            title = stringResource(id = if (isIncome) R.string.label_recurring_income else R.string.label_recurring_payment),
            description = stringResource(id = if (isIncome) R.string.label_recurring_income_desc else R.string.label_recurring_payment_desc),
            checked = isRecurring,
            onCheckedChange = onRecurringChange
        )
        HorizontalDivider(color = colorResource(id = R.color.divider_color))
        RecurringPaymentRow(
            icon = Icons.Filled.Notifications,
            title = stringResource(id = R.string.label_reminder),
            description = stringResource(id = if (isIncome) R.string.label_reminder_income_desc else R.string.label_reminder_desc),
            checked = isReminder,
            onCheckedChange = onReminderChange
        )
    }
}

@Composable
private fun RecurringPaymentRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorResource(id = R.color.bottom_bar_fab),
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(colorResource(id = R.color.quick_action_background))
                .padding(10.dp)
        )
        Column(
            modifier = modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colorResource(id = R.color.text_primary)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = colorResource(id = R.color.text_secondary)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colorResource(id = R.color.bottom_bar_fab),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = colorResource(id = R.color.switch_unchecked_track)
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddTransactionScreenPreview() {
    AddTransactionScreen(navController = rememberNavController())
}