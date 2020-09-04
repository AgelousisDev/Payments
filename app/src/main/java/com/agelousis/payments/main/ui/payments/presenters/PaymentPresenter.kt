package com.agelousis.payments.main.ui.payments.presenters

import com.agelousis.payments.main.ui.payments.models.PersonModel

interface PaymentPresenter {
    fun onPaymentSelected(personModel: PersonModel)
    fun onPaymentLongPressed(personModel: PersonModel)
}