package com.agelousis.monthlyfees.main.ui.files.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.databinding.HeaderRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.files.models.HeaderModel

class HeaderViewHolder(private val binding: HeaderRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(headerModel: HeaderModel) {
        binding.headerModel = headerModel
        binding.executePendingBindings()
    }

}