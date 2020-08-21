package com.agelousis.monthlyfees.main.ui.newPayment.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.databinding.PaymentAmountRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.newPayment.presenters.NewPaymentPresenter
import com.agelousis.monthlyfees.main.ui.payments.models.PaymentAmountModel

class PaymentAmountViewHolder(private val binding: PaymentAmountRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(paymentAmountModel: PaymentAmountModel, presenter: NewPaymentPresenter) {
        binding.paymentAmountModel = paymentAmountModel
        binding.presenter = presenter
        itemView.setOnLongClickListener {
            presenter.onPaymentAmountLongPressed(
                paymentAmountModel = binding.paymentAmountModel
            )
            true
        }
        binding.executePendingBindings()
    }

}