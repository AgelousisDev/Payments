package com.agelousis.payments.utils.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.agelousis.payments.R
import com.agelousis.payments.login.LoginActivity
import com.agelousis.payments.utils.models.NotificationDataModel

object NotificationHelper {

    fun triggerNotification(context: Context, notificationDataModel: NotificationDataModel) {
        val channelId = context.resources.getString(R.string.key_payments_notifications_channel)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelId, importance)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
        notificationManager?.notify(
            notificationDataModel.notificationId,
            createNotification(
                context = context,
                notificationDataModel = notificationDataModel
            ).build()
        )
    }

    private fun createNotification(context: Context, notificationDataModel: NotificationDataModel): NotificationCompat.Builder {
        val mBuilder = NotificationCompat.Builder(context, context.resources.getString(R.string.key_payments_notifications_channel))
        mBuilder.setContentTitle(notificationDataModel.title)
        mBuilder.setContentText(notificationDataModel.body)
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        mBuilder.setSmallIcon(R.drawable.ic_payment)
        mBuilder.color = ContextCompat.getColor(context, R.color.colorAccent)
        mBuilder.setDefaults(Notification.DEFAULT_ALL)
        mBuilder.setAutoCancel(true)
        mBuilder.setLights(-0x1450dd, 2000, 2000)
        val intent = Intent(context, LoginActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(resultPendingIntent)
        return mBuilder
    }

}