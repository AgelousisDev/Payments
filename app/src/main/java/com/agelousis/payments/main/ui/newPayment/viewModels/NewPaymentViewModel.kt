package com.agelousis.payments.main.ui.newPayment.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.ClientModel

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

}