package com.agelousis.payments.main.ui.clientsSelector.viewModel

import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.database.InsertionSuccessBlock
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.GroupModel

class ClientsSelectorViewModel: ViewModel() {

    suspend fun insertGroup(userId: Int?, groupModel: GroupModel, groupInsertionSuccessBlock: (groupId: Long?) -> Unit) {
        DBManager.insertGroupWithIds(
            userId = userId,
            groupModel = groupModel,
            groupInsertionSuccessBlock = groupInsertionSuccessBlock
        )
    }

    suspend fun insertClients(userId: Int?, clientModelList: List<ClientModel>, clientsInsertionSuccessBlock: InsertionSuccessBlock) {
        DBManager.insertClients(
            userId = userId,
            clientModelList = clientModelList,
            insertionSuccessBlock = clientsInsertionSuccessBlock
        )
    }

}