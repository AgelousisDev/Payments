package com.agelousis.payments.main.ui.countrySelector.interfaces

import com.agelousis.payments.main.ui.countrySelector.enumerations.CountryDataModel

interface CountrySelectorFragmentPresenter {
    fun onCountrySelected(countryDataModel: CountryDataModel)
}