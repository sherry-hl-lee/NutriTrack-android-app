package com.example.ass2.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.ass2.MainActivity
import com.example.ass2.data.local.UserReminder
import com.example.ass2.util.ReminderTimeUtils

object ReminderScheduler {

    private const val REQUEST_CODE_SHOW = 1002

    fun schedule(context: Context, reminder: UserReminder) {
        if (!reminder.enabled || reminder.id == 0L) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAt = ReminderTimeUtils.triggerTimeMillis(reminder.hour, reminder.minute)
        val alarmIntent = alarmPendingIntent(context, reminder.id)
        val showIntent = showPendingIntent(context, reminder.id)

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerAt, showIntent),
            alarmIntent
        )
    }

    fun cancel(context: Context, reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmPendingIntent(context, reminderId))
    }

    fun rescheduleAll(context: Context, reminders: List<UserReminder>) {
        reminders.forEach { cancel(context, it.id) }
        reminders.filter { it.enabled }.forEach { schedule(context, it) }
    }

    private fun alarmPendingIntent(context: Context, reminderId: Long): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminderId)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCodeFor(reminderId),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun showPendingIntent(context: Context, reminderId: Long): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminderId)
        }
        return PendingIntent.getActivity(
            context,
            REQUEST_CODE_SHOW + requestCodeFor(reminderId),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun requestCodeFor(reminderId: Long): Int =
        (reminderId and 0x7FFFFFFF).toInt()
}
