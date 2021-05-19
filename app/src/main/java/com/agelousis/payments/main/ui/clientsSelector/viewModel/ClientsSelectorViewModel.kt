package com.agelousis.payments.main.ui.clientsSelector.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.GroupModel

class ClientsSelectorViewModel: ViewModel() {

    val groupsInsertionStateLiveData by lazy { MutableLiveData<Boolean>() }
    val clientInsertionStateLiveData by lazy { MutableLiveData<Boolean>() }

    suspend fun insertGroups(context: Context, userId: Int?, groupModelList: List<GroupModel>) {
        val dbManager = DBManager(context = context)
        dbManager.insertGroups(
            userId = userId,
            groupModelList = groupModelList
        ) {
            groupsInsertionStateLiveData.value = true
        }
    }

    suspend fun insertClients(context: Context, userId: Int?, clientModeList: List<ClientModel>) {
        val dbManager = DBManager(context = context)
        dbManager.insertClients(
            userId = userId,
            clientModelList = clientModeList
        ) {
            clientInsertionStateLiveData.value = true
        }
    }

}