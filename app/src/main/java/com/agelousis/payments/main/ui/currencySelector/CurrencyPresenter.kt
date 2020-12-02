package com.agelousis.payments.main.ui.currencySelector

import com.agelousis.payments.main.ui.currencySelector.enumerations.CurrencyType

interface CurrencyPresenter {
    fun onCurrencySelected(currencyType: CurrencyType)
}