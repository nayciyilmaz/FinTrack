package com.example.fintrack.presentation.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fintrack.R
import com.example.fintrack.core.constants.categoryKeyToIcon
import com.example.fintrack.core.constants.categoryKeyToLabelResId
import com.example.fintrack.core.constants.expenseCategories
import com.example.fintrack.presentation.components.EditIconButton
import com.example.fintrack.presentation.components.EditOutlinedTextField
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.EditTextButton
import com.example.fintrack.presentation.components.ProgressBar

@Composable
fun BudgetLimitsScreen(
    navController: NavController,
    viewModel: BudgetLimitsViewModel = hiltViewModel(),
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

    if (uiState.showManageDialog) {
        ManageLimitsDialog(
            uiState = uiState,
            income = actionState.income,
            onAmountChange = viewModel::onDialogAmountChange,
            onActiveToggle = viewModel::onDialogActiveToggle,
            onDismiss = viewModel::onDismissManageDialog,
            onSave = viewModel::saveDialogBudgets
        )
    }

    EditScaffold(
        title = stringResource(id = R.string.title_budget_limits),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            BudgetCard(
                totalBudget = actionState.income,
                usedBudget = actionState.expense
            )
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.label_category_limits),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                EditTextButton(
                    text = stringResource(id = R.string.label_edit),
                    onClick = viewModel::onShowManageDialog,
                    color = colorResource(id = R.color.bottom_bar_fab)
                )
            }
            BudgetLimitsContent(actionState = actionState)
        }
    }
}

@Composable
private fun BudgetLimitsContent(
    actionState: BudgetLimitsActionState,
    modifier: Modifier = Modifier
) {
    when {
        actionState.isLoading -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.bottom_bar_fab)
                )
            }
        }
        actionState.isError -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.error_general),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.text_secondary)
                )
            }
        }
        actionState.budgets.isEmpty() -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.label_no_budgets),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.text_secondary)
                )
            }
        }
        else -> {
            actionState.budgets.forEach { budget ->
                val usedAmount = actionState.categoryExpenses[budget.category] ?: 0
                CategoryLimitCard(
                    icon = categoryKeyToIcon(budget.category),
                    categoryName = stringResource(id = categoryKeyToLabelResId(budget.category)),
                    usedAmount = usedAmount,
                    limitAmount = budget.limitAmount.toInt()
                )
            }
        }
    }
}

@Composable
private fun ManageLimitsDialog(
    uiState: BudgetLimitsUiState,
    income: Int,
    onAmountChange: (String, String) -> Unit,
    onActiveToggle: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val categoryListMaxHeight = (LocalConfiguration.current.screenHeightDp * 0.55f).dp

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.label_category_limits),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(
                    modifier = modifier
                        .heightIn(max = categoryListMaxHeight)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    expenseCategories.forEach { category ->
                        val isActive = uiState.dialogActiveStates[category.key] ?: false
                        val amount = uiState.dialogAmounts[category.key] ?: ""

                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                tint = if (isActive) colorResource(id = R.color.bottom_bar_fab) else Color.Gray,
                                modifier = modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isActive) colorResource(id = R.color.quick_action_background)
                                        else Color.LightGray.copy(alpha = 0.3f)
                                    )
                                    .padding(8.dp)
                            )
                            Text(
                                text = stringResource(id = category.labelResId),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = if (isActive) colorResource(id = R.color.text_primary) else Color.Gray,
                                modifier = modifier.weight(1f)
                            )
                            EditOutlinedTextField(
                                value = amount,
                                onValueChange = { onAmountChange(category.key, it) },
                                modifier = modifier.weight(1f),
                                enabled = isActive,
                                placeholder = {
                                    Text(
                                        text = "0",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        defaultKeyboardAction(ImeAction.Done)
                                        keyboardController?.hide()
                                    }
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            EditIconButton(
                                onClick = { onActiveToggle(category.key) },
                                imageVector = if (isActive) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                tint = if (isActive) colorResource(id = R.color.bottom_bar_fab) else Color.Gray
                            )
                        }
                    }
                }

                HorizontalDivider(color = colorResource(id = R.color.divider_color))

                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.label_total_limit),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colorResource(id = R.color.text_primary)
                    )
                    Text(
                        text = "₺${"%,d".format(uiState.totalLimit).replace(",", ".")}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = colorResource(id = R.color.bottom_bar_fab)
                    )
                }

                if (uiState.totalLimit > income) {
                    Text(
                        text = stringResource(id = R.string.warning_budget_exceeds_income),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = colorResource(id = R.color.expense_red)
                    )
                }
            }
        },
        confirmButton = {
            EditTextButton(
                text = stringResource(id = R.string.label_save),
                onClick = onSave
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
private fun BudgetCard(
    totalBudget: Int,
    usedBudget: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalBudget > 0) usedBudget.toFloat() / totalBudget.toFloat() else 0f
    val remaining = totalBudget - usedBudget
    val percentage = (progress * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(id = R.string.label_monthly_budget),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White
                    )
                    Text(
                        text = "₺${"%,d".format(totalBudget).replace(",", ".")}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_used),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = "₺${"%,d".format(usedBudget).replace(",", ".")}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            ProgressBar(
                progress = progress.coerceIn(0f, 1f),
                trackColor = Color.White.copy(alpha = 0.2f),
                progressColor = Color.White.copy(alpha = 0.85f)
            )

            Text(
                text = "Bütçenin %$percentage'ini kullandın · ₺${
                    "%,d".format(remaining).replace(",", ".")
                } kaldı",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun CategoryLimitCard(
    icon: ImageVector,
    categoryName: String,
    usedAmount: Int,
    limitAmount: Int,
    modifier: Modifier = Modifier
) {
    val progress = usedAmount.toFloat() / limitAmount.toFloat()
    val percentage = (progress * 100).toInt()

    val isOverLimit = progress >= 1f
    val isWarning = progress >= 0.75f

    val activeColor = when {
        isOverLimit -> colorResource(id = R.color.expense_red)
        isWarning -> colorResource(id = R.color.bottom_bar_fab)
        else -> colorResource(id = R.color.income_green)
    }

    val activeBackgroundColor = when {
        isOverLimit -> colorResource(id = R.color.transaction_expense_background)
        isWarning -> colorResource(id = R.color.quick_action_background)
        else -> colorResource(id = R.color.transaction_income_background)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = activeColor,
                        modifier = modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(activeBackgroundColor)
                            .padding(8.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colorResource(id = R.color.text_primary)
                        )
                        Text(
                            text = if (isOverLimit) stringResource(id = R.string.budget_status_exceeded) else if (isWarning) stringResource(id = R.string.budget_status_warning) else stringResource(id = R.string.budget_status_normal),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = activeColor
                        )
                    }
                }
                Text(
                    text = "%$percentage",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = activeColor
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "₺${"%,d".format(usedAmount).replace(",", ".")}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = colorResource(id = R.color.text_primary)
                    )
                    Text(
                        text = "₺${"%,d".format(limitAmount).replace(",", ".")}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                        color = Color.DarkGray
                    )
                }
                ProgressBar(
                    progress = progress.coerceIn(0f, 1f),
                    trackColor = activeBackgroundColor,
                    progressColor = activeColor
                )
            }
        }
    }
}
