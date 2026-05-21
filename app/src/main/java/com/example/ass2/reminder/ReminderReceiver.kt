package com.example.ass2.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.ass2.MainActivity
import com.example.ass2.data.local.AppDatabase
import com.example.ass2.data.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val reminderId = intent?.getLongExtra(EXTRA_REMINDER_ID, -1L) ?: -1L
        if (reminderId <= 0L) return

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                handleReminder(context.applicationContext, reminderId)
            } finally {
                pending.finish()
            }
        }
    }

    private suspend fun handleReminder(context: Context, reminderId: Long) {
        val db = AppDatabase.getDatabase(context)
        val repo = ReminderRepository(db.userReminderDao())
        val reminder = repo.getById(reminderId) ?: return
        if (!reminder.enabled) return

        val type = ReminderType.fromId(reminder.type) ?: return
        val prefs = ReminderPreferences(context)
        prefs.setPendingAlert(
            userEmail = reminder.userEmail,
            title = type.dialogTitle,
            body = type.dialogBody
        )

        createChannel(context)
        showNotification(
            context = context,
            reminderId = reminderId,
            title = type.notificationTitle,
            body = type.notificationBody
        )

        ReminderScheduler.schedule(context, reminder)
    }

    private fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "NutriTrack reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Meal and hydration reminders."
            enableVibration(true)
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    private fun showNotification(
        context: Context,
        reminderId: Long,
        title: String,
        body: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_REMINDER_ID, reminderId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(notificationIdFor(reminderId), notification)
        } catch (_: SecurityException) {
        }
    }

    companion object {
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val CHANNEL_ID = "nutritrack_reminders"
        private fun notificationIdFor(reminderId: Long): Int = (2000 + reminderId).toInt()
    }
}
