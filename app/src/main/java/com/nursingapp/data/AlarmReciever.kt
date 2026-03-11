package com.nursingapp.data

import android.os.Build
import androidx.annotation.RequiresApi

class AlarmReceiver : android.content.BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: android.content.Context, intent: android.content.Intent) {
        val activityName = intent.getStringExtra("activity_name") ?: "Activity Reminder"
        val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val channelId = "nursing_reminders"

        // Create Channel (Required for Android 8.0+)
        val channel = android.app.NotificationChannel(channelId, "Reminders",
                        android.app.NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val notification = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Scheduled Activity")
            .setContentText("Time for: $activityName")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}