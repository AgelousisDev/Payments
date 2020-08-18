package com.agelousis.monthlyfees.main.ui.newPayment.presenters

import com.agelousis.monthlyfees.main.ui.payments.models.PaymentAmountModel

interface NewPaymentPresenter {
    fun onPaymentViewOrAdd(paymentAmountModel: PaymentAmountModel? = null)
}