package com.agelousis.payments.notificationActivity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.agelousis.payments.databinding.ActivityNotificationBinding
import com.agelousis.payments.utils.models.NotificationDataModel

class NotificationActivity : AppCompatActivity() {

    companion object {
        const val BUBBLE_NOTIFICATION_EXTRA = "BubbleNotificationActivity=bubbleNotificationExtra"
    }

    private val notificationDataModelExtra by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent?.extras?.getParcelable(BUBBLE_NOTIFICATION_EXTRA, NotificationDataModel::class.java)
        else
            intent?.extras?.getParcelable(BUBBLE_NOTIFICATION_EXTRA)
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