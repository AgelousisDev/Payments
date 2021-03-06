package com.agelousis.payments.main.ui.personalInformation.presenter

interface OptionPresenter {
    fun onChangeProfilePicture()
    fun onUsernameChange(newUsername: String)
    fun onPasswordChange(newPassword: String)
    fun onPasswordPinChange(newPasswordPin: String)
    fun onBiometricsState(state: Boolean)
    fun onAddressChange(newAddress: String)
    fun onIdCardNumberChange(newIdCardNumber: String)
    fun onSocialInsuranceNumberChange(newSocialInsuranceNumber: String)
    fun onFirstNameChange(newFirstName: String)
    fun onLastNameChange(newLastName: String)
    fun onVatChange(newVat: Int)
    fun onPaymentAmountChange(newPaymentAmount: Double)
    fun onMessageTemplateChange(newMessageTemplate: String)
    fun onDeleteUser()
    fun onExportDatabase()
    fun onChangeCurrency()
    fun onChangeCountry()
}