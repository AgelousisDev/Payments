package com.agelousis.monthlyfees.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.databinding.GroupRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel
import com.agelousis.monthlyfees.main.ui.payments.presenters.GroupPresenter

class GroupViewHolder(private val binding: GroupRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(groupModel: GroupModel, presenter: GroupPresenter) {
        binding.groupModel = groupModel
        binding.presenter = presenter
        binding.executePendingBindings()
    }

}