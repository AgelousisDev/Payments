package com.agelousis.payments.main.ui.payments.viewModels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.firebase.models.FirebaseMessageModel
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.ui.files.models.FileDataModel
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.network.repositories.FirebaseMessageRepository
import com.agelousis.payments.network.responses.ErrorModel
import com.agelousis.payments.network.responses.FirebaseResponseModel
import com.agelousis.payments.utils.extensions.pdfFormattedCurrentDate
import java.io.File
import java.util.*

class PaymentsViewModel: ViewModel() {

    val paymentsLiveData by lazy { MutableLiveData<List<Any>>() }
    val deletionLiveData by lazy { MutableLiveData<Boolean>() }
    val firebaseResponseLiveData by lazy { MutableLiveData<FirebaseResponseModel>() }
    val firebaseErrorLiveData by lazy { MutableLiveData<ErrorModel>() }
    val paymentsMutableStateList by lazy { mutableStateOf<List<Any>>(listOf()) }

    suspend fun initializePayments(context: Context, userModel: UserModel?) {
        val dbManager = DBManager(
            context = context
        )
        dbManager.initializePayments(
            userId = userModel?.id
        ) {
            paymentsLiveData.value = it
        }
        dbManager.close()
    }

    suspend fun initializePayments(context: Context, userModel: UserModel?, groupModel: GroupModel, completionBlock: (List<ClientModel>) -> Unit) {
        val dbManager = DBManager(
            context = context
        )
        dbManager.initializePayments(
            userId = userModel?.id,
            groupId = groupModel.groupId,
            personsClosure = completionBlock
        )
        dbManager.close()
    }

    suspend fun deleteItem(context: Context, item: Any?) {
        val dbManager = DBManager(context = context)
        when(item) {
            is GroupModel ->
                dbManager.deleteGroup(
                    groupId = item.groupId
                ) {
                    deletionLiveData.value = true
                }
            is ClientModel ->
                dbManager.deleteClients(
                    personIds = listOf(
                        item.personId ?: return
                    )
                ) {
                    deletionLiveData.value = true
                }
            is PaymentAmountModel ->
                dbManager.deletePayments(
                    paymentIds = listOf(
                        item.paymentId ?: return
                    )
                ) {
                    deletionLiveData.value = true
                }
        }
    }

    suspend fun deletePayments(context: Context, personIds: List<Int>) {
        val dbManager = DBManager(context = context)
        dbManager.deleteClients(
            personIds = personIds
        ) {
            deletionLiveData.value = true
        }
    }

    suspend fun insertFile(context: Context, userModel: UserModel?, file: File, description: String) {
        val dbManager = DBManager(context = context)
        dbManager.insertFile(
            userId = userModel?.id,
            fileDataModel = FileDataModel(
                description = description,
                fileName = file.name,
                dateTime = Date().pdfFormattedCurrentDate
            ).also {
                it.fileData = file.readBytes()
            }
        )
    }

    suspend fun updateUserBalance(context: Context, userModel: UserModel?, balance: Double) {
        val dbManager = DBManager(context = context)
        dbManager.updateUserBalance(
            userId = userModel?.id,
            balance = balance
        )
    }

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