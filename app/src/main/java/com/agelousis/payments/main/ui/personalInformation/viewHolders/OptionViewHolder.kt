package com.agelousis.payments.main.ui.personalInformation.viewHolders

import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.OptionTypeRowLayoutBinding
import com.agelousis.payments.main.ui.personalInformation.presenter.OptionPresenter
import com.agelousis.payments.main.ui.personalInformation.models.OptionType

class OptionViewHolder(private val binding: OptionTypeRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(optionType: OptionType, optionPresenter: OptionPresenter) {
        binding.optionType = optionType
        binding.presenter = optionPresenter
        binding.optionField.doAfterTextChanged {
            when(binding.optionType) {
                OptionType.CHANGE_FIRST_NAME ->
                    optionPresenter.onFirstNameChange(
                        newFirstName = it?.toString() ?: return@doAfterTextChanged
                    )
                OptionType.CHANGE_LAST_NAME ->
                    optionPresenter.onLastNameChange(
                        newLastName = it?.toString() ?: return@doAfterTextChanged
                    )
                OptionType.CHANGE_USERNAME ->
                    optionPresenter.onUsernameChange(
                        newUsername = it?.toString() ?: return@doAfterTextChanged
                    )
                OptionType.CHANGE_PASSWORD ->
                    optionPresenter.onPasswordChange(
                        newPassword = it?.toString() ?: return@doAfterTextChanged
                    )
                OptionType.CHANGE_ADDRESS ->
                    optionPresenter.onAddressChange(
                        newAddress = it?.toString() ?: return@doAfterTextChanged
                    )
                OptionType.CHANGE_ID_CARD_NUMBER ->
                    optionPresenter.onIdCardNumberChange(
                        newIdCardNumber = it?.toString() ?: return@doAfterTextChanged
                    )
                OptionType.CHANGE_SOCIAL_INSURANCE_NUMBER ->
                    optionPresenter.onSocialInsuranceNumberChange(
                        newSocialInsuranceNumber = it?.toString() ?: return@doAfterTextChanged
                    )
                OptionType.VAT ->
                    optionPresenter.onVatChange(
                        newVat = it?.toString()?.toIntOrNull() ?: return@doAfterTextChanged
                    )
                else -> {}
            }
        }
        binding.executePendingBindings()
    }

}