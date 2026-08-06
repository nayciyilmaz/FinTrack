package com.example.fintrack.core.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "selected_language"
    private const val DEFAULT_LANGUAGE = "tr"

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)

        return context.createConfigurationContext(configuration)
    }

    fun getLanguage(context: Context): String {
        return AppPreferences.getString(context, SELECTED_LANGUAGE, DEFAULT_LANGUAGE)
    }

    fun saveLanguage(context: Context, languageCode: String) {
        AppPreferences.saveString(context, SELECTED_LANGUAGE, languageCode)
    }

    fun getLocale(context: Context): Locale {
        return Locale(getLanguage(context))
    }
}
