package com.example.fintrack.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun schedule() {
        Timber.d("Scheduling reminder checks")
        enqueueFirst(MORNING_WORK_NAME, MORNING_HOUR, MORNING_MINUTE)
        enqueueFirst(AFTERNOON_WORK_NAME, AFTERNOON_HOUR, AFTERNOON_MINUTE)
    }

    fun cancel() {
        Timber.d("Cancelling reminder checks")
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(MORNING_WORK_NAME)
        workManager.cancelUniqueWork(AFTERNOON_WORK_NAME)
    }

    fun rescheduleNextDay(workName: String, targetHour: Int, targetMinute: Int) {
        Timber.d("Rescheduling reminder check for next day: workName=%s", workName)
        WorkManager.getInstance(context).enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            buildRequest(workName, targetHour, targetMinute, delayFor(targetHour, targetMinute, alreadyPassedToday = true))
        )
    }

    private fun enqueueFirst(workName: String, targetHour: Int, targetMinute: Int) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.KEEP,
            buildRequest(workName, targetHour, targetMinute, delayFor(targetHour, targetMinute, alreadyPassedToday = false))
        )
    }

    private fun buildRequest(workName: String, targetHour: Int, targetMinute: Int, delay: Duration) =
        OneTimeWorkRequestBuilder<ReminderCheckWorker>()
            .setInitialDelay(delay)
            .setInputData(
                workDataOf(
                    KEY_WORK_NAME to workName,
                    KEY_TARGET_HOUR to targetHour,
                    KEY_TARGET_MINUTE to targetMinute
                )
            )
            .build()

    private fun delayFor(targetHour: Int, targetMinute: Int, alreadyPassedToday: Boolean): Duration {
        val now = LocalDateTime.now()
        var target = now.toLocalDate().atTime(LocalTime.of(targetHour, targetMinute))
        if (alreadyPassedToday || !target.isAfter(now)) {
            target = target.plusDays(1)
        }
        return Duration.between(now, target)
    }

    companion object {
        const val KEY_WORK_NAME = "work_name"
        const val KEY_TARGET_HOUR = "target_hour"
        const val KEY_TARGET_MINUTE = "target_minute"
        const val MORNING_WORK_NAME = "reminder_check_morning"
        const val AFTERNOON_WORK_NAME = "reminder_check_afternoon"
        const val MORNING_HOUR = 10
        const val MORNING_MINUTE = 0
        const val AFTERNOON_HOUR = 14
        const val AFTERNOON_MINUTE = 0
    }
}
