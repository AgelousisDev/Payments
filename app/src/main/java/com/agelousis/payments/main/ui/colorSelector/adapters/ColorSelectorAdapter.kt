package com.agelousis.payments.main.ui.colorSelector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.ColorPickerRowLayoutBinding
import com.agelousis.payments.main.ui.colorSelector.models.ColorDataModel
import com.agelousis.payments.main.ui.colorSelector.presenters.ColorSelectorPresenter
import com.agelousis.payments.main.ui.colorSelector.viewHolders.ColorPickerViewHolder

class ColorSelectorAdapter(private val colorDataModelList: List<ColorDataModel>, private val colorSelectorPresenter: ColorSelectorPresenter):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ColorPickerViewHolder(
            binding = ColorPickerRowLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ColorPickerViewHolder)?.bind(
            colorDataModel = colorDataModelList.getOrNull(
                index = position
            ) ?: return,
            colorSelectorPresenter = colorSelectorPresenter
        )
    }

    override fun getItemCount() = colorDataModelList.size

}