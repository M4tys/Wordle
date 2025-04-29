package com.example.wordle.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.wordle.MainActivity
import com.example.wordle.R

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        showNotification()
        NotificationScheduler.scheduleDailyNotification(applicationContext)
        return Result.success()
    }

    private fun showNotification() {
        val channelId = "wordle_reminder_channel"
        val notificationId = 1

        // Utwórz Intent, który otworzy MainActivity po kliknięciu w powiadomienie
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Utwórz PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Stwórz kanał powiadomień (wymagane od Androida 8.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Wordle Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily Wordle game reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Zbuduj powiadomienie z PendingIntent
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Czas na Wordle!")
            .setContentText("Odgadnij słowa!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Ustaw PendingIntent
            .setAutoCancel(true) // Powiadomienie znika po kliknięciu
            .build()

        notificationManager.notify(notificationId, notification)
    }
}