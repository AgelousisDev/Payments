package com.agelousis.payments.main.ui.payments.presenters

import com.agelousis.payments.main.ui.payments.models.ClientModel

interface ClientPresenter {
    fun onClientSelected(clientModel: ClientModel, adapterPosition: Int)
    fun onClientLongPressed(paymentIndex: Int, isSelected: Boolean)
}