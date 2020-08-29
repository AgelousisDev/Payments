package com.agelousis.monthlyfees.main.ui.personalInformation.viewHolders

import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.databinding.OptionTypeRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.personalInformation.presenter.OptionPresenter
import com.agelousis.monthlyfees.main.ui.personalInformation.models.OptionType

class OptionViewHolder(private val binding: OptionTypeRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(optionType: OptionType, optionPresenter: OptionPresenter) {
        binding.optionType = optionType
        binding.presenter = optionPresenter
        binding.optionField.doAfterTextChanged {
            when(binding.optionType) {
                OptionType.CHANGE_USERNAME ->
                    optionPresenter.onUsernameChange(
                        newUsername = it?.toString() ?: return@doAfterTextChanged
                    )
                OptionType.CHANGE_PASSWORD ->
                    optionPresenter.onPasswordChange(
                        newPassword = it?.toString() ?: return@doAfterTextChanged
                    )
                else -> {}
            }
        }
        binding.executePendingBindings()
    }

}