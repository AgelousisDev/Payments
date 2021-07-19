package com.agelousis.payments.main.ui.groupSelector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.GroupSelectionRowLayoutBinding
import com.agelousis.payments.main.ui.groupSelector.interfaces.GroupSelectorFragmentPresenter
import com.agelousis.payments.main.ui.groupSelector.viewHolders.GroupSelectionViewHolder
import com.agelousis.payments.main.ui.payments.models.GroupModel

class GroupsSelectionAdapter(private val groupModelList: List<GroupModel>, private val groupSelectorFragmentPresenter: GroupSelectorFragmentPresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GroupSelectionViewHolder(
            binding = GroupSelectionRowLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? GroupSelectionViewHolder)?.bind(
            groupModel = groupModelList.getOrNull(
                index = position
            ) ?: return,
            presenter = groupSelectorFragmentPresenter
        )
    }

    override fun getItemCount() = groupModelList.size

    fun reloadData() = notifyDataSetChanged()

}