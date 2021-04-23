package com.agelousis.payments.main.ui.personalInformation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.HeaderRowLayoutBinding
import com.agelousis.payments.databinding.OptionActionRowLayoutBinding
import com.agelousis.payments.databinding.OptionTypeRowLayoutBinding
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.main.ui.files.viewHolders.HeaderViewHolder
import com.agelousis.payments.main.ui.personalInformation.enumerations.OptionTypeAdapterType
import com.agelousis.payments.main.ui.personalInformation.presenter.OptionPresenter
import com.agelousis.payments.main.ui.personalInformation.models.OptionType
import com.agelousis.payments.main.ui.personalInformation.viewHolders.OptionActionViewHolder
import com.agelousis.payments.main.ui.personalInformation.viewHolders.OptionViewHolder

class OptionTypesAdapter(private val list: List<Any>, private val optionPresenter: OptionPresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            OptionTypeAdapterType.HEADER_VIEW.type ->
                HeaderViewHolder(
                    binding = HeaderRowLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
            OptionTypeAdapterType.OPTION_VIEW.type ->
                OptionViewHolder(
                    binding = OptionTypeRowLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
            OptionTypeAdapterType.OPTION_ACTION_VIEW.type ->
                OptionActionViewHolder(
                    binding = OptionActionRowLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
            else ->
                HeaderViewHolder(
                    binding = HeaderRowLayoutBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
        }
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        (list.getOrNull(index = position) as? HeaderModel)?.let {
            return OptionTypeAdapterType.HEADER_VIEW.type
        }
        (list.getOrNull(index = position) as? OptionType)?.let {
            return when(it) {
                OptionType.DELETE_USER, OptionType.EXPORT_DATABASE, OptionType.CHANGE_CURRENCY, OptionType.CHANGE_COUNTRY -> OptionTypeAdapterType.OPTION_ACTION_VIEW.type
                else -> OptionTypeAdapterType.OPTION_VIEW.type
            }
        }
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? HeaderViewHolder)?.bind(
            headerModel = list.getOrNull(
                index = position
            ) as? HeaderModel ?: return
        )
        (holder as? OptionViewHolder)?.bind(
            optionType = list.getOrNull(
                index = position
            ) as? OptionType ?: return,
            optionPresenter = optionPresenter
        )
        (holder as? OptionActionViewHolder)?.bind(
            optionType = list.getOrNull(
                index = position
            ) as? OptionType ?: return,
            optionPresenter = optionPresenter
        )
    }

    fun reloadData() = notifyDataSetChanged()

}