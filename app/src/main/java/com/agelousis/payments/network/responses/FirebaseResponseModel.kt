package com.agelousis.payments.network.responses

import com.google.gson.annotations.SerializedName

data class FirebaseResponseModel(
    @SerializedName(value = "multicast_id") val multicastId: Long?,
    @SerializedName(value = "success") val success: Int?,
    @SerializedName(value = "failure") val failure: Int?,
    @SerializedName(value = "canonical_ids") val canonicalIds: Int?,
    @SerializedName(value = "results") val results: List<FirebaseResultModel>?
) {
    val isSuccessful
        get() = success == 1
}

data class FirebaseResultModel(
    @SerializedName(value = "message_id") val messageId: String?,
    @SerializedName(value = "error") val error: String?
)