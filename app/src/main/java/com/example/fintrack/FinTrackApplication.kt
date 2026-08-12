package com.example.fintrack

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.fintrack.core.util.LocaleHelper
import com.example.fintrack.core.util.ThemeHelper
import com.example.fintrack.notification.ReminderNotificationHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class FinTrackApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun attachBaseContext(base: Context) {
        val languageCode = LocaleHelper.getLanguage(base)
        val localizedContext = LocaleHelper.setLocale(base, languageCode)
        val theme = ThemeHelper.getTheme(localizedContext)
        super.attachBaseContext(ThemeHelper.setTheme(localizedContext, theme))
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        ReminderNotificationHelper.createChannel(this)
    }
}
