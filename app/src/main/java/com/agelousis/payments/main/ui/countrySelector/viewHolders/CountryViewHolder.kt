package com.agelousis.payments.main.ui.countrySelector.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.CountryRowLayoutBinding
import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel
import com.agelousis.payments.main.ui.countrySelector.interfaces.CountrySelectorFragmentPresenter

class CountryViewHolder(private val binding: CountryRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(countryDataModel: CountryDataModel, selectedCountryDataIndex: Int?, countrySelectorFragmentPresenter: CountrySelectorFragmentPresenter) {
        binding.countryDataModel = countryDataModel
        binding.isSelected = selectedCountryDataIndex == adapterPosition
        binding.presenter = countrySelectorFragmentPresenter
        binding.executePendingBindings()
    }

}