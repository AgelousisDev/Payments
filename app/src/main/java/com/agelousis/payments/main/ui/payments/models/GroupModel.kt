package com.agelousis.payments.main.ui.payments.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroupModel(
    val groupId: Int? = null,
    var color: Int?,
    var groupName: String?
): Parcelable