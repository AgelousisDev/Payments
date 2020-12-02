package com.agelousis.payments.main.ui.currencySelector.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.CurrencyRowLayoutBinding
import com.agelousis.payments.main.ui.currencySelector.CurrencyPresenter
import com.agelousis.payments.main.ui.currencySelector.enumerations.CurrencyType

class CurrencyViewHolder(private val binding: CurrencyRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(currencyType: CurrencyType, presenter: CurrencyPresenter) {
        binding.currencyType = currencyType
        binding.presenter = presenter
        binding.executePendingBindings()
    }

}