package com.agelousis.payments.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.PaymentAmountSumLayoutBinding
import com.agelousis.payments.main.ui.payments.models.PaymentAmountSumModel

class PaymentAmountSumViewHolder(private val binding: PaymentAmountSumLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(paymentAmountSumModel: PaymentAmountSumModel) {
        binding.paymentAmountSumModel = paymentAmountSumModel
        binding.executePendingBindings()
    }

}