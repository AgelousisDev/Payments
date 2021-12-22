package com.agelousis.payments.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.ClientRowLayoutBinding
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.presenters.ClientPresenter

class ClientViewHolder(private val binding: ClientRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(clientModel: ClientModel, presenter: ClientPresenter) {
        binding.clientModel = clientModel
        binding.adapterPosition = adapterPosition
        binding.presenter = presenter
        itemView.setOnLongClickListener {
            presenter.onClientLongPressed(
                paymentIndex = adapterPosition,
                isSelected = !clientModel.isSelected
            )
            true
        }
        binding.entryCustomerNameTextView.isSelected = true
        binding.entryDateTextView.isSelected = true
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