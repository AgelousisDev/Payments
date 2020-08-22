package com.agelousis.monthlyfees.main.ui.payments.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.monthlyfees.database.DBManager
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel
import com.agelousis.monthlyfees.main.ui.payments.models.PersonModel

class PaymentListViewModel: ViewModel() {

    val paymentsLiveData by lazy { MutableLiveData<List<Any>>() }
    val deletionLiveData by lazy { MutableLiveData<Boolean>() }

    suspend fun initializePayments(context: Context, userModel: UserModel) {
        val dbManager = DBManager(
            context = context
        )
        dbManager.initializePayments(
            userId = userModel.id
        ) {
            paymentsLiveData.value = it
        }
        dbManager.close()
    }

    suspend fun deleteItem(context: Context, item: Any?) {
        val dbManager = DBManager(context = context)
        (item as? GroupModel)?.let {
            dbManager.deleteGroup(
                groupId = it.groupId
            ) {
                deletionLiveData.value = true
            }
        }
        (item as? PersonModel)?.let {
            dbManager.deletePayment(
                paymentId = it.paymentId
            ) {
                deletionLiveData.value = true
            }
        }
    }

}