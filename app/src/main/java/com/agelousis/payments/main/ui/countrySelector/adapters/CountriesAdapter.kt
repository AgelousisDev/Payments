package com.agelousis.payments.main.ui.countrySelector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.CountryRowLayoutBinding
import com.agelousis.payments.databinding.EmptyRowLayoutBinding
import com.agelousis.payments.main.ui.countrySelector.enumerations.SelectorAdapterViewType
import com.agelousis.payments.main.ui.countrySelector.models.CountryDataModel
import com.agelousis.payments.main.ui.countrySelector.interfaces.CountrySelectorFragmentPresenter
import com.agelousis.payments.main.ui.countrySelector.viewHolders.CountryViewHolder
import com.agelousis.payments.main.ui.payments.models.EmptyModel
import com.agelousis.payments.main.ui.payments.viewHolders.EmptyViewHolder

class CountriesAdapter(
        private val itemList: List<Any>,
        private val selectedCountryDataIndex: Int?,
        private val countrySelectorFragmentPresenter: CountrySelectorFragmentPresenter
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                CountryViewHolder(
                    binding = CountryRowLayoutBinding.inflate(
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
        (holder as? CountryViewHolder)?.bind(
            countryDataModel = itemList.getOrNull(
                index = position
            ) as? CountryDataModel ?: return,
            selectedCountryDataIndex = selectedCountryDataIndex,
            countrySelectorFragmentPresenter = countrySelectorFragmentPresenter
        )
    }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int): Int {
        (itemList.getOrNull(index = position) as? EmptyModel)?.let { return SelectorAdapterViewType.EMPTY_VIEW.type }
        (itemList.getOrNull(index = position) as? CountryDataModel)?.let { return SelectorAdapterViewType.ITEM_VIEW.type }
        return super.getItemViewType(position)
    }

    fun reloadData() = notifyDataSetChanged()
}