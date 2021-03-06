package com.agelousis.payments.main.ui.currencySelector

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.databinding.CurrencySelectorFragmentLayoutBinding
import com.agelousis.payments.main.ui.currencySelector.adapters.CurrenciesAdapter
import com.agelousis.payments.main.ui.currencySelector.enumerations.CurrencyType
import com.agelousis.payments.main.ui.currencySelector.interfaces.CurrencySelectorFragmentPresenter
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.currencySymbol
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class CurrencySelectorDialogFragment: DialogFragment(), CurrencyPresenter {

    companion object {
        fun show(supportFragmentManager: FragmentManager, currencySelectorFragmentPresenter: CurrencySelectorFragmentPresenter) {
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

    private lateinit var binding: CurrencySelectorFragmentLayoutBinding
    private var currencySelectorFragmentPresenter: CurrencySelectorFragmentPresenter? = null
    private val sharedPreferences by lazy { context?.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }
    private val currencyTypes by lazy { CurrencyType.values().toList() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
            it.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = CurrencySelectorFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        binding.currencyRecyclerView.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW).also {
            it.flexDirection = FlexDirection.ROW
            it.justifyContent = JustifyContent.CENTER
            it.alignItems = AlignItems.CENTER
        }
        binding.currencyRecyclerView.adapter = CurrenciesAdapter(
            currencyTypes = currencyTypes,
            presenter = this
        )
    }

}