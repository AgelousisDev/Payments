package com.agelousis.payments.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.receivers.interfaces.NotificationListener

class NotificationDataReceiver: BroadcastReceiver() {

    var notificationListener: NotificationListener? = null

    override fun onReceive(p0: Context?, p1: Intent?) {
        notificationListener?.onNotificationReceived(
            firebaseNotificationData = p1?.extras?.getParcelable(MainActivity.FIREBASE_NOTIFICATION_DATA_EXTRA) ?: return
        )
    }

}