package com.agelousis.monthlyfees.main.ui.payments.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.databinding.PaymentRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.payments.models.PersonModel

class PaymentViewHolder(private val binding: PaymentRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(personModel: PersonModel) {
        binding.personModel = personModel
        binding.executePendingBindings()
    }

}