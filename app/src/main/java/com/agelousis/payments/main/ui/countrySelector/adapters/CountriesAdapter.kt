package com.agelousis.payments.main.ui.countrySelector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.CountryRowLayoutBinding
import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel
import com.agelousis.payments.main.ui.countrySelector.interfaces.CountrySelectorFragmentPresenter
import com.agelousis.payments.main.ui.countrySelector.viewHolders.CountryViewHolder

class CountriesAdapter(
        private val countryDataModelList: List<CountryDataModel>,
        private val selectedCountryDataIndex: Int?,
        private val countrySelectorFragmentPresenter: CountrySelectorFragmentPresenter
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CountryViewHolder(
            binding = CountryRowLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? CountryViewHolder)?.bind(
            countryDataModel = countryDataModelList.getOrNull(
                index = position
            ) ?: return,
            selectedCountryDataIndex = selectedCountryDataIndex,
            countrySelectorFragmentPresenter = countrySelectorFragmentPresenter
        )
    }

    override fun getItemCount() = countryDataModelList.size

    fun reloadData() = notifyDataSetChanged()
}