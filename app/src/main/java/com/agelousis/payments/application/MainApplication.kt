package com.agelousis.payments.application

import android.app.Application
import android.content.Context
import com.agelousis.payments.main.ui.countrySelector.enumerations.CountryDataModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.countryDataModel
import com.agelousis.payments.utils.extensions.currencySymbol
import java.util.*

class MainApplication: Application() {

    companion object {
        var currencySymbol: String? = null
        var countryDataModel: CountryDataModel? = null
    }

    private val sharedPreferences by lazy { getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }

    override fun onCreate() {
        super.onCreate()
        setLocaleCurrency()
    }

    private fun setLocaleCurrency() {
        currencySymbol = sharedPreferences.currencySymbol ?: Currency.getInstance(Locale.getDefault()).symbol
        countryDataModel = sharedPreferences.countryDataModel
    }

}