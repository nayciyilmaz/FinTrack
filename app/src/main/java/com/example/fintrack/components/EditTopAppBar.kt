package com.example.fintrack.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTopAppBar(
    title: String,
    navController: NavController,
    showBackButton: Boolean = false
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackButton) {
                EditIconButton(
                    onClick = { navController.popBackStack() },
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack
                )
            }
        }
    )
}