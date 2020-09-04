package com.agelousis.payments.main.ui.newPayment.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.PaymentAmountRowLayoutBinding
import com.agelousis.payments.main.ui.newPayment.presenters.NewPaymentPresenter
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel

class PaymentAmountViewHolder(private val binding: PaymentAmountRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(paymentAmountModel: PaymentAmountModel, presenter: NewPaymentPresenter) {
        binding.paymentAmountModel = paymentAmountModel
        binding.presenter = presenter
        itemView.setOnLongClickListener {
            presenter.onPaymentAmountLongPressed(
                adapterPosition = adapterPosition
            )
            true
        }
        binding.executePendingBindings()
    }

}