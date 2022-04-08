package com.agelousis.payments.firebase

import android.content.Intent
import com.agelousis.payments.firebase.models.FirebaseNotificationData
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.utils.constants.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class FirebaseMessaging: FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Intent().also { intent ->
            intent.putExtra(
                MainActivity.FIREBASE_NOTIFICATION_DATA_EXTRA,
                Gson().fromJson(p0.data.toString(), FirebaseNotificationData::class.java)
            )
            intent.action = Constants.SHOW_NOTIFICATION_INTENT_ACTION
            sendBroadcast(intent)
        }
    }

}