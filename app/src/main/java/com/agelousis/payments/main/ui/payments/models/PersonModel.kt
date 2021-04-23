package com.agelousis.payments.main.ui.payments.models

import android.content.Context
import android.os.Parcelable
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.main.ui.newPayment.enumerations.PaymentAmountRowState
import com.agelousis.payments.main.ui.payments.enumerations.PaymentType
import com.agelousis.payments.main.ui.paymentsFiltering.enumerations.PaymentsFilteringOptionType
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class PersonModel(val personId: Int? = null,
                       val groupId: Int?,
                       val groupName: String?,
                       val firstName: String?,
                       val surname: String?,
                       val phone: String?,
                       val parentName: String?,
                       val parentPhone: String?,
                       val email: String?,
                       val active: Boolean?,
                       val free: Boolean?,
                       val payments: List<PaymentAmountModel>?,
                       val groupColor: Int? = null,
                       val groupImage: String? = null,
                       val messageTemplate: String?,
                       val paymentType: PaymentType?
): Parcelable {

    @IgnoredOnParcel
    var isSelected = false

    val totalPaymentAmount: Double?
        get() = payments?.mapNotNull { it.paymentAmount }?.takeIf { it.isNotEmpty() }?.sum()

    val fullName: String
        get() = String.format("%s %s", firstName ?: "", surname ?: "")

    fun getCommunicationData(context: Context): String {
        return ifLet(phone, email) {
            String.format("%s, %s", it.first(), it.second())
        } ?: run {
            phone ?: email ?: context.resources.getString(R.string.key_empty_field_label)
        }
    }

    @IgnoredOnParcel var backgroundDrawable = R.drawable.payment_row_background

    infix fun getPaymentsFilteringOptionType(paymentsFilteringOptionTypes: List<PaymentsFilteringOptionType>?) =
        when {
            free == true -> PaymentsFilteringOptionType.FREE.also { paymentsFilteringOptionType ->
                paymentsFilteringOptionType.position = paymentsFilteringOptionTypes?.firstOrNull { it == PaymentsFilteringOptionType.FREE }?.position ?: return@also
            }
            payments?.any { it.paymentMonthDate?.toCalendar(plusMonths = 1)?.time?.isDatePassed == true } == true -> PaymentsFilteringOptionType.EXPIRED.also { paymentsFilteringOptionType ->
                paymentsFilteringOptionType.position = paymentsFilteringOptionTypes?.firstOrNull { it == PaymentsFilteringOptionType.EXPIRED }?.position ?: return@also
            }
            else -> PaymentsFilteringOptionType.CHARGE.also { paymentsFilteringOptionType ->
                paymentsFilteringOptionType.position = paymentsFilteringOptionTypes?.firstOrNull { it == PaymentsFilteringOptionType.CHARGE }?.position ?: return@also
            }
        }

}

@Parcelize
data class PaymentAmountModel(val paymentId: Int? = null,
                              val paymentAmount: Double?,
                              val paymentMonth: String?,
                              val paymentDate: String?,
                              val skipPayment: Boolean?,
                              val paymentNote: String?,
                              val paymentDateNotification: Boolean?,
                              val singlePayment: Boolean?
): Parcelable {

    @IgnoredOnParcel var paymentAmountRowState = PaymentAmountRowState.NORMAL

    fun getAmountWithoutVat(context: Context, vat: Int?) =
        paymentAmount.getAmountWithoutVat(
            vat = vat
        ).euroFormattedString ?: context.resources.getString(R.string.key_empty_field_label)

    fun getVatAmount(vat: Int?) =
        vat.takeIf { !it.isZero }?.let { paymentAmount.getVatAmount(
            vat = vat
        ).euroFormattedString } ?: String.format(
            "${MainApplication.currencySymbol ?: "€"}%s",
            "0.00"
        )

    val paymentYearMonthWithArrow
        get() = String.format(
            "%s ⤳ %s",
            paymentMonth?.split(" ")?.firstOrNull() ?: "",
            paymentMonth?.split(" ")?.getOrNull(index = 1) ?: ""
        )

    val paymentColor: Int
        get() {
            val paymentMonthCalendar = paymentMonthDate?.toCalendar(plusMonths = 1) ?: return paymentAmountRowState.backgroundTint
            return if (paymentAmountRowState == PaymentAmountRowState.NORMAL)
                if (paymentMonthCalendar.time.isDatePassed)
                    R.color.orange
                else paymentAmountRowState.backgroundTint
            else
                paymentAmountRowState.backgroundTint
        }

    val paymentMonthDate
        get() = paymentMonth?.toDateWith(pattern = Constants.MONTH_DATE_FORMAT, locale = Locale.US)

}