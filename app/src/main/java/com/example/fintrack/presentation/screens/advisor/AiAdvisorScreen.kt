package com.example.fintrack.presentation.screens.advisor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
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
import androidx.compose.ui.res.colorResource
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
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.EditTextButton
import com.example.fintrack.presentation.components.ScreenStateContent
import com.example.fintrack.presentation.components.SectionHeaderRow
import com.example.fintrack.core.constants.quickQuestionCategoryIcon
import com.example.fintrack.core.constants.quickQuestionCategoryLabelResId
import com.example.fintrack.core.constants.quickQuestionTitleFor
import com.example.fintrack.core.constants.quickQuestions
import com.example.fintrack.domain.model.AdvisorInsight

@Composable
fun AiAdvisorScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: AiAdvisorViewModel = hiltViewModel()
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

    if (uiState.showQuickQuestionsDialog) {
        QuickQuestionsDialog(
            onDismiss = viewModel::onDismissQuickQuestionsDialog,
            onQuestionSelected = viewModel::onQuestionAsked
        )
    }

    EditScaffold(
        title = stringResource(id = R.string.title_ai_advisor),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AiAssistantCard(
                periodText = actionState.periodText,
                healthScore = actionState.healthScore,
                riskLevel = actionState.riskLevel
            )
            SectionHeaderRow(
                title = stringResource(id = R.string.label_automatic_comments),
                actionText = stringResource(id = R.string.label_quick_questions),
                onActionClick = viewModel::onShowQuickQuestionsDialog,
                modifier = modifier.padding(vertical = 4.dp)
            )
            ScreenStateContent(
                isLoading = actionState.isLoading,
                isError = actionState.isError,
                isEmpty = actionState.insights.isEmpty(),
                emptyMessageResId = R.string.label_no_insights,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                actionState.insights.forEach { insight ->
                    AiCommentCard(
                        insight = insight,
                        isRefreshing = actionState.refreshingInsightId == insight.id,
                        onRefreshClick = { viewModel.onInsightRefreshRequested(insight.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickQuestionsDialog(
    onDismiss: () -> Unit,
    onQuestionSelected: (categoryKey: String, question: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = quickQuestions.map { it.categoryResId }.distinct()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.label_quick_questions),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.forEach { categoryResId ->
                    Text(
                        text = stringResource(id = categoryResId),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = colorResource(id = R.color.bottom_bar_fab)
                    )
                    val questions = quickQuestions.filter { it.categoryResId == categoryResId }
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(colorResource(id = R.color.quick_action_background))
                    ) {
                        questions.forEachIndexed { qIndex, question ->
                            val questionText = stringResource(id = question.questionResId)
                            Text(
                                text = questionText,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = colorResource(id = R.color.text_primary),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .clickable { onQuestionSelected(question.categoryKey, questionText) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            )
                            if (qIndex < questions.size - 1) {
                                HorizontalDivider(color = colorResource(id = R.color.divider_color))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            EditTextButton(
                text = stringResource(id = R.string.label_cancel),
                onClick = onDismiss
            )
        }
    )
}

@Composable
private fun AiAssistantCard(
    periodText: String,
    healthScore: Int,
    riskLevel: String,
    modifier: Modifier = Modifier
) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SmartToy,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(8.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = stringResource(id = R.string.label_fintrack_assistant),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = periodText,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_financial_health_score),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$healthScore",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White
                        )
                        Text(
                            text = "/ 100",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = modifier.padding(bottom = 3.dp)
                        )
                    }
                }
                Column(
                    modifier = modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_risk_level),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = riskLevel,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun AiCommentCard(
    insight: AdvisorInsight,
    isRefreshing: Boolean,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = quickQuestionCategoryIcon(insight.categoryKey)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_background))
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        tint = colorResource(id = R.color.bottom_bar_fab),
                        modifier = modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(colorResource(id = R.color.quick_action_background))
                            .padding(8.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = quickQuestionTitleFor(insight.question),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = colorResource(id = R.color.text_primary)
                        )
                        Text(
                            text = stringResource(id = quickQuestionCategoryLabelResId(insight.categoryKey)),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = colorResource(id = R.color.text_secondary)
                        )
                    }
                }
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = colorResource(id = R.color.bottom_bar_fab)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = colorResource(id = R.color.bottom_bar_fab),
                        modifier = modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onRefreshClick() }
                            .padding(4.dp)
                    )
                }
            }
            Text(
                text = insight.answer,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                color = colorResource(id = R.color.text_primary)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AiAdvisorScreenPreview() {
    AiAdvisorScreen(navController = rememberNavController())
}