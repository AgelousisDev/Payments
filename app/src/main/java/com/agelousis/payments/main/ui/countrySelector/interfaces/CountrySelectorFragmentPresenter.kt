package com.agelousis.payments.main.ui.countrySelector.interfaces

import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel

interface CountrySelectorFragmentPresenter {
    fun onCountrySelected(countryDataModel: CountryDataModel)
}