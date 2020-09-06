package com.agelousis.payments.userSelection.presenters

import com.agelousis.payments.login.models.UserModel

interface UserSelectionPresenter {
    fun onUserSelected(userModel: UserModel)
}