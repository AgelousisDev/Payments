package com.agelousis.payments.main.ui.dashboard.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.R
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.ui.dashboard.enumerations.DashboardStatisticsType
import com.agelousis.payments.main.ui.dashboard.model.DashboardStatisticsDataModel
import com.agelousis.payments.main.ui.dashboard.presenter.DashboardPresenter
import com.agelousis.payments.main.ui.files.models.InvoiceDataModel
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

    var clientModelListMutableState by mutableStateOf<List<ClientModel>?>(value = null)
    var paymentAmountModelListMutableState by mutableStateOf<List<PaymentAmountModel>?>(value = null)

    val groupModelListLiveData: LiveData<List<GroupModel>>
        get() = groupModelListMutableLiveData
    private val fileDataModelListMutableLiveData by lazy {
        MutableLiveData<List<InvoiceDataModel>>()
    }
    val invoiceDataModelListLiveData: LiveData<List<InvoiceDataModel>>
        get() = fileDataModelListMutableLiveData

    val todayPaymentClientName
        get() = clientModelListMutableState?.filter { clientModel ->
            clientModel.hasPaymentToday
        }?.joinToString { clientModel ->
            clientModel.fullName
        }?.takeIf {
            it.isNotEmpty()
        }

    fun initializeDashboardDataWith() {
        dashboardStatisticsDataMutableState = listOf(
            DashboardStatisticsDataModel(
                dashboardStatisticsType = DashboardStatisticsType.GROUPS,
                size = groupModelListLiveData.value?.size ?: 0,
                labelResource = R.string.key_groups_label,
                backgroundColor = R.color.orange,
                icon = R.drawable.ic_group
            ),
            DashboardStatisticsDataModel(
                dashboardStatisticsType = DashboardStatisticsType.CLIENTS,
                size = clientModelListMutableState?.size ?: 0,
                labelResource = R.string.key_clients_label,
                backgroundColor = R.color.lightBlue,
                icon = R.drawable.ic_client
            ),
            DashboardStatisticsDataModel(
                dashboardStatisticsType = DashboardStatisticsType.PAYMENTS,
                size = paymentAmountModelListMutableState?.size ?: 0,
                labelResource = R.string.key_payments_label,
                backgroundColor = R.color.red,
                icon = R.drawable.ic_payment
            ),
            DashboardStatisticsDataModel(
                dashboardStatisticsType = DashboardStatisticsType.INVOICES,
                size = invoiceDataModelListLiveData.value?.size ?: 0,
                labelResource = R.string.key_invoices_label,
                backgroundColor = R.color.green,
                icon = R.drawable.ic_invoice
            )
        )
    }

    suspend fun initializePayments(userModel: UserModel?) {
        DBManager.initializePayments(
            userId = userModel?.id
        ) { data ->
            clientModelListMutableState = data.filterIsInstance<ClientModel>()
            paymentAmountModelListMutableState = listOf(
                *clientModelListMutableState?.asSequence()?.mapNotNull {
                    it.payments
                }?.flatten()?.toList()?.toTypedArray() ?: arrayOf(),
                *data.filterIsInstance<PaymentAmountModel>().toTypedArray()
            )
        }
    }

    suspend infix fun fetchGroups(
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
        clientModelListMutableState?.groupBy { clientModel ->
            clientModel.groupId
        }?.forEach { map ->
            totalPaymentGroupAmountList.add(
                map.value.mapNotNull { clientModel ->
                    clientModel.totalPaymentAmount
                }.sum() + (paymentAmountModelListMutableState?.filter { paymentAmountModel ->
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
        clientModelListMutableState?.groupBy { clientModel ->
            clientModel.groupId
        }?.forEach { map ->
            totalPaymentGroupNamePair.add(
                map.value.firstOrNull()?.groupName to
                        map.value.mapNotNull { clientModel ->
                            clientModel.totalPaymentAmount
                        }.sum() + (paymentAmountModelListMutableState?.filter { paymentAmountModel ->
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