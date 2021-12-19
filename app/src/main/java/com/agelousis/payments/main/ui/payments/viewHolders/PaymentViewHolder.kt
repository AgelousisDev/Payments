package com.agelousis.payments.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.PaymentRowLayoutBinding
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.payments.presenters.PaymentPresenter

class PaymentViewHolder(private val binding: PaymentRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(paymentAmountModel: PaymentAmountModel, presenter: PaymentPresenter) {
        binding.paymentAmountModel = paymentAmountModel
        binding.adapterPosition = adapterPosition
        binding.presenter = presenter
        itemView.setOnLongClickListener {
            presenter.onPaymentLongPressed(
                paymentIndex = adapterPosition,
                isSelected = !paymentAmountModel.isSelected
            )
            true
        }
        binding.singlePaymentProductsTextView.isSelected = true
        binding.notesTextView.isSelected = true
        /*itemView.animation = AnimationUtils.loadAnimation(
            itemView.context,
            R.anim.fade_scale_view_holder_animation
        )*/
        binding.executePendingBindings()
    }

    /*fun clearAnimation() {
        itemView.clearAnimation()
    }*/

}