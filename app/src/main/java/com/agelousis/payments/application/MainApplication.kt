package com.agelousis.payments.application

import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.agelousis.payments.database.DBManager
import com.agelousis.payments.database.SQLiteHelper
import com.agelousis.payments.firebase.FirebaseInstanceHelper
import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel
import com.agelousis.payments.main.ui.paymentsFiltering.enumerations.PaymentsFilteringOptionType
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.countryDataModel
import com.agelousis.payments.utils.extensions.currencySymbol
import com.agelousis.payments.utils.extensions.paymentsFilteringOptionTypes
import com.agelousis.payments.utils.helpers.CountryHelper
import com.google.android.material.color.DynamicColors
import java.util.*
import kotlin.collections.ArrayList

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
        configureLifecycleEvents()
        DynamicColors.applyToActivitiesIfAvailable(this)
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
                    it.position = it.ordinal
                },
                PaymentsFilteringOptionType.CHARGE.also {
                    it.position = it.ordinal
                },
                PaymentsFilteringOptionType.EXPIRED.also {
                    it.position = it.ordinal
                },
                PaymentsFilteringOptionType.SINGLE_PAYMENT.also {
                    it.position = it.ordinal
                },
                PaymentsFilteringOptionType.INACTIVE.also {
                    it.position = it.ordinal
                }
            )
            sharedPreferences.paymentsFilteringOptionTypes = paymentsFilteringOptionTypes
        }
        else {
            val paymentsFilteringOptionTypes = ArrayList(sharedPreferences.paymentsFilteringOptionTypes ?: listOf())
            if (PaymentsFilteringOptionType.INACTIVE !in paymentsFilteringOptionTypes) {
                paymentsFilteringOptionTypes.add(
                    PaymentsFilteringOptionType.INACTIVE
                )
                sharedPreferences.paymentsFilteringOptionTypes = paymentsFilteringOptionTypes
            }
            paymentsFilteringOptionTypes.forEachIndexed { index, paymentsFilteringOptionType ->
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

    private fun initializeDBManager() {
        DBManager.sqLiteHelper = SQLiteHelper(
            context = applicationContext
        )
        DBManager.database = DBManager.sqLiteHelper?.writableDatabase
    }

    private fun configureLifecycleEvents() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object: DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    super.onStop(owner)
                    DBManager.close()
                }

                override fun onStart(owner: LifecycleOwner) {
                    super.onStart(owner)
                    initializeDBManager()
                }
            }
        )
    }

}