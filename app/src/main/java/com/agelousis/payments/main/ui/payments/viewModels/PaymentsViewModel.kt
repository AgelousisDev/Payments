package com.agelousis.payments.main.ui.payments.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.firebase.models.FirebaseMessageModel
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.ui.files.models.FileDataModel
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.network.repositories.FirebaseMessageRepository
import com.agelousis.payments.network.responses.ErrorModel
import com.agelousis.payments.utils.extensions.pdfFormattedCurrentDate
import java.io.File
import java.util.*

class PaymentsViewModel: ViewModel() {

    val paymentsLiveData by lazy { MutableLiveData<List<Any>>() }
    val deletionLiveData by lazy { MutableLiveData<Boolean>() }
    val firebaseResponseLiveData by lazy { MutableLiveData<String?>() }
    val firebaseErrorLiveData by lazy { MutableLiveData<ErrorModel>() }

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
        (item as? GroupModel)?.let {
            dbManager.deleteGroup(
                groupId = it.groupId
            ) {
                deletionLiveData.value = true
            }
        }
        (item as? ClientModel)?.let {
            dbManager.deletePayment(
                personIds = listOf(
                    it.personId ?: return@let
                )
            ) {
                deletionLiveData.value = true
            }
        }
    }

    suspend fun deletePayments(context: Context, personIds: List<Int>) {
        val dbManager = DBManager(context = context)
        dbManager.deletePayment(
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