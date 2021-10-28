package com.agelousis.payments.main.ui.payments.presenters

import com.agelousis.payments.main.ui.payments.models.ClientModel

interface PaymentPresenter {
    fun onPaymentSelected(clientModel: ClientModel, adapterPosition: Int)
    fun onPaymentLongPressed(paymentIndex: Int, isSelected: Boolean)
}