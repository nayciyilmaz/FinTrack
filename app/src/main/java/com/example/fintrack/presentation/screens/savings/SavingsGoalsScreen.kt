package com.example.fintrack.presentation.screens.savings

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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fintrack.R
import com.example.fintrack.core.constants.goalCategories
import com.example.fintrack.core.util.currencySymbol
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.presentation.components.EditOutlinedTextField
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.EditTextButton
import com.example.fintrack.presentation.components.ProgressBar
import com.example.fintrack.presentation.components.ScreenStateContent
import com.example.fintrack.presentation.components.SectionHeaderRow
import com.example.fintrack.presentation.components.ValidationErrorText

@Composable
fun SavingsGoalsScreen(
    navController: NavController,
    viewModel: SavingsGoalsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
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
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (uiState.showAddDialog) {
        AddGoalDialog(
            uiState = uiState,
            onGoalNameChange = viewModel::onGoalNameChange,
            onTargetAmountChange = viewModel::onTargetAmountChange,
            onCategoryChange = viewModel::onCategoryChange,
            onDismiss = viewModel::dismissAddDialog,
            onConfirm = viewModel::addGoal
        )
    }

    uiState.selectedGoal?.let { goal ->
        GoalDetailDialog(
            goal = goal,
            uiState = uiState,
            onAddAmountChange = viewModel::onAddAmountChange,
            onNewTargetAmountChange = viewModel::onNewTargetAmountChange,
            onDismiss = viewModel::dismissGoalDetail,
            onUpdate = viewModel::updateGoal,
            onRequestDelete = viewModel::onRequestDeleteConfirm,
            onCancelDelete = viewModel::onCancelDeleteConfirm,
            onConfirmDelete = viewModel::deleteGoal
        )
    }

    EditScaffold(
        title = stringResource(id = R.string.title_savings_goals),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_lg))
                .verticalScroll(rememberScrollState())
        ) {
            SavingsCard(
                lastMonthSavings = actionState.lastMonthSavings,
                totalSavings = actionState.totalSavings
            )
            SectionHeaderRow(
                title = stringResource(id = R.string.label_active_goals),
                actionText = stringResource(id = R.string.label_new_goal),
                onActionClick = viewModel::showAddDialog,
                modifier = modifier.padding(vertical = dimensionResource(id = R.dimen.padding_xs))
            )
            ScreenStateContent(
                isLoading = actionState.isLoading,
                isError = actionState.isError,
                isEmpty = actionState.goals.isEmpty(),
                emptyMessageResId = R.string.label_no_goals,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.padding_md))
            ) {
                actionState.goals.forEach { goal ->
                    GoalCard(
                        icon = goalCategoryIcon(goal.category),
                        goal = goal,
                        estimatedDate = actionState.estimatedDates[goal.id],
                        onClick = { viewModel.selectGoal(goal) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddGoalDialog(
    uiState: SavingsGoalsUiState,
    onGoalNameChange: (String) -> Unit,
    onTargetAmountChange: (String) -> Unit,
    onCategoryChange: (Int, String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.label_new_goal_add),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_md))
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.label_category),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colorResource(id = R.color.text_primary)
                    )
                    Row(
                        modifier = modifier
                            .padding(top = dimensionResource(id = R.dimen.padding_sm))
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))
                    ) {
                        goalCategories.forEach { category ->
                            val isSelected = uiState.selectedCategoryResId == category.nameResId
                            val categoryName = stringResource(id = category.nameResId)
                            Column(
                                modifier = modifier
                                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_md)))
                                    .background(
                                        if (isSelected) colorResource(id = R.color.bottom_bar_fab)
                                        else colorResource(id = R.color.quick_action_background)
                                    )
                                    .clickable { onCategoryChange(category.nameResId, categoryName) }
                                    .padding(horizontal = dimensionResource(id = R.dimen.padding_md), vertical = dimensionResource(id = R.dimen.padding_sm)),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_xs))
                            ) {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else colorResource(id = R.color.bottom_bar_fab),
                                    modifier = modifier
                                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_sm)))
                                        .background(
                                            if (isSelected) Color.White.copy(alpha = 0.2f)
                                            else colorResource(id = R.color.card_background)
                                        )
                                        .padding(dimensionResource(id = R.dimen.padding_sm))
                                )
                                Text(
                                    text = categoryName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = if (isSelected) Color.White else colorResource(id = R.color.bottom_bar_fab)
                                )
                            }
                        }
                    }
                    uiState.categoryError?.let { ValidationErrorText(error = it) }
                }
                Column {
                    EditOutlinedTextField(
                        value = uiState.goalName,
                        onValueChange = onGoalNameChange,
                        modifier = modifier.fillMaxWidth(),
                        label = { Text(text = stringResource(id = R.string.savings_goal_name_label)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                defaultKeyboardAction(ImeAction.Done)
                                keyboardController?.hide()
                            }
                        )
                    )
                    uiState.nameError?.let { ValidationErrorText(error = it) }
                }
                Column {
                    EditOutlinedTextField(
                        value = uiState.targetAmount,
                        onValueChange = onTargetAmountChange,
                        modifier = modifier.fillMaxWidth(),
                        label = { Text(text = stringResource(id = R.string.savings_goal_amount_label)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                defaultKeyboardAction(ImeAction.Done)
                                keyboardController?.hide()
                            }
                        )
                    )
                    uiState.amountError?.let { ValidationErrorText(error = it) }
                }
            }
        },
        confirmButton = {
            EditTextButton(
                text = stringResource(id = R.string.label_add),
                onClick = onConfirm
            )
        },
        dismissButton = {
            EditTextButton(
                text = stringResource(id = R.string.label_cancel),
                onClick = onDismiss
            )
        }
    )
}

