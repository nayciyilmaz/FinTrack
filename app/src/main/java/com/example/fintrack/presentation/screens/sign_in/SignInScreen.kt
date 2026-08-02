package com.example.fintrack.presentation.screens.sign_in

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun SignInScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    LaunchedEffect(actionState.isSuccess) {
        if (actionState.isSuccess) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            navigateAndClearBackStack(
                navController = navController,
                destination = FinTrackScreens.HomeScreen.route,
                popUpToRoute = FinTrackScreens.SignInScreen.route,
                inclusive = true
            )
        }
    }

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
                text = stringResource(id = R.string.sign_in_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = colorResource(id = R.color.sign_in_title),
                modifier = modifier.padding(bottom = 20.dp)
            )
            Column(modifier = modifier.fillMaxWidth()) {
                EditOutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
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
                        imeAction = ImeAction.Next
                    )
                )
                uiState.validationErrors.emailError?.let { ValidationErrorText(error = it) }
            }
            Column(modifier = modifier.fillMaxWidth()) {
                EditOutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    modifier = modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(id = R.string.sign_in_password))
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
                            onClick = viewModel::togglePasswordVisibility,
                            imageVector = if (uiState.isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        )
                    },
                    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )
                uiState.validationErrors.passwordError?.let { ValidationErrorText(error = it) }
            }
            EditTextButton(
                onClick = { navController.navigate(FinTrackScreens.ForgotPasswordScreen.route) },
                text = stringResource(id = R.string.sign_in_forgot_password),
                modifier = modifier
                    .align(Alignment.End)
                    .padding(vertical = 4.dp),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            EditButton(
                onClick = viewModel::login,
                text = stringResource(id = R.string.sign_in_title),
                modifier = modifier.fillMaxWidth()
            )
            OutlinedButton(
                onClick = { viewModel.launchGoogleSignIn(context) },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = colorResource(id = R.color.sign_in_google_button)),
                border = BorderStroke(1.dp, colorResource(id = R.color.bottom_bar_background))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_g_2015),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(id = R.string.sign_in_google_button),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = colorResource(id = R.color.text_on_surface),
                    modifier = Modifier.padding(8.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.sign_in_no_account),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.text_secondary)
                )
                EditTextButton(
                    onClick = { navController.navigate(FinTrackScreens.SignUpScreen.route) },
                    text = stringResource(id = R.string.sign_in_register)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(navController = rememberNavController())
}
