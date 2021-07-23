package com.agelousis.payments.application

import android.app.Application
import android.content.Context
import com.agelousis.payments.firebase.FirebaseInstanceHelper
import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel
import com.agelousis.payments.main.ui.paymentsFiltering.enumerations.PaymentsFilteringOptionType
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.countryDataModel
import com.agelousis.payments.utils.extensions.currencySymbol
import com.agelousis.payments.utils.extensions.paymentsFilteringOptionTypes
import com.agelousis.payments.utils.helpers.CountryHelper
import java.util.*

class MainApplication: Application() {

    companion object {
        var currencySymbol: String? = null
        var countryDataModel: CountryDataModel? = null
        var paymentsFilteringOptionTypes: List<PaymentsFilteringOptionType>? = null
        var firebaseToken: String? = null
    }

    private val sharedPreferences by lazy { getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }

    override fun onCreate() {
        super.onCreate()
        setLocaleCurrency()
        setPaymentsFilteringOptionTypes()
        initializeFirebase()
    }

    private fun setLocaleCurrency() {
        currencySymbol = sharedPreferences.currencySymbol ?: Currency.getInstance(Locale.getDefault()).symbol
        countryDataModel = sharedPreferences.countryDataModel ?: CountryHelper getCurrentCountryData applicationContext
    }

    private fun setPaymentsFilteringOptionTypes() {
        if (sharedPreferences.paymentsFilteringOptionTypes.isNullOrEmpty()) {
            paymentsFilteringOptionTypes = listOf(
                PaymentsFilteringOptionType.FREE.also {
                    it.position = 0
                },
                PaymentsFilteringOptionType.CHARGE.also {
                    it.position = 1
                },
                PaymentsFilteringOptionType.EXPIRED.also {
                    it.position = 2
                },
                PaymentsFilteringOptionType.SINGLE_PAYMENT.also {
                    it.position = 3
                }
            )
            sharedPreferences.paymentsFilteringOptionTypes = paymentsFilteringOptionTypes
        }
        else {
            val paymentsFilteringOptionTypes = sharedPreferences.paymentsFilteringOptionTypes
            paymentsFilteringOptionTypes?.forEachIndexed { index, paymentsFilteringOptionType ->
                paymentsFilteringOptionType.position = index
            }
            MainApplication.paymentsFilteringOptionTypes = paymentsFilteringOptionTypes
        }
    }

    private fun initializeFirebase() {
        FirebaseInstanceHelper.shared.initializeFirebaseToken {
            firebaseToken = it
        }
    }

}