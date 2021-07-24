package com.agelousis.payments.main.ui.countrySelector

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
import com.agelousis.payments.databinding.CountrySelectorFragmentLayoutBinding
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.ui.countrySelector.adapters.CountriesAdapter
import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel
import com.agelousis.payments.main.ui.countrySelector.interfaces.CountrySelectorFragmentPresenter
import com.agelousis.payments.main.ui.payments.models.EmptyModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.countryDataModel
import com.agelousis.payments.utils.helpers.CountryHelper
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import java.util.*

class CountrySelectorDialogFragment: DialogFragment(), CountrySelectorFragmentPresenter {

    companion object {
        fun show(
            supportFragmentManager: FragmentManager,
            countrySelectorFragmentPresenter: CountrySelectorFragmentPresenter,
            selectedCountryDataModel: CountryDataModel?,
            userModel: UserModel?,
            updateGlobalCountryState: Boolean = true
        ) {
            CountrySelectorDialogFragment().also {
                it.countrySelectorFragmentPresenter = countrySelectorFragmentPresenter
                it.selectedCountryDataModel = selectedCountryDataModel
                it.userModel = userModel
                it.updateGlobalCountryState = updateGlobalCountryState
            }.show(
                supportFragmentManager,
                Constants.COUNTRY_SELECTOR_FRAGMENT_TAG
            )
        }
    }

    override fun onCountrySelected(countryDataModel: CountryDataModel) {
        if (updateGlobalCountryState) {
            sharedPreferences?.countryDataModel = countryDataModel
            MainApplication.countryDataModel = countryDataModel
        }
        countrySelectorFragmentPresenter?.onCountrySelected(
            countryDataModel = countryDataModel
        )
        dismiss()
    }

    private lateinit var binding: CountrySelectorFragmentLayoutBinding
    private val sharedPreferences by lazy { context?.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }
    private val countryDataModelList by lazy {
        context?.let {
            CountryHelper.getCountryDataModelList(
                context = it
            )
        }
    }
    private val filteredItemList = arrayListOf<Any>()
    private var countrySelectorFragmentPresenter: CountrySelectorFragmentPresenter? = null
    private var selectedCountryDataModel: CountryDataModel? = null
    private var userModel: UserModel? = null
    private var updateGlobalCountryState = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
            it.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = CountrySelectorFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.userModel = userModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        setupUI()
    }

    private fun configureRecyclerView() {
        filteredItemList.addAll(
            countryDataModelList ?: return
        )
        binding.countryRecyclerView.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW).also {
            it.flexDirection = FlexDirection.ROW
            it.justifyContent = JustifyContent.CENTER
            it.alignItems = AlignItems.CENTER
        }
        binding.countryRecyclerView.adapter = CountriesAdapter(
            itemList = filteredItemList,
            selectedCountryDataIndex = selectedCountryDataModel?.let { countryDataModel ->
                filteredItemList.indexOf(
                    countryDataModel
                ).takeIf { it != -1 }
            },
            countrySelectorFragmentPresenter = this
        )
        binding.countryRecyclerView.post {
            binding.countryRecyclerView.scrollToPosition(
                filteredItemList.indexOf(
                    selectedCountryDataModel ?: return@post
                )
            )
        }
    }

    private fun setupUI() {
        binding.searchLayout.onQueryListener { query ->
            filterCountries(
                query = query
            )
        }
    }

    private fun filterCountries(query: String?) {
        filteredItemList.clear()
        filteredItemList.addAll(
            countryDataModelList?.filter { countryDataModel ->
                countryDataModel.countryName.lowercase().contains(query?.lowercase() ?: "")
            } ?: listOf()
        )
        if (filteredItemList.isEmpty())
            filteredItemList.add(
                EmptyModel(
                    message = String.format(
                        resources.getString(R.string.key_no_results_found_value),
                        query ?: ""
                    ),
                    imageIconResource = R.drawable.ic_colored_search
                )
            )
        (binding.countryRecyclerView.adapter as? CountriesAdapter)?.reloadData()
    }

}