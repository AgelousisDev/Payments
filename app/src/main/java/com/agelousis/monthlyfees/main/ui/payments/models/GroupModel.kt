package com.agelousis.monthlyfees.main.ui.payments.models

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroupModel(
    val groupId: Int? = null,
    val color: Int?,
    val groupName: String?
): Parcelable