package com.agelousis.payments.main.ui.dashboard.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.GroupDataRowLayoutBinding
import com.agelousis.payments.main.ui.payments.models.GroupModel

class GroupDataViewHolder(private val binding: GroupDataRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(groupModel: GroupModel) {
        binding.groupModel = groupModel
        binding.groupNameView.isSelected = true
        binding.executePendingBindings()
    }

}