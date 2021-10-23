package com.agelousis.payments.main.ui.paymentsFiltering.enumerations

import android.content.Context
import androidx.core.content.ContextCompat
import com.agelousis.payments.R

enum class PaymentsFilteringOptionType {
    FREE, CHARGE, EXPIRED, SINGLE_PAYMENT, INACTIVE;

    val label
        get() = when(this) {
            FREE -> R.string.key_free_label
            CHARGE -> R.string.key_charge_label
            EXPIRED -> R.string.key_expired_label
            SINGLE_PAYMENT -> R.string.key_single_payment_label
            INACTIVE -> R.string.key_inactive_label
        }

    infix fun getHeaderColor(context: Context) =
        when(this) {
            FREE -> ContextCompat.getColor(context, R.color.lightBlue)
            CHARGE -> ContextCompat.getColor(context, R.color.lightGreen)
            EXPIRED -> ContextCompat.getColor(context, R.color.fadedRed)
            SINGLE_PAYMENT -> ContextCompat.getColor(context, R.color.lightYellow)
            INACTIVE -> ContextCompat.getColor(context, R.color.lightSea)
        }

    var position = 0

}