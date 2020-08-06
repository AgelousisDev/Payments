package com.agelousis.monthlyfees.main.ui.payments.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.monthlyfees.database.DBManager
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.main.ui.payments.models.PaymentModel

class PaymentListViewModel: ViewModel() {

    val paymentsLiveData by lazy { MutableLiveData<List<PaymentModel>>() }

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

}