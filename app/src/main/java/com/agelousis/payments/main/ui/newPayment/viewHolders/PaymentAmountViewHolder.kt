package com.agelousis.payments.main.ui.newPayment.viewHolders

import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.R
import com.agelousis.payments.databinding.PaymentAmountRowLayoutBinding
import com.agelousis.payments.main.ui.newPayment.presenters.NewPaymentPresenter
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.utils.extensions.inPixel

class PaymentAmountViewHolder(private val binding: PaymentAmountRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(paymentAmountModel: PaymentAmountModel, vat: Int?, title: String?, presenter: NewPaymentPresenter) {
        binding.paymentAmountModel = paymentAmountModel
        binding.vat = vat
        binding.title = title
        binding.presenter = presenter
        configureBottomConstraints()
        binding.executePendingBindings()
    }

    private fun configureBottomConstraints() {
        if (binding.paymentAmountModel?.paymentNote.isNullOrEmpty()) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.constraintLayout)
            constraintSet.connect(
                binding.paymentAmountMonthLabel.id,
                ConstraintSet.BOTTOM,
                binding.constraintLayout.id,
                ConstraintSet.BOTTOM,
                8.inPixel.toInt()
            )
            constraintSet.applyTo(binding.constraintLayout)
        }
    }

}