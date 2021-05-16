package com.agelousis.payments.network.repositories

import com.agelousis.payments.network.NetworkHelper
import com.agelousis.payments.firebase.models.FirebaseMessageModel
import com.agelousis.payments.network.apis.FirebaseMessageAPI
import com.agelousis.payments.network.responses.ErrorModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FirebaseMessageRepository {

    /**
     * @param firebaseMessageModel
     */
    fun sendFirebaseMessage(firebaseMessageModel: FirebaseMessageModel, onSuccess: (String) -> Unit, onFail: (ErrorModel) -> Unit) {
        NetworkHelper.create<FirebaseMessageAPI>()?.sendFirebaseMessage(
            firebaseMessageModel = firebaseMessageModel
        )?.enqueue(object:
            Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                onSuccess(response.body() ?: return)
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                onFail(
                    ErrorModel(localizedMessage = t.localizedMessage)
                )
            }
        })
    }

}