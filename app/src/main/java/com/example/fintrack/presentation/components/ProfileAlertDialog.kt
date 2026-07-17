package com.example.fintrack.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.fintrack.R
import com.example.fintrack.presentation.screens.profile.ProfileDialogType
import com.example.fintrack.presentation.screens.profile.ProfileUiState
import com.example.fintrack.presentation.screens.profile.ProfileViewModel
import com.example.fintrack.presentation.screens.profile.SettingsDialogType

@Composable
fun ProfileDialogHost(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel
) {
    when (uiState.activeDialog) {
        ProfileDialogType.NAME -> {
            NameEditDialog(
                uiState = uiState,
                onFirstNameChange = viewModel::onFirstNameInputChange,
                onLastNameChange = viewModel::onLastNameInputChange,
                onDismiss = viewModel::onDismissDialog,
                onUpdate = {}
            )
        }
        ProfileDialogType.EMAIL -> {
            EmailEditDialog(
                uiState = uiState,
                onEmailChange = viewModel::onEmailInputChange,
                onDismiss = viewModel::onDismissDialog,
                onUpdate = {}
            )
        }
        ProfileDialogType.PASSWORD -> {
            PasswordEditDialog(
                uiState = uiState,
                onCurrentPasswordChange = viewModel::onCurrentPasswordInputChange,
                onNewPasswordChange = viewModel::onNewPasswordInputChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordInputChange,
                onToggleCurrentPasswordVisibility = viewModel::toggleCurrentPasswordVisibility,
                onToggleNewPasswordVisibility = viewModel::toggleNewPasswordVisibility,
                onToggleConfirmPasswordVisibility = viewModel::toggleConfirmPasswordVisibility,
                onDismiss = viewModel::onDismissDialog,
                onUpdate = {}
            )
        }
        null -> Unit
    }
}

