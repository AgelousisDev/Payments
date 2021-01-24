package com.agelousis.payments.main.menuOptions.enumerations

import com.agelousis.payments.R

enum class PaymentsMenuOptionType {
    CLEAR_PAYMENTS,
    CSV_EXPORT,
    SEND_SMS_GLOBALLY;

    val icon
        get() = when(this) {
            CLEAR_PAYMENTS -> R.drawable.ic_clear_all
            CSV_EXPORT -> R.drawable.ic_excel
            SEND_SMS_GLOBALLY -> R.drawable.ic_speech_bubble
        }

    val title
        get() = when(this) {
            CLEAR_PAYMENTS -> R.string.key_clear_all_payments_payments_label
            CSV_EXPORT -> R.string.key_export_payments_to_csv_label
            SEND_SMS_GLOBALLY -> R.string.key_send_sms_globally_label
        }

    var isEnabled = false

}