package com.agelousis.payments.main.ui.groupSelector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.EmptyRowLayoutBinding
import com.agelousis.payments.databinding.GroupSelectionRowLayoutBinding
import com.agelousis.payments.main.ui.countrySelector.enumerations.SelectorAdapterViewType
import com.agelousis.payments.main.ui.groupSelector.interfaces.GroupSelectorFragmentPresenter
import com.agelousis.payments.main.ui.groupSelector.viewHolders.GroupSelectionViewHolder
import com.agelousis.payments.main.ui.payments.models.EmptyModel
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.viewHolders.EmptyViewHolder

class GroupsSelectionAdapter(private val itemList: List<Any>, private val groupSelectorFragmentPresenter: GroupSelectorFragmentPresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            SelectorAdapterViewType.EMPTY_VIEW.type ->
                EmptyViewHolder(
                    binding = EmptyRowLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
            else ->
                GroupSelectionViewHolder(
                    binding = GroupSelectionRowLayoutBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ),
                        parent,
                        false
                    )
                )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? EmptyViewHolder)?.bind(
            emptyModel = itemList.getOrNull(
                index = position
            ) as? EmptyModel ?: return
        )
        (holder as? GroupSelectionViewHolder)?.bind(
            groupModel = itemList.getOrNull(
                index = position
            ) as? GroupModel ?: return,
            presenter = groupSelectorFragmentPresenter
        )
    }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int): Int {
        (itemList.getOrNull(index = position) as? EmptyModel)?.let { return SelectorAdapterViewType.EMPTY_VIEW.type }
        (itemList.getOrNull(index = position) as? GroupModel)?.let { return SelectorAdapterViewType.ITEM_VIEW.type }
        return super.getItemViewType(position)
    }

    fun reloadData() = notifyDataSetChanged()

}