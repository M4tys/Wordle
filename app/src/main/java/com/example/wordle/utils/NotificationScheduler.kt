package com.example.wordle.utils

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationScheduler {

    companion object {
        private const val NOTIFICATION_HOUR = 23
        private const val NOTIFICATION_MINUTE = 8
        private const val WORK_TAG = "daily_wordle_notification"

        fun scheduleDailyNotification(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR)
                set(Calendar.MINUTE, NOTIFICATION_MINUTE)
                set(Calendar.SECOND, 0)

                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val delay = calendar.timeInMillis - System.currentTimeMillis()

            val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(WORK_TAG)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    WORK_TAG,
                    ExistingWorkPolicy.REPLACE,
                    notificationWork
                )
        }
    }
}