package com.example.fintrack.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.domain.usecase.GetReminderTransactionsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltWorker
class ReminderCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getReminderTransactionsUseCase: GetReminderTransactionsUseCase,
    private val tokenManager: TokenManager,
    private val scheduler: ReminderNotificationScheduler
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val workName = inputData.getString(ReminderNotificationScheduler.KEY_WORK_NAME)
            ?: return Result.failure()
        val targetHour = inputData.getInt(
            ReminderNotificationScheduler.KEY_TARGET_HOUR,
            ReminderNotificationScheduler.MORNING_HOUR
        )
        val targetMinute = inputData.getInt(
            ReminderNotificationScheduler.KEY_TARGET_MINUTE,
            ReminderNotificationScheduler.MORNING_MINUTE
        )

        val token = tokenManager.getToken().firstOrNull()
        if (token == null) {
            scheduler.rescheduleNextDay(workName, targetHour, targetMinute)
            return Result.success()
        }

        return when (val result = getReminderTransactionsUseCase()) {
            is Resource.Success -> {
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val todayCount = result.data.orEmpty().count { it.date == today }
                if (todayCount > 0) {
                    ReminderNotificationHelper.showReminderSummary(applicationContext, todayCount)
                }
                scheduler.rescheduleNextDay(workName, targetHour, targetMinute)
                Result.success()
            }
            is Resource.Error -> {
                if (runAttemptCount < MAX_ATTEMPTS) {
                    Result.retry()
                } else {
                    scheduler.rescheduleNextDay(workName, targetHour, targetMinute)
                    Result.success()
                }
            }
            is Resource.Loading -> Result.success()
        }
    }

    companion object {
        private const val MAX_ATTEMPTS = 3
    }
}
