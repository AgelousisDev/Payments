package com.agelousis.payments.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.EmptyRowLayoutBinding
import com.agelousis.payments.main.ui.payments.models.EmptyModel

class EmptyViewHolder(private val binding: EmptyRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(emptyModel: EmptyModel) {
        binding.emptyModel = emptyModel
        binding.executePendingBindings()
    }

}