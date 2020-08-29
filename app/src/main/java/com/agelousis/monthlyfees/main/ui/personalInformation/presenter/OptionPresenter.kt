package com.agelousis.monthlyfees.main.ui.personalInformation.presenter

interface OptionPresenter {
    fun onChangeProfilePicture()
    fun onUsernameChange(newUsername: String)
    fun onPasswordChange(newPassword: String)
    fun onBiometricsState(state: Boolean)
}