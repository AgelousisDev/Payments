package com.agelousis.monthlyfees.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.databinding.GroupRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel

class GroupViewHolder(private val binding: GroupRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(groupModel: GroupModel) {
        binding.groupModel = groupModel
        binding.executePendingBindings()
    }

}