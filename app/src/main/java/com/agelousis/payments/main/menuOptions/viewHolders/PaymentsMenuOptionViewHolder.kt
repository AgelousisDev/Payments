package com.agelousis.payments.main.menuOptions.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.PaymentsMenuOptionRowLayoutBinding
import com.agelousis.payments.main.menuOptions.enumerations.PaymentsMenuOptionType
import com.agelousis.payments.main.menuOptions.presenters.PaymentsMenuOptionPresenter

class PaymentsMenuOptionViewHolder(private val binding: PaymentsMenuOptionRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(paymentsMenuOptionType: PaymentsMenuOptionType, presenter: PaymentsMenuOptionPresenter) {
        binding.menuOption = paymentsMenuOptionType
        binding.presenter = presenter
        binding.executePendingBindings()
    }

}