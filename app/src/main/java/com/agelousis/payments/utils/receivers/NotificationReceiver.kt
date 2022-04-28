package com.agelousis.payments.utils.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.agelousis.payments.utils.helpers.NotificationHelper
import com.agelousis.payments.utils.models.NotificationDataModel

class NotificationReceiver: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_DATA_MODEL_EXTRA = "NotificationReceiver=notificationDataModelExtra"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_POWER_CONNECTED,
            Intent.ACTION_POWER_DISCONNECTED ->
                intent.extras?.getBundle("bundle")?.apply {
                    val notificationDataModel = getParcelable<NotificationDataModel>(NOTIFICATION_DATA_MODEL_EXTRA) ?: return
                    NotificationHelper.triggerNotification(
                        context = context ?: return,
                        notificationDataModel = notificationDataModel
                    )
                }
        }
    }

}