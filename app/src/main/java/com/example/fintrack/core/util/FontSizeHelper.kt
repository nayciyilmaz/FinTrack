package com.example.fintrack.core.util

import android.content.Context

object FontSizeHelper {

    private const val SELECTED_FONT_SIZE = "selected_font_size"
    private const val DEFAULT_FONT_SIZE = "medium"

    fun getFontSize(context: Context): String {
        return AppPreferences.getString(context, SELECTED_FONT_SIZE, DEFAULT_FONT_SIZE)
    }

    fun saveFontSize(context: Context, fontSize: String) {
        AppPreferences.saveString(context, SELECTED_FONT_SIZE, fontSize)
    }

    fun getScale(context: Context): Float {
        return when (getFontSize(context)) {
            "small" -> 0.85f
            "large" -> 1.15f
            else -> 1.0f
        }
    }
}
