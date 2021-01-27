package com.agelousis.payments.main.ui.paymentsFiltering.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.PaymentsFilteringOptionRowLayoutBinding
import com.agelousis.payments.main.ui.paymentsFiltering.enumerations.PaymentsFilteringOptionType

class PaymentsFilteringOptionViewHolder(private val binding: PaymentsFilteringOptionRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(paymentsFilteringOptionType: PaymentsFilteringOptionType) {
        binding.paymentsFilteringOptionType = paymentsFilteringOptionType
        binding.executePendingBindings()
    }

}