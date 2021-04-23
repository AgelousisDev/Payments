package com.agelousis.payments.main.ui.payments.presenters

import com.agelousis.payments.main.ui.payments.models.PersonModel

interface PaymentPresenter {
    fun onPaymentSelected(personModel: PersonModel)
    fun onPaymentShareMessage(personModel: PersonModel)
    fun onPaymentLongPressed(paymentIndex: Int, isSelected: Boolean)
}