@Composable
private fun GoalDetailDialog(
    goal: SavingsGoal,
    uiState: SavingsGoalsUiState,
    onAddAmountChange: (String) -> Unit,
    onNewTargetAmountChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit,
    onRequestDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_10))
                ) {
                    Icon(
                        imageVector = goalCategoryIcon(goal.category),
                        contentDescription = null,
                        tint = colorResource(id = R.color.bottom_bar_fab),
                        modifier = modifier
                            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_10)))
                            .background(colorResource(id = R.color.quick_action_background))
                            .padding(dimensionResource(id = R.dimen.padding_sm))
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_2xs))) {
                        Text(
                            text = goal.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = goal.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorResource(id = R.color.text_secondary)
                        )
                    }
                }
                if (!uiState.isConfirmingDelete) {
                    Box(
                        modifier = modifier
                            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_10)))
                            .background(colorResource(id = R.color.transaction_expense_background))
                            .clickable { onRequestDelete() }
                            .padding(dimensionResource(id = R.dimen.padding_sm)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = colorResource(id = R.color.expense_red)
                        )
                    }
                }
            }
        },
        text = {
            if (uiState.isConfirmingDelete) {
                Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_lg))) {
                    Text(
                        text = stringResource(id = R.string.message_delete_goal),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(id = R.color.text_primary)
                    )
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        EditTextButton(
                            text = stringResource(id = R.string.label_cancel),
                            onClick = onCancelDelete
                        )
                        EditTextButton(
                            text = stringResource(id = R.string.label_confirm_action),
                            onClick = onConfirmDelete,
                            color = colorResource(id = R.color.expense_red)
                        )
                    }
                }
            } else {
                Column(
                    modifier = modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_md))
                ) {
                    EditOutlinedTextField(
                        value = uiState.addAmount,
                        onValueChange = onAddAmountChange,
                        modifier = modifier.fillMaxWidth(),
                        label = { Text(text = stringResource(id = R.string.savings_goal_add_amount_label)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                defaultKeyboardAction(ImeAction.Done)
                                keyboardController?.hide()
                            }
                        )
                    )
                    EditOutlinedTextField(
                        value = uiState.newTargetAmount,
                        onValueChange = onNewTargetAmountChange,
                        modifier = modifier.fillMaxWidth(),
                        label = { Text(text = stringResource(id = R.string.savings_goal_update_amount_label)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                defaultKeyboardAction(ImeAction.Done)
                                keyboardController?.hide()
                            }
                        )
                    )
                    uiState.updateError?.let { ValidationErrorText(error = it) }
                }
            }
        },
        confirmButton = {
            if (!uiState.isConfirmingDelete) {
                EditTextButton(
                    text = stringResource(id = R.string.label_save),
                    onClick = onUpdate
                )
            }
        },
        dismissButton = {
            if (!uiState.isConfirmingDelete) {
                EditTextButton(
                    text = stringResource(id = R.string.label_cancel),
                    onClick = onDismiss
                )
            }
        }
    )
}

