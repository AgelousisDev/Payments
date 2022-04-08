package com.agelousis.payments.main.ui.personalInformation.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.OptionActionRowLayoutBinding
import com.agelousis.payments.main.ui.personalInformation.models.OptionType
import com.agelousis.payments.main.ui.personalInformation.presenter.OptionPresenter

class OptionActionViewHolder(private val binding: OptionActionRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(optionType: OptionType, optionPresenter: OptionPresenter) {
        binding.optionType = optionType
        binding.presenter = optionPresenter
        binding.executePendingBindings()
    }

}