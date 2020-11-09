package com.agelousis.payments.utils.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class NotificationDataModel(val calendar: Calendar,
                                 val notificationId: Int,
                                 val title: String?,
                                 val body: String?
): Parcelable