package com.agelousis.monthlyfees.main.ui.payments.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroupModel(
    val groupId: Int? = null,
    val groupName: String?
): Parcelable