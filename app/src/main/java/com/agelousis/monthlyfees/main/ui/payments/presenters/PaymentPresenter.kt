package com.agelousis.monthlyfees.main.ui.payments.presenters

import com.agelousis.monthlyfees.main.ui.payments.models.PersonModel

interface PaymentPresenter {
    fun onPaymentSelected(personModel: PersonModel)
    fun onPaymentLongPressed(personModel: PersonModel)
}