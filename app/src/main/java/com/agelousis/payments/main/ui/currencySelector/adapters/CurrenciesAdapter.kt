package com.agelousis.payments.main.ui.currencySelector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.CurrencyRowLayoutBinding
import com.agelousis.payments.main.ui.currencySelector.CurrencyPresenter
import com.agelousis.payments.main.ui.currencySelector.enumerations.CurrencyType
import com.agelousis.payments.main.ui.currencySelector.viewHolders.CurrencyViewHolder

class CurrenciesAdapter(private val currencyTypes: List<CurrencyType>, private val presenter: CurrencyPresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CurrencyViewHolder(
            binding = CurrencyRowLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? CurrencyViewHolder)?.bind(
            currencyType = currencyTypes.getOrNull(index = position) ?: return,
            presenter = presenter
        )
    }

    override fun getItemCount() = currencyTypes.size

}