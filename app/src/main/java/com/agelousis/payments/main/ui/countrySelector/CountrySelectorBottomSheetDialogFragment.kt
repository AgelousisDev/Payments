package com.agelousis.payments.main.ui.countrySelector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.databinding.CountrySelectorFragmentLayoutBinding
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.ui.countrySelector.adapters.CountriesAdapter
import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel
import com.agelousis.payments.main.ui.countrySelector.interfaces.CountrySelectorFragmentPresenter
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.applyAnimationOnKeyboard
import com.agelousis.payments.utils.extensions.countryDataModel
import com.agelousis.payments.utils.helpers.CountryHelper
import com.agelousis.payments.views.bottomSheet.BasicBottomSheetDialogFragment
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class CountrySelectorBottomSheetDialogFragment: BasicBottomSheetDialogFragment(), CountrySelectorFragmentPresenter {

    companion object {
        fun show(
            supportFragmentManager: FragmentManager,
            countrySelectorFragmentPresenter: CountrySelectorFragmentPresenter,
            selectedCountryDataModel: CountryDataModel?,
            userModel: UserModel?,
            updateGlobalCountryState: Boolean = true
        ) {
            CountrySelectorBottomSheetDialogFragment().also {
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
        } ?: listOf()
    }
    private var countrySelectorFragmentPresenter: CountrySelectorFragmentPresenter? = null
    private var selectedCountryDataModel: CountryDataModel? = null
    private var userModel: UserModel? = null
    private var updateGlobalCountryState = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = CountrySelectorFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.userModel = userModel
            it.dismissBlock = this::dismiss
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.applyAnimationOnKeyboard()
        configureRecyclerView()
        setupUI()
    }

    private fun configureRecyclerView() {
        binding.countryRecyclerView.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW).also {
            it.flexDirection = FlexDirection.ROW
            it.justifyContent = JustifyContent.CENTER
            it.alignItems = AlignItems.CENTER
        }
        binding.countryRecyclerView.adapter = CountriesAdapter(
            itemList = countryDataModelList,
            selectedCountryDataIndex = selectedCountryDataModel?.let { countryDataModel ->
                countryDataModelList.indexOf(
                    countryDataModel
                ).takeIf {
                    it != -1
                }
            },
            countrySelectorFragmentPresenter = this
        )
        binding.countryRecyclerView.post {
            binding.countryRecyclerView.scrollToPosition(
                countryDataModelList.indexOf(
                    selectedCountryDataModel ?: return@post
                )
            )
        }
    }

    private fun setupUI() {
        binding.searchLayout.onQueryListener { query ->
            this scrollToSelectedCountryWith query
        }
    }

    private infix fun scrollToSelectedCountryWith(
        query: String?
    ) {
        binding.countryRecyclerView.smoothScrollToPosition(
            countryDataModelList.indexOfFirst { countryDataModel ->
                (query?.lowercase() ?: "") in countryDataModel.countryName.lowercase()
            }.takeIf {
                it > -1
            } ?: return
        )
    }

}