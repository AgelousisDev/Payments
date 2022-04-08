package com.agelousis.payments.main.ui.countrySelector.models

data class CountryDataModel(val countryName: String,
                            val countryCode: String,
                            val countryIso: String,
                            val countryZipCode: String?,
                            val countryFlagUrl: String
)