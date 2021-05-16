package com.agelousis.payments.main.ui.newPayment.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.ClientModel

class NewPaymentViewModel: ViewModel() {

    val groupsLiveData by lazy { MutableLiveData<List<GroupModel>>() }
    val paymentInsertionStateLiveData by lazy { MutableLiveData<Boolean>() }

    suspend fun initializeGroups(context: Context, userId: Int?) {
        val dbManager = DBManager(context = context)
        dbManager.initializeGroups(
            userId = userId
        ) {
            groupsLiveData.value = it
        }
    }

    suspend fun addPayment(context: Context, userId: Int?, clientModel: ClientModel) {
        val dbManager = DBManager(context = context)
        dbManager.insertPayment(
            userId = userId,
            clientModel = clientModel
        ) {
            paymentInsertionStateLiveData.value = true
        }
    }

    suspend fun updatePayment(context: Context, userId: Int?, clientModel: ClientModel) {
        val dbManager = DBManager(context = context)
        dbManager.updatePayment(
            userId = userId,
            clientModel = clientModel
        ) {
            paymentInsertionStateLiveData.value = true
        }
    }

}