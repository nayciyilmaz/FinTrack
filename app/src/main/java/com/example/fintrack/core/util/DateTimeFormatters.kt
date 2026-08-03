package com.example.fintrack.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.time.format.DateTimeFormatter

val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun dateFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("dd MMMM yyyy", LocaleHelper.getLocale(LocalContext.current))
}

@Composable
fun monthFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("MMMM yyyy", LocaleHelper.getLocale(LocalContext.current))
}