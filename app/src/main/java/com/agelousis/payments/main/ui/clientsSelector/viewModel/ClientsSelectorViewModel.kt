package com.agelousis.payments.main.ui.clientsSelector.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.database.InsertionSuccessBlock
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.GroupModel

class ClientsSelectorViewModel: ViewModel() {

    suspend fun insertGroup(context: Context, userId: Int?, groupModel: GroupModel, groupInsertionSuccessBlock: (groupId: Long?) -> Unit) {
        val dbManager = DBManager(context = context)
        dbManager.insertGroupWithIds(
            userId = userId,
            groupModel = groupModel,
            groupInsertionSuccessBlock = groupInsertionSuccessBlock
        )
    }

    suspend fun insertClients(context: Context, userId: Int?, clientModelList: List<ClientModel>, clientsInsertionSuccessBlock: InsertionSuccessBlock) {
        val dbManager = DBManager(context = context)
        dbManager.insertClients(
            userId = userId,
            clientModelList = clientModelList,
            insertionSuccessBlock = clientsInsertionSuccessBlock
        )
    }

}