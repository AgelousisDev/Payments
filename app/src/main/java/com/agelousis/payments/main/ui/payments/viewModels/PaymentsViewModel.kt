package com.agelousis.payments.main.ui.payments.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.database.InsertionSuccessBlock
import com.agelousis.payments.database.UpdateSuccessBlock
import com.agelousis.payments.firebase.models.FirebaseMessageModel
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.ui.files.models.InvoiceDataModel
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

    suspend fun initializePayments(userModel: UserModel?) {
        DBManager.initializePayments(
            userId = userModel?.id
        ) {
            paymentsLiveData.value = it
        }
    }

    suspend fun initializePayments(userModel: UserModel?, groupModel: GroupModel, completionBlock: (List<ClientModel>) -> Unit) {
        DBManager.initializePayments(
            userId = userModel?.id,
            groupId = groupModel.groupId,
            personsClosure = completionBlock
        )
    }

    suspend fun deleteItem(item: Any?) {
        when(item) {
            is GroupModel ->
                DBManager.deleteGroup(
                    groupId = item.groupId
                ) {
                    deletionLiveData.value = true
                }
            is ClientModel ->
                DBManager.deleteClients(
                    personIds = listOf(
                        item.personId ?: return
                    )
                ) {
                    deletionLiveData.value = true
                }
            is PaymentAmountModel ->
                DBManager.deletePayments(
                    paymentIds = listOf(
                        item.paymentId ?: return
                    )
                ) {
                    deletionLiveData.value = true
                }
        }
    }

    suspend fun deletePayments(personIds: List<Int>) {
        DBManager.deleteClients(
            personIds = personIds
        ) {
            deletionLiveData.value = true
        }
    }

    suspend fun insertFile(userModel: UserModel?, file: File, description: String) {
        DBManager.insertFile(
            userId = userModel?.id,
            invoiceDataModel = InvoiceDataModel(
                description = description,
                fileName = file.name,
                dateTime = Date().pdfFormattedCurrentDate
            ).also {
                it.fileData = file.readBytes()
            }
        )
    }

    suspend fun updateUserBalance(userModel: UserModel?, balance: Double) {
        DBManager.updateUserBalance(
            userId = userModel?.id,
            balance = balance
        )
    }

    suspend fun updateGroup(
        groupModel: GroupModel,
        updateSuccessBlock: UpdateSuccessBlock
    ) {
        DBManager.updateGroup(
            groupModel = groupModel,
            updateSuccessBlock = updateSuccessBlock
        )
    }

    suspend fun insertGroups(
        userId: Int?,
        groupModelList: List<GroupModel>,
        insertionSuccessBlock: InsertionSuccessBlock
    ) {
        DBManager.insertGroups(
            userId = userId,
            groupModelList = groupModelList,
            insertionSuccessBlock = insertionSuccessBlock
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