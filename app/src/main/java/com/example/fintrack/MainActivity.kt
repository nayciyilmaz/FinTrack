package com.example.fintrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.fintrack.core.util.LocaleHelper
import com.example.fintrack.presentation.navigation.FinTrackNavigation
import com.example.fintrack.ui.theme.FinTrackTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var openReminders by mutableStateOf(false)

    override fun attachBaseContext(newBase: Context) {
        val languageCode = LocaleHelper.getLanguage(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, languageCode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        openReminders = intent.getBooleanExtra(EXTRA_OPEN_REMINDERS, false)
        setContent {
            FinTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FinTrackNavigation(
                        openReminders = openReminders,
                        onRemindersConsumed = { openReminders = false }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openReminders = intent.getBooleanExtra(EXTRA_OPEN_REMINDERS, false)
    }

    companion object {
        const val EXTRA_OPEN_REMINDERS = "open_reminders"
    }
}