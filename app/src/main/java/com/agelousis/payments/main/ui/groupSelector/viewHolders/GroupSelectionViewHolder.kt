package com.agelousis.payments.main.ui.groupSelector.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.GroupSelectionRowLayoutBinding
import com.agelousis.payments.main.ui.groupSelector.interfaces.GroupSelectorFragmentPresenter
import com.agelousis.payments.main.ui.payments.models.GroupModel

class GroupSelectionViewHolder(private val binding: GroupSelectionRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(groupModel: GroupModel, presenter: GroupSelectorFragmentPresenter) {
        binding.groupModel = groupModel
        binding.presenter = presenter
        binding.executePendingBindings()
    }

}