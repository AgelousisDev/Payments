package com.agelousis.payments.main.ui.paymentsFiltering.enumerations

import android.content.Context
import androidx.core.content.ContextCompat
import com.agelousis.payments.R

enum class PaymentsFilteringOptionType {
    FREE, CHARGE, INACTIVE;

    val label
        get() = when(this) {
            FREE -> R.string.key_free_label
            CHARGE -> R.string.key_with_charge_label
            INACTIVE -> R.string.key_inactive_label
        }

    infix fun getHeaderColor(context: Context) =
        when(this) {
            FREE -> ContextCompat.getColor(context, R.color.lightBlue)
            CHARGE -> ContextCompat.getColor(context, R.color.lightGreen)
            INACTIVE -> ContextCompat.getColor(context, R.color.lightSea)
        }

}