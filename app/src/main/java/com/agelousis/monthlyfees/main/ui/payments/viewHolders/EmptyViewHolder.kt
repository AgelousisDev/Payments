package com.agelousis.monthlyfees.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.databinding.EmptyRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.payments.models.EmptyModel

class EmptyViewHolder(private val binding: EmptyRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(emptyModel: EmptyModel) {
        binding.emptyModel = emptyModel
        binding.executePendingBindings()
    }

}