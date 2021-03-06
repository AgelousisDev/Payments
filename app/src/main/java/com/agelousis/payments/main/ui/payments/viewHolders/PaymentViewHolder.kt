package com.agelousis.payments.main.ui.payments.viewHolders

import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.PaymentRowLayoutBinding
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.presenters.PaymentPresenter
import com.agelousis.payments.R

class PaymentViewHolder(private val binding: PaymentRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(clientModel: ClientModel, presenter: PaymentPresenter) {
        binding.clientModel = clientModel
        binding.adapterPosition = adapterPosition
        binding.presenter = presenter
        itemView.setOnLongClickListener {
            presenter.onPaymentLongPressed(
                paymentIndex = adapterPosition,
                isSelected = !clientModel.isSelected
            )
            true
        }
        binding.entryCustomerNameTextView.isSelected = true
        binding.entryDateTextView.isSelected = true
        itemView.animation = AnimationUtils.loadAnimation(
            itemView.context,
            R.anim.fade_scale_view_holder_animation
        )
        binding.executePendingBindings()
    }

    fun clearAnimation() {
        itemView.clearAnimation()
    }

}