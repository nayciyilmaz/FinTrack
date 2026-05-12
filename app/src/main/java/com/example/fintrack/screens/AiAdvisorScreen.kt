package com.example.fintrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.components.EditScaffold
import com.example.fintrack.components.EditTextButton
import com.example.fintrack.core.quickQuestions

@Composable
fun AiAdvisorScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var showQuickQuestionsDialog by remember { mutableStateOf(false) }

    if (showQuickQuestionsDialog) {
        QuickQuestionsDialog(
            onDismiss = { showQuickQuestionsDialog = false }
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
            AiAssistantCard()
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.label_automatic_comments),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                EditTextButton(
                    text = stringResource(id = R.string.label_quick_questions),
                    onClick = { showQuickQuestionsDialog = true },
                    color = colorResource(id = R.color.bottom_bar_fab)
                )
            }
            AiCommentCard(
                icon = Icons.Filled.TrendingDown,
                title = "Harcamalarını Azalt",
                subtitle = "Gider",
                description = "Eğlence kategorisinde geçen aya göre %23 daha fazla harcama yaptın. Bu ayın geri kalanında bu kategoride limit koymanı öneririm."
            )
            AiCommentCard(
                icon = Icons.Filled.TrendingUp,
                title = "Tasarruf Fırsatı",
                subtitle = "Tasarruf",
                description = "Aylık gelirinin %10'unu otomatik tasarrufa yönlendirirsen, yıl sonunda ₺30.000 biriktirebilirsin."
            )
            AiCommentCard(
                icon = Icons.Filled.Star,
                title = "Hedef Güncelleme",
                subtitle = "Hedefler",
                description = "Ev peşinatı hedefinize bu ay ₺2.000 daha eklemen, tahmini bitiş tarihinizi 3 ay öne alır."
            )
        }
    }
}

@Composable
private fun QuickQuestionsDialog(
    onDismiss: () -> Unit,
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
                            Text(
                                text = stringResource(id = question.questionResId),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = colorResource(id = R.color.text_primary),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .clickable { }
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
                            text = "Mart 2026 analizi",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(8.dp)
                )
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
                            text = "72",
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
                        text = "Orta",
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
    icon: ImageVector,
    title: String,
    subtitle: String,
    description: String,
    modifier: Modifier = Modifier
) {
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = colorResource(id = R.color.text_primary)
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = colorResource(id = R.color.text_secondary)
                    )
                }
            }
            Text(
                text = description,
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