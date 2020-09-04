package com.agelousis.payments.main.ui.personalInformation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.OptionTypeRowLayoutBinding
import com.agelousis.payments.main.ui.personalInformation.presenter.OptionPresenter
import com.agelousis.payments.main.ui.personalInformation.models.OptionType
import com.agelousis.payments.main.ui.personalInformation.viewHolders.OptionViewHolder

class OptionTypesAdapter(private val optionTypes: List<OptionType>, private val optionPresenter: OptionPresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OptionViewHolder(
            binding = OptionTypeRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = optionTypes.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? OptionViewHolder)?.bind(
            optionType = optionTypes.getOrNull(
                index = position
            ) ?: return,
            optionPresenter = optionPresenter
        )
    }

    fun reloadData() = notifyDataSetChanged()

}