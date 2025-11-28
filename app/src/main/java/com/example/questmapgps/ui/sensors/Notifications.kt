package com.example.questmapgps.ui.screens.main_content

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.questmapgps.MainActivity
import com.example.questmapgps.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "proximity_notification_channel"
        const val ACTION_SHOW_POINT = "com.example.questmapgps.ACTION_SHOW_POINT"
        const val ACTION_NAVIGATE_TO_ABOUT = "com.example.questmapgps.ACTION_NAVIGATE_TO_ABOUT"
        const val EXTRA_POINT_ID = "com.example.questmapgps.EXTRA_POINT_ID"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Powiadomienia gry"
            val descriptionText = "Powiadomienia o punktach i strefach"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showProximityNotification(pointName: String, pointId: Int) {
        if (!hasPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_SHOW_POINT
            putExtra(EXTRA_POINT_ID, pointId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            pointId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Upewnij się, że masz tę ikonę
            .setContentTitle("Jesteś blisko punktu!")
            .setContentText(pointName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notify(1000 + pointId, builder)
    }

    fun showDebugWelcomeNotification() {
        if (!hasPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_NAVIGATE_TO_ABOUT
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            9999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Witaj w QuestMapGPS")
            .setContentText("Kliknij, aby zobaczyć informacje.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notify(9999, builder)
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun notify(id: Int, builder: NotificationCompat.Builder) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(id, builder.build())
        }
    }
}