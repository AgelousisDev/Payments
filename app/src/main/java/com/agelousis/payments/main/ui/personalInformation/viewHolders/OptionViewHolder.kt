package com.agelousis.payments.main.ui.personalInformation.viewHolders

import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.OptionTypeRowLayoutBinding
import com.agelousis.payments.main.ui.personalInformation.models.OptionType
import com.agelousis.payments.main.ui.personalInformation.presenter.OptionPresenter

class OptionViewHolder(private val binding: OptionTypeRowLayoutBinding): RecyclerView.ViewHolder(
    binding.root
) {

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
                OptionType.DEFAULT_PAYMENT_AMOUNT ->
                    optionPresenter.onPaymentAmountChange(
                        newPaymentAmount = it?.toString()?.toDoubleOrNull()
                            ?: return@doAfterTextChanged
                    )
                else -> {}
            }
        }
        binding.footerOptionField.doAfterTextChanged {
            when(binding.optionType) {
                OptionType.DEFAULT_MESSAGE_TEMPLATE ->
                    optionPresenter.onMessageTemplateChange(
                        newMessageTemplate = it?.toString() ?: return@doAfterTextChanged
                    )
                else -> {}
            }
        }
        configureConstraints()
        binding.executePendingBindings()
    }

    private fun configureConstraints() {
        val set = ConstraintSet()
        set.clone(binding.constraintLayout)
        if (binding.optionType == OptionType.DEFAULT_MESSAGE_TEMPLATE) {
            set.clear(binding.materialTextView.id, ConstraintSet.BOTTOM)
            set.applyTo(binding.constraintLayout)
        }
        else {
            set.connect(binding.materialTextView.id, ConstraintSet.BOTTOM, binding.constraintLayout.id, ConstraintSet.BOTTOM, 0)
            set.applyTo(binding.constraintLayout)
        }
    }

}