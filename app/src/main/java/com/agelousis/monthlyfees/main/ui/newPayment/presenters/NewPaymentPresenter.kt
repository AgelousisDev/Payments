package com.agelousis.monthlyfees.main.ui.newPayment.presenters

import com.agelousis.monthlyfees.main.ui.payments.models.PaymentAmountModel

interface NewPaymentPresenter {
    fun onPaymentAmount(paymentAmountModel: PaymentAmountModel? = null)
    fun onPaymentAmountLongPressed(paymentAmountModel: PaymentAmountModel?)
}