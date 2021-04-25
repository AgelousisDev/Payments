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
                       val groupId: Int? = null,
                       val groupName: String? = null,
                       val firstName: String? = null,
                       val surname: String? = null,
                       val phone: String? = null,
                       val parentName: String? = null,
                       val parentPhone: String? = null,
                       val email: String? = null,
                       val active: Boolean? = null,
                       val free: Boolean? = null,
                       val payments: List<PaymentAmountModel>? = null,
                       val groupColor: Int? = null,
                       val groupImage: String? = null,
                       val messageTemplate: String? = null,
                       val paymentType: PaymentType? = null
): Parcelable {

    @IgnoredOnParcel
    var isSelected = false

    val totalPaymentAmount: Double?
        get() = payments?.mapNotNull { it.paymentAmount }?.takeIf { it.isNotEmpty() }?.sum()

    val fullName: String
        get() = String.format("%s %s%s", firstName ?: "", surname ?: "", singlePaymentProducts?.takeIf { it.isNotBlank() }?.let { " - $it" } ?: "")

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
            payments?.any { it.singlePayment == true } == true -> PaymentsFilteringOptionType.SINGLE_PAYMENT.also { paymentsFilteringOptionType ->
                paymentsFilteringOptionType.position = paymentsFilteringOptionTypes?.firstOrNull { it == PaymentsFilteringOptionType.SINGLE_PAYMENT }?.position ?: return@also
            }
            else -> PaymentsFilteringOptionType.CHARGE.also { paymentsFilteringOptionType ->
                paymentsFilteringOptionType.position = paymentsFilteringOptionTypes?.firstOrNull { it == PaymentsFilteringOptionType.CHARGE }?.position ?: return@also
            }
        }

    private val singlePaymentProducts
        get() = payments?.mapNotNull { it.singlePaymentProducts }?.flatten()?.joinToString(separator = ",")

}

@Parcelize
data class PaymentAmountModel(val paymentId: Int? = null,
                              val paymentAmount: Double?,
                              val paymentMonth: String?,
                              val paymentDate: String?,
                              val skipPayment: Boolean?,
                              val paymentNote: String?,
                              val paymentDateNotification: Boolean?,
                              val singlePayment: Boolean?,
                              val singlePaymentProducts: List<String>?
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