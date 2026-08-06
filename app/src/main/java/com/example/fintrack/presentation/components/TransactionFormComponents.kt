package com.example.fintrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fintrack.R
import com.example.fintrack.core.util.currencySymbol
import com.example.fintrack.presentation.model.TransactionCategory

@Composable
fun CategorySelector(
    categories: List<TransactionCategory>,
    selectedCategory: TransactionCategory?,
    onCategorySelected: (TransactionCategory) -> Unit,
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
                CategoryItem(
                    category = category,
                    isSelected = selectedCategory?.key == category.key,
                    onCategorySelected = { onCategorySelected(category) }
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
            .background(if (isSelected) colorResource(id = R.color.bottom_bar_fab) else colorResource(id = R.color.card_background))
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
fun AmountInput(
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
                    text = stringResource(id = R.string.label_amount_placeholder, currencySymbol()),
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
                focusedContainerColor = colorResource(id = R.color.card_background),
                unfocusedContainerColor = colorResource(id = R.color.card_background)
            )
        )
    }
}

@Composable
fun NoteInput(
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
                focusedContainerColor = colorResource(id = R.color.card_background),
                unfocusedContainerColor = colorResource(id = R.color.card_background)
            )
        )
    }
}

@Composable
fun DateTimeSection(
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
                    disabledContainerColor = colorResource(id = R.color.card_background),
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
                    disabledContainerColor = colorResource(id = R.color.card_background),
                    disabledTextColor = colorResource(id = R.color.text_primary),
                    disabledLeadingIconColor = colorResource(id = R.color.bottom_bar_fab),
                    disabledPlaceholderColor = colorResource(id = R.color.text_secondary)
                )
            )
        }
    }
}

@Composable
fun RecurringPaymentSection(
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
            .background(colorResource(id = R.color.card_background))
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
    icon: ImageVector,
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
