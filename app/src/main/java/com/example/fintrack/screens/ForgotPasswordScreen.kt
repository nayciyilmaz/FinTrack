package com.example.fintrack.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.components.EditButton
import com.example.fintrack.components.EditIconButton
import com.example.fintrack.components.EditOutlinedTextField
import com.example.fintrack.components.EditTextButton
import com.example.fintrack.components.WaveBackground
import com.example.fintrack.navigation.auth.AuthScreens
import com.example.fintrack.navigation.navigateAndClearBackStack

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var codeSent by rememberSaveable { mutableStateOf(false) }
    var passwordReset by rememberSaveable { mutableStateOf(false) }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var newPasswordRepeat by rememberSaveable { mutableStateOf("") }
    var newPasswordRepeatVisible by rememberSaveable { mutableStateOf(false) }
    val codeDigits = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Box(modifier = modifier.fillMaxSize()) {
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

            when {
                !codeSent -> EmailSection(
                    email = email,
                    onEmailChange = { email = it },
                    onSendClick = { codeSent = true },
                    onSignInClick = { navController.navigate(AuthScreens.SignInScreen.route) }
                )
                !passwordReset -> CodeSection(
                    codeDigits = codeDigits,
                    focusRequesters = focusRequesters,
                    onConfirmClick = { passwordReset = true },
                    onBackClick = { codeSent = false }
                )
                else -> NewPasswordSection(
                    newPassword = newPassword,
                    onNewPasswordChange = { newPassword = it },
                    newPasswordVisible = newPasswordVisible,
                    onNewPasswordVisibleChange = { newPasswordVisible = !newPasswordVisible },
                    newPasswordRepeat = newPasswordRepeat,
                    onNewPasswordRepeatChange = { newPasswordRepeat = it },
                    newPasswordRepeatVisible = newPasswordRepeatVisible,
                    onNewPasswordRepeatVisibleChange = { newPasswordRepeatVisible = !newPasswordRepeatVisible },
                    onSaveClick = {
                        navigateAndClearBackStack(
                            navController = navController,
                            destination = AuthScreens.SignInScreen.route,
                            popUpToRoute = AuthScreens.ForgotPasswordScreen.route,
                            inclusive = true
                        )
                    },
                    onBackClick = { passwordReset = false }
                )
            }
        }
    }
}

@Composable
private fun EmailSection(
    email: String,
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
    codeDigits: MutableList<String>,
    focusRequesters: List<FocusRequester>,
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
                        codeDigits[index] = value
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
    onNewPasswordChange: (String) -> Unit,
    newPasswordVisible: Boolean,
    onNewPasswordVisibleChange: () -> Unit,
    newPasswordRepeat: String,
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