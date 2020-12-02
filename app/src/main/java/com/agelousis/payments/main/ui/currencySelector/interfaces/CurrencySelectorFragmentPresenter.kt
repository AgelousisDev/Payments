package com.agelousis.payments.main.ui.currencySelector.interfaces

import com.agelousis.payments.main.ui.currencySelector.enumerations.CurrencyType

interface CurrencySelectorFragmentPresenter {
    fun onCurrencySelected(currencyType: CurrencyType)
}