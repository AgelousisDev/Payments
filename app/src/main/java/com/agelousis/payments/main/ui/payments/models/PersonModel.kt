package com.agelousis.payments.main.ui.payments.models

import android.content.Context
import android.os.Parcelable
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.newPayment.enumerations.PaymentAmountRowState
import com.agelousis.payments.utils.extensions.ifLet
import com.agelousis.payments.utils.extensions.second
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PersonModel(val paymentId: Int? = null,
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
                       val groupColor: Int? = null
): Parcelable {
    val totalPaymentAmount: Double?
        get() = payments?.mapNotNull { it.paymentAmount }?.sum()

    val fullName: String
        get() = String.format("%s %s", firstName ?: "", surname ?: "")

    @IgnoredOnParcel var showLine = true

    fun getCommunicationData(context: Context): String {
        return ifLet(phone, email) {
            String.format("%s, %s", it.first(), it.second())
        } ?: run {
            phone ?: email ?: context.resources.getString(R.string.key_empty_field_label)
        }
    }

}

@Parcelize
data class PaymentAmountModel(val paymentId: Int? = null,
                              val paymentAmount: Double?,
                              val startDate: String?,
                              val paymentDate: String?,
                              val endDate: String?,
                              val skipPayment: Boolean?,
                              val paymentNote: String?
): Parcelable {

    @IgnoredOnParcel var paymentAmountRowState = PaymentAmountRowState.NORMAL

}