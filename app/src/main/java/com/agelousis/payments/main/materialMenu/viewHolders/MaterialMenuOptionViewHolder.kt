package com.agelousis.payments.main.materialMenu.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.MaterialMenuOptionRowLayoutBinding
import com.agelousis.payments.main.materialMenu.enumerations.MaterialMenuOption
import com.agelousis.payments.main.materialMenu.presenters.MaterialMenuFragmentPresenter

class MaterialMenuOptionViewHolder(private val binding: MaterialMenuOptionRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(materialMenuOption: MaterialMenuOption, materialMenuFragmentPresenter: MaterialMenuFragmentPresenter) {
        binding.materialMenuOption = materialMenuOption
        binding.presenter = materialMenuFragmentPresenter
        binding.executePendingBindings()
    }

}