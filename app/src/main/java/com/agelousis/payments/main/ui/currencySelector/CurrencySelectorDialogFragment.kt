package com.agelousis.payments.main.ui.currencySelector

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.main.ui.currencySelector.enumerations.CurrencyType
import com.agelousis.payments.main.ui.currencySelector.interfaces.CurrencySelectorFragmentPresenter
import com.agelousis.payments.main.ui.currencySelector.ui.CurrencySelectorLayout
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.appColors
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.currencySymbol

class CurrencySelectorDialogFragment: DialogFragment(), CurrencySelectorFragmentPresenter {

    companion object {
        fun show(
            supportFragmentManager: FragmentManager,
            currencySelectorFragmentPresenter: CurrencySelectorFragmentPresenter
        ) {
            CurrencySelectorDialogFragment().also {
                it.currencySelectorFragmentPresenter = currencySelectorFragmentPresenter
            }.show(
                supportFragmentManager,
                Constants.CURRENCY_SELECTOR_FRAGMENT_TAG
            )
        }
    }

    override fun onCurrencySelected(currencyType: CurrencyType) {
        sharedPreferences?.currencySymbol = currencyType.symbol
        MainApplication.currencySymbol = currencyType.symbol
        currencySelectorFragmentPresenter?.onCurrencySelected(
            currencyType = currencyType
        )
        dismiss()
    }

    private var currencySelectorFragmentPresenter: CurrencySelectorFragmentPresenter? = null
    private val sharedPreferences by lazy { context?.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }
    private val currencyTypes by lazy {
        CurrencyType.values().toList().also { currencyTypes ->
            currencyTypes.firstOrNull { currencyType ->
                currencyType.symbol == sharedPreferences?.currencySymbol
            }?.isSelected = true
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
            it.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(
                    typography = Typography,
                    colors = appColors()
                ) {
                    CurrencySelectorLayout(
                        currencyTypes = currencyTypes,
                        currencyPresenter = this@CurrencySelectorDialogFragment
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    fun CurrencySelectorDialogFragmentComposablePreview() {
        CurrencySelectorLayout(
            currencyTypes = currencyTypes,
            currencyPresenter = this@CurrencySelectorDialogFragment
        )
    }

}