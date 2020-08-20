package com.agelousis.monthlyfees.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.databinding.PaymentRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.payments.models.PersonModel
import com.agelousis.monthlyfees.main.ui.payments.presenters.PaymentPresenter

class PaymentViewHolder(private val binding: PaymentRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(personModel: PersonModel, presenter: PaymentPresenter) {
        binding.personModel = personModel
        binding.presenter = presenter
        binding.executePendingBindings()
    }

}