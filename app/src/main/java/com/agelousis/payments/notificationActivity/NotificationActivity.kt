package com.agelousis.payments.notificationActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.agelousis.payments.databinding.ActivityNotificationBinding
import com.agelousis.payments.utils.models.NotificationDataModel

class NotificationActivity : AppCompatActivity() {

    companion object {
        const val BUBBLE_NOTIFICATION_EXTRA = "BubbleNotificationActivity=bubbleNotificationExtra"
    }

    private val notificationDataModelExtra by lazy {
        intent?.extras?.getParcelable<NotificationDataModel?>(BUBBLE_NOTIFICATION_EXTRA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityNotificationBinding.inflate(
                layoutInflater
            ).also {
                it.notificationDataModel = notificationDataModelExtra
            }.root
        )
    }
}