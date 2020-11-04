package com.agelousis.payments.main.ui.payments.enumerations

import android.content.res.Resources
import com.agelousis.payments.R

enum class PaymentType {
    CASH_PAYMENT,
    ONLINE_PAYMENT,
    CHECK_PAYMENT;

    fun getLocalizedTitle(resources: Resources) =
        when(this) {
            CASH_PAYMENT -> resources.getString(R.string.key_cash_payment_label)
            ONLINE_PAYMENT -> resources.getString(R.string.key_online_payment_label)
            CHECK_PAYMENT -> resources.getString(R.string.key_check_payment_label)
        }

}