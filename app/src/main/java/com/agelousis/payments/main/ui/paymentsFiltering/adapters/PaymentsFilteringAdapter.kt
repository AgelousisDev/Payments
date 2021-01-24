package com.agelousis.payments.main.ui.paymentsFiltering.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.PaymentsFilteringOptionRowLayoutBinding
import com.agelousis.payments.main.ui.paymentsFiltering.enumerations.PaymentsFilteringOptionType
import com.agelousis.payments.main.ui.paymentsFiltering.viewHolders.PaymentsFilteringOptionViewHolder

class PaymentsFilteringAdapter(private val paymentsFilteringOptionTypeList: List<PaymentsFilteringOptionType>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PaymentsFilteringOptionViewHolder(
            binding = PaymentsFilteringOptionRowLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? PaymentsFilteringOptionViewHolder)?.bind(
            paymentsFilteringOptionType = paymentsFilteringOptionTypeList.getOrNull(
                index = position
            ) ?: return
        )
    }

    override fun getItemCount() = paymentsFilteringOptionTypeList.size

    fun reloadData() = notifyDataSetChanged()

}