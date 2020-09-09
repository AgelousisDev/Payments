package com.agelousis.payments.main.ui.payments.models

import android.content.Context
import com.agelousis.payments.R
import com.agelousis.payments.utils.extensions.euroFormattedString

data class PaymentAmountSumModel(
    val sum: Double?,
    val color: Int?,
) {

    fun getFormattedPaymentsSum(context: Context) =
        String.format(
            context.resources.getString(R.string.key_total_amount_value_label),
            sum?.euroFormattedString ?: context.resources.getString(R.string.key_empty_field_label)
        )

}