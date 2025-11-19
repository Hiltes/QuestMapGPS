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
import com.example.questmapgps.ui.navigation.Routes // NOWY IMPORT

class NotificationHelper(private val context: Context) {

    companion object {
        const val ACTION_SHOW_POINT = "com.example.questmapgps.ACTION_SHOW_POINT"
        const val EXTRA_POINT_ID = "com.example.questmapgps.EXTRA_POINT_ID"
        // NOWA AKCJA
        const val ACTION_NAVIGATE_TO_ABOUT = "com.example.questmapgps.ACTION_NAVIGATE_TO_ABOUT"
    }

    private val CHANNEL_ID = "proximity_notification_channel"
    private val NOTIFICATION_ID_PREFIX = 1000

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Powiadomienia o bliskości"
            val descriptionText = "Powiadomienia, gdy zbliżysz się do punktu na mapie"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showProximityNotification(pointName: String, pointId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_SHOW_POINT
            putExtra(EXTRA_POINT_ID, pointId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, pointId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Zbliżasz się do punktu")
            .setContentText(pointName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_PREFIX + pointId, builder.build())
        }
    }

    // ZMIENIONA FUNKCJA DEBUGUJĄCA
    fun showDebugWelcomeNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // NOWE: Tworzymy intencję nawigacji do strony "O aplikacji"
        val intent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_NAVIGATE_TO_ABOUT
            // Wyczyść stos, aby upewnić się, że nawigacja działa poprawnie
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Unikalny requestCode dla tej akcji
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 9998, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("QuestMapGPS")
            .setContentText("Witamy! Kliknij, aby zobaczyć informacje o apce.") // Zmieniony tekst
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // NOWE: Przypisanie akcji po kliknięciu

        with(NotificationManagerCompat.from(context)) {
            notify(9999, builder.build())
        }
    }
}