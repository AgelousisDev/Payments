package com.agelousis.payments.main.menuOptions.enumerations

import com.agelousis.payments.R

enum class PaymentsMenuOptionType {
    CLIENTS_ORDER,
    CLEAR_CLIENTS,
    CSV_EXPORT,
    SEND_SMS_GLOBALLY,
    QR_CODE_GENERATOR,
    SCAN_QR_CODE;

    val icon
        get() = when(this) {
            CLIENTS_ORDER -> R.drawable.ic_sort
            CLEAR_CLIENTS -> R.drawable.ic_clear_all
            CSV_EXPORT -> R.drawable.ic_invoice
            SEND_SMS_GLOBALLY -> R.drawable.ic_speech_bubble
            QR_CODE_GENERATOR -> R.drawable.ic_qr_code
            SCAN_QR_CODE -> R.drawable.ic_qr_scan
        }

    val title
        get() = when(this) {
            CLIENTS_ORDER -> R.string.key_clients_order_label
            CLEAR_CLIENTS -> R.string.key_clear_all_clients_label
            CSV_EXPORT -> R.string.key_export_payments_to_csv_and_invoice_label
            SEND_SMS_GLOBALLY -> R.string.key_send_sms_globally_label
            QR_CODE_GENERATOR -> R.string.key_clients_payments_generate_qr_code_label
            SCAN_QR_CODE -> R.string.key_scan_qr_code_label
        }

    val subtitle
        get() = when(this) {
            QR_CODE_GENERATOR -> R.string.key_clients_payments_generate_qr_code__message
            SCAN_QR_CODE -> R.string.key_scan_qr_code_message
            else -> null
        }

    var isEnabled = false

}