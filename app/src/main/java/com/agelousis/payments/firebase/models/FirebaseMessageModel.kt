package com.agelousis.payments.firebase.models

import android.os.Parcelable
import com.agelousis.payments.main.ui.payments.models.PersonModel
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class FirebaseMessageModel(
    @SerializedName(value = "to") val firebaseToken: String,
    @SerializedName(value = "data") val firebaseNotificationData: FirebaseNotificationData
)

@Parcelize
data class FirebaseNotificationData(@SerializedName(value = "clients") val personModelList: List< PersonModel>): Parcelable