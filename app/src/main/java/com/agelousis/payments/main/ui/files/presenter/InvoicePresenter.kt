package com.agelousis.payments.main.ui.files.presenter

import com.agelousis.payments.main.ui.files.models.FileDataModel

interface InvoicePresenter {
    fun onInvoiceSelected(fileDataModel: FileDataModel, adapterPosition: Int)
    fun onInvoiceLongPressed(adapterPosition: Int)
}