package com.agelousis.payments.main.ui.payments.enumerations

enum class PaymentsAdapterViewType(val type: Int) {
    EMPTY_VIEW(type = 0),
    BALANCE_VIEW(type = 1),
    GROUP_VIEW(type = 2),
    CLIENT_VIEW(type = 3),
    PAYMENT_VIEW(type = 4),
    PAYMENT_AMOUNT_SUM_VIEW(type = 5)
}