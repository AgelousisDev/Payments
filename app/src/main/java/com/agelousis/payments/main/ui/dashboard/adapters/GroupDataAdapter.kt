package com.agelousis.payments.main.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.GroupDataRowLayoutBinding
import com.agelousis.payments.main.ui.dashboard.viewHolders.GroupDataViewHolder
import com.agelousis.payments.main.ui.payments.models.GroupModel

class GroupDataAdapter(private val groupModelList: List<GroupModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GroupDataViewHolder(
            binding = GroupDataRowLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? GroupDataViewHolder)?.bind(
            groupModel = groupModelList.getOrNull(
                index = position
            ) ?: return
        )
    }

    override fun getItemCount() = groupModelList.size


}