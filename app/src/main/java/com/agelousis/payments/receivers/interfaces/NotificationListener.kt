package com.agelousis.payments.receivers.interfaces

import com.agelousis.payments.firebase.models.FirebaseNotificationData

interface NotificationListener {
    fun onNotificationReceived(firebaseNotificationData: FirebaseNotificationData)
}