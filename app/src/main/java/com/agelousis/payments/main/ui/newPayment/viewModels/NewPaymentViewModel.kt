package com.agelousis.payments.main.ui.newPayment.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.database.InsertionSuccessBlock
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.utils.extensions.whenNull

class NewPaymentViewModel: ViewModel() {

    val groupsLiveData by lazy { MutableLiveData<List<GroupModel>>() }
    val clientInsertionStateLiveData by lazy { MutableLiveData<Boolean>() }

    suspend fun initializeGroups(userId: Int?) {
        DBManager.initializeGroups(
            userId = userId
        ) {
            groupsLiveData.value = it
        }
    }

    suspend fun addClient(userId: Int?, clientModel: ClientModel) {
        DBManager.insertClients(
            userId = userId,
            clientModelList = listOf(
                clientModel
            )
        ) {
            clientInsertionStateLiveData.value = true
        }
    }

    suspend fun updateClient(userId: Int?, clientModel: ClientModel) {
        DBManager.updateClient(
            userId = userId,
            clientModel = clientModel
        ) {
            clientInsertionStateLiveData.value = true
        }
    }

    suspend fun insertPayment(
        paymentAmountModel: PaymentAmountModel,
        insertionSuccessBlock: InsertionSuccessBlock
    ) {
        paymentAmountModel.paymentId.whenNull {
            DBManager.insertPayment(
                paymentAmountModel = paymentAmountModel,
                insertionSuccessBlock = insertionSuccessBlock
            )
        }?.let {
            DBManager.updatePayment(
                paymentAmountModel = paymentAmountModel,
                insertionSuccessBlock = insertionSuccessBlock
            )
        }
    }

}