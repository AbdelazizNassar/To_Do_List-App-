package com.example.todolist

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log


class TaskAlarm {
    fun requestExactAlarmPermission(
        context: Context,
        timeInMillis: Long,
        taskName: String,
        onSwitchChecked: (Boolean) -> Unit // Callback to handle switch state
    ) {
        Log.d("TaskAlarm", "Before requesting exact alarm permission")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Prompt the user to allow exact alarm scheduling
                RequestAlertDialog.showAlertDialog(
                    context,
                    context.getString(R.string.alarm),
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                ) {
                    onSwitchChecked(false) // Reset the switch state after dialog is dismissed
                }
            } else {
                // Permission is granted, set the alarm
                setAlarm(context, timeInMillis, taskName)
            }
        } else {
            // For lower versions, directly set the alarm
            setAlarm(context, timeInMillis, taskName)
        }
    }

     fun setAlarm(context: Context, timeInMillis: Long, taskName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("task_name", taskName)
            putExtra("time_in_millis", timeInMillis)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            timeInMillis.toInt(), // Consider using a unique request code if multiple alarms are set
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
            Log.d("TaskAlarm", "Alarm set for task: '$taskName' at $timeInMillis")
        } catch (e: SecurityException) {
            Log.e("TaskAlarm", "Permission denied: ${e.message}")
        }
    }
     fun cancelAlarm(context: Context, timeInMillis: Long,taskName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("task_name", taskName)
            putExtra("time_in_millis", timeInMillis)
        }
        // Create the same PendingIntent used to set the alarm
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            timeInMillis.toInt(), // Use the same request code to find the correct alarm
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        try {
            // Cancel the alarm
            alarmManager.cancel(pendingIntent)
            Log.d("TaskAlarm", "Alarm canceled at $timeInMillis")
        } catch (e: SecurityException) {
            Log.e("TaskAlarm", "Failed to cancel alarm: ${e.message}")
        }
    }

}
