package com.agelousis.payments.userSelection.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.UserRowLayoutBinding
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.userSelection.presenters.UserSelectionPresenter

class UserViewHolder(private val binding: UserRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(userModel: UserModel, presenter: UserSelectionPresenter) {
        binding.userModel = userModel
        binding.presenter = presenter
        binding.executePendingBindings()
    }

}