package com.agelousis.payments.login.presenter

import com.agelousis.payments.login.enumerations.UIMode

interface LoginPresenter {
    fun onProfileSelect()
    fun onSignIn()
    fun onSignUp()
    fun onImport()
    fun onUsersSelect()
    fun onBiometrics()
    fun onUIModeChanged(uiMode: UIMode)
}