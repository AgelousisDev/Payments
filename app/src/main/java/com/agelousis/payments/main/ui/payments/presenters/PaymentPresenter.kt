package com.agelousis.payments.main.ui.payments.presenters

import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel

interface PaymentPresenter {
    fun onPaymentSelected(paymentAmountModel: PaymentAmountModel, adapterPosition: Int)
    fun onPaymentLongPressed(paymentIndex: Int, isSelected: Boolean)
}