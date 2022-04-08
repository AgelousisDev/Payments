package com.agelousis.payments.main.menuOptions.presenters

import com.agelousis.payments.main.ui.qrCode.enumerations.QRCodeSelectionType

interface PaymentsMenuOptionPresenter {
    fun onClearPayments()
    fun onCsvExport()
    fun onSendSmsGlobally()
    fun onPaymentsOrder()
    fun onQrCode(qrCodeSelectionType: QRCodeSelectionType)
}