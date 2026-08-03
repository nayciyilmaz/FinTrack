package com.example.fintrack.core.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

object CurrencyHelper {

    private const val SELECTED_CURRENCY = "selected_currency"
    private const val DEFAULT_CURRENCY = "TRY"

    fun getCurrency(context: Context): String {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY
    }

    fun saveCurrency(context: Context, currencyCode: String) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString(SELECTED_CURRENCY, currencyCode).apply()
    }

    fun getSymbol(context: Context): String {
        return when (getCurrency(context)) {
            "EUR" -> "€"
            "USD" -> "$"
            else -> "₺"
        }
    }
}

@Composable
fun currencySymbol(): String {
    return CurrencyHelper.getSymbol(LocalContext.current)
}
