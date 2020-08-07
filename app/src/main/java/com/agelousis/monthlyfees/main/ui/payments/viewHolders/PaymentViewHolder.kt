package com.agelousis.monthlyfees.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.databinding.PaymentRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.payments.models.PaymentModel

class PaymentViewHolder(private val binding: PaymentRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(paymentModel: PaymentModel) {
        binding.paymentModel = paymentModel
        binding.executePendingBindings()
    }

}