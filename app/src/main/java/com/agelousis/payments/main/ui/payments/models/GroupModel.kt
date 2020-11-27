package com.agelousis.payments.main.ui.payments.models

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class GroupModel(val groupId: Int? = null,
                      var color: Int? = null,
                      var groupName: String? = null,
                      var groupImage: String? = null
): Parcelable {

    @IgnoredOnParcel var groupImageData: ByteArray? = null

}