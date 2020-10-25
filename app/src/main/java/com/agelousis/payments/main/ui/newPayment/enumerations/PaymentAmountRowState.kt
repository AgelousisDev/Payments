package com.agelousis.payments.main.ui.newPayment.enumerations

import com.agelousis.payments.R

enum class PaymentAmountRowState {
    NORMAL, CAN_BE_DISMISSED;

    val backgroundTint
        get() = when(this) {
            NORMAL -> R.color.green
            CAN_BE_DISMISSED -> R.color.red
        }

    val otherState
        get() = when(this) {
            NORMAL -> CAN_BE_DISMISSED
            CAN_BE_DISMISSED -> NORMAL
        }

}