package com.agelousis.payments.main.ui.qrCode.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.firebase.models.FirebaseMessageModel
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.network.repositories.FirebaseMessageRepository
import com.agelousis.payments.network.responses.ErrorModel
import com.agelousis.payments.network.responses.FirebaseResponseModel

class QRCodeViewModel: ViewModel() {

    var selectedClientModelList: List<ClientModel>? = null
    val firebaseResponseLiveData by lazy { MutableLiveData<FirebaseResponseModel>() }
    val firebaseErrorLiveData by lazy { MutableLiveData<ErrorModel>() }

    fun sendClientDataRequestNotification(firebaseMessageModel: FirebaseMessageModel) {
        FirebaseMessageRepository.sendFirebaseMessage(
            firebaseMessageModel = firebaseMessageModel,
            onSuccess = {
                firebaseResponseLiveData.value = it
            },
            onFail = {
                firebaseErrorLiveData.value = it
            }
        )
    }

}