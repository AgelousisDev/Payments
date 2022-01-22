package com.agelousis.payments.forgotPassword.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agelousis.payments.database.DBManager

class ForgotPasswordViewModel: ViewModel() {

    val pinLiveData by lazy { MutableLiveData<String>() }
    val newPasswordLiveData by lazy { MutableLiveData<String>() }
    val repeatNewPasswordLiveData by lazy { MutableLiveData<String>() }
    val updatePasswordLiveData by lazy { MutableLiveData<Boolean>() }

    suspend fun updatePassword(userId: Int, pin: String) {
        DBManager.updateUserPassword(
            userId = userId,
            pin = pin,
            newPassword = repeatNewPasswordLiveData.value ?: return,
        ) {
            updatePasswordLiveData.value = it
        }
    }

}