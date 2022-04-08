package com.agelousis.payments.main.ui.clientsSelector.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.ClientSelectorRowLayoutBinding
import com.agelousis.payments.main.ui.clientsSelector.presenters.ClientSelectorPresenter
import com.agelousis.payments.main.ui.payments.models.ClientModel

class ClientSelectorViewHolder(private val binding: ClientSelectorRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(clientModel: ClientModel, clientSelectorPresenter: ClientSelectorPresenter) {
        binding.clientModel = clientModel
        binding.materialCheckBox.setOnCheckedChangeListener { _, isChecked ->
            clientSelectorPresenter.onClientSelected(
                adapterPosition = bindingAdapterPosition,
                isSelected = isChecked
            )
        }
        binding.constraintLayout.setOnClickListener {
            binding.materialCheckBox.isChecked = !binding.materialCheckBox.isChecked
        }
        binding.executePendingBindings()
    }

}