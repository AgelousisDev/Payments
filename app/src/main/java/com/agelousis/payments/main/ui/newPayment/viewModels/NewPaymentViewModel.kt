package com.agelousis.payments.main.ui.newPayment.viewModels

import android.content.Context
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

    suspend fun initializeGroups(context: Context, userId: Int?) {
        val dbManager = DBManager(context = context)
        dbManager.initializeGroups(
            userId = userId
        ) {
            groupsLiveData.value = it
        }
    }

    suspend fun addClient(context: Context, userId: Int?, clientModel: ClientModel) {
        val dbManager = DBManager(context = context)
        dbManager.insertClients(
            userId = userId,
            clientModelList = listOf(
                clientModel
            )
        ) {
            clientInsertionStateLiveData.value = true
        }
    }

    suspend fun updateClient(context: Context, userId: Int?, clientModel: ClientModel) {
        val dbManager = DBManager(context = context)
        dbManager.updateClient(
            userId = userId,
            clientModel = clientModel
        ) {
            clientInsertionStateLiveData.value = true
        }
    }

    suspend fun insertPayment(
        context: Context,
        paymentAmountModel: PaymentAmountModel,
        insertionSuccessBlock: InsertionSuccessBlock
    ) {
        val dbManager = DBManager(
            context = context
        )
        paymentAmountModel.paymentId.whenNull {
            dbManager.insertPayment(
                paymentAmountModel = paymentAmountModel,
                insertionSuccessBlock = insertionSuccessBlock
            )
        }?.let {
            dbManager.updatePayment(
                paymentAmountModel = paymentAmountModel,
                insertionSuccessBlock = insertionSuccessBlock
            )
        }
    }

}