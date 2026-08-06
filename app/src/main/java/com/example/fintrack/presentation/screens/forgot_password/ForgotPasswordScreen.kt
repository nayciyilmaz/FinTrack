package com.example.fintrack.presentation.screens.forgot_password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.presentation.components.EditButton
import com.example.fintrack.presentation.components.EditIconButton
import com.example.fintrack.presentation.components.EditOutlinedTextField
import com.example.fintrack.presentation.components.EditTextButton
import com.example.fintrack.presentation.components.ValidationErrorText
import com.example.fintrack.presentation.components.WaveBackground
import com.example.fintrack.presentation.navigation.FinTrackScreens
import com.example.fintrack.presentation.navigation.navigateAndClearBackStack

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val focusRequesters = remember { List(6) { FocusRequester() } }

    LaunchedEffect(actionState.isSuccess) {
        if (actionState.isSuccess) {
            navigateAndClearBackStack(
                navController = navController,
                destination = FinTrackScreens.SignInScreen.route,
                popUpToRoute = FinTrackScreens.ForgotPasswordScreen.route,
                inclusive = true
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.auth_background))
    ) {
        WaveBackground()
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.forgot_password_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = colorResource(id = R.color.sign_in_title),
                modifier = modifier.padding(bottom = 12.dp)
            )
            when (uiState.step) {
                ForgotPasswordStep.EMAIL -> EmailSection(
                    email = uiState.email,
                    emailError = uiState.validationErrors.emailError,
                    onEmailChange = viewModel::onEmailChange,
                    onSendClick = viewModel::sendCode,
                    onSignInClick = { navController.navigate(FinTrackScreens.SignInScreen.route) }
                )
                ForgotPasswordStep.CODE -> CodeSection(
                    codeDigits = uiState.codeDigits,
                    codeError = uiState.validationErrors.codeError,
                    focusRequesters = focusRequesters,
                    onCodeDigitChange = viewModel::onCodeDigitChange,
                    onConfirmClick = viewModel::verifyCode,
                    onBackClick = viewModel::goBackToEmail
                )
                ForgotPasswordStep.NEW_PASSWORD -> NewPasswordSection(
                    newPassword = uiState.newPassword,
                    newPasswordError = uiState.validationErrors.newPasswordError,
                    onNewPasswordChange = viewModel::onNewPasswordChange,
                    newPasswordVisible = uiState.isNewPasswordVisible,
                    onNewPasswordVisibleChange = viewModel::toggleNewPasswordVisibility,
                    newPasswordRepeat = uiState.newPasswordRepeat,
                    confirmPasswordError = uiState.validationErrors.confirmPasswordError,
                    onNewPasswordRepeatChange = viewModel::onNewPasswordRepeatChange,
                    newPasswordRepeatVisible = uiState.isNewPasswordRepeatVisible,
                    onNewPasswordRepeatVisibleChange = viewModel::toggleNewPasswordRepeatVisibility,
                    onSaveClick = viewModel::resetPassword,
                    onBackClick = viewModel::goBackToCode
                )
            }
        }
    }
}

@Composable
private fun EmailSection(
    email: String,
    emailError: String?,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Text(
        text = stringResource(id = R.string.forgot_password_description),
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        textAlign = TextAlign.Center,
        modifier = modifier.padding(bottom = 8.dp)
    )
    Column(modifier = modifier.fillMaxWidth()) {
        EditOutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            label = {
                Text(text = stringResource(id = R.string.sign_in_email))
            },
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
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
        emailError?.let { ValidationErrorText(error = it) }
    }
    EditButton(
        onClick = onSendClick,
        text = stringResource(id = R.string.forgot_password_send),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.padding(top = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.forgot_password_remembered),
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(id = R.color.text_secondary)
        )
        EditTextButton(
            onClick = onSignInClick,
            text = stringResource(id = R.string.sign_in_title)
        )
    }
}

@Composable
private fun CodeSection(
    codeDigits: List<String>,
    codeError: String?,
    focusRequesters: List<FocusRequester>,
    onCodeDigitChange: (Int, String) -> Unit,
    onConfirmClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Text(
        text = stringResource(id = R.string.forgot_password_check_email),
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        textAlign = TextAlign.Center,
        modifier = modifier.padding(bottom = 24.dp)
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        codeDigits.forEachIndexed { index, digit ->
            TextField(
                value = digit,
                onValueChange = { value ->
                    if (value.length <= 1 && value.all { it.isDigit() }) {
                        onCodeDigitChange(index, value)
                        if (value.isNotEmpty()) {
                            if (index < 5) {
                                focusRequesters[index + 1].requestFocus()
                            } else {
                                focusManager.clearFocus()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(46.dp)
                    .focusRequester(focusRequesters[index]),
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (index == 5) ImeAction.Done else ImeAction.Next
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(id = R.color.sign_in_google_button),
                    unfocusedContainerColor = colorResource(id = R.color.sign_in_google_button),
                    focusedIndicatorColor = colorResource(id = R.color.bottom_bar_fab),
                    unfocusedIndicatorColor = colorResource(id = R.color.bottom_bar_background)
                )
            )
        }
    }
    codeError?.let { ValidationErrorText(error = it, modifier = modifier.fillMaxWidth()) }
    EditButton(
        onClick = onConfirmClick,
        text = stringResource(id = R.string.forgot_password_confirm),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
    )
    EditTextButton(
        onClick = onBackClick,
        text = stringResource(id = R.string.forgot_password_go_back),
        modifier = modifier.padding(top = 8.dp)
    )
}

@Composable
private fun NewPasswordSection(
    newPassword: String,
    newPasswordError: String?,
    onNewPasswordChange: (String) -> Unit,
    newPasswordVisible: Boolean,
    onNewPasswordVisibleChange: () -> Unit,
    newPasswordRepeat: String,
    confirmPasswordError: String?,
    onNewPasswordRepeatChange: (String) -> Unit,
    newPasswordRepeatVisible: Boolean,
    onNewPasswordRepeatVisibleChange: () -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Text(
        text = stringResource(id = R.string.forgot_password_new_password),
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        textAlign = TextAlign.Center,
        modifier = modifier.padding(bottom = 8.dp)
    )
    Column(modifier = modifier.fillMaxWidth()) {
        EditOutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            label = {
                Text(text = stringResource(id = R.string.forgot_password_new_password_label))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = colorResource(id = R.color.icon_orange)
                )
            },
            trailingIcon = {
                EditIconButton(
                    onClick = onNewPasswordVisibleChange,
                    imageVector = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                )
            },
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )
        newPasswordError?.let { ValidationErrorText(error = it) }
    }
    Column(modifier = modifier.fillMaxWidth()) {
        EditOutlinedTextField(
            value = newPasswordRepeat,
            onValueChange = onNewPasswordRepeatChange,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            label = {
                Text(text = stringResource(id = R.string.forgot_password_new_password_repeat))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = colorResource(id = R.color.icon_orange)
                )
            },
            trailingIcon = {
                EditIconButton(
                    onClick = onNewPasswordRepeatVisibleChange,
                    imageVector = if (newPasswordRepeatVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                )
            },
            visualTransformation = if (newPasswordRepeatVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
        confirmPasswordError?.let { ValidationErrorText(error = it) }
    }
    EditButton(
        onClick = onSaveClick,
        text = stringResource(id = R.string.forgot_password_save),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    )
    EditTextButton(
        onClick = onBackClick,
        text = stringResource(id = R.string.forgot_password_go_back),
        modifier = modifier.padding(top = 8.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(navController = rememberNavController())
}