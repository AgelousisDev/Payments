package com.agelousis.payments.main.menuOptions.enumerations

import com.agelousis.payments.R

enum class PaymentsMenuOptionType {
    CLEAR_PAYMENTS,
    CSV_EXPORT;

    val icon
        get() = when(this) {
            CLEAR_PAYMENTS -> R.drawable.ic_clear_all
            CSV_EXPORT -> R.drawable.ic_excel
        }

    val title
        get() = when(this) {
            CLEAR_PAYMENTS -> R.string.key_clear_all_payments_payments_label
            CSV_EXPORT -> R.string.key_export_payments_to_csv_label
        }

    var isEnabled = false

}