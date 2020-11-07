package com.agelousis.payments.utils.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationDataModel(val notificationId: Int, val title: String?, val body: String?): Parcelable