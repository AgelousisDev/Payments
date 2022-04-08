package com.agelousis.payments.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.PaymentAmountSumLayoutBinding
import com.agelousis.payments.main.ui.payments.models.PaymentAmountSumModel
import com.agelousis.payments.main.ui.payments.presenters.PaymentAmountSumPresenter

class PaymentAmountSumViewHolder(private val binding: PaymentAmountSumLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(paymentAmountSumModel: PaymentAmountSumModel, presenter: PaymentAmountSumPresenter) {
        binding.paymentAmountSumModel = paymentAmountSumModel
        binding.presenter = presenter
        binding.executePendingBindings()
    }

}