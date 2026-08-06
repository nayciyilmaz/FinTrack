package com.example.fintrack.core.util

import android.content.Context
import android.content.res.Configuration

object ThemeHelper {

    private const val SELECTED_THEME = "selected_theme"
    private const val DEFAULT_THEME = "light"

    fun setTheme(context: Context, theme: String): Context {
        val configuration = Configuration(context.resources.configuration)
        val nightModeFlag = if (theme == "dark") Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
        configuration.uiMode = (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or nightModeFlag

        return context.createConfigurationContext(configuration)
    }

    fun getTheme(context: Context): String {
        return AppPreferences.getString(context, SELECTED_THEME, DEFAULT_THEME)
    }

    fun saveTheme(context: Context, theme: String) {
        AppPreferences.saveString(context, SELECTED_THEME, theme)
    }
}
