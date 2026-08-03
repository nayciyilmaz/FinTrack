package com.example.fintrack.presentation.screens.profile

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fintrack.R
import com.example.fintrack.presentation.components.EditAlertDialog
import com.example.fintrack.presentation.components.EditButton
import com.example.fintrack.presentation.components.EditScaffold
import com.example.fintrack.presentation.components.ProfileDialogHost
import com.example.fintrack.presentation.components.SettingsDialogHost

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.shouldRecreateActivity) {
        if (uiState.shouldRecreateActivity) {
            (context as? Activity)?.recreate()
            viewModel.onRecreateActivityHandled()
        }
    }

    ProfileDialogHost(uiState = uiState, viewModel = viewModel)
    SettingsDialogHost(uiState = uiState, viewModel = viewModel)

    if (uiState.showLogoutConfirmDialog) {
        EditAlertDialog(
            title = stringResource(id = R.string.profile_logout),
            message = stringResource(id = R.string.profile_logout_confirm_message),
            onDismiss = viewModel::onCancelLogout,
            onConfirm = viewModel::onConfirmLogout,
            confirmColor = colorResource(id = R.color.expense_red)
        )
    }

    EditScaffold(
        title = stringResource(id = R.string.title_profile),
        navController = navController
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(
                initials = actionState.initials,
                fullName = "${actionState.firstName} ${actionState.lastName}".trim(),
                email = actionState.email
            )
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        value = actionState.transactionCount.toString(),
                        label = stringResource(id = R.string.profile_stat_total_transactions),
                        modifier = modifier.weight(1f)
                    )
                    StatCard(
                        value = actionState.activeGoalsCount.toString(),
                        label = stringResource(id = R.string.profile_stat_active_goals),
                        modifier = modifier.weight(1f)
                    )
                    StatCard(
                        value = if (actionState.showUsageInDays) {
                            stringResource(id = R.string.profile_usage_days_value, actionState.usageDays)
                        } else {
                            stringResource(id = R.string.profile_usage_months_value, actionState.usageMonths)
                        },
                        label = stringResource(id = R.string.profile_stat_usage),
                        modifier = modifier.weight(1f)
                    )
                }
                Text(
                    text = stringResource(id = R.string.profile_section_account_info),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = modifier.padding(start = 4.dp, bottom = 2.dp)
                )
                AccountInfoCard(
                    fullName = "${actionState.firstName} ${actionState.lastName}".trim(),
                    email = actionState.email,
                    passwordChangedAtDisplay = actionState.passwordChangedAtDisplay,
                    onNameClick = viewModel::onShowNameDialog,
                    onEmailClick = viewModel::onShowEmailDialog,
                    onPasswordClick = viewModel::onShowPasswordDialog
                )
                Text(
                    text = stringResource(id = R.string.profile_section_app_settings),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = modifier.padding(start = 4.dp, bottom = 2.dp)
                )
                AppSettingsCard(
                    currentLanguageDisplay = uiState.currentLanguageDisplay,
                    onCurrencyClick = viewModel::onShowCurrencyDialog,
                    onLanguageClick = viewModel::onShowLanguageDialog,
                    onFontSizeClick = viewModel::onShowFontSizeDialog
                )
                EditButton(
                    onClick = viewModel::onRequestLogout,
                    text = stringResource(id = R.string.profile_logout),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .border(
                            0.5.dp,
                            colorResource(id = R.color.expense_red).copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = colorResource(id = R.color.expense_red)
                    ),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = colorResource(id = R.color.expense_red)
                    )
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    initials: String,
    fullName: String,
    email: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.bottom_bar_fab))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(1.5.dp, Color.White, CircleShape)
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            Text(
                text = fullName,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = colorResource(id = R.color.bottom_bar_fab)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(id = R.color.text_quaternary)
        )
    }
}

@Composable
private fun AccountInfoCard(
    fullName: String,
    email: String,
    passwordChangedAtDisplay: String,
    onNameClick: () -> Unit,
    onEmailClick: () -> Unit,
    onPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        InfoRow(
            icon = Icons.Filled.Person,
            title = stringResource(id = R.string.profile_full_name),
            subtitle = fullName,
            onClick = onNameClick
        )
        HorizontalDivider(
            modifier = modifier.padding(horizontal = 16.dp),
            color = colorResource(id = R.color.divider_color)
        )
        InfoRow(
            icon = Icons.Filled.Email,
            title = stringResource(id = R.string.sign_in_email),
            subtitle = email,
            onClick = onEmailClick
        )
        HorizontalDivider(
            modifier = modifier.padding(horizontal = 16.dp),
            color = colorResource(id = R.color.divider_color)
        )
        InfoRow(
            icon = Icons.Filled.Lock,
            title = stringResource(id = R.string.profile_change_password),
            subtitle = stringResource(id = R.string.profile_password_last_changed, passwordChangedAtDisplay),
            onClick = onPasswordClick
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(id = R.color.quick_action_background))
                .padding(9.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorResource(id = R.color.icon_orange)
            )
        }
        Column(modifier = modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = colorResource(id = R.color.text_primary)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Black.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun AppSettingsCard(
    currentLanguageDisplay: String,
    onCurrencyClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onFontSizeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDarkMode by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        SettingRowWithValue(
            icon = Icons.Filled.AccountBalance,
            title = stringResource(id = R.string.profile_currency),
            value = stringResource(id = R.string.profile_currency_try),
            onClick = onCurrencyClick
        )
        HorizontalDivider(
            modifier = modifier.padding(horizontal = 16.dp),
            color = colorResource(id = R.color.divider_color)
        )
        SettingRowWithValue(
            icon = Icons.Filled.Language,
            title = stringResource(id = R.string.profile_language),
            value = currentLanguageDisplay,
            onClick = onLanguageClick
        )
        HorizontalDivider(
            modifier = modifier.padding(horizontal = 16.dp),
            color = colorResource(id = R.color.divider_color)
        )
        SettingRowWithValue(
            icon = Icons.Filled.TextFields,
            title = stringResource(id = R.string.profile_font_size),
            value = stringResource(id = R.string.profile_font_size_medium),
            onClick = onFontSizeClick
        )
        HorizontalDivider(
            modifier = modifier.padding(horizontal = 16.dp),
            color = colorResource(id = R.color.divider_color)
        )
        DarkModeRow(checked = isDarkMode, onCheckedChange = { isDarkMode = it })
    }
}

@Composable
private fun DarkModeRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(id = R.color.quick_action_background))
                .padding(9.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.DarkMode,
                contentDescription = null,
                tint = colorResource(id = R.color.icon_orange)
            )
        }
        Text(
            text = stringResource(id = R.string.profile_dark_mode),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = colorResource(id = R.color.text_primary),
            modifier = modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colorResource(id = R.color.bottom_bar_fab),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = colorResource(id = R.color.switch_unchecked_track),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun SettingRowWithValue(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(id = R.color.quick_action_background))
                .padding(9.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorResource(id = R.color.icon_orange)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = colorResource(id = R.color.text_primary),
            modifier = modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.2f)
            )
        }
    }
}