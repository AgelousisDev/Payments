package com.agelousis.payments.main.ui.dashboard.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.R
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.main.ui.dashboard.enumerations.DashboardStatisticsType
import com.agelousis.payments.main.ui.dashboard.model.DashboardStatisticsDataModel
import com.agelousis.payments.main.ui.dashboard.presenter.DashboardPresenter
import com.agelousis.payments.main.ui.files.models.FileDataModel
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.utils.extensions.isZero

class DashboardViewModel: ViewModel() {

    var dashboardPresenter: DashboardPresenter? = null
    var dashboardStatisticsDataMutableState by mutableStateOf(emptyList<DashboardStatisticsDataModel>())
        private set
    private val groupModelListMutableLiveData by lazy {
        MutableLiveData<List<GroupModel>>()
    }
    val groupModelListLiveData: LiveData<List<GroupModel>>
        get() = groupModelListMutableLiveData
    private val fileDataModelListMutableLiveData by lazy {
        MutableLiveData<List<FileDataModel>>()
    }
    val fileDataModelListLiveData: LiveData<List<FileDataModel>>
        get() = fileDataModelListMutableLiveData
    var clientModelList: List<ClientModel>? = null
    var paymentAmountModelList: List<PaymentAmountModel>? = null
    val todayPaymentClientName
        get() = clientModelList?.filter { clientModel ->
            clientModel.hasPaymentToday
        }?.joinToString { clientModel ->
            clientModel.fullName
        }?.takeIf {
            it.isNotEmpty()
        }

    fun initializeDashboardDataWith(
        groupModelList: List<GroupModel>,
        clientModelList: List<ClientModel>,
        paymentAmountModeList: List<PaymentAmountModel>,
        fileDataModelList: List<FileDataModel>
    ) {
        dashboardStatisticsDataMutableState = listOf(
            DashboardStatisticsDataModel(
                dashboardStatisticsType = DashboardStatisticsType.GROUPS,
                size = groupModelList.size,
                labelResource = R.string.key_groups_label,
                backgroundColor = R.color.orange,
                icon = R.drawable.ic_group
            ),
            DashboardStatisticsDataModel(
                dashboardStatisticsType = DashboardStatisticsType.CLIENTS,
                size = clientModelList.size,
                labelResource = R.string.key_clients_label,
                backgroundColor = R.color.lightBlue,
                icon = R.drawable.ic_client
            ),
            DashboardStatisticsDataModel(
                dashboardStatisticsType = DashboardStatisticsType.PAYMENTS,
                size = paymentAmountModeList.size,
                labelResource = R.string.key_payments_label,
                backgroundColor = R.color.red,
                icon = R.drawable.ic_payment
            ),
            DashboardStatisticsDataModel(
                dashboardStatisticsType = DashboardStatisticsType.INVOICES,
                size = fileDataModelList.size,
                labelResource = R.string.key_invoices_label,
                backgroundColor = R.color.green,
                icon = R.drawable.ic_invoice
            )
        )
    }

    suspend fun fetchGroups(
        userId: Int?
    ) {
        DBManager.initializeGroups(
            userId = userId
        ) {
            groupModelListMutableLiveData.value = it
        }
    }

    suspend fun fetchInvoices(
        userId: Int?
    ) {
        DBManager.initializeFiles(
            userId = userId
        ) {
            fileDataModelListMutableLiveData.value  = it
        }
    }

    fun onDashboardPage(bottomNavigationMenuItemId: Int) {
        dashboardPresenter?.onDashboardPage(
            bottomNavigationMenuItemId = bottomNavigationMenuItemId
        )
    }

    infix fun getMaxIncomingGroupAmount(
        maxAmount: Boolean,
    ): Double? {
        val totalPaymentGroupAmountList = arrayListOf<Double>()
        clientModelList?.groupBy { clientModel ->
            clientModel.groupId
        }?.forEach { map ->
            totalPaymentGroupAmountList.add(
                map.value.mapNotNull { clientModel ->
                    clientModel.totalPaymentAmount
                }.sum() + (paymentAmountModelList?.filter { paymentAmountModel ->
                    paymentAmountModel.groupId == map.value.firstOrNull()?.groupId
                }?.mapNotNull { paymentAmountModel ->
                    paymentAmountModel.paymentAmount
                }?.sum() ?: 0.0)
            )
        }
        totalPaymentGroupAmountList.removeAll {
            it.isZero
        }
        return if (maxAmount) totalPaymentGroupAmountList.maxOrNull() else totalPaymentGroupAmountList.minOrNull()
    }

    infix fun getMaxIncomingGroupName(
        maxAmount: Boolean,
    ): String? {
        val totalPaymentGroupNamePair = arrayListOf<Pair<String?, Double>>()
        clientModelList?.groupBy { clientModel ->
            clientModel.groupId
        }?.forEach { map ->
            totalPaymentGroupNamePair.add(
                map.value.firstOrNull()?.groupName to
                        map.value.mapNotNull { clientModel ->
                            clientModel.totalPaymentAmount
                        }.sum() + (paymentAmountModelList?.filter { paymentAmountModel ->
                    paymentAmountModel.groupId == map.value.firstOrNull()?.groupId
                }?.mapNotNull { paymentAmountModel ->
                    paymentAmountModel.paymentAmount
                }?.sum() ?: 0.0)
            )
        }
        totalPaymentGroupNamePair.removeAll {
            it.second.isZero
        }
        return if (maxAmount)
            totalPaymentGroupNamePair.maxByOrNull {
                it.second
            }?.first
        else
            totalPaymentGroupNamePair.minByOrNull {
                it.second
            }?.first
    }

}