package com.agelousis.payments.main.ui.payments.models

import android.content.Context
import android.os.Parcelable
import com.agelousis.payments.R
import com.agelousis.payments.utils.extensions.euroFormattedString
import com.agelousis.payments.utils.extensions.getAmountWithoutVat
import com.agelousis.payments.utils.extensions.getVatAmount
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentAmountSumModel(val sum: Double?,
                                 val color: Int?
): Parcelable {

    fun getFormattedPaymentAmountWithoutVat(context: Context, vat: Int?) =
        String.format(
            "%s:   %s",
            context.resources.getString(R.string.key_subtotal_label),
            sum?.getAmountWithoutVat(vat = vat)?.euroFormattedString ?: context.resources.getString(R.string.key_empty_field_label)
        )

    fun getFormattedPaymentVatAmount(context: Context, vat: Int?) =
        String.format(
            "%s:                %s",
            context.resources.getString(R.string.key_vat_label),
            sum?.getVatAmount(vat = vat)?.euroFormattedString ?: context.resources.getString(R.string.key_empty_field_label)
        )

    fun getFormattedPaymentsSum(context: Context) =
        String.format(
            context.resources.getString(R.string.key_total_amount_value_label),
            sum?.euroFormattedString ?: context.resources.getString(R.string.key_empty_field_label)
        )

}