package com.agelousis.payments.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.GroupRowLayoutBinding
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.presenters.GroupPresenter

class GroupViewHolder(private val binding: GroupRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(groupModel: GroupModel, presenter: GroupPresenter) {
        binding.groupModel = groupModel
        binding.presenter = presenter
        itemView.setOnLongClickListener {
            presenter.onGroupLongPressed(
                groupModel = binding.groupModel ?: return@setOnLongClickListener true
            )
            true
        }
        binding.executePendingBindings()
    }

}