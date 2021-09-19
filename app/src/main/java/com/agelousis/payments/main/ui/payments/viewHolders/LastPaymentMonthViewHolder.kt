package com.agelousis.payments.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.LastPaymentMonthRowLayoutBinding
import com.agelousis.payments.main.ui.payments.models.LastPaymentMonthDataModel

class LastPaymentMonthViewHolder(private val binding: LastPaymentMonthRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(lastPaymentMonthDataModel: LastPaymentMonthDataModel) {
        binding.lastPaymentMonthDataModel = lastPaymentMonthDataModel
        binding.executePendingBindings()
    }

}