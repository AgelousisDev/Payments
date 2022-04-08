package com.agelousis.payments.login.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.ui.payments.models.GroupModel

class LoginViewModel: ViewModel() {

    val usersLiveData by lazy { MutableLiveData<List<UserModel>>() }
    val groupsLiveData by lazy { MutableLiveData<List<GroupModel>>() }
    val personsImagesLiveData by lazy { MutableLiveData<List<Pair<String?, ByteArray?>>>() }

    suspend fun initializeUsers() {
        DBManager.checkUsers {
            usersLiveData.value = it.takeIf { it.isNotEmpty() } ?: return@checkUsers
        }
    }

    suspend fun initializeGroups() {
        DBManager.initializeGroups inner@ {
            groupsLiveData.value = it.takeIf { it.isNotEmpty() } ?: return@inner
        }
    }

}