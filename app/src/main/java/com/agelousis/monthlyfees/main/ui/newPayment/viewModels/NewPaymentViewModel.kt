package com.agelousis.monthlyfees.main.ui.newPayment.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.monthlyfees.database.DBManager
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel

class NewPaymentViewModel: ViewModel() {

    val groupsLiveData by lazy { MutableLiveData<List<GroupModel>>() }

    suspend fun initializeGroups(context: Context, userId: Int?) {
        val dbManager = DBManager(context = context)
        dbManager.initializeGroups(
            userId = userId
        ) {
            groupsLiveData.value = it
        }
    }

}