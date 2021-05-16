package com.agelousis.payments.network.apis

import com.agelousis.payments.firebase.models.FirebaseMessageModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FirebaseMessageAPI {

    @Headers(
        "Authorization: key=AAAAJIQ98AQ:APA91bGyxdb00HxBzBOZTz_-gySrXEx9bpJcslCpy2yRXXcF2g2IatJ_2uk-CaRfmdSkEkeekEZ9TbZ5rmw_5acy5Rz80dHplvAkQ6UfjMyNJS7QBei4sy7GZBWxpymjhpZSQK_3eUaQ",
        "Content-Type:application/json"
    )
    @POST(value = "send")
    fun sendFirebaseMessage(
        @Body firebaseMessageModel: FirebaseMessageModel
    ): Call<String?>

}