@Composable
private fun SavingsCard(
    lastMonthSavings: Int,
    totalSavings: Int,
    modifier: Modifier = Modifier
) {
    val percentage = if (totalSavings != 0) ((lastMonthSavings.toFloat() / totalSavings.toFloat()) * 100).toInt() else 0

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
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_xs))) {
                    Text(
                        text = stringResource(id = R.string.label_last_month_savings),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White
                    )
                    Text(
                        text = "${currencySymbol()}${"%,d".format(lastMonthSavings).replace(",", ".")}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_xs))
                ) {
                    Text(
                        text = stringResource(id = R.string.label_total_savings),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = "${currencySymbol()}${"%,d".format(totalSavings).replace(",", ".")}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_lg)))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(dimensionResource(id = R.dimen.padding_lg)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_lg))
            ) {
                Icon(
                    imageVector = Icons.Filled.TrendingUp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = modifier
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_md)))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(dimensionResource(id = R.dimen.padding_10))
                )
                Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_2xs))) {
                    Text(
                        text = stringResource(id = R.string.savings_goal_monthly_share, percentage),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                    Text(
                        text = stringResource(id = R.string.label_goal_closer),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalCard(
    icon: ImageVector,
    goal: SavingsGoal,
    estimatedDate: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat() else 0f
    val percentage = (progress * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(id = R.dimen.padding_sm)),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_xl)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_sm)),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_background))
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_lg)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_lg))
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_10))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colorResource(id = R.color.bottom_bar_fab),
                        modifier = modifier
                            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_10)))
                            .background(colorResource(id = R.color.quick_action_background))
                            .padding(dimensionResource(id = R.dimen.padding_sm))
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_2xs))) {
                        Text(
                            text = goal.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colorResource(id = R.color.text_primary)
                        )
                        Text(
                            text = goal.category,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = colorResource(id = R.color.text_secondary)
                        )
                    }
                }
                Box(
                    modifier = modifier
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_10)))
                        .background(colorResource(id = R.color.quick_action_background))
                        .clickable { onClick() }
                        .padding(dimensionResource(id = R.dimen.padding_sm)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = colorResource(id = R.color.bottom_bar_fab)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${currencySymbol()}${"%,d".format(goal.currentAmount.toInt()).replace(",", ".")}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = colorResource(id = R.color.text_primary)
                    )
                    Text(
                        text = "${currencySymbol()}${"%,d".format(goal.targetAmount.toInt()).replace(",", ".")}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                        color = colorResource(id = R.color.text_secondary)
                    )
                }
                ProgressBar(
                    progress = progress,
                    trackColor = colorResource(id = R.color.quick_action_background),
                    progressColor = colorResource(id = R.color.bottom_bar_fab)
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.radius_md)))
                    .background(colorResource(id = R.color.quick_action_background))
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_md), vertical = dimensionResource(id = R.dimen.padding_sm)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_sm))
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = colorResource(id = R.color.bottom_bar_fab)
                )
                Text(
                    text = if (estimatedDate != null) {
                        stringResource(id = R.string.savings_goal_status_with_date_format, "%$percentage", estimatedDate)
                    } else {
                        stringResource(id = R.string.format_percentage_completed, "%$percentage")
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colorResource(id = R.color.bottom_bar_fab)
                )
            }
        }
    }
}

@Composable
private fun goalCategoryIcon(category: String): ImageVector {
    goalCategories.forEach { goalCategory ->
        if (stringResource(id = goalCategory.nameResId) == category) {
            return goalCategory.icon
        }
    }
    return Icons.Filled.MoreHoriz
}