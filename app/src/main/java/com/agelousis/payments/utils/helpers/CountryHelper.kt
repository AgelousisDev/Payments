package com.agelousis.payments.utils.helpers

import android.content.Context
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel
import com.agelousis.payments.utils.extensions.second
import java.util.*

object CountryHelper {

    private const val COUNTRY_FLAG_URL = "https://flagpedia.net/data/flags/normal/%s.png"

    fun getCountryDataModelList(context: Context): List<CountryDataModel> {
        // A collection to store our country object
        val countries = arrayListOf<CountryDataModel>()

        // Get ISO countries, create Country object and
        // store in the collection.
        val isoCountries = Locale.getISOCountries()

        for (country in isoCountries) {
            val locale = Locale(Locale.ENGLISH.displayLanguage, country)
            val iso = locale.isO3Country
            val code = locale.country
            val name = locale.displayCountry
            val countryZipCode = context.resources.getStringArray(R.array.CountryCodes).firstOrNull {
                it.split(",").second().lowercase() == code.lowercase()
            }?.split(",")?.firstOrNull()

            if (iso.isNotEmpty() && code.isNotEmpty() && name.isNotEmpty() && countryZipCode?.isNotEmpty() == true)
                countries.add(
                    CountryDataModel(
                        countryName = name,
                        countryCode = code,
                        countryIso = iso,
                        countryZipCode = String.format(
                            "+%s",
                            countryZipCode
                        ),
                        countryFlagUrl = String.format(
                            COUNTRY_FLAG_URL,
                            code.lowercase()
                        )
                    )
                )
        }
        // Sort the country by their name and then display the content
        // of countries collection object.
        return countries
    }

    infix fun getCurrentCountryData(context: Context) =
        CountryDataModel(
            countryName = context.resources.configuration.locales[0].displayCountry,
            countryCode = context.resources.configuration.locales[0].country,
            countryIso = context.resources.configuration.locales[0].isO3Country,
            countryZipCode = context.resources.getStringArray(R.array.CountryCodes).firstOrNull {
                it.split(",").second().lowercase() == context.resources.configuration.locales[0].country.lowercase()
            }?.split(",")?.firstOrNull(),
            countryFlagUrl = String.format(
                COUNTRY_FLAG_URL,
                context.resources.configuration.locales[0].country.lowercase()
            )
        )

    fun getCountryDataFromZipCode(context: Context, zipCode: String?) =
        getCountryDataModelList(
            context = context
        ).firstOrNull { countryDataModel ->
            countryDataModel.countryZipCode == zipCode
        }

    fun getCountryCode(context: Context): String? {
        return context.resources.getStringArray(R.array.CountryCodes)
            .firstOrNull { it.contains(context.resources.configuration.locales[0].country ?: "") }?.split(",")?.second()
    }


}