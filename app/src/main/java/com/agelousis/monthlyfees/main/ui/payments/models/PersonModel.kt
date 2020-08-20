package com.agelousis.monthlyfees.main.ui.payments.models

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PersonModel(val paymentId: Int? = null,
                       val groupId: Int?,
                       val groupName: String?,
                       val firstName: String?,
                       val phone: String?,
                       val parentName: String?,
                       val parentPhone: String?,
                       val email: String?,
                       val active: Boolean?,
                       val free: Boolean?,
                       val payments: List<PaymentAmountModel>?
): Parcelable {
    val totalPaymentAmount: Double?
        get() = payments?.mapNotNull { it.paymentAmount }?.sum()
    @IgnoredOnParcel var showLine = true
}

@Parcelize
data class PaymentAmountModel(val paymentId: Int? = null,
                              val paymentAmount: Double?,
                              val paymentDate: String?,
                              val skipPayment: Boolean?,
                              val paymentNote: String?
): Parcelable