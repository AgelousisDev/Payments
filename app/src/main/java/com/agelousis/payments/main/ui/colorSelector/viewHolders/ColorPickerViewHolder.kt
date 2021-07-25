package com.agelousis.payments.main.ui.colorSelector.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.ColorPickerRowLayoutBinding
import com.agelousis.payments.main.ui.colorSelector.models.ColorDataModel
import com.agelousis.payments.main.ui.colorSelector.presenters.ColorSelectorPresenter

class ColorPickerViewHolder(private val binding: ColorPickerRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(colorDataModel: ColorDataModel, colorSelectorPresenter: ColorSelectorPresenter) {
        binding.colorDataModel = colorDataModel
        binding.presenter = colorSelectorPresenter
        binding.executePendingBindings()
    }

}