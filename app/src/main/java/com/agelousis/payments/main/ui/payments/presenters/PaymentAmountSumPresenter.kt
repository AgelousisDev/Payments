package com.agelousis.payments.main.ui.payments.presenters

import com.agelousis.payments.main.ui.payments.models.PaymentAmountSumModel

interface PaymentAmountSumPresenter {
    fun onPaymentAmountSumSelected(paymentAmountSumModel: PaymentAmountSumModel)
}