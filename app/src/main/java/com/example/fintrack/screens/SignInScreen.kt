package com.example.fintrack.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.R
import com.example.fintrack.components.WaveBackground
import com.example.fintrack.features.auth.AuthScreens
import com.example.fintrack.features.main.MainScreens
import com.example.fintrack.util.navigateAndClearBackStack

@Composable
fun SignInScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

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

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
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
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = R.color.bottom_bar_fab),
                    focusedLabelColor = colorResource(id = R.color.bottom_bar_fab)
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
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
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = colorResource(id = R.color.icon_orange)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = R.color.bottom_bar_fab),
                    focusedLabelColor = colorResource(id = R.color.bottom_bar_fab)
                )
            )

            TextButton(
                onClick = { navController.navigate(AuthScreens.ForgotPasswordScreen.route) },
                modifier = modifier
                    .align(Alignment.End)
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.sign_in_forgot_password),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colorResource(id = R.color.bottom_bar_fab)
                )
            }

            Button(
                onClick = {
                    navigateAndClearBackStack(
                        navController = navController,
                        destination = MainScreens.HomeScreen.route,
                        popUpToRoute = AuthScreens.SignInScreen.route,
                        inclusive = true
                    )
                },
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.bottom_bar_fab))
            ) {
                Text(
                    text = stringResource(id = R.string.sign_in_title),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = modifier.padding(8.dp)
                )
            }

            OutlinedButton(
                onClick = {},
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
                    modifier = modifier.size(20.dp)
                )
                Text(
                    text = stringResource(id = R.string.sign_in_google_button),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = colorResource(id = R.color.text_on_surface),
                    modifier = modifier.padding(8.dp)
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
                TextButton(onClick = { navController.navigate(AuthScreens.SignUpScreen.route) }) {
                    Text(
                        text = stringResource(id = R.string.sign_in_register),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = colorResource(id = R.color.bottom_bar_fab)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(navController = rememberNavController())
}