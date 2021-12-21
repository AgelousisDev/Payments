package com.agelousis.payments.main.ui.newPayment.presenters

import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel

interface NewPaymentPresenter {
    fun onPaymentAmount(paymentAmountModel: PaymentAmountModel? = null) {}
    fun onCalendarEvent(paymentAmountModel: PaymentAmountModel?) {}
    fun onContactSelection() {}
    fun onClearPayments() {}
    fun onClientShareMessage() {}
}