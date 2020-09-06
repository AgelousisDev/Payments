package com.agelousis.payments.login.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.login.models.UserModel

class LoginViewModel: ViewModel() {

    val usersLiveData by lazy { MutableLiveData<List<UserModel>>() }

    suspend fun initializeUsers(context: Context) {
        val dbManager = DBManager(context = context)
        dbManager.checkUsers {
            usersLiveData.value = it
        }
    }

}