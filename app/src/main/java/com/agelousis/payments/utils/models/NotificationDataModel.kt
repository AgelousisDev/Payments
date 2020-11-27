package com.agelousis.payments.utils.models

import android.content.Context
import android.os.Parcelable
import com.agelousis.payments.R
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class NotificationDataModel(val calendar: Calendar,
                                 val notificationId: Int,
                                 val title: String?,
                                 val body: String?,
                                 val date: String?,
                                 val groupName: String?,
                                 val groupImage: String?,
                                 val groupTint: Int?
): Parcelable {

    fun getFormattedDate(context: Context) =
        String.format(
            "%s - %s",
            context.resources.getString(R.string.key_execution_date_label),
            date
        )

}