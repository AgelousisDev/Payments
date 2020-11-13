package com.agelousis.payments.main.ui.personalInformation.extensions

import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.BindingAdapter
import com.agelousis.payments.R
import com.agelousis.payments.main.ui.personalInformation.models.OptionType

@BindingAdapter("personalInformation")
fun setPersonalInformation(appCompatEditText: AppCompatEditText, optionType: OptionType?) {
    optionType?.let {
        appCompatEditText.setText(when(it) {
            OptionType.CHANGE_FIRST_NAME ->
                it.userModel?.firstName
                    ?: appCompatEditText.context.resources.getString(R.string.key_empty_field_label)
            OptionType.CHANGE_LAST_NAME ->
                it.userModel?.lastName
                    ?: appCompatEditText.context.resources.getString(R.string.key_empty_field_label)
            OptionType.CHANGE_USERNAME ->
                it.userModel?.username
                    ?: appCompatEditText.context.resources.getString(R.string.key_empty_field_label)
            OptionType.CHANGE_PASSWORD ->
                it.userModel?.password
                    ?: appCompatEditText.context.resources.getString(R.string.key_empty_field_label)
            OptionType.CHANGE_ADDRESS ->
                it.userModel?.address
                    ?: appCompatEditText.context.resources.getString(R.string.key_empty_field_label)
            OptionType.CHANGE_ID_CARD_NUMBER ->
                it.userModel?.idCardNumber
                    ?: appCompatEditText.context.resources.getString(R.string.key_empty_field_label)
            OptionType.CHANGE_SOCIAL_INSURANCE_NUMBER ->
                it.userModel?.socialInsuranceNumber
                    ?: appCompatEditText.context.resources.getString(R.string.key_empty_field_label)
            OptionType.VAT ->
                it.userModel?.vat?.toString()
                    ?: "0"
            OptionType.DEFAULT_PAYMENT_AMOUNT ->
                it.userModel?.defaultPaymentAmount?.toString()
                    ?: "0"
            OptionType.DEFAULT_MESSAGE_TEMPLATE ->
                it.userModel?.defaultMessageTemplate
                    ?: appCompatEditText.context.resources.getString(R.string.key_empty_field_label)
            else -> null
        })
    }
}