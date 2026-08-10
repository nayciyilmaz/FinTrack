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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.fintrack.R
import com.example.fintrack.core.constants.categoryKeyToIcon
import com.example.fintrack.core.constants.categoryKeyToLabelResId
import com.example.fintrack.core.util.currencySymbol
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.presentation.model.TransactionCategory

@Composable
fun QuickAddSuggestions(
    suggestions: List<Transaction>,
    onSuggestionSelected: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestions.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_10))
    ) {
        Text(
            text = stringResource(id = R.string.label_quick_add),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_10))
        ) {
            suggestions.forEach { suggestion ->
                QuickAddItem(
                    transaction = suggestion,
                    onClick = { onSuggestionSelected(suggestion) }
                )
            }
        }
    }
}

@Composable
private fun QuickAddItem(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_xl)))
            .background(colorResource(id = R.color.card_background))
            .clickable { onClick() }
            .padding(horizontal = dimensionResource(id = R.dimen.padding_lg), vertical = dimensionResource(id = R.dimen.padding_md)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))
    ) {
        Icon(
            imageVector = categoryKeyToIcon(transaction.category),
            contentDescription = null,
            tint = colorResource(id = R.color.bottom_bar_fab)
        )
        Text(
            text = stringResource(id = categoryKeyToLabelResId(transaction.category)),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colorResource(id = R.color.text_primary)
        )
        Text(
            text = "${currencySymbol()}${transaction.amount.toInt()}",
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(id = R.color.text_secondary)
        )
    }
}

@Composable
fun CategorySelector(
    categories: List<TransactionCategory>,
    selectedCategory: TransactionCategory?,
    onCategorySelected: (TransactionCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_10))
    ) {
        Text(
            text = stringResource(id = R.string.label_category),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_10))
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
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_xl)))
            .background(if (isSelected) colorResource(id = R.color.bottom_bar_fab) else colorResource(id = R.color.card_background))
            .clickable { onCategorySelected() }
            .padding(horizontal = dimensionResource(id = R.dimen.padding_lg), vertical = dimensionResource(id = R.dimen.padding_lg)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else colorResource(id = R.color.bottom_bar_fab),
            modifier = modifier.padding(dimensionResource(id = R.dimen.padding_2xs))
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
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))
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
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))
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
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))
    ) {
        Text(
            text = stringResource(id = R.string.label_date_time),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_md))
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
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_lg)))
            .background(colorResource(id = R.color.card_background))
            .padding(horizontal = dimensionResource(id = R.dimen.padding_lg), vertical = dimensionResource(id = R.dimen.padding_xs))
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
            .padding(vertical = dimensionResource(id = R.dimen.padding_lg)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_md))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorResource(id = R.color.bottom_bar_fab),
            modifier = modifier
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_md)))
                .background(colorResource(id = R.color.quick_action_background))
                .padding(dimensionResource(id = R.dimen.padding_10))
        )
        Column(
            modifier = modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_2xs))
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
