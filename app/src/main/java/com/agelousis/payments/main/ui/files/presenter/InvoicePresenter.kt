package com.agelousis.payments.main.ui.files.presenter

import com.agelousis.payments.main.ui.files.models.InvoiceDataModel

interface InvoicePresenter {
    fun onInvoiceSelected(invoiceDataModel: InvoiceDataModel, adapterPosition: Int) {}
    fun onInvoiceLongPressed(adapterPosition: Int) {}
}