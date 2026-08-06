package com.example.fintrack.core.util

import android.content.Context

object AppPreferences {

    private const val PREFS_NAME = "app_prefs"

    fun getString(context: Context, key: String, default: String): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(key, default) ?: default
    }

    fun saveString(context: Context, key: String, value: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(key, value).apply()
    }
}
