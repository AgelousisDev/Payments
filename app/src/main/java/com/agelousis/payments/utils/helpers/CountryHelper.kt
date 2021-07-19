package com.agelousis.payments.utils.helpers

import android.content.Context
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel
import com.agelousis.payments.utils.extensions.second
import java.util.*

object CountryHelper {

    val countryDataModelList by lazy {
        // A collection to store our country object
        val countries = arrayListOf<CountryDataModel>()

        // Get ISO countries, create Country object and
        // store in the collection.
        val isoCountries = Locale.getISOCountries()

        for (country in isoCountries) {
            val locale = Locale("en", country)
            val iso = locale.isO3Country
            val code = locale.country
            val name = locale.displayCountry

            if (iso.isNotEmpty() && code.isNotEmpty() && name.isNotEmpty()) {
                countries.add(
                    CountryDataModel(
                        countryName = name,
                        countryCode = code,
                        countryIso = iso
                    )
                )
            }
        }

        // Sort the country by their name and then display the content
        // of countries collection object.
        countries
    }

    fun getCountryCode(context: Context): String? {
        return context.resources.getStringArray(R.array.CountryCodes)
            .firstOrNull { it.contains(context.resources.configuration.locales[0].country ?: "") }?.split(",")?.second()
    }


}