package com.agelousis.payments.main.materialMenu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.MaterialMenuOptionRowLayoutBinding
import com.agelousis.payments.main.materialMenu.enumerations.MaterialMenuOption
import com.agelousis.payments.main.materialMenu.presenters.MaterialMenuFragmentPresenter
import com.agelousis.payments.main.materialMenu.viewHolders.MaterialMenuOptionViewHolder

class MaterialMenuOptionsAdapter(private val materialMenuOptionList: List<MaterialMenuOption>, private val materialMenuFragmentPresenter: MaterialMenuFragmentPresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MaterialMenuOptionViewHolder(
            binding = MaterialMenuOptionRowLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? MaterialMenuOptionViewHolder)?.bind(
            materialMenuOption = materialMenuOptionList.getOrNull(
                index = position
            ) ?: return,
            materialMenuFragmentPresenter = materialMenuFragmentPresenter
        )
    }

    override fun getItemCount() = materialMenuOptionList.size


}