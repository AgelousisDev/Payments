package com.agelousis.payments.main.ui.personalInformation.extensions

import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.BindingAdapter
import com.agelousis.payments.R
import com.agelousis.payments.custom.itemDecoration.DividerItemRecyclerViewDecorator
import com.agelousis.payments.custom.itemDecoration.HeaderItemDecoration
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.main.ui.personalInformation.PersonalInformationFragment
import com.agelousis.payments.main.ui.personalInformation.adapters.OptionTypesAdapter
import com.agelousis.payments.main.ui.personalInformation.models.OptionType
import com.agelousis.payments.utils.extensions.applyFloatingButtonBottomMarginWith
import com.agelousis.payments.utils.extensions.inPixel
import com.agelousis.payments.utils.extensions.isLastAt

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
            OptionType.CHANGE_PASSWORD_PIN ->
                it.userModel?.passwordPin
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

fun PersonalInformationFragment.configureRecyclerView() {
    binding?.optionRecyclerView?.adapter = OptionTypesAdapter(
        list = optionList,
        optionPresenter = this
    )
    binding?.optionRecyclerView?.addItemDecoration(
        DividerItemRecyclerViewDecorator(
            context = context ?: return,
            margin = resources.getDimension(R.dimen.activity_general_horizontal_margin).toInt()
        ) { position ->
            optionList.getOrNull(index = position) !is HeaderModel
                    && optionList.getOrNull(index = position) != OptionType.DELETE_USER
                    && !(optionList isLastAt position)
        }
    )
    binding?.optionRecyclerView?.addItemDecoration(
        HeaderItemDecoration(
            parent = binding?.optionRecyclerView ?: return
        ) { position ->
            optionList.getOrNull(index = position) is HeaderModel
        }
    )
    binding?.optionRecyclerView?.applyFloatingButtonBottomMarginWith(
        items = optionList
    )
    binding?.optionRecyclerView?.setOnScrollChangeListener { _, _, _, _, _ ->
        binding?.headerConstraintLayout?.elevation = if (binding?.optionRecyclerView?.canScrollVertically(-1) == true) 8.inPixel else 0.0f
    }
}