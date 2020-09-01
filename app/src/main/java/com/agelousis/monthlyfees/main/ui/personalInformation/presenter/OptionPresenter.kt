package com.agelousis.monthlyfees.main.ui.personalInformation.presenter

interface OptionPresenter {
    fun onChangeProfilePicture()
    fun onUsernameChange(newUsername: String)
    fun onPasswordChange(newPassword: String)
    fun onBiometricsState(state: Boolean)
    fun onAddressChange(newAddress: String)
    fun onIdCardNumberChange(newIdCardNumber: String)
    fun onSocialInsuranceNumberChange(newSocialInsuranceNumber: String)
    fun onFirstNameChange(newFirstName: String)
    fun onLastNameChange(newLastName: String)
}