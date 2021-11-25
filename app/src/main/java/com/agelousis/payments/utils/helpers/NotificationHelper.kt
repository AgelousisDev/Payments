package com.agelousis.payments.utils.helpers

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.agelousis.payments.R
import com.agelousis.payments.notificationActivity.NotificationActivity
import com.agelousis.payments.utils.models.NotificationDataModel

object NotificationHelper {

    fun triggerNotification(context: Context, notificationDataModel: NotificationDataModel) {
        val channelId = context.resources.getString(R.string.key_payments_notifications_channel)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel = NotificationChannel(channelId, channelId, importance)
        notificationManager?.createNotificationChannel(notificationChannel)
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
        createBitmapFrom(
            context = context,
            fileName = notificationDataModel.groupImage
        )?.let {
            mBuilder.setLargeIcon(it)
        }
        mBuilder.color = ContextCompat.getColor(context, R.color.colorAccent)
        mBuilder.setDefaults(Notification.DEFAULT_ALL)
        mBuilder.setAutoCancel(true)
        mBuilder.setLights(-0x1450dd, 2000, 2000)
        val resultPendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, NotificationActivity::class.java).also {
                it.putExtra(NotificationActivity.BUBBLE_NOTIFICATION_EXTRA, notificationDataModel)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        return mBuilder
    }

    private fun createBitmapFrom(context: Context, fileName: String?) =
        fileName?.let { BitmapFactory.decodeFile("${context.filesDir.absolutePath}/$fileName") }

}