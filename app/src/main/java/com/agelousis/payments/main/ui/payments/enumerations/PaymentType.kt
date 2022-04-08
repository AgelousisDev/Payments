package com.agelousis.payments.main.ui.payments.enumerations

import android.content.res.Resources
import com.agelousis.payments.R

enum class PaymentType {
    CASH_PAYMENT,
    ONLINE_PAYMENT,
    CHECK_PAYMENT;

    fun getLocalizedTitle(resources: Resources) =
        when(this) {
            CASH_PAYMENT -> resources.getStringArray(R.array.key_payment_type_array)[0]
            ONLINE_PAYMENT -> resources.getStringArray(R.array.key_payment_type_array)[1]
            CHECK_PAYMENT -> resources.getStringArray(R.array.key_payment_type_array)[2]
        }

}