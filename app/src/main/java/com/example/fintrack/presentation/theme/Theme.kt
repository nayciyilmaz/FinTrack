package com.example.fintrack.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.core.view.WindowCompat
import com.example.fintrack.R

@Composable
private fun fintrackColorScheme(darkTheme: Boolean) = if (darkTheme) {
    darkColorScheme(
        primary = colorResource(id = R.color.bottom_bar_fab),
        onPrimary = Color.White,
        secondary = colorResource(id = R.color.icon_orange),
        onSecondary = Color.White,
        tertiary = colorResource(id = R.color.income_green),
        onTertiary = Color.White,
        background = colorResource(id = R.color.background_color),
        onBackground = colorResource(id = R.color.text_primary),
        surface = colorResource(id = R.color.card_background),
        onSurface = colorResource(id = R.color.text_primary),
        surfaceVariant = colorResource(id = R.color.surface_gray),
        onSurfaceVariant = colorResource(id = R.color.text_secondary),
        error = colorResource(id = R.color.expense_red),
        onError = Color.White,
        outline = colorResource(id = R.color.divider_color)
    )
} else {
    lightColorScheme(
        primary = colorResource(id = R.color.bottom_bar_fab),
        onPrimary = Color.White,
        secondary = colorResource(id = R.color.icon_orange),
        onSecondary = Color.White,
        tertiary = colorResource(id = R.color.income_green),
        onTertiary = Color.White,
        background = colorResource(id = R.color.background_color),
        onBackground = colorResource(id = R.color.text_primary),
        surface = colorResource(id = R.color.card_background),
        onSurface = colorResource(id = R.color.text_primary),
        surfaceVariant = colorResource(id = R.color.surface_gray),
        onSurfaceVariant = colorResource(id = R.color.text_secondary),
        error = colorResource(id = R.color.expense_red),
        onError = Color.White,
        outline = colorResource(id = R.color.divider_color)
    )
}

@Composable
fun FinTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = fintrackColorScheme(darkTheme)
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
