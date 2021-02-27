package com.navigatorsguide.app.messaging

import android.R.id.message
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.navigatorsguide.app.MainActivity
import com.navigatorsguide.app.R
import com.navigatorsguide.app.database.AppDatabase


class AppMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.notification != null) {
            val notificationRepository: NotificationRepository =
                NotificationRepository.getInstance()
            notificationRepository.init(this.application)
            val notificationModel =
                NotificationModel("Section", "Insert", "123", "Check It")
            notificationRepository.processNotification(notificationModel)
        }
    }
}