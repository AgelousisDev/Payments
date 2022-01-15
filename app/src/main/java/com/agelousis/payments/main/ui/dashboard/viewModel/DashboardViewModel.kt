package com.agelousis.payments.main.ui.dashboard.viewModel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.agelousis.payments.R
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.database.FilesSuccessBlock
import com.agelousis.payments.database.GroupsSuccessBlock
import com.agelousis.payments.main.ui.dashboard.model.DashboardStatisticsDataModel
import com.agelousis.payments.main.ui.files.models.FileDataModel
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel

class DashboardViewModel: ViewModel() {

    var dashboardStatisticsDataMutableState by mutableStateOf(emptyList<DashboardStatisticsDataModel>())
        private set

    fun initializeDashboardDataWith(
        groupModelList: List<GroupModel>,
        clientModelList: List<ClientModel>,
        paymentAmountModeList: List<PaymentAmountModel>,
        fileDataModelList: List<FileDataModel>
    ) {
        dashboardStatisticsDataMutableState = listOf(
            DashboardStatisticsDataModel(
                size = groupModelList.size,
                labelResource = R.string.key_groups_label,
                backgroundColor = R.color.orange,
                icon = R.drawable.ic_group
            ),
            DashboardStatisticsDataModel(
                size = clientModelList.size,
                labelResource = R.string.key_clients_label,
                backgroundColor = R.color.lightBlue,
                icon = R.drawable.ic_client
            ),
            DashboardStatisticsDataModel(
                size = paymentAmountModeList.size,
                labelResource = R.string.key_payments_label,
                backgroundColor = R.color.red,
                icon = R.drawable.ic_payment
            ),
            DashboardStatisticsDataModel(
                size = fileDataModelList.size,
                labelResource = R.string.key_invoices_label,
                backgroundColor = R.color.green,
                icon = R.drawable.ic_invoice
            )
        )
    }

    suspend fun fetchGroups(
        context: Context,
        userId: Int?,
        groupsSuccessBlock: GroupsSuccessBlock
    ) {
        val dbManager = DBManager(
            context = context
        )
        dbManager.initializeGroups(
            userId = userId,
            groupsSuccessBlock = groupsSuccessBlock
        )
    }

    suspend fun fetchInvoices(
        context: Context,
        userId: Int?,
        filesSuccessBlock: FilesSuccessBlock
    ) {
        val dbManager = DBManager(
            context = context
        )
        dbManager.initializeFiles(
            userId = userId,
            filesSuccessBlock = filesSuccessBlock
        )
    }

}