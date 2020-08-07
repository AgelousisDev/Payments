package com.agelousis.monthlyfees.main.ui.payments.models

data class PaymentModel(val paymentId: Int?,
                        val groupId: Int?,
                        val groupName: String?,
                        val firstName: String?,
                        val phone: String?,
                        val parentName: String?,
                        val parentPhone: String?,
                        val email: String?,
                        val active: Boolean?,
                        val free: Boolean?,
                        val paymentAmountModel: PaymentAmountModel?
) {
    var showLine = true
}

data class PaymentAmountModel(val paymentAmount: Double?,
                              val paymentDate: String?,
                              val skipPayment: Boolean?,
                              val paymentNote: String?
)