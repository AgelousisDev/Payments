package com.agelousis.payments.main.ui.paymentsFiltering.enumerations

import android.content.Context
import androidx.core.content.ContextCompat
import com.agelousis.payments.R

enum class PaymentsFilteringOptionType {
    FREE, CHARGE, EXPIRED;

    val label
        get() = when(this) {
            FREE -> R.string.key_free_label
            CHARGE -> R.string.key_charge_label
            EXPIRED -> R.string.key_expired_label
        }

    infix fun getHeaderColor(context: Context) =
        when(this) {
            FREE -> ContextCompat.getColor(context, R.color.lightBlue)
            CHARGE -> ContextCompat.getColor(context, R.color.lightGreen)
            EXPIRED -> ContextCompat.getColor(context, R.color.fadedRed)
        }

    var position = 0

}