package com.agelousis.payments.main.ui.clientsSelector.presenters

interface ClientSelectorPresenter {
    fun onClientSelected(adapterPosition: Int, isSelected: Boolean)
}