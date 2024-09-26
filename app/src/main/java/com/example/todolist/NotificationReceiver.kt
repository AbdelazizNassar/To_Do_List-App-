package com.example.todolist

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent) {
        Log.d("NotificationReceiver", "Alarm received!")

        // Retrieve the task name from the Intent's extras
        val taskName = intent.getStringExtra("task_name") ?: "You have a task to complete"
        val timeInMillis = intent.getLongExtra("time_in_millis", 0L)

        // Build the notification
        val builder = NotificationCompat.Builder(context!!, "1")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Task Reminder")
            .setContentText("Don't forget to complete your task: $taskName")  // Use the task name in the notification
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("You have a task to complete: $taskName.")
            )
            .setAutoCancel(true)

        // Add action to open the app when the notification is clicked
        val notificationIntent = Intent(context, MainActivity::class.java) // Replace with your main activity
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        builder.setContentIntent(pendingIntent)

        // Trigger the notification
        NotificationManagerCompat.from(context).notify(timeInMillis.toInt(), builder.build())
    }
}