@Composable
fun NameEditDialog(
    uiState: ProfileUiState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.profile_full_name),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditOutlinedTextField(
                    value = uiState.firstNameInput,
                    onValueChange = onFirstNameChange,
                    modifier = modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.sign_up_first_name)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = colorResource(id = R.color.icon_orange)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
                EditOutlinedTextField(
                    value = uiState.lastNameInput,
                    onValueChange = onLastNameChange,
                    modifier = modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.sign_up_last_name)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = colorResource(id = R.color.icon_orange)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
            }
        },
        confirmButton = {
            EditTextButton(
                text = stringResource(id = R.string.label_update),
                onClick = onUpdate
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
fun EmailEditDialog(
    uiState: ProfileUiState,
    onEmailChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.sign_in_email),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditOutlinedTextField(
                    value = uiState.emailInput,
                    onValueChange = onEmailChange,
                    modifier = modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.sign_in_email)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null,
                            tint = colorResource(id = R.color.icon_orange)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    )
                )
            }
        },
        confirmButton = {
            EditTextButton(
                text = stringResource(id = R.string.label_update),
                onClick = onUpdate
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
fun PasswordEditDialog(
    uiState: ProfileUiState,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.profile_change_password),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditOutlinedTextField(
                    value = uiState.currentPasswordInput,
                    onValueChange = onCurrentPasswordChange,
                    modifier = modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.profile_current_password_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = colorResource(id = R.color.icon_orange)
                        )
                    },
                    trailingIcon = {
                        EditIconButton(
                            onClick = onToggleCurrentPasswordVisibility,
                            imageVector = if (uiState.isCurrentPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        )
                    },
                    visualTransformation = if (uiState.isCurrentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )
                EditOutlinedTextField(
                    value = uiState.newPasswordInput,
                    onValueChange = onNewPasswordChange,
                    modifier = modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.forgot_password_new_password_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = colorResource(id = R.color.icon_orange)
                        )
                    },
                    trailingIcon = {
                        EditIconButton(
                            onClick = onToggleNewPasswordVisibility,
                            imageVector = if (uiState.isNewPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        )
                    },
                    visualTransformation = if (uiState.isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )
                EditOutlinedTextField(
                    value = uiState.confirmPasswordInput,
                    onValueChange = onConfirmPasswordChange,
                    modifier = modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.forgot_password_new_password_repeat)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = colorResource(id = R.color.icon_orange)
                        )
                    },
                    trailingIcon = {
                        EditIconButton(
                            onClick = onToggleConfirmPasswordVisibility,
                            imageVector = if (uiState.isConfirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        )
                    },
                    visualTransformation = if (uiState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )
            }
        },
        confirmButton = {
            EditTextButton(
                text = stringResource(id = R.string.label_update),
                onClick = onUpdate
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
fun SettingsDialogHost(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel
) {
    when (uiState.activeSettingsDialog) {
        SettingsDialogType.CURRENCY -> {
            CurrencySelectDialog(
                uiState = uiState,
                onOptionSelect = viewModel::onCurrencyOptionSelect,
                onDismiss = viewModel::onDismissSettingsDialog,
                onApply = {}
            )
        }
        SettingsDialogType.LANGUAGE -> {
            LanguageSelectDialog(
                uiState = uiState,
                onOptionSelect = viewModel::onLanguageOptionSelect,
                onDismiss = viewModel::onDismissSettingsDialog,
                onApply = {}
            )
        }
        SettingsDialogType.FONT_SIZE -> {
            FontSizeSelectDialog(
                uiState = uiState,
                onOptionSelect = viewModel::onFontSizeOptionSelect,
                onDismiss = viewModel::onDismissSettingsDialog,
                onApply = {}
            )
        }
        null -> Unit
    }
}

@Composable
fun CurrencySelectDialog(
    uiState: ProfileUiState,
    onOptionSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        stringResource(id = R.string.profile_currency_try),
        stringResource(id = R.string.profile_currency_eur),
        stringResource(id = R.string.profile_currency_usd)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.profile_currency),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                options.forEach { option ->
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = option == uiState.selectedCurrencyOption,
                                onClick = { onOptionSelect(option) }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == uiState.selectedCurrencyOption,
                            onClick = { onOptionSelect(option) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colorResource(id = R.color.bottom_bar_fab)
                            )
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorResource(id = R.color.text_primary)
                        )
                    }
                }
            }
        },
        confirmButton = {
            EditTextButton(
                text = stringResource(id = R.string.label_apply),
                onClick = onApply
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
fun LanguageSelectDialog(
    uiState: ProfileUiState,
    onOptionSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        stringResource(id = R.string.profile_language_tr),
        stringResource(id = R.string.profile_language_en),
        stringResource(id = R.string.profile_language_de)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.profile_language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                options.forEach { option ->
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = option == uiState.selectedLanguageOption,
                                onClick = { onOptionSelect(option) }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == uiState.selectedLanguageOption,
                            onClick = { onOptionSelect(option) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colorResource(id = R.color.bottom_bar_fab)
                            )
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorResource(id = R.color.text_primary)
                        )
                    }
                }
            }
        },
        confirmButton = {
            EditTextButton(
                text = stringResource(id = R.string.label_apply),
                onClick = onApply
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
fun FontSizeSelectDialog(
    uiState: ProfileUiState,
    onOptionSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        stringResource(id = R.string.profile_font_size_small),
        stringResource(id = R.string.profile_font_size_medium),
        stringResource(id = R.string.profile_font_size_large)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.profile_font_size),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                options.forEach { option ->
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = option == uiState.selectedFontSizeOption,
                                onClick = { onOptionSelect(option) }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == uiState.selectedFontSizeOption,
                            onClick = { onOptionSelect(option) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colorResource(id = R.color.bottom_bar_fab)
                            )
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorResource(id = R.color.text_primary)
                        )
                    }
                }
            }
        },
        confirmButton = {
            EditTextButton(
                text = stringResource(id = R.string.label_apply),
                onClick = onApply